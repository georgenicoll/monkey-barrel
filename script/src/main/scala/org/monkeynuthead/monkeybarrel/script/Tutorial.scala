package org.monkeynuthead.monkeybarrel.script

import org.scalajs.jquery.jQuery
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scalatags.Text.all._

object TutorialApp extends JSApp {

  val ClickMe = "Click Me!"
  val ClickedFrom = "Clicked from ScalaJS"

  def appendPar(text: String): Unit = {
    jQuery("body").append(p(text).render)
  }

  def addClickedMessage(message: String): Unit = {
    appendPar(message)
  }

  def setupUI(): Unit = {
    jQuery(button(ClickMe, `type`:="button").render)
      .click(() => addClickedMessage(ClickedFrom))
      .appendTo(jQuery("body"))
    appendPar("Hello World!")
  }

  @JSExport
  override def main(): Unit = {
    jQuery(setupUI())
  }

}
