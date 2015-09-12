package org.monkeynuthead.monkeybarrel.web

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpec}

import scala.language.{implicitConversions, postfixOps}

/**
 * Testing for the BroadcastMessagesActor.
 */
class BroadcastMessagesActorSpec extends WordSpec with MustMatchers with BeforeAndAfterAll {

  implicit val system = ActorSystem()

  "The Broadcast Actor" must {

    import BroadcastMessagesActor._

    import scala.concurrent.duration._

    val NoMsgTimeout = 150 millis

    implicit def intToID(i: Int) = new ID(i.toLong, 0)

    "Allow 2 actors to be registered and broadcast to all" in {
      val broadcast = TestActorRef(create())
      val a1 = new TestKit(system)
      val a2 = new TestKit(system)

      broadcast ! Register(1, a1.testActor)
      broadcast ! Register(2, a2.testActor)

      val source = Source.single("Hello")
      broadcast ! Message(source)

      a1.expectMsg(Message(source))
      a2.expectMsg(Message(source))
    }

    "Allow 2 actors to be registered and messages to be sent between them" in {
      val broadcast = TestActorRef(create())
      val a1 = new TestKit(system)
      val a2 = new TestKit(system)

      broadcast ! Register(1, a1.testActor)
      broadcast ! Register(2, a2.testActor)

      val hello2 = Message(Source.single("Hello2"), Some(1))
      broadcast ! hello2
      a1.expectNoMsg(NoMsgTimeout)
      a2.expectMsg(hello2)

      val hello1 = Message(Source.single("Hello1"), Some(2))
      broadcast ! hello1
      a1.expectMsg(hello1)
      a2.expectNoMsg(NoMsgTimeout)
    }

    "Allow 3 actors to be registered and broadcast messages between them" in {
      val broadcast = TestActorRef(create())
      val a1 = new TestKit(system)
      val a2 = new TestKit(system)
      val a3 = new TestKit(system)

      broadcast ! Register(1, a1.testActor)
      broadcast ! Register(2, a2.testActor)
      broadcast ! Register(3, a3.testActor)

      val hello2And3 = Message(Source.single("Hello2+3"), Some(1))
      broadcast ! hello2And3
      a1.expectNoMsg(NoMsgTimeout)
      a2.expectMsg(hello2And3)
      a3.expectMsg(hello2And3)
    }

    "Remove actors on unregistration" in {
      val broadcast = TestActorRef(create())
      val a1 = new TestKit(system)
      val a2 = new TestKit(system)
      val a3 = new TestKit(system)

      broadcast ! Register(1, a1.testActor)
      broadcast ! Register(2, a2.testActor)
      broadcast ! Register(3, a3.testActor)
      broadcast ! Unregister(2)

      val helloSome = Message(Source.single("HelloSome"), Some(1))
      broadcast ! helloSome
      a1.expectNoMsg(NoMsgTimeout)
      a2.expectNoMsg(NoMsgTimeout)
      a3.expectMsg(helloSome)
    }

  }

  override protected def afterAll(): Unit = {
    system.shutdown()
    super.afterAll()
  }

}
