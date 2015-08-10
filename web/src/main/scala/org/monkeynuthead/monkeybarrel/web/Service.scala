package org.monkeynuthead.monkeybarrel.web

import akka.actor.ActorSystem
import akka.dispatch.ExecutorServiceConfigurator
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import org.monkeynuthead.monkeybarrel.web.Model.HelloResult
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable

import scala.concurrent.ExecutionContextExecutor

/**
 * Pattern taken from the typesafe activator akka-http-microservice project
 */
trait Service extends Protocols {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  val routes = path("hello") {
    get {
      complete {
        <html>
          <body>
            <h1>Hello from akka-http!</h1>
          </body>
        </html>
      }
    } ~
    (post & entity(as[Model.HelloData])) { helloData =>
      complete {
        ToResponseMarshallable(HelloResult(helloData.from, helloData.say.getOrElse("Howdy!")))
      }
    }
  }

}
