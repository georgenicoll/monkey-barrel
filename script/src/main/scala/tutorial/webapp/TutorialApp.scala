package tutorial.webapp

import org.scalajs.jquery.jQuery

import scala.scalajs.js.JSApp

object TutorialApp extends JSApp {

  val HelloWorld = "Hello World!"
  val ClickMe = "Click Me!"
  val YouClickedTheButton = "You Clicked the Button"

  def appendPar(msg: String): Unit = {
    jQuery("body").append(s"<p>${msg}</p>")
  }

  def setupUI(): Unit = {
    jQuery(s"""<button type="button">${ClickMe}</button>""")
      .click(addClickedMessage _)
      .appendTo(jQuery("body"))
    appendPar(HelloWorld)
  }

  def addClickedMessage(): Unit = {
    appendPar(YouClickedTheButton)
  }

  def main(): Unit = {
    jQuery(setupUI _)
    println(s"Started ${getClass().getName()}")
  }

}
