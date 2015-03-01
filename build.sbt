import Dependencies._

lazy val commonSettings = Seq(
  organization := "org.monkeynuthead",
  version := "0.1.0",
  scalaVersion := "2.11.5",
  sbtVersion := "0.13.7",
  scalacOptions ++= Seq("-feature","-unchecked","-deprecation")
)

//Aggregate Project
lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  aggregate(core, script, web)

//Core classes
lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(
    name := "monkey-barrel-core"
  )
  
//Scala js
lazy val script = (project in file("script")).
  enablePlugins(ScalaJSPlugin).
  settings(commonSettings: _*).
  settings(
    name := "monkey-barrel-script",
    scalaJSStage := FastOptStage, //Requires node.js to be installed
    libraryDependencies ++= Seq(orgScalaJs %%% "scalajs-dom" % scalaJsDomVersion)
  ).
  dependsOn(core)
  
//The web application (play)
lazy val web = (project in file("web")).
  settings(commonSettings: _*).
  settings(
    name := "monkey-barrel-web"
  ).
  dependsOn(core, script)
  