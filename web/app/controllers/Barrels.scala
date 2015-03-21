package controllers

import models.Barrel
import org.monkeynuthead.monkeybarrel.core.Types.Attribute
import org.monkeynuthead.monkeybarrel.core.{Row, Table, Types}
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.language.postfixOps

object Barrels extends Controller {

  import org.monkeynuthead.monkeybarrel.CoreFormatters._

  def reports = Action {
    Ok(Json.toJson(Barrel.reportNames))
  }

  def meta(report: String) = Action {
    Barrel.meta(report).map { attributes =>
      Ok(Json.toJson(attributes))
    }.getOrElse(NotFound(s"No Report Found with Name '${report}'"))
  }

  def aggregate(report: String, attributes: String = "") = Action {
    Barrel.aggregate(report, attributes.split("/")).map { aggregated =>
      val json = Json.toJson(aggregated)
      Ok(json)
    }.getOrElse(NotFound(s"No Report Found with Name '${report}'"))
  }

}
