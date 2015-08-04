package org.monkeynuthead.monkeybarrel.streams

import akka.actor.ActorSystem
import org.scalatest.{BeforeAndAfterAll, Suite}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait StreamsTestSetup extends BeforeAndAfterAll {

  self: Suite =>

  var system: Option[ActorSystem] = None

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    system = Some(ActorSystem(getClass.getSimpleName))
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    system = system.flatMap { sys =>
      sys.terminate()
      Await.result(sys.whenTerminated, Duration.Inf)
      None
    }
  }

  protected final def withSystem(f: (ActorSystem) => Unit): Unit = {
    system match {
      case Some(sys) => f(sys)
      case None => fail("Test System is not setup")
    }
  }

}
