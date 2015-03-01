package tutorial.webapp

import org.scalajs.dom
import org.scalajs.dom.document

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object TutorialApp extends JSApp {

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }

  @JSExport
  def addClickedMessage(): Unit = {
    appendPar(document.body, "You clicked the button!")
  }

  def main(): Unit = {
    appendPar(document.body, "Hello World!")
  }

}
