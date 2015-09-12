package org.monkeynuthead.monkeybarrel.web

import java.util.UUID

import akka.actor._
import akka.stream.scaladsl.Source

/**
 * Actor that allows actors to be registered with it.  Messages can either be broadcast to all
 * or broadcast to everyone apart from a specific actor.
 */
class BroadcastMessagesActor extends Actor with ActorLogging {

  import BroadcastMessagesActor._

  var registered = Map.empty[ID, ActorRef]

  def receive = {
    case Register(id, actor) =>
      context.watch(actor)
      registered = registered + (id -> actor)
      log.info("Registered {} with id {}", actor, id)

    case Unregister(id) =>
      for(actor <- registered.get(id)) {
        registered = registered - id
        context.unwatch(actor)
        log.info("Unregistered {}", actor)
      }

    case m @ Message(source, optId) =>
      registered
        .filterKeys(
          optId.map((excluded) => { (id: ID) => id != excluded })
            .getOrElse((_) => true))
        .foreach {
          case(id,actor) =>
            actor ! m
            log.debug("Message sent to {}[{}]: {}", id, actor, source)
        }

    case Terminated(actor) =>
      registered.find {
        case (id, ref) => actor == ref
      }.foreach {
        case (id, ref) =>
          registered = registered - id
          log.info("Terminated {} with id {}", actor, id)
      }

  }

}

object BroadcastMessagesActor {

  type ID = UUID

  case class Register(id: ID, ref: ActorRef)
  case class Unregister(id: ID)
  case class Message(source: Source[String, _], fromId: Option[ID] = None)

  def create() = Props(new BroadcastMessagesActor())

}
