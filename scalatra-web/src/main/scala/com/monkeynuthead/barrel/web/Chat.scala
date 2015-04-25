package com.monkeynuthead.barrel.web

import java.util.Date

import grizzled.slf4j.Logging
import org.json4s.JsonDSL._
import org.json4s.{JValue, Formats}
import org.scalatra.atmosphere._
import org.scalatra.json.{JacksonJsonSupport, JValueResult}
import org.scalatra.scalate.ScalateSupport
import org.scalatra.{ErrorHandler, SessionSupport, UrlGeneratorSupport, ScalatraServlet}

class Chat extends ScalatraServlet with ScalateSupport
  with JValueResult with JacksonJsonSupport with SessionSupport with AtmosphereSupport
  with Logging {

  override protected implicit def jsonFormats: Formats = org.json4s.DefaultFormats

  get("/") {
    contentType="text/html"
    ssp("/index")
  }

  import scala.concurrent.ExecutionContext.Implicits._

  atmosphere("/the-chat") {
    new AtmosphereClient {
      def receive: AtmoReceive = {
        case Connected =>
          info("Client %s is connected" format uuid)
          broadcast(("author" -> "Someone") ~
            ("message" -> "joined the room") ~
            ("time" -> (new Date().getTime.toString )), Everyone)

        case Disconnected(ClientDisconnected, _) =>
          broadcast(("author" -> "Someone") ~
            ("message" -> "has left the room") ~
            ("time" -> (new Date().getTime.toString )), Everyone)

        case Disconnected(ServerDisconnected, _) =>
          info("Server disconnected the client %s" format uuid)

        case _: TextMessage =>
          send(("author" -> "system") ~
            ("message" -> "Only json is allowed") ~
            ("time" -> (new Date().getTime.toString )))

        case JsonMessage(json) =>
          info("Got message %s from %s".format((json \ "message").extract[String], (json \ "author").extract[String]))
          val msg = json merge (("time" -> (new Date().getTime().toString)): JValue)
          broadcast(msg) // by default a broadcast is to everyone but self
        //  send(msg) // also send to the sender
      }
    }
  }

  val myErrorHandler: ErrorHandler = {
    case t: Throwable => t.printStackTrace()
  }
  error(handler = myErrorHandler)

}
