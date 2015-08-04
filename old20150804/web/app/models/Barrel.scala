package models

import org.monkeynuthead.monkeybarrel.core.{Aggregator, Types, Row, Table}

object Barrel {

  private[this] val Country = "Country"
  private[this] val City = "City"
  private[this] val Size = "Size"
  private[this] val Population = "Pop"

  private[this] val cities = Table(List(Country, City, Size), List(
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

  private[this] val Section = "Section"
  private[this] val Item = "Item"
  private[this] val Price = "Price"

  private[this] val groceries = Table(List(Section, Item), List(
    Row(Map(Section -> "Dairy", Item -> "Milk"), Map(Price -> 1.19)),
    Row(Map(Section -> "Dairy", Item -> "Butter"), Map(Price -> 2.49)),
    Row(Map(Section -> "Dairy", Item -> "Yoghurt"), Map(Price -> 55)),
    Row(Map(Section -> "Meat", Item -> "Beef"), Map(Price -> 11.28)),
    Row(Map(Section -> "Meat", Item -> "Pork"), Map(Price -> 7.22)),
    Row(Map(Section -> "Meat", Item -> "Rat"), Map(Price -> 4.22)),
    Row(Map(Section -> "Vegetables", Item -> "Turnip"), Map(Price -> 0.90)),
    Row(Map(Section -> "Vegetables", Item -> "Swede"), Map(Price -> 0.79))
  ))

  private[this] val reports: Map[String,(Table,Map[String,(Double,Double) => Double])] = Map(
    "Cities" -> (cities, Map(Population -> (_ + _))),
    "Groceries" -> (groceries, Map(Price -> (_ + _)))
  )

  def reportNames: Set[String] = reports.keySet

  def meta(report: String): Option[Seq[Types.Attribute]] =
    reports.get(report).map { case (table, _) => table.attributes }

  def aggregate(report: String, attributes: Seq[Types.Attribute]): Option[Table] =
    reports.get(report).map { case (table, functions) => Aggregator(table).aggregate(attributes, functions) }

}
