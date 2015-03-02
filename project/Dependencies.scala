import sbt._

object Dependencies {
  //Versions
  val _AkkaVersion_ = "2.3.9"
  val _JUnitVersion_ = "4.12"
  val _ScalaJsDomVersion_ = "0.8.0"
  val _ScalaJsJQueryVersion_ = "0.8.0"
  val _ScalaTagsVersion_ = "0.4.5"
  val _ScalaTestVersion_ = "1.3"
  val _ScalazVersion_ = "7.1.1"
  val _Slf4jVersion_ = "1.7.10"
  val _UTestVersion_ = "0.3.0"

  val _Test_ = "test"

  //Libraries
  private[this] val _ComTypesafeAkka_ = "com.typesafe.akka"
  val _AkkaActor_ = _ComTypesafeAkka_ %% "akka-actor" % _AkkaVersion_

  private[this] val _OrgScalaz_ = "org.scalaz"
  val _ScalazDeps_ = Seq(
    _OrgScalaz_ %% "scalaz-core" % _ScalazVersion_,
    _OrgScalaz_ %% "scalaz-effect" % _ScalazVersion_,
    _OrgScalaz_ %% "scalaz-typelevel" % _ScalazVersion_,
    _OrgScalaz_ %% "scalaz-scalacheck-binding" % _ScalazVersion_ % _Test_
  )
  
  val _Slf4japi_ = "org.slf4j" % "slf4j-api" % _Slf4jVersion_
  val _Slf4jsimple_ = "org.slf4j" % "slf4j-simple" % _Slf4jVersion_
  
  val _JUnit_ = "junit" % "junit" % _JUnitVersion_
  
  val _Scalatest_ = "org.scalatest" %% "scalatest" % _ScalaTestVersion_ % _Test_

  val _ComLihaoyi_ = "com.lihaoyi"
  
}