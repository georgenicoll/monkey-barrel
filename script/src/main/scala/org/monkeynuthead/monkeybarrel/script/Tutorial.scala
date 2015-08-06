package org.monkeynuthead.monkeybarrel.script

import org.scalajs.jquery.jQuery
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object TutorialApp extends JSApp {

  val ClickMe = "Click Me!"
  val ClickedFrom = "Clicked from ScalaJS"

  def appendPar(text: String): Unit = {
    jQuery("body").append(s"<p>$text</p>")
  }

  def addClickedMessage(message: String): Unit = {
    appendPar(message)
  }

  def setupUI(): Unit = {
    jQuery(s"""<button type="button">$ClickMe</button>""")
      .click(() => addClickedMessage(ClickedFrom))
      .appendTo(jQuery("body"))
    appendPar("Hello World!")
  }

  @JSExport
  override def main(): Unit = {
    jQuery(setupUI _)
  }

}
