package org.monkeynuthead.barrel.web

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.iteratee.{Input, Enumerator, Iteratee}

import scala.concurrent.{Promise, ExecutionContext, Await}
import scala.concurrent.duration._

import scala.language.postfixOps

/** Unit Test 'play ground' for trying things with Iteratees.
  */
@RunWith(classOf[JUnitRunner])
class IterateeSpec extends WordSpec with MustMatchers {

  "An iteratee" must {

    "sum values fed from an Enumerator" in {
      implicit val ec = ExecutionContext.global

      val sumIteratee = Iteratee.fold(0) { (sum: Int, chunk: Int) => sum + chunk }
      val intEnumerator = Enumerator(1,2,3,4,5,6)
      val iterateeFuture = intEnumerator(sumIteratee)
      Await.result(iterateeFuture.flatMap(_.run), 500 millis) must equal(21)
    }

  }

}
