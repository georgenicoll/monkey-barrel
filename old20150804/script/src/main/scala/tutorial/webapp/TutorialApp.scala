package tutorial.webapp

import org.scalajs.jquery.jQuery

import scala.scalajs.js.JSApp
import scalatags.Text.all._

object TutorialApp extends JSApp {

  val HelloWorld = "Hello World!"
  val ClickMe = "Click Me!"
  val YouClickedTheButton = "You Clicked the Button"

  def appendPar(msg: String): Unit = {
    val par = p(msg)
    jQuery("body").append(par.render)
  }

  def setupUI(): Unit = {
    val btn = button(ClickMe)
    jQuery(btn.render).click(addClickedMessage _).appendTo(jQuery("body"))
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
