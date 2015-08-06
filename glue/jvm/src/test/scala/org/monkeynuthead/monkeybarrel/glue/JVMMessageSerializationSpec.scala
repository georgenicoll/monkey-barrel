package org.monkeynuthead.monkeybarrel.glue

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{MustMatchers, WordSpec}

@RunWith(classOf[JUnitRunner])
class JVMMessageSerializationSpec extends WordSpec with MustMatchers {

  "JVM Serialization" must {

    import org.monkeynuthead.monkeybarrel.glue.Messages._

    "allow me to serialize and deserialize a Message" in {

      import org.monkeynuthead.monkeybarrel.glue.MicroPickleSerializers._

      val message1 = Message("Do what you need to")

      val s = message1.serialize

      val message2 = s.deserialize

      message2 must equal(message1)
    }

  }

}
