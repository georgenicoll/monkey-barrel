package org.monkeynuthead.monkeybarrel.web

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.testkit.{TestKit, TestActorRef}
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpec}
import org.scalatest.junit.JUnitRunner

import scala.concurrent.Await

import scala.language.postfixOps

/**
 * Tests for the [[org.monkeynuthead.monkeybarrel.web.ToUppercaseActor]]
 */
@RunWith(classOf[JUnitRunner])
class ToUppercaseActorSpec extends WordSpec with MustMatchers with BeforeAndAfterAll {

  import scala.concurrent.duration._
  import ToUppercaseActor._

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  "The ToUppercaseActor" must {

    "convert Some(a) to Some(a.toUpperCase())" in {

      import system.dispatcher

      val kit = new TestKit(system)
      val ref = TestActorRef(ToUppercaseActor.create())

      ref ! Next(kit.testActor)
      ref ! Message(Source.single("George Test"))

      val finalString = kit.expectMsgPF(3 seconds) {
        case Message(source) => source
      }.runFold(new StringBuilder()) { (b, s) => b.append(s) }.map(_.toString())

      Await.result(finalString, 3 seconds) must equal("George Test".toUpperCase())
    }

  }

  override protected def afterAll(): Unit = {
    system.shutdown()
    super.afterAll()
  }

}
