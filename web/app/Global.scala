import java.lang.reflect.Constructor
import java.text.SimpleDateFormat
import java.util.Date

import models.UserDetail
import play.api.mvc.{Session, RequestHeader}
import play.api.{Application, GlobalSettings, Logger}
import securesocial.core.{LoginEvent, Event, EventListener, RuntimeEnvironment}
import securesocial.core.providers.UsernamePasswordProvider
import services.InMemoryUserService

import scala.collection.immutable.ListMap

object Global extends GlobalSettings {

  private val log = Logger("global")

  override def onStart(app: Application): Unit = {
    log.info(s"Starting at ${new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())}")
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
