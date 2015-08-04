import sbt._
import sbt.Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._

val Organization = "org.monkeynuthead"
val Name = "scalatra-web"
val Version = "0.1.0-SNAPSHOT"
val ScalaVersion = "2.11.6"
val ScalatraVersion = "2.4.0.M3"
val Json4sVersion = "3.2.11"
val JettyVersion = "9.1.5.v20140505" //"9.3.0.M2"
val ServletVersion = "3.1.0"
val LogbackVersion = "1.1.2"
val GrizzledVersion = "1.0.2"

lazy val scalatra_web = (project in file(".")).
  settings(ScalatraPlugin.scalatraWithJRebel: _*).
  settings(scalateSettings: _*).
  settings(
    organization := Organization,
    name := Name,
    version := Version,
    scalaVersion := ScalaVersion,
    resolvers += Classpaths.typesafeReleases,
    //resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
    resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases",
    libraryDependencies ++= Seq(
      "org.json4s" %% "json4s-jackson" % Json4sVersion,
      "org.scalatra" %% "scalatra" % ScalatraVersion,
      "org.scalatra" %% "scalatra-auth" % ScalatraVersion,
      "org.scalatra" %% "scalatra-atmosphere" % ScalatraVersion,
      "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
      "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
      "ch.qos.logback" % "logback-classic" % LogbackVersion % "runtime",
      "org.eclipse.jetty" % "jetty-webapp" % JettyVersion % "container",
      "org.eclipse.jetty" % "jetty-plus" % JettyVersion % "container",
      "org.eclipse.jetty.websocket" % "websocket-server" % JettyVersion % "container",
      "javax.servlet" % "javax.servlet-api" % ServletVersion,
      "org.clapper" %% "grizzled-slf4j" % GrizzledVersion
    ),
    scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
      Seq(
        TemplateConfig(
          base / "webapp" / "WEB-INF" / "templates",
          Seq.empty,  /* default imports should be added here */
          Seq(
            Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
          ),  /* add extra bindings here */
          Some("templates")
        )
      )
    }
  )
