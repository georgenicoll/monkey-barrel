package com.monkeynuthead.barrel.web

import com.sun.xml.internal.ws.resources.ServerMessages
import org.scalatra.{UrlGeneratorSupport, Ok, BadRequest, ScalatraServlet}

class Articles extends ScalatraServlet with UrlGeneratorSupport with AuthenticationSupport {

  val getArticle = get("/articles/:id") {
    //<h1>Article {params("id")}</h1>
    basicAuth
    <html>
      <body>
        <h1>OK Article {params("id")}</h1>
        <p>You are authenticated</p>
      </body>
    </html>
  }

  get("/articles2/:id") {
    redirect(url(getArticle, "id" -> (params("id") + "2")))
  }

  post("/articles") {
    //Submit/Create an article
  }

  put("/articles/:id") {
    //Update the article with :id
  }

  delete("/articles/:id") {
    //Delete the article with :id
  }

}
