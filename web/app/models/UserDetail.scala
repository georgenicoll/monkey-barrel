package models

import securesocial.core.BasicProfile

import scala.concurrent.{ExecutionContext, Future}

case class UserDetail(main: BasicProfile, identities: List[BasicProfile])
