package controllers

import play.api.libs.concurrent.Promise
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

object Barrels extends Controller {

  def barrel = Action { implicit request =>
    Ok(views.html.barrels.barrel("Hello from the here and now!"))
  }

  def asyncBarrel = Action.async { implicit request =>
    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    val message = Future {
      //Simulated to take some time
      Thread.sleep(1000)
      "Hi There From the Future!"
    }
    val timeout = Promise.timeout("Ooopps: Too Slow", 990 milliseconds)
    val firstResult = Future.firstCompletedOf(Seq(message, timeout) )
    firstResult.map(value => Ok(views.html.barrels.barrel(value)))
  }

}
