package org.monkeynuthead.monkeybarrel.web

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.RequestEntity
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, MustMatchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class MicroPickleSupportSpec extends WordSpec with MustMatchers with BeforeAndAfterAll {

  import MicroPickleSupport._
  import Model._

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  "MicroPickleSupport" must {
    import system.dispatcher

    "marshal and unmarshal my Model.HelloData objects" in {

      val hello = HelloData("Rodney", Some("Trotter"))
      val entity = Await.result(Marshal(hello).to[RequestEntity], 100 millis)
      val unmarshalled = Await.result(Unmarshal(entity).to[HelloData], 100 millis)
      unmarshalled mustBe hello

    }

    "marshal and unmarshal my Model.HelloResult objects" in {

      val result = HelloResult("Delboy", "Trotter")
      val entity = Await.result(Marshal(result).to[RequestEntity], 100 millis)
      val unmarshalled = Await.result(Unmarshal(entity).to[HelloResult], 100 millis)
      unmarshalled mustBe result

    }

    "let me see what a serialized object looks like" in {
      import upickle.default._

      val data = HelloData("Bob", Some("A Job"))

      val dataJson = write(data)
      println(dataJson)

      val loaded = read[HelloData](dataJson)

      loaded mustBe data
    }

  }

}
