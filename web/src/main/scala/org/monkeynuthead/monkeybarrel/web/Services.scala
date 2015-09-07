package org.monkeynuthead.monkeybarrel.web

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{TextMessage, Message}
import akka.http.scaladsl.server.Directives._
import akka.stream.{OverflowStrategy, Materializer}
import akka.stream.scaladsl.{Keep, Sink, Source, Flow}
import akka.stream.stage.{TerminationDirective, SyncDirective, Context, PushStage}
import akkahttptwirl.TwirlSupport
import org.monkeynuthead.monkeybarrel.web.Model.HelloResult
import org.monkeynuthead.monkeybarrel.web.ToUppercaseActor.ClearNext
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
      .map(
        ToUppercaseActor.Message(_)
      )
      .to(
        Sink.actorRef(uppercaseActor, ClearNext)
      )

    val out = Source.actorRef[ToUppercaseActor.Message](10, OverflowStrategy.fail)
      .mapMaterializedValue(
        uppercaseActor ! ToUppercaseActor.Next(_)
      )
      .map(
        _.contents
      )

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
    .via(toUpperCaseFlow)
    .map {
      case Some(text) => TextMessage(text)
      case None => TextMessage("Failed in Processing")
    }
    .via(reportErrorsFlow(identifier))

    websockflow
  }

}
