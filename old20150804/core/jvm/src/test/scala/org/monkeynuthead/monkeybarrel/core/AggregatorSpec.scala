package org.monkeynuthead.monkeybarrel.core

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{Matchers, MustMatchers, WordSpec}
import shapeless.HMap

/**
 * Unit Tests for the {@link Aggregator}.
 */
@RunWith(classOf[JUnitRunner])
class AggregatorSpec extends WordSpec with MustMatchers {

  import Types._

  class AttributeMap[Attribute,V]
  implicit val stringToString = new AttributeMap[Attribute,String]

  val Country = "Country"
  val City = "City"
  val Currency = "Currency"
  val Attributes = List[Attribute](Country, City, Currency)

  val Population = "Population"
  val Area = "Area"
  val Score = "Score"

  val cities = Table(Attributes, Vector(
    Row(Map(Country -> "UK", City -> "London", Currency -> "GBP"),
      Map(Population -> 8, Area -> 14, Score -> 10)),
    Row(Map(Country -> "FR", City -> "Paris", Currency -> "EUR"),
      Map(Population -> 4, Area -> 12, Score -> 2)),
    Row(Map(Country -> "DR", City -> "Berlin", Currency -> "EUR"),
      Map(Population -> 6, Area -> 8, Score -> 5)),
    Row(Map(Country -> "EI", City -> "Dublin", Currency -> "EUR"),
      Map(Population -> 2, Area -> 4, Score -> 4))
  ))

  def rowSortFunc(attributes: Seq[Attribute])(row1: Row, row2: Row): Boolean = {
    var i = 0
    var diff = 0
    while(diff == 0 && i < attributes.length) {
      val attr = attributes(i)
      val att1 = row1.attributes.get(attr)
      val att2 = row2.attributes.get(attr)
      diff = (att1, att2) match {
        case (Some(val1), Some(val2)) => val1.compareTo(val2)
        case (None, _) => -1
        case (_, None) => 1
      }
      i = i + 1
    }
    diff < 0
  }

  "The Aggregator" must {

    "when aggregating by all attributes, just restrict the measures" in {

      val aggMeasures = Map[Measure, AggFunc](Population -> (_ + _), Area -> (_ + _))

      val aggregator = Aggregator(cities)

      val aggregated = aggregator.aggregate(Attributes, aggMeasures)

      aggregated.length must equal(cities.length)
      aggregated.attributes must equal(Attributes)
      aggregated.rows.sortWith(rowSortFunc(Attributes)).zip(cities.rows.sortWith(rowSortFunc(Attributes))).foreach {
        case (aggRow, originalRow) =>
          aggRow.attributes must equal(originalRow.attributes)
          aggRow.measures must equal(originalRow.measures.filterKeys(aggMeasures.contains(_)))
      }
    }

    "when aggregating with a single attribute do what is expected" in {

      val aggAttributes = List("Currency")
      val aggMeasures = Map[Measure, AggFunc](Score -> (_ * _))

      val aggregator = Aggregator(cities)

      val aggregated = aggregator.aggregate(aggAttributes, aggMeasures)

      aggregated.attributes must equal(aggAttributes)
      aggregated.length must equal(2)
      val gbps = aggregated.rows.filter(_.attributes.get(Currency) == Some("GBP"))
      gbps.size must equal(1)
      gbps.head.attributes must equal(Map(Currency -> "GBP"))
      gbps.head.measures must equal(Map(Score -> 10))
      val eurs = aggregated.rows.filter(_.attributes.get(Currency) == Some("EUR"))
      eurs.size must equal(1)
      eurs.head.attributes must equal(Map(Currency -> "EUR"))
      eurs.head.measures must equal(Map(Score -> 40))
    }

    "when aggregating with no attributes do what is expected" in {

      val aggAttributes = List()
      val aggMeasures = Map[Measure, AggFunc](Population -> (_ + _))

      val aggregator = Aggregator(cities)

      val aggregated = aggregator.aggregate(aggAttributes, aggMeasures)

      aggregated.attributes must equal(aggAttributes)
      aggregated.length must equal(1)
      aggregated.rows.head.attributes must equal(Map())
      aggregated.rows.head.measures must equal(Map(Population -> 20))

    }

  }

}
