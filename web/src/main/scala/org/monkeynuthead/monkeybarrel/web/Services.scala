package org.monkeynuthead.monkeybarrel.web

import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{PoisonPill, ActorSystem}
import akka.http.scaladsl.model.ws.{TextMessage, Message}
import akka.http.scaladsl.server.Directives._
import akka.stream.{OverflowStrategy, Materializer}
import akka.stream.scaladsl.{Keep, Sink, Source, Flow}
import akka.stream.stage.{TerminationDirective, SyncDirective, Context, PushStage}
import akkahttptwirl.TwirlSupport
import org.monkeynuthead.monkeybarrel.web.BroadcastMessagesActor.Register
import org.monkeynuthead.monkeybarrel.web.Model.HelloResult
import scala.concurrent.ExecutionContextExecutor

/**
 * Pattern taken from the typesafe activator akka-http-microservice project
 */
trait Services extends MicroPickleSupport with TwirlSupport {

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  val routes = path("hello" / Segments) { segs =>
    get {
      complete {
        views.html.hello(segs.mkString(" "))
      }
    } ~
    (post & entity(as[Model.HelloData])) { helloData =>
      complete {
        HelloResult(helloData.from, helloData.say.getOrElse("Howdy!"))
      }
    }
  } ~
  path("client") {
    get {
      complete {
        views.html.client()
      }
    }
  } ~
  path("echo" / Segment) { seg =>
    handleWebsocketMessages(echoInUppercaseFlow(seg))
  } ~
  path("echo") {
    handleWebsocketMessages(echoInUppercaseFlow("Undefined"))
  } ~
  path("scripts" / Segment) { segment =>
    getFromResource(segment)
  }

  type SourceString = Source[String, _]

  //Perform the update via a simple actor
  private[this] def toUpperCaseFlow: Flow[Option[SourceString],Option[SourceString],Unit] = {

    val uppercaseActor = system.actorOf(ToUppercaseActor.create())

    val in = Flow[Option[SourceString]]
      .filter {
        case Some(source) => true
        case _ => false
      }
      .map {
        case Some(source) => ToUppercaseActor.Message(source)
        case _ => ToUppercaseActor.Message(Source.single("Unexpected"))
      }
      .to(Sink.actorRef(uppercaseActor, PoisonPill))

    val out = Source.actorRef[ToUppercaseActor.Message](10, OverflowStrategy.fail)
      .mapMaterializedValue(uppercaseActor ! ToUppercaseActor.Next(_))
      .map(m => Some(m.contents))

    Flow.wrap(in, out)(Keep.none)
  }

  lazy val broadcaster = system.actorOf(BroadcastMessagesActor.create(), "broadcaster")

  //Set up broadcasting flow
  private[this] def broadcastFlow: Flow[Option[SourceString],Option[SourceString],Unit] = {

    val id = UUID.randomUUID()

    val in = Flow[Option[SourceString]]
      .filter {
        case Some(source) => true
        case _ => false
      }
      .map {
        case Some(source) => BroadcastMessagesActor.Message(source, Some(id))
        case _ => BroadcastMessagesActor.Message(Source.single("Unexpected"), Some(id))
      }
      .to(Sink.actorRef(broadcaster, BroadcastMessagesActor.Unregister(id)))

    val out = Source.actorRef[BroadcastMessagesActor.Message](10, OverflowStrategy.fail)
      .mapMaterializedValue(broadcaster ! BroadcastMessagesActor.Register(id, _))
      .map(m => Some(m.source))

    Flow.wrap(in, out)(Keep.none)
  }

  //Ashamedly copied from https://github.com/jrudolph/akka-http-scala-js-websocket-chat/
  private[this] def reportErrorsFlow[T](identifier: String): Flow[T, T, Unit] = {
    Flow[T].transform { () =>
      new PushStage[T, T] {
        override def onPush(elem: T, ctx: Context[T]): SyncDirective = ctx.push(elem)

        override def onUpstreamFailure(cause: Throwable, ctx: Context[T]): TerminationDirective = {
          println(s"WebSocket '$identifier' failed with cause: $cause")
          super.onUpstreamFailure(cause, ctx)
        }


        @throws[Exception](classOf[Exception])
        override def postStop(): Unit = {
          println(s"WebSocket '$identifier' stopped.")
          super.postStop()
        }

      }
    }
  }

  private[this] def echoInUppercaseFlow(identifier: String): Flow[Message, Message, Unit] = {
    println(s"Constructing web socket flow for '$identifier'")
    //Go via another flow - this will be replaced with something useful, for the moment just use the uppercase flow
    val websockflow = Flow[Message].map {
      case m: TextMessage => Some(m.textStream)
      case _ => None
    }
    //.via(toUpperCaseFlow)
    .via(broadcastFlow)
    .map {
      case Some(text) => TextMessage(text)
      case None => TextMessage("Failed in Processing")
    }
    .via(reportErrorsFlow(identifier))

    websockflow
  }

}
