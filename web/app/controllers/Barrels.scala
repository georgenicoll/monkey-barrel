package controllers

import models.Barrel
import org.monkeynuthead.format.JsonFormatters._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.language.postfixOps

object Barrels extends Controller {

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
      Ok(Json.toJson(aggregated))
    }.getOrElse(NotFound(s"No Report Found with Name '${report}'"))
  }

}
