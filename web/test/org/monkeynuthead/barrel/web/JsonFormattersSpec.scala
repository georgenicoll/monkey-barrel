package org.monkeynuthead.barrel.web

import org.junit.runner.RunWith
import org.monkeynuthead.monkeybarrel.core.{Row, Table}
import org.scalatest.{Inside, MustMatchers, WordSpec}
import org.scalatest.junit.JUnitRunner
import play.api.libs.json.{JsSuccess, Json}

@RunWith(classOf[JUnitRunner])
class JsonFormattersSpec extends WordSpec with MustMatchers {

 "json formatters" must {

   import JsonFormatters._

   "write and read a row to/from json<->string" in {
     val row = Row(Map("Species" -> "Monkey", "Name" -> "Robert"), Map("Price" -> 34.2, "Amount" -> 1.0))
     val jsonValue = Json.toJson(row)
     val jsonString = Json.stringify(jsonValue)
     val parsed = Json.parse(jsonString)
     val result = parsed.validate[Row]
     result must equal(JsSuccess(row))
   }

   "write and read a table to/from json<->string" in {
     val attributes = Seq("Something", "Or", "Other")
     val rows = Seq(
       Row(Map("Something" -> "Yep", "Or" -> "No", "Other" -> "What?"), Map("Satisfaction" -> 0.02)),
       Row(Map("Something" -> "Nah", "Or" -> "OK", "Other" -> "So?"), Map("Satisfaction" -> 0.04)),
       Row(Map("Something" -> "Mebbeh", "Or" -> "Not", "Other" -> "Jeez"), Map("Satisfaction" -> 0.10))
     )

     val table = Table(attributes, rows)
     val jsonValue = Json.toJson(table)
     val jsonString = Json.stringify(jsonValue)
     val parsed = Json.parse(jsonString)
     val result = parsed.validate[Table]
     result must matchPattern {
       case JsSuccess(table, _) => ()
     }
     result.get.attributes must equal(attributes)
     result.get.rows must equal(rows)
   }

 }

}
