package play.markdown

import play.templates._

import java.io._

object Helper {
  case class CompilationError(message: String, line: Int, column: Int) extends RuntimeException(message)

  class CompilerHelper(sourceDir: File, generatedDir: File, generatedClasses: File) {
    import scala.tools.nsc.Global
    import scala.tools.nsc.Settings
    import scala.tools.nsc.reporters.ConsoleReporter
    import scala.reflect.internal.util.Position
    import scala.collection.mutable

    import java.net._

    val templateCompiler = ScalaTemplateCompiler

    val classloader = new URLClassLoader(Array(generatedClasses.toURI.toURL), Class.forName("play.templates.ScalaTemplateCompiler").getClassLoader)

    // A list of the compile errors from the most recent compiler run
    val compileErrors = new mutable.ListBuffer[CompilationError]

    val compiler = {

      def additionalClassPathEntry: Option[String] = Some(
        Class.forName("play.templates.ScalaTemplateCompiler").getClassLoader.asInstanceOf[URLClassLoader].getURLs.map(_.getFile).mkString(":"))

      val settings = new Settings
      settings processArgumentString "-usejavacp"
      val scalaObjectSource = Class.forName("scala.ScalaObject").getProtectionDomain.getCodeSource

      // is null in Eclipse/OSGI but luckily we don't need it there
      if (scalaObjectSource != null) {
        val compilerPath = Class.forName("scala.tools.nsc.Interpreter").getProtectionDomain.getCodeSource.getLocation
        val libPath = scalaObjectSource.getLocation
        val pathList = List(compilerPath, libPath)
        val origBootclasspath = settings.bootclasspath.value
        settings.bootclasspath.value = ((origBootclasspath :: pathList) ::: additionalClassPathEntry.toList) mkString File.pathSeparator
        settings.outdir.value = generatedClasses.getAbsolutePath
      }
      settings.outdir.value = generatedClasses.getAbsolutePath

      val compiler = new Global(settings, new ConsoleReporter(settings) {
        override def printMessage(pos: Position, msg: String) = {
          compileErrors.append(CompilationError(msg, pos.line, pos.point))
        }
      })

      new compiler.Run

      compiler
    }

    def compile[T](templateName: String, className: String): T = {
      val templateFile = new File(sourceDir, templateName)
      val Some(generated) = templateCompiler.compile(templateFile, sourceDir, generatedDir, "play.markdown.HtmlFormat")

      val mapper = GeneratedSource(generated)

      val run = new compiler.Run

      compileErrors.clear()

      run.compile(List(generated.getAbsolutePath))

      compileErrors.headOption.foreach {
        case CompilationError(msg, line, column) => {
          compileErrors.clear()
          throw CompilationError(msg, mapper.mapLine(line), mapper.mapPosition(column))
        }
      }

      val t = classloader.loadClass(className + "$").getDeclaredField("MODULE$").get(null)

      t.getClass.getDeclaredMethod("f").invoke(t).asInstanceOf[T]
    }
  }
}
