package com.monkeynuthead.monkeybarrel.glue

import utest._
import utest.framework.TestSuite

object SerializeDeserializeTest extends TestSuite {

  import org.monkeynuthead.monkeybarrel.glue.Messages._
  import org.monkeynuthead.monkeybarrel.glue.MicroPickleSerializers._

  override def tests = TestSuite {

    'SerializeDeserializeMessage {

      val message1 = Message("Java script Message")

      val s = message1.serialize

      val message2 = s.deserialize

      assert(message1 == message2)

    }

  }

}
