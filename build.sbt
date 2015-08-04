val junit = "junit" % "junit" % "4.12" % "test"
val scalatest = "org.scalatest" %% "scalatest" % "2.2.5" % "test"

lazy val commonSettings = Seq(
  organization := "org.monkeynuthead",
  version := "0.1.0",
  scalaVersion := "2.11.7",
  sbtVersion := "0.13.8",
  scalacOptions ++= Seq("-feature","-unchecked","-deprecation")
)

lazy val monkeybarrel = (project in file(".")).
  aggregate(
    monkeybarrel_script,
    monkeybarrel_web
  )
  
lazy val monkeybarrel_script = (project in file("script")).
  settings(commonSettings: _*).
  settings(
    name := "monkeybarrel-script"
  )
  
lazy val monkeybarrel_web = (project in file("web")).
  settings(commonSettings: _*).
  settings(
    name := "monkeybarrel-web"
  )
  