package org.monkeynuthead.monkeybarrel.aggregator

import akka.actor.ActorSystem
import akka.actor.Status.Success
import akka.stream.{OverflowStrategy, ActorMaterializer}
import akka.stream.scaladsl._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpec}

import scala.collection.mutable.{Builder => MutBldr}
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Basic setting up of an Actor based stream.
 */
@RunWith(classOf[JUnitRunner])
class SetUpStreamsSpec extends WordSpec with MustMatchers with BeforeAndAfterAll {

  implicit var system: ActorSystem = null

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    system = ActorSystem("SetUpStreams")
  }

  override protected def afterAll(): Unit = {
    Await.ready(system.terminate(), 2 seconds)
    super.afterAll()
  }

  "I" must {

    "understand how to set up a simple stream" in {
      implicit val materializer = ActorMaterializer()

      val text = "My First Stream Test"

      val futureBuilder =
        Source(() => text.split(" ").iterator)
          .map(_.toUpperCase())
          .runFold(List.newBuilder[String]) { (bld, str) =>
            bld += str
          }

      val result = Await.result(futureBuilder, 1 second).result()
      result must equal(text.toUpperCase().split(" ").toList)
    }

    "connect up a source and sink and pump data through" in {
      implicit val materializer = ActorMaterializer()

      val actorSource = Source.actorRef[String](100, OverflowStrategy.fail)

      val builderSink = Sink.fold[MutBldr[String, List[String]], String](List.newBuilder) { (bld, s) => bld += s }

      val (actor, futureBuilder) = FlowGraph.closed(actorSource, builderSink)((_,_)) { implicit builder =>
        (actor, sink) =>
          import FlowGraph.Implicits._
          actor ~> sink
      }.run()

      val words = List("Feed", "Me", "From", "An", "Actor")

      words.foreach(actor ! _)
      actor ! Success(0)

      val result = Await.result(futureBuilder, 1 second).result()
      result must equal(words)
    }

    "connect a source, sink and flow and pump data through" in {
      implicit val materializer = ActorMaterializer()

      val source = Source.actorRef[String](100, OverflowStrategy.fail)
      val flow = Flow[String].map(_.toUpperCase())
      val sink = Sink.fold[MutBldr[String, List[String]],String](List.newBuilder) { (bld, s) =>
        bld += s
      }

      val (inputActor, futureBuilder) = FlowGraph.closed(source, flow, sink)((a,f,b) => (a, b)) { implicit builder =>
        (source, flow, sink) =>
          import FlowGraph.Implicits._
          source ~> flow ~> sink
      }.run()

      val words = List("Push", "Data", "Through", "Flow", "To", "Sink")
      words.foreach(inputActor ! _)
      inputActor ! Success(0)

      val result = Await.result(futureBuilder, 1 second).result()
      result must equal(words.map(_.toUpperCase))
    }

    "broadcast from a source to multiple sinks" in {
      implicit val materializer = ActorMaterializer()

      val source = Source.actorRef[String](100, OverflowStrategy.fail)
      def sink() : Sink[String,Future[MutBldr[String,List[String]]]] =
        Sink.fold[MutBldr[String,List[String]],String](List.newBuilder) { (bld, s) => bld += s }

      val (inputActor, futBldr1, futBldr2) = FlowGraph.closed(source, sink(), sink())((i,o1,o2) => (i,o1,o2)) {
        implicit builder =>
          (source, sink1, sink2) =>
            import FlowGraph.Implicits._
            val bcast = builder.add(Broadcast[String](2))
            source ~> bcast ~> sink1
                      bcast ~> sink2
      }.run()

      val words = List("Send", "this", "to", "both", "please")
      words.foreach(inputActor ! _)
      inputActor ! Success(0)

      val result1 = Await.result(futBldr1, 1 second).result()
      result1 must equal(words)

      val result2 = Await.result(futBldr2, 1 second).result()
      result2 must equal(words)
    }

    "broadcast and then zip into a single sink" in {
      implicit val materializer = ActorMaterializer()

      type Strings = (String,String)

      val source = Source.actorRef[String](100, OverflowStrategy.fail)
      val sink = Sink.fold[MutBldr[Strings,List[Strings]],Strings](List.newBuilder) { (bld, strs) => bld += strs }

      val (inputActor, futureBldr) = FlowGraph.closed(source, sink) ((_, _)) { implicit builder =>
        (source, sink) =>
          import FlowGraph.Implicits._

          val bcast = builder.add(Broadcast[String](2))
          val zip = builder.add(Zip[String,String])
          val idFlow = Flow[String]
          val upFlow = Flow[String].map(_.toUpperCase())

          source ~> bcast ~> idFlow ~> zip.in0
                    bcast ~> upFlow ~> zip.in1
          zip.out ~> sink
      }.run()

      val words = List("Upper", "And", "Lower", "Together", "Please")

      words.foreach(inputActor ! _)
      inputActor ! Success(0)

      val result = Await.result(futureBldr, 1 second).result()
      result must equal(words.zip(words.map(_.toUpperCase())))
    }

  }

}
