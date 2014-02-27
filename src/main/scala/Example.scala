import java.io._
import play.markdown.Helper.CompilerHelper
import play.markdown.Html

object Example extends App {

  val sourceDir = new File("src/test/resources")
  val generatedDir = new File("target/test/generated-templates")
  val generatedClasses = new File("target/test/generated-classes")
  scalax.file.Path(generatedDir).deleteRecursively()
  scalax.file.Path(generatedClasses).deleteRecursively()
  scalax.file.Path(generatedClasses).createDirectory()

  val helper = new CompilerHelper(sourceDir, generatedDir, generatedClasses)
  val m = 
    helper.compile[(() => Html)]("static.scala.html", "html.static")()
    //helper.compile[((String, List[String]) => (Int) => Html)]("real.scala.html", "html.real")("World", List("A", "B"))(4)
    //helper.compile[((String) => Html)]("patternMatching.scala.html", "html.patternMatching")("12345")
    //helper.compile[((String) => Html)]("hello.scala.html", "html.hello")("World")
    //helper.compile[((collection.immutable.Set[String]) => Html)]("set.scala.html", "html.set")(Set("first","second","third"))
    //helper.compile[(() => Html)]("error.scala.html", "html.error")
    //helper.compile[(() => Html)]("long.scala.html", "html.long")()
  println(m)

}