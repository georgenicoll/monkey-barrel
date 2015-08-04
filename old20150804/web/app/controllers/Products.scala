package controllers

import models.Product
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Lang, Messages}
import play.api.mvc._

object Products extends Controller {

  private def productForm(implicit lang: Lang): Form[Product] = Form(
    mapping(
      "name" -> nonEmptyText.verifying(
        Messages("products.new.namealreadyexists"), ((name) => Product.findByName(name).isEmpty )),
      "description" -> nonEmptyText
    )(Product.apply)(Product.unapply)
  )

  def list = Action { implicit request =>
    val products = Product.findAll().sortBy(_.name)
    Ok(views.html.products.list(products))
  }

  def view(name: String) = Action { implicit request =>
    Product.findByName(name).map { product =>
      Ok(views.html.products.details(product))
    }.getOrElse(NotFound)
  }

  def create = Action { implicit request =>
    val newProductForm = productForm.bindFromRequest()

    newProductForm.fold(
      hasErrors = { form =>
        Redirect(routes.Products.newProduct())
          .flashing(Flash(form.data) + ("error" -> Messages("validation.errors")))
      },
      success = { newProduct =>
        Product.insert(newProduct)
        Redirect(routes.Products.list())
          .flashing("success" -> Messages("products.new.added", newProduct.name))
      }
    )
  }

  def newProduct = Action { implicit request =>
    val form = if (request.flash.get("error").isDefined)
      productForm.bind(request.flash.data)
    else
      productForm
    Ok(views.html.products.newproduct(form))
  }

  def delete(name: String) = Action { implicit request =>
    Product.delete(name)
    Redirect(routes.Products.list())
      .flashing("success" -> Messages("products.delete.deleted", name))
  }

}
