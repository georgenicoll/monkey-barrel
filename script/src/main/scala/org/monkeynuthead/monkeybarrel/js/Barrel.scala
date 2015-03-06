package org.monkeynuthead.monkeybarrel.js

import org.monkeynuthead.monkeybarrel.core.{Aggregator, Row, Table}

import scala.scalajs.js.JSApp
import scalatags.Text.all._
import org.scalajs.jquery._

/**
 * Basic first attempt to access the aggregation code from the javascript.
 */
object Barrel extends JSApp {

  private[this] val Country = "Country"
  private[this] val City = "City"
  private[this] val Size = "Size"

  private[this] val Population = "Pop"

  private[this] val Attributes = List(Country, City)

  private[this] val table = Table(Attributes, List(
    Row(Map(Country -> "UK", City -> "London", Size -> "Big"), Map(Population -> 14100000)),
    Row(Map(Country -> "UK", City -> "Manchester", Size -> "Medium"), Map(Population -> 2975000)),
    Row(Map(Country -> "UK", City -> "Leeds", Size -> "Medium"), Map(Population -> 2125000)),
    Row(Map(Country -> "Germany", City -> "Berlin", Size -> "Big"), Map(Population -> 4375000)),
    Row(Map(Country -> "Germany", City -> "Munich", Size -> "Medium"), Map(Population -> 2150000)),
    Row(Map(Country -> "Germany", City -> "Frankfurt", Size -> "Medium"), Map(Population -> 3175000)),
    Row(Map(Country -> "France", City -> "Paris", Size -> "Big"), Map(Population -> 11200000)),
    Row(Map(Country -> "France", City -> "Lyon", Size -> "Small"), Map(Population -> 1910000)),
    Row(Map(Country -> "France", City -> "Marseille", Size -> "Small"), Map(Population -> 1630000))
  ))

  private[this] val ControlDivId = "controls"
  private[this] val DataDivId = "data"
  private[this] val AggSelectId = "aggregationAttribute"

  private[this] def aggregate(attribute: String): Unit = {
    jQuery(s"#${DataDivId}").empty()
    val aggregator = Aggregator(table)
    val aggregated = aggregator.aggregate(List(attribute), Map(Population -> (_ + _)))
    val d = div(
      aggregated.rows.map { row =>
        p(label(s"${row.attributes(attribute)} = ${row.measures(Population)}"))
      }
    )
    println(d.render)
    jQuery(d.render).appendTo(s"#${DataDivId}")
  }

  private[this] def selectChanged(event: JQueryEventObject): Unit = {
    val selected = jQuery(s"#${AggSelectId}").find("option:selected")
    println(s"select changed: ${selected.text()}")
    aggregate(selected.text())
  }

  private[this] def setupUI(): Unit = {
    val attributeSelect = select(id := AggSelectId)(
      option(selected := "true")(Country),
      option(City),
      option(Size)
    )
    val para = p(
      p(div(id := ControlDivId)(label("Attribute: "))),
      p(div(
        p(label("Data:")),
        p(
          div(id := DataDivId)
        )
      ))
    )
    jQuery(para.render).appendTo(jQuery("body"))
    jQuery(attributeSelect.render).change(selectChanged _).appendTo(jQuery(s"#${ControlDivId}"))
    aggregate(Country)
  }

  def main(): Unit = {
    jQuery(setupUI())
    println("Started")
  }

}
