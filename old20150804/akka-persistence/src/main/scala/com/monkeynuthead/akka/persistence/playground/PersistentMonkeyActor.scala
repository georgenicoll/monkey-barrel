package com.monkeynuthead.akka.persistence.playground

import akka.actor.{Props, ActorSystem}
import akka.persistence.{SnapshotOffer, PersistentActor}

case class Cmd(data: String)
case class Evt(data: String)

case class ExampleState(events: List[String] = Nil) {
  def updated(evt: Evt): ExampleState = copy(events = evt.data :: events)
  def size: Int = events.size
  override def toString: String = events.reverse.toString
}

/**
 * Shamelessly stolen from the akka persistence examples:
 * http://doc.akka.io/docs/akka/snapshot/scala/persistence.html
 */
class PersistentMonkeyActor extends PersistentActor {

  override val persistenceId = "stable-monkey-id-1"

  var state = ExampleState()

  @throws[Exception](classOf[Exception]) override
  def preStart(): Unit = super.preStart()

  def updateState(event: Evt): Unit = state = state.updated(event)

  def numEvents = state.size

  override def receiveRecover: Receive = {
    case evt: Evt => updateState(evt)
    case SnapshotOffer(_, snapshot: ExampleState) => state = snapshot
  }

  override def receiveCommand: Receive = {
    case Cmd(data) =>
      persist(Evt(s"${data}-${numEvents}"))(updateState)
      persist(Evt(s"${data}-${numEvents+1}")) { event =>
        updateState(event)
        context.system.eventStream.publish(event)
      }
    case "snap" => saveSnapshot(state)
    case "print" => println(state)
  }

}

object PersistentMonkeyActor extends App {

  val system = ActorSystem("example")
  val persistentActor = system.actorOf(Props[PersistentMonkeyActor], "persistent-monkey")

  persistentActor ! Cmd("foo")
  persistentActor ! Cmd("baz")
  persistentActor ! Cmd("bar")
  persistentActor ! "snap"
  persistentActor ! Cmd("buzz")
  persistentActor ! "print"

  Thread.sleep(1000)
  system.shutdown()

}