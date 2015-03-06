package org.monkeynuthead.monkeybarrel.core

import scala.language.higherKinds

import Types._

/**
 * A table containing a number of rows which (optionally) contain values for the attributes.
 */
class Table(val attributes: Seq[Attribute], private[core] val rows: Seq[Row]) {

  /**
   * Add a row to the table returning another table.
   */
  def addRow(row: Row) = new Table(attributes, rows :+ row)

  /**
   * Add multiple rows to the table retuning another table.
   */
  def addRows(newRows: Seq[Row]) = new Table(attributes, rows ++ newRows)

  /**
   * Returns the length of the table, depending on the implementation of Seq may take linear time.
   */
  def length = rows.length

}

object Table {

  def apply(attributes: Seq[Attribute], rows: Seq[Row]): Table = new Table(attributes, rows)

  def apply(attributes: Seq[Attribute]): Table = apply(attributes, Seq.empty)

}
