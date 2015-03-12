package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Redirect(routes.Application.barrel())
  }

  def barrel = Action { implicit request =>
    Ok(views.html.barrel())
  }

}