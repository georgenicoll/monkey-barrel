package org.monkeynuthead.monkeybarrel.core

import scala.language.higherKinds

import Types._

/**
 * Takes a table and allows aggregations to be carried out upon it.
 */
class Aggregator(private[this] val table: Table) {

  def aggregate(groupBy: Seq[Attribute], aggFuncs: Map[Measure, AggFunc]): Table = {

    val grouped: Map[Seq[(Attribute, Option[AttributeValue])],Seq[Row]] = table.rows.groupBy { row =>
      groupBy.map(attr => (attr, row.attributes.get(attr)))
    }

    val rows = grouped.foldLeft(table.rows.genericBuilder[Row]) {
      case (builder, (values, rows)) =>
        //Calculate the new attributes
        val attributes = values.foldLeft(Map.newBuilder[Attribute, AttributeValue]) {
          case (builder, (attr, Some(value))) => builder += (attr -> value)
          case (builder, _) => builder
        }.result()
        //And the new measures
        val measureVals = scala.collection.mutable.Map.empty[Measure, Double]
        for (
          row <- rows;
          (measure, func) <- aggFuncs;
          rowMeasureVal <- row.measures.get(measure)
        ) {
          measureVals(measure) = measureVals.get(measure) match {
            case Some(currentVal) => func.apply(currentVal, rowMeasureVal)
            case None => rowMeasureVal
          }
        }
        builder += Row(attributes, measureVals.toMap)
    }.result()
    new Table(groupBy, rows)
  }

}

object Aggregator {

  def apply(table: Table): Aggregator = new Aggregator(table)

}
