package com.monkeynuthead.barrel.web

import org.scalatra.ScalatraBase
import org.scalatra.auth.{ScentryConfig, ScentrySupport}
import org.scalatra.auth.strategy.BasicAuthSupport

trait AuthenticationSupport extends ScentrySupport[User] with BasicAuthSupport[User] {

  self: ScalatraBase =>

  val realm = "Monkey Barrel Scalatra Example"

  protected def fromSession = { case id: String => User(id) }
  protected def toSession = { case User(id) => id }

  protected val scentryConfig = (new ScentryConfig {}).asInstanceOf[ScentryConfiguration]

  override protected def configureScentry(): Unit = {
    scentry.unauthenticated {
      scentry.strategies("Basic").unauthenticated()
    }
  }

  override protected def registerAuthStrategies(): Unit = {
    scentry.register("Basic", app => new MyBasicAuthStrategy(app, realm))
  }

}
