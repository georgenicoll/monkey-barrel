//Unit Testing
val JUnit = "junit" % "junit" % "4.12" % "test"
val ScalaTest = "org.scalatest" %% "scalatest" % "2.2.5" % "test"

//Scala JS Versions
val ScalaJSDomVersion = "0.8.0"
val ScalaJSJQueryVersion = "0.8.0"
val MicroPickleVersion = "0.3.4"
val UTestVersion = "0.3.0"

lazy val commonSettings = Seq(
  organization := "org.monkeynuthead",
  version := "0.1.0",
  scalaVersion := "2.11.7",
  sbtVersion := "0.13.8",
  scalacOptions ++= Seq("-feature","-unchecked","-deprecation")
)

lazy val monkeybarrel = (project in file(".")).
  aggregate(
    monkeybarrel_glue_js,
    monkeybarrel_glue_jvm,
    monkeybarrel_script,
    monkeybarrel_web
  )
  
lazy val monkeybarrel_glue = (crossProject in file("glue")).
  settings(commonSettings: _*).
  settings(
    name := "monkeybarrel-glue",
    libraryDependencies ++= Seq(
    )
  ).
  jvmSettings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % MicroPickleVersion,
      JUnit,
      ScalaTest
    )
  ).
  jsSettings(
    testFrameworks += new TestFramework("utest.runner.Framework"),
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % MicroPickleVersion,
      "com.lihaoyi" %%% "utest" % UTestVersion % "test"
    )
  )
lazy val monkeybarrel_glue_jvm = monkeybarrel_glue.jvm   
lazy val monkeybarrel_glue_js = monkeybarrel_glue.js
  
lazy val monkeybarrel_script = (project in file("script")).
  enablePlugins(ScalaJSPlugin).
  settings(commonSettings: _*).
  settings(
    name := "monkeybarrel-script",
    scalaJSStage in Global := FastOptStage, //Needs node
    skip in packageJSDependencies := false,
    jsDependencies += RuntimeDOM, //Need PhantomJS
    testFrameworks += new TestFramework("utest.runner.Framework"),
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    libraryDependencies ++= Seq(
      //"org.scala-js" %%% "scalajs-dom" % ScalaJSDomVersion
      "be.doeraene" %%% "scalajs-jquery" % ScalaJSJQueryVersion,
      "com.lihaoyi" %%% "utest" % UTestVersion % "test"
    )
  )
  
lazy val monkeybarrel_web = (project in file("web")).
  settings(commonSettings: _*).
  settings(
    name := "monkeybarrel-web"
  )
  