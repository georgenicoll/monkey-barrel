package services

import models.UserDetail
import play.api.Logger
import securesocial.core.providers.{MailToken, UsernamePasswordProvider}
import securesocial.core.services.{SaveMode, UserService}
import securesocial.core.{BasicProfile, PasswordInfo}

import scala.concurrent.Future

class InMemoryUserService extends UserService[UserDetail] {

  private val logger = Logger("InMemoryUserService")

  var users = Map[(String, String), UserDetail]()
  private var tokens = Map[String, MailToken]()

  def find(providerId: String, userId: String): Future[Option[BasicProfile]] = {
    logger.debug(s"findByUserId $userId, users = $users")

    val result = for (
      user <- users.values;
      basicProfile <- user.identities.find(su => su.providerId == providerId && su.userId == userId)
    ) yield {
        basicProfile
      }
    Future.successful(result.headOption)
  }

  def findByEmailAndProvider(email: String, providerId: String): Future[Option[BasicProfile]] = {
    logger.debug(s"findByEmail $email, users = $users")

    val someEmail = Some(email)
    val result = for (
      user <- users.values;
      basicProfile <- user.identities.find(su => su.providerId == providerId && su.email == someEmail)
    ) yield {
        basicProfile
      }
    Future.successful(result.headOption)
  }

  private def findProfile(p: BasicProfile) = {
    logger.debug(s"findByProfile $p, users = $users")

    users.find {
      case (key, value) if value.identities.exists(su => su.providerId == p.providerId && su.userId == p.userId) => true
      case _ => false
    }
  }

  private def updateProfile(user: BasicProfile, entry: ((String, String), UserDetail)): Future[UserDetail] = {
    logger.debug(s"updateProfile $user, $entry, users = $users")

    val identities = entry._2.identities
    val updatedList = identities.patch(identities.indexWhere(i => i.providerId == user.providerId && i.userId == user.userId), Seq(user), 1)
    val updatedUser = entry._2.copy(identities = updatedList)
    users = users + (entry._1 -> updatedUser)
    Future.successful(updatedUser)
  }

  def save(user: BasicProfile, mode: SaveMode): Future[UserDetail] = {
    logger.debug(s"save $user, users = $users, mode = $mode")

    mode match {
      case SaveMode.SignUp =>
        val newUser = UserDetail(user, List(user))
        users = users + ((user.providerId, user.userId) -> newUser)
        Future.successful(newUser)
      case SaveMode.LoggedIn =>
        // first see if there is a user with this BasicProfile already.
        findProfile(user) match {
          case Some(existingUser) =>
            updateProfile(user, existingUser)

          case None =>
            val newUser = UserDetail(user, List(user))
            users = users + ((user.providerId, user.userId) -> newUser)
            Future.successful(newUser)
        }

      case SaveMode.PasswordChange =>
        findProfile(user).map { entry => updateProfile(user, entry) }.getOrElse(
          // this should not happen as the profile will be there
          throw new Exception("missing profile)")
        )
    }
  }

  def link(current: UserDetail, to: BasicProfile): Future[UserDetail] = {
    logger.debug(s"link $current, to $to, users = $users")

    if (current.identities.exists(i => i.providerId == to.providerId && i.userId == to.userId)) {
      Future.successful(current)
    } else {
      val added = to :: current.identities
      val updatedUser = current.copy(identities = added)
      users = users + ((current.main.providerId, current.main.userId) -> updatedUser)
      Future.successful(updatedUser)
    }
  }

  def saveToken(token: MailToken): Future[MailToken] = {
    logger.debug(s"saveToken $token, users = $users")

    Future.successful {
      tokens += (token.uuid -> token)
      token
    }
  }

  def findToken(token: String): Future[Option[MailToken]] = {
    logger.debug(s"findToken $token, users = $users")

    Future.successful {
      tokens.get(token)
    }
  }

  def deleteToken(uuid: String): Future[Option[MailToken]] = {
    logger.debug(s"deleteToken $uuid, users = $users")

    Future.successful {
      tokens.get(uuid) match {
        case Some(token) =>
          tokens -= uuid
          Some(token)
        case None => None
      }
    }
  }

  def deleteExpiredTokens() {
    logger.debug(s"deleteExpiredTokens")

    tokens = tokens.filter(!_._2.isExpired)
  }

  override def updatePasswordInfo(user: UserDetail, info: PasswordInfo): Future[Option[BasicProfile]] = {
    logger.debug(s"updatePasswordInfo $user, users = $users")

    Future.successful {
      for (
        found <- users.values.find(_ == user);
        identityWithPasswordInfo <- found.identities.find(_.providerId == UsernamePasswordProvider.UsernamePassword)
      ) yield {
        val idx = found.identities.indexOf(identityWithPasswordInfo)
        val updated = identityWithPasswordInfo.copy(passwordInfo = Some(info))
        val updatedIdentities = found.identities.patch(idx, Seq(updated), 1)
        val updatedEntry = found.copy(identities = updatedIdentities)
        users = users + ((updatedEntry.main.providerId, updatedEntry.main.userId) -> updatedEntry)
        updated
      }
    }
  }

  override def passwordInfoFor(user: UserDetail): Future[Option[PasswordInfo]] = {
    logger.debug(s"passwordInfoFor $user, users = $users")

    Future.successful {
      for (
        found <- users.values.find(u => u.main.providerId == user.main.providerId && u.main.userId == user.main.userId);
        identityWithPasswordInfo <- found.identities.find(_.providerId == UsernamePasswordProvider.UsernamePassword)
      ) yield {
        identityWithPasswordInfo.passwordInfo.get
      }
    }
  }
}