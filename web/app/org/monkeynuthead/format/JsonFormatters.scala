package org.monkeynuthead.format

import org.monkeynuthead.monkeybarrel.core.{Row, Table}
import play.api.libs.json._

object JsonFormatters {

  implicit val rowFormat = Json.format[Row]

  implicit val tableFormat = Json.format[Table]

}
