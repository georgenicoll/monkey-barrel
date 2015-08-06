package org.monkeynuthead.monkeybarrel.glue

trait Serializer[A] {

  def serialize(a: A): String

  def deserialize(s: String): A

}
