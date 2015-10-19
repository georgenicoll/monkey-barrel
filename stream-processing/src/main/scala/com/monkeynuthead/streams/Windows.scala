package com.monkeynuthead.streams

import akka.stream.scaladsl.{Broadcast, Flow, FlowGraph}
import akka.stream.stage.{StatefulStage, SyncDirective, Context, PushPullStage}

object Windows {

  /**
   * The data type we will be processing in the stream
   */
  type DataType = Map[String, Any]

  /**
   * Type representing the builder type for new data
   */
  def newBuilder = Map.newBuilder[String, Any]

  /**
   * A Mapping Function, takes an optional DataType (if one has been seen before) and a new data type
   * to maybe be merged with the one seen before.
   */
  type MappingFunction = (Option[DataType], DataType) => DataType

  import FlowGraph.Implicits._

  private[this] class ByAttributes(private val function: MappingFunction, private val attributes: String*)
      extends PushPullStage[DataType, DataType] {

    private val NullValue = "Null"
    private var seenSoFar = Map.empty[DataType,DataType]

    override def onPush(elem: DataType, ctx: Context[DataType]): SyncDirective = {
      val valuesMap = attributes.foldLeft(newBuilder) { (builder, attr) =>
        elem.get(attr).map(value => builder += (attr -> value)).getOrElse(builder)
      }.result()
      val mapped = function(seenSoFar.get(valuesMap), elem)
      val result = valuesMap ++ mapped
      seenSoFar = seenSoFar + (valuesMap -> result)
      ctx.push(result)
    }

    override def onPull(ctx: Context[DataType]): SyncDirective = {
      ctx.pull()
    }

  }

  private[this] class NoAttributes extends PushPullStage[DataType, DataType] {

    override def onPush(elem: DataType, ctx: Context[DataType]): SyncDirective = ctx.push(elem)

    override def onPull(ctx: Context[DataType]): SyncDirective = ctx.pull()

  }

  def byAttributes[Mat](in: Flow[DataType, DataType, Mat], mappingFunction: Option[MappingFunction], attributes: String*)
      : Flow[DataType, DataType, Mat] = {
    in.transform { () =>
      if (attributes.isEmpty)
        new NoAttributes
      else
        mappingFunction.map(new ByAttributes(_, attributes: _*)).getOrElse(new NoAttributes)
    }
  }

}