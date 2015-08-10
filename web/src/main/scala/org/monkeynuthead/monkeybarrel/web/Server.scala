package org.monkeynuthead.monkeybarrel.web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.io.StdIn

object Server extends App with Service {

  implicit val system = ActorSystem("http-server")
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val Port = 8888;

  val bindingFuture = Http().bindAndHandle(routes, "localhost", Port)

  println(s"Server on-line at http://localhost:$Port\nPress RETURN to stop...")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.shutdown())

}
