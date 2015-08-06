package org.monkeynuthead.monkeybarrel.glue

trait Serializers {

  implicit class SerializeWrapper[A](a: A)(implicit serializer: Serializer[A]) {

    def serialize: String = serializer.serialize(a)

  }

  implicit class DeserializeWrapper[A](s: String)(implicit serializer: Serializer[A]) {

    def deserialize: A = serializer.deserialize(s)

  }

}
