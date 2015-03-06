import sbt._

object Dependencies {
  val _ScalaVersion_ = "2.11.6"
  val _SbtVersion_ = "0.13.7"

  //Versions
  val _AkkaVersion_ = "2.3.9"
  val _JUnitVersion_ = "4.12"
  val _ScalaJsDomVersion_ = "0.8.0"
  val _ScalaJsJQueryVersion_ = "0.8.0"
  val _ScalaTagsVersion_ = "0.4.5"
  val _ScalaTestVersion_ = "2.2.4"
  val _ScalazVersion_ = "7.1.1"
  val _ShapelessVersion_ = "2.1.0"
  val _Slf4jVersion_ = "1.7.10"
  val _UTestVersion_ = "0.3.0"

  val _Test_ = "test"

  //Libraries
  private[this] val _ComTypesafeAkka_ = "com.typesafe.akka"
  val _AkkaActor_ = _ComTypesafeAkka_ %% "akka-actor" % _AkkaVersion_

  val _ComLihaoyi_ = "com.lihaoyi"

  val _JUnit_ = "junit" % "junit" % _JUnitVersion_

  val _Scalatest_ = "org.scalatest" %% "scalatest" % _ScalaTestVersion_ % _Test_

  private[this] val _OrgScalaz_ = "org.scalaz"
  val _ScalazDeps_ = Seq(
    _OrgScalaz_ %% "scalaz-core" % _ScalazVersion_,
    _OrgScalaz_ %% "scalaz-effect" % _ScalazVersion_,
    _OrgScalaz_ %% "scalaz-typelevel" % _ScalazVersion_,
    _OrgScalaz_ %% "scalaz-scalacheck-binding" % _ScalazVersion_ % _Test_
  )

  val _Shapeless_ = "com.chuusai" %% "shapeless" % _ShapelessVersion_

  val _Slf4japi_ = "org.slf4j" % "slf4j-api" % _Slf4jVersion_
  val _Slf4jsimple_ = "org.slf4j" % "slf4j-simple" % _Slf4jVersion_

}