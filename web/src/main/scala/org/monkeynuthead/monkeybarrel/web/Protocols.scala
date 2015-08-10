package org.monkeynuthead.monkeybarrel.web

import org.monkeynuthead.monkeybarrel.web.Model.{HelloResult, HelloData}
import spray.json.DefaultJsonProtocol

/**
 * Pattern taken from the typesafe activator akka-http-microservice project
 */
trait Protocols extends DefaultJsonProtocol {

  implicit val helloDataFormat = jsonFormat2(HelloData.apply)
  implicit val helloDataReultFormat = jsonFormat2(HelloResult.apply)

}
