import java.lang.reflect.Constructor
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date

import models.UserDetail
import play.api.libs.iteratee.Iteratee
import play.api.libs.oauth.{OAuthCalculator, RequestToken, ConsumerKey}
import play.api.libs.ws.WS
import play.api.mvc.{Session, RequestHeader}
import play.api.{Application, GlobalSettings, Logger}
import securesocial.core.{LoginEvent, Event, EventListener, RuntimeEnvironment}
import securesocial.core.providers.UsernamePasswordProvider
import services.InMemoryUserService

import scala.collection.immutable.ListMap

object Global extends GlobalSettings {

  implicit val globalExecutionContext = scala.concurrent.ExecutionContext.global

  private val log = Logger("global")

  val loggingIteratee = Iteratee.foreach[Array[Byte]] { chunk =>
    val chunkString = new String(chunk, "UTF-8")
    println(s"Chunk: ${chunkString}")
  }

  private[this] def optionalConfig(path: String)(implicit app: Application): Option[String] = {
    if (app.configuration.underlying.hasPath(path)) {
      Some(app.configuration.underlying.getString(path))
    } else {
      None
    }
  }

  override def onStart(app: Application): Unit = {
    log.info(s"Starting at ${new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())}")

    //Example iteratee usage - only if all keys are specified
    implicit val application = app
    for(
      consKey <- optionalConfig("twitter.consumer.key");
      consSecret <- optionalConfig("twitter.consumer.secret");
      accessKey <- optionalConfig("twitter.access.key");
      accessSecret <- optionalConfig("twitter.access.secret")
    ) {
      val consumerKey = ConsumerKey(consKey, consSecret)
      val accessToken = RequestToken(accessKey, accessSecret)
      WS.url("https://stream.twitter.com/1.1/statuses/sample.json")
        .sign(OAuthCalculator(consumerKey, accessToken))
        .get(_ => loggingIteratee)
    }

  }

  class LoginEventListener extends EventListener[UserDetail] {
    override def onEvent(event: Event[UserDetail], request: RequestHeader, session: Session): Option[Session] =
      event match {
        case LoginEvent(user) =>
          log.info(s"Login for '$user'")
          Some(session)
        case _ => None
      }
  }

  /**
   * The runtime environment
   */
  object SecureSocialRuntimeEnvironment extends RuntimeEnvironment.Default[UserDetail] {
    //override lazy val routes = new CustomRoutesService()
    override lazy val userService: InMemoryUserService = new InMemoryUserService()
    override lazy val eventListeners = List(new LoginEventListener())
    override lazy val providers = ListMap(
      include(new UsernamePasswordProvider[UserDetail](userService, avatarService, viewTemplates, passwordHashers))
      // ... other providers
    )
  }

  /**
   * An implementation that checks if the controller expects a RuntimeEnvironment and
   * passes the instance to it if required.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    val instance = controllerClass.getConstructors.find { c =>
      val params = c.getParameterTypes
      params.length == 1 && params(0) == classOf[RuntimeEnvironment[UserDetail]]
    }.map {
      _.asInstanceOf[Constructor[A]].newInstance(SecureSocialRuntimeEnvironment)
    }
    instance.getOrElse(super.getControllerInstance(controllerClass))
  }

}
