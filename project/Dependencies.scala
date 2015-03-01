import sbt._

object Dependencies {
  //Versions
  val akkaVersion = "2.3.9"
  val junitVersion = "4.12"
  val scalaTestVersion = "1.3"
  val scalazVersion = "7.1.1"
  val slf4jVersion = "1.7.10"

  //Libraries
  private[this] val comTypesafeAkka = "com.typesafe.akka"
  val akkaActor = comTypesafeAkka %% "akka-actor" % akkaVersion
  
  private[this] val orgScalaz = "org.scalaz"
  val scalazDeps = Seq(
    orgScalaz %% "scalaz-core" % scalazVersion,
    orgScalaz %% "scalaz-effect" % scalazVersion,
    orgScalaz %% "scalaz-typelevel" % scalazVersion,
    orgScalaz %% "scalaz-scalacheck-binding" % scalazVersion % "test"
  )
  
  val slf4japi = "org.slf4j" % "slf4j-api" % slf4jVersion
  val slf4jsimple = "org.slf4j" % "slf4j-simple" % slf4jVersion
  
  val junit = "junit" % "junit" % junitVersion
  
  val scalatest = "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  
  //Projects
  
}