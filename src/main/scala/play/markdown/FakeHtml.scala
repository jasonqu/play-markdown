package play.markdown

import play.templates._

case class Html(text: String) extends Appendable[Html] {
  val buffer = new StringBuilder(text)
  
  def +=(other: Html) = {
    buffer.append(other.buffer)
    this
  }
  override def toString = buffer.toString
}

object HtmlFormat extends Format[Html] {
  /**
   * Creates a raw (unescaped) HTML fragment.
   */
  def raw(text: String): Html = Html(text)

  /**
   * Creates a safe (escaped) HTML fragment.
   */
  def escape(text: String): Html = {
    // Using our own algorithm here because commons lang escaping wasn't designed for protecting against XSS, and there
    // don't seem to be any other good generic escaping tools out there.
    val sb = new StringBuilder(text.length)
    text.foreach {
      case '<' => sb.append("&lt;")
      case '>' => sb.append("&gt;")
      case '"' => sb.append("&quot;")
      case '\'' => sb.append("&#x27;")
      case '&' => sb.append("&amp;")
      case c => sb += c
    }
    Html(sb.toString())
  }
}