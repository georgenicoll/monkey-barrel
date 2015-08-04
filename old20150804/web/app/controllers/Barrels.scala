package controllers

import models.Barrel
import org.monkeynuthead.barrel.web.JsonFormatters._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import scala.language.postfixOps

object Barrels extends Controller {
  //class Barrels(override implicit val env: RuntimeEnvironment[UserDetail])
  //  extends SecureSocial[UserDetail] {

  def reports = /*Secured*/ Action {
    Ok(Json.toJson(Barrel.reportNames))
  }

  def meta(report: String) = /*Secured*/ Action {
    Barrel.meta(report).map { attributes =>
      Ok(Json.toJson(attributes))
    }.getOrElse(NotFound(s"No Report Found with Name '${report}'"))
  }

  def aggregate(report: String, attributes: String = "") = /*Secured*/ Action {
    Barrel.aggregate(report, attributes.split("/")).map { aggregated =>
      Ok(Json.toJson(aggregated))
    }.getOrElse(NotFound(s"No Report Found with Name '${report}'"))
  }

}
