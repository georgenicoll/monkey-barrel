import java.text.SimpleDateFormat
import java.util.Date

import play.api.{Application, GlobalSettings, Logger}

object Global extends GlobalSettings {

  private val log = Logger("global")

  override def onStart(app: Application): Unit = {
    log.info(s"Starting at ${new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())}")
  }

}
