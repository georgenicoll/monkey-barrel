package org.monkeynuthead.monkeybarrel.core

import scala.language.higherKinds

import Types._

/**
 * Represents a row in an aggregatable table, this equates to a number of attribute co-ordinates pointing to a set
 * of measure values.
 */
case class Row(attributes: Map[Attribute,AttributeValue], measures: Map[String,Double]) {

}
