package handson

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom

object HandsOn extends JSApp {

  @JSExport
  override def main(): Unit = {
    dom.alert("It's working")
  }

}
