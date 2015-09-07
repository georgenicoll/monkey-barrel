package org.monkeynuthead.monkeybarrel.web

import akka.actor.{ActorRef, Props, Actor}
import akka.actor.Actor.Receive
import akka.stream.scaladsl.Source

/**
 * V simple actor that converts an Option[Source[String, _]] to it's upper case variant.
 */
class ToUppercaseActor() extends Actor {

  import ToUppercaseActor._

  var next: Option[ActorRef] = None

  override def receive: Receive = {
    case Message(source) =>
      next.foreach(_ ! Message(source.map(_.map(_.toUpperCase()))))
    case Next(ref) =>
      next = Some(ref)
    case ClearNext =>
      next = None
  }

}

object ToUppercaseActor {

  def create(): Props = Props(new ToUppercaseActor())

  case class Next(ref: ActorRef)
  case class ClearNext()
  case class Message(contents: Option[Source[String, _]])

}
