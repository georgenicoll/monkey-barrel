package tutorial.webapp

import utest._

import org.scalajs.jquery.jQuery

/**
 * Unit Tests for the {@link TutorialApp}
 */
object TutorialAppTest extends TestSuite {

  import TutorialApp._

  // Initialise the App
  setupUI()

  def tests = TestSuite {
    'HelloWorld {
      assert(jQuery(s"p:contains('${HelloWorld}')").length == 1)
    }
    'ButtonClick {
      def messageCount = jQuery(s"p:contains('${YouClickedTheButton}')").length

      val button = jQuery(s"button:contains('${ClickMe}')")

      assert(button.length == 1)
      assert(messageCount == 0)

      for(c <- 1 to 5) {
        button.click()
        assert(messageCount == c)
      }
    }
  }

}
