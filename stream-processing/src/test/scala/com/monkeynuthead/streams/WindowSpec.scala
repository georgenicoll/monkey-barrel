package com.monkeynuthead.streams

import akka.actor.Status.Success
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpec}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Tests for the WindowByAttributes Window.
 */
@RunWith(classOf[JUnitRunner])
class WindowByAttributeSpec extends WordSpec with MustMatchers with BeforeAndAfterAll {

  implicit val system = ActorSystem(classOf[WindowByAttributeSpec].getSimpleName)
  implicit val materializer = ActorMaterializer()
  implicit val context = system.dispatcher

  import Windows._

  "window by attribute" must {

    "push out each event when no aggregation" in {
      val maps = Source.actorRef[DataType](10, OverflowStrategy.dropTail)
      val flow = maps.via(byAttributes(Flow[DataType], None))
      val sink: Sink[DataType, Future[List[DataType]]] = Sink.fold(List.newBuilder[DataType]) { (b, data: DataType) =>
        b += data
      }.mapMaterializedValue(_.map(_.result()))
      val graph = flow.toMat(sink)(Keep.both)
      val (actor, futureList) = graph.run()

      val m1 = Map("George" -> "Nicoll")
      val m2 = Map("Monkey" -> "NutHead")

      actor ! m1
      actor ! m2
      actor ! Success("Done")

      val result = Await.result(futureList, 3 seconds)
      result must equal(List(m1, m2))
    }

    "'aggregate' like events" in {

      val howMuchMapping: MappingFunction = (optExisting: Option[DataType], newData: DataType) => {
        val existingNum = optExisting.flatMap(_.get("HowMuch")).map(_.asInstanceOf[Double]).getOrElse(0d)
        val newNum = newData.get("HowMuch").map(_.asInstanceOf[Double]).getOrElse(0d)
        Map("HowMuch" -> (existingNum + newNum))
      }

      val maps = Source.actorRef[DataType](50, OverflowStrategy.fail)
      val flow = maps.via(byAttributes(Flow[DataType], Some(howMuchMapping), "George"))
      val sink: Sink[DataType, Future[List[DataType]]] = Sink.fold(List.newBuilder[DataType]) { (b, data: DataType) =>
        b += data
      }.mapMaterializedValue(_.map(_.result()))
      val graph = flow.toMat(sink)(Keep.both)
      val (actor, futureList) = graph.run()

      val m1 = Map("George" -> "Nicoll", "Monkey" -> "Hanger", "HowMuch" -> 56d)
      val m2 = Map("George" -> "Formby", "Monkey" -> "Lumpy", "HowMuch" -> 12d)
      val m3 = Map("George" -> "Nicoll", "Monkey" -> "Trumpy", "HowMuch" -> 24d)
      val m4 = Map("George" -> "Formby", "Monkey" -> "Magic", "HowMuch" -> -6d)

      actor ! m1
      actor ! m2
      actor ! m3
      actor ! m4
      actor ! Success("Done")

      val result = Await.result(futureList, 3 seconds)
      result must equal(List(
        Map("George" -> "Nicoll", "HowMuch" -> 56d),
        Map("George" -> "Formby", "HowMuch" -> 12d),
        Map("George" -> "Nicoll", "HowMuch" -> 80d),
        Map("George" -> "Formby", "HowMuch" -> 6d)
      ))
    }

  }

  override protected def afterAll(): Unit = {
    system.shutdown()
    super.afterAll()
  }
}
