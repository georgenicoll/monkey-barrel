package org.monkeynuthead.monkeybarrel.js

import org.monkeynuthead.monkeybarrel.core.{Aggregator, Row, Table}
import org.scalajs.jquery._
import rx.core.{Obs, Var}

import scala.scalajs.js
import scala.scalajs.js.{Object, JSApp}
import scalatags.Text.all._

/**
 * Basic first attempt to access the aggregation code from the javascript.
 */
object Barrel extends JSApp {

  private[this] val Country = "Country"
  private[this] val City = "City"
  private[this] val Size = "Size"

  private[this] val Population = "Pop"

  private[this] val Attributes = List(Country, City)

  private[this] val Missing = "<Missing>"

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

  private[this] val aggregated: Var[Option[Table]] = Var(None)

  private[this] val ControlDivId = "controls"
  private[this] val DataDivId = "data"
  private[this] val AggSelectId = "aggregationAttribute"

  private[this] def tableChanged(tableOpt: Option[Table]): Unit = {
    jQuery(s"#$DataDivId").empty()
    val ps = tableOpt.map { tab =>

      // Update the chart
      val attribute = tab.attributes.head
      val objs: Seq[js.Object] = tab.rows.map { row =>
        js.Dynamic.literal(attribute -> js.Any.fromString(row.attributes.getOrElse(attribute, Missing)),
          Population -> js.Any.fromDouble(row.measures.getOrElse(Population, 0d)))
      }
      val data: js.Array[js.Object] = js.Array(objs: _*)
      js.Dynamic.global.createChart(attribute, Population, data)

      // Construct the paragraph for the data
      tab.rows.map { row =>
        p(label(s"${tab.attributes.map(a => (a, row.attributes(a))).mkString(",")} = ${row.measures(Population)}"))
      }
    }.toSeq
    val d = div(ps)
    println(d.render)
    jQuery(d.render).appendTo(s"#$DataDivId")
  }

  private[this] def aggregate(attribute: String): Unit = {
    aggregated() = Some(Aggregator(table).aggregate(List(attribute), Map(Population -> (_ + _))))
  }

  private[this] def selectChanged(event: JQueryEventObject): Unit = {
    val selected = jQuery(s"#$AggSelectId").find("option:selected")
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
    jQuery(attributeSelect.render).change(selectChanged _).appendTo(jQuery(s"#$ControlDivId"))

    Obs(aggregated, skipInitial = true) {
      tableChanged(aggregated())
    }

    aggregate(Country)
  }

  def main(): Unit = {
    jQuery(setupUI())
    println("Started")
  }

}
