package models

case class Product(name: String, description: String)

object Product {

  private var products = Set(
    Product("My First Product", "This is my first product"),
    Product("My Second Product", "This is my second product")
  )

  def findAll(): List[Product] = products.toList

  def findByName(name: String): Option[Product] = products.find(_.name == name)

  def delete(name: String): Unit = products = products.filterNot(_.name == name)

  def insert(product: Product): Unit = products = products + product

}
