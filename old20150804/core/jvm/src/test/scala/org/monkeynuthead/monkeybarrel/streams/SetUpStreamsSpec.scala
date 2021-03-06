package org.monkeynuthead.monkeybarrel.streams

import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{MustMatchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

@RunWith(classOf[JUnitRunner])
class SetUpStreamsSpec extends WordSpec with MustMatchers with StreamsTestSetup {

  "akka streams" must {

    "be possible to setup" in {
      withSystem { implicit system =>

        implicit val materializer = ActorMaterializer()

        val someText = "I am some text"
        val builder = List.newBuilder[String]

        val completeFuture = Source(() => someText.split(" ").iterator)
          .map(_.toUpperCase)
          .runForeach(builder += _)

        Await.ready(completeFuture, 3 seconds)

        builder.result().toArray must equal(someText.toUpperCase().split(" "))
      }
    }

  }

}
