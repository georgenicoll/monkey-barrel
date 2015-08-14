package org.monkeynuthead.monkeybarrel.web

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.unmarshalling.{Unmarshaller, FromEntityUnmarshaller}
import akka.stream.Materializer

object MicroPickleSupport extends MicroPickleSupport

trait MicroPickleSupport {

  import ContentTypes._
  import upickle.default._

  implicit def microPickleMarshaller[A: Writer]: ToEntityMarshaller[A] =
    Marshaller.StringMarshaller.wrap(`application/json`) { obj =>
      write(obj)
    }

  implicit def microPickleUnmarshaller[A: Reader](implicit mat: Materializer): FromEntityUnmarshaller[A] =
    Unmarshaller.stringUnmarshaller.forContentTypes(`application/json`).map { s =>
      read[A](s)
    }

}
