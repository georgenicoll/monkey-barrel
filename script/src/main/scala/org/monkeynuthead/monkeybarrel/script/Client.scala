package org.monkeynuthead.monkeybarrel.script

import org.scalajs.dom
import org.scalajs.dom.raw.WebSocket
import rx._
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._
import scalatags.rx.all._

object Client extends JSApp {

  //val WsService = "ws://echo.websocket.org"
  val WsService = "ws://localhost:8888/echo"

  val status = Var("Not Connected")

  def infoText(text: String): Unit = {
    status() = text
  }

  def socketOpened(socket: WebSocket): Unit = {
    infoText("Open")

    val input = textarea().render
    dom.document.body.appendChild(input)

    val sendButton = button("SendData").render
    dom.document.body.appendChild(sendButton)
    sendButton.onclick = (e: dom.MouseEvent) => {
      val dataToSend = input.value
      socket.send(dataToSend)
      input.value = ""
    }

  }

  def setupUI(): Unit = {
    val statusText = Rx { s"Current Status: ${status()}" }
    dom.document.body.appendChild(
      p(statusText).render
    )
    val location = WsService
    val websock = new dom.WebSocket(location)
    websock.onerror = (e: dom.ErrorEvent) => infoText(s"Error: ${e.message}")
    websock.onopen = (e: dom.Event) => socketOpened(websock)
    websock.onclose = (e: dom.CloseEvent) => infoText(s"Closed: ${e.code} '${e.reason}'")
    websock.onmessage = (e: dom.MessageEvent) => infoText(s"Message: " + e.data.toString())
  }

  @JSExport
  override def main(): Unit = {
    dom.document.onreadystatechange = (e: dom.Event) => {
      println(dom.document.readyState)
      if (dom.document.readyState == "complete") {
        setupUI()
      }
    }
  }

}
