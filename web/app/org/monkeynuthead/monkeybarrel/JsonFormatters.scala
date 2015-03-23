package org.monkeynuthead.monkeybarrel

import org.monkeynuthead.monkeybarrel.core.{Row, Types, Table}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object JsonFormatters {

  implicit object RowFormat extends Format[Row] {

    private[this] val Attributes = "attributes"
    private[this] val Measures = "measures"

    override def reads(json: JsValue): JsResult[Row] = JsSuccess(Row(
      (json \ Attributes).as[Map[Types.Attribute,Types.AttributeValue]],
      (json \ Measures).as[Map[Types.Measure,Double]]
    ))

    override def writes(row: Row): JsValue = (JsObject(Seq(
      Attributes -> Json.toJson(row.attributes),
      Measures -> Json.toJson(row.measures)
    )))

  }

  implicit object TableFormat extends Format[Table] {

    private[this] val Attributes = "attributes"
    private[this] val Rows = "rows"

    override def reads(json: JsValue): JsResult[Table] = JsSuccess(Table(
      (json \ Attributes).as[Seq[Types.Attribute]],
      (json \ Rows).as[Seq[Row]]
    ))

    override def writes(table: Table): JsValue = JsObject(Seq(
      Attributes -> Json.toJson(table.attributes),
      Rows -> Json.toJson(table.rows)
    ))

  }


}
