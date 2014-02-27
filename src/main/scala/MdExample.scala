import java.io._
import play.markdown.Helper.CompilerHelper
import play.markdown.Html

object MdExample extends App {

  val sourceDir = new File("src/test/resources/markdown")
  val generatedDir = new File("target/test/generated-templates")
  val generatedClasses = new File("target/test/generated-classes")
  scalax.file.Path(generatedDir).deleteRecursively()
  scalax.file.Path(generatedClasses).deleteRecursively()
  scalax.file.Path(generatedClasses).createDirectory()

  val helper = new CompilerHelper(sourceDir, generatedDir, generatedClasses)
  val m = helper.compile[(() => Html)]("index.scala.md", "md.index")()

  val lines = m.toString().split("\n").toList
  val md = lines.drop(1).dropWhile { !_.startsWith("---") }

  import org.pegdown.PegDownProcessor
  val pro = new PegDownProcessor()

  println(pro.markdownToHtml(md.drop(1).mkString("\n")))

}