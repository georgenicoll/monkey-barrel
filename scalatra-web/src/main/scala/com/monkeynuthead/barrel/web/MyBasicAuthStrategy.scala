package com.monkeynuthead.barrel.web

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import org.scalatra.ScalatraBase
import org.scalatra.auth.strategy.BasicAuthStrategy

class MyBasicAuthStrategy(protected override val app: ScalatraBase, realm: String)
  extends BasicAuthStrategy[User](app: ScalatraBase, realm: String) {

  override protected def getUserId(user: User)
                                  (implicit request: HttpServletRequest, response: HttpServletResponse): String =
    user.id

  override protected def validate(userName: String, password: String)
                                 (implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] =
    if (userName == "monkey" && password == "barrel")
      Some(User("monkey"))
    else
      None

}
