package controllers

import models.{Barrel, UserDetail}
import org.monkeynuthead.barrel.web.JsonFormatters._
import play.api.libs.json._
import securesocial.core.{RuntimeEnvironment, SecureSocial}

import scala.language.postfixOps

class Barrels(override implicit val env: RuntimeEnvironment[UserDetail]) extends SecureSocial[UserDetail] {

  def reports = SecuredAction {
    Ok(Json.toJson(Barrel.reportNames))
  }

  def meta(report: String) = SecuredAction {
    Barrel.meta(report).map { attributes =>
      Ok(Json.toJson(attributes))
    }.getOrElse(NotFound(s"No Report Found with Name '${report}'"))
  }

  def aggregate(report: String, attributes: String = "") = SecuredAction {
    Barrel.aggregate(report, attributes.split("/")).map { aggregated =>
      Ok(Json.toJson(aggregated))
    }.getOrElse(NotFound(s"No Report Found with Name '${report}'"))
  }

}
