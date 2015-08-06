package com.monkeynuthead.monkeybarrel.script

import utest._
import org.scalajs.jquery.jQuery

object TutorialTest extends TestSuite {

  //Initialise App
  TutorialApp.setupUI()

  override def tests = TestSuite {

    'HelloWorld {
      assert(jQuery("p:contains('Hello World')").length == 1)
    }

    'ButtonClick {
      def messageCount = jQuery(s"p:contains('${TutorialApp.ClickedFrom}')").length

      val button = jQuery(s"button:contains('${TutorialApp.ClickMe}')")
      assert(button.length == 1)
      assert(messageCount == 0)

      for(c <- 1 to 5) {
        button.click()
        assert(messageCount == c)
      }

    }
  }

}
