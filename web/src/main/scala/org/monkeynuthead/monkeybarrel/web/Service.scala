package org.monkeynuthead.monkeybarrel.web

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import akkahttptwirl.TwirlSupport
import org.monkeynuthead.monkeybarrel.web.Model.HelloResult
import scala.concurrent.ExecutionContextExecutor

/**
 * Pattern taken from the typesafe activator akka-http-microservice project
 */
trait Service extends MicroPickleSupport with TwirlSupport {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  val routes = path("hello" / Segments) { segs =>
    get {
      complete {
        views.html.hello.render(segs.mkString(" "))
      }
    } ~
    (post & entity(as[Model.HelloData])) { helloData =>
      complete {
        HelloResult(helloData.from, helloData.say.getOrElse("Howdy!"))
      }
    }
  }

}
