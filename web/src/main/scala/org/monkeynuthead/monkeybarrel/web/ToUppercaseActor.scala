package org.monkeynuthead.monkeybarrel.web

import akka.actor.{ActorLogging, ActorRef, Props, Actor}
import akka.actor.Actor.Receive
import akka.stream.scaladsl.Source

/**
 * V simple actor that converts an Option[Source[String, _]] to it's upper case variant.
 */
class ToUppercaseActor() extends Actor with ActorLogging {

  import ToUppercaseActor._

  var next: Option[ActorRef] = None

  override def receive: Receive = {
    case Message(source) =>
      next.foreach(_ ! Message(source.map(_.toUpperCase())))
    case Next(ref) =>
      next = Some(ref)
  }


  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    log.info(s"${this.getClass.getSimpleName} actor started")
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    log.info(s"${this.getClass.getSimpleName} actor stopped")
    super.postStop()
  }
}

object ToUppercaseActor {

  def create(): Props = Props(new ToUppercaseActor())

  case class Next(ref: ActorRef)
  case class Message(contents: Source[String, _])

}
