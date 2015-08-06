package org.monkeynuthead.monkeybarrel.glue

object MicroPickleSerializers extends Serializers {

  import Messages._
  import upickle.default._

  implicit val messageSerializer = new Serializer[Message] {

    override def serialize(a: Message): String = write(a)
    override def deserialize(s: String): Message = read[Message](s)

  }

}
