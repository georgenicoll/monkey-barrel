package org.monkeynuthead.monkeybarrel.web

object Model {

  case class HelloData(from: String, say: Option[String])
  case class HelloResult(to: String, say: String)

}
