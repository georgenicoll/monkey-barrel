package handson

import org.scalajs.dom
import scalatags.JsDom.all._

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object HandsOn extends JSApp {

  def setupUI(): Unit = {
    val canvas = dom.document.getElementById("canvas").asInstanceOf[dom.html.Canvas]
    val renderer = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    renderer.fillStyle = "#f8f8f8"
    renderer.fillRect(0, 0, canvas.width, canvas.height)

    renderer.fillStyle = "black"
    var down = false
    canvas.onmousedown = (_: dom.MouseEvent) => down = true
    canvas.onmouseup = (_: dom.MouseEvent) => down = false
    canvas.onmousemove = (e: dom.MouseEvent) => {
      val rect = canvas.getBoundingClientRect()
      if (down) renderer.fillRect(
        e.clientX - rect.left,
        e.clientY - rect.top,
        5, 5
      )
    }

  }

  @JSExport
  override def main(): Unit = {
    dom.document.onreadystatechange = (event: dom.Event) => {
      if (dom.document.readyState == "complete") {
        setupUI()
      }
    }
  }

}
