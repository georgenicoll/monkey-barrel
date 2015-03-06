import Dependencies._

lazy val commonSettings = Seq(
  organization := "org.monkeynuthead",
  version := "0.1.0",
  scalaVersion := _ScalaVersion_,
  sbtVersion := _SbtVersion_,
  scalacOptions ++= Seq("-feature","-unchecked","-deprecation")
)

//Aggregate Project
lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  aggregate(core_jvm, core_js, script, web)

//Core classes (cross compiled for usage on the jvm and from java script)
lazy val core = (crossProject in file("core")).
  settings(commonSettings: _*).
  settings(
    name := "monkey-barrel-core",
    libraryDependencies ++= Seq(
      _Shapeless_,
      _JUnit_, _Scalatest_
    )
  ).
  jvmSettings().
  jsSettings()
lazy val core_jvm = core.jvm
lazy val core_js = core.js
  
//Scala js
lazy val script = (project in file("script")).
  enablePlugins(ScalaJSPlugin).
  settings(commonSettings: _*).
  settings(
    name := "monkey-barrel-script",
    scalaJSStage := FullOptStage, //FastOptStage, //Requires node.js to be installed and on the path
    skip in packageJSDependencies := false,
    jsDependencies += RuntimeDOM, //Requires PhantomJS to be on the path
    testFrameworks += new TestFramework("utest.runner.Framework"),
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    //libraryDependencies ++= Seq("org.scala-js" %%% "scalajs-dom" % scalaJsDomVersion)
    libraryDependencies ++= Seq(
      "be.doeraene" %%% "scalajs-jquery" % _ScalaJsJQueryVersion_,
      _ComLihaoyi_ %%% "scalatags" % _ScalaTagsVersion_,
      _ComLihaoyi_ %%% "utest" % _UTestVersion_ % _Test_
    )
  ).
  dependsOn(core_js)
  
//The web application (play)
lazy val web = (project in file("web")).
  settings(commonSettings: _*).
  settings(
    name := "monkey-barrel-web"
  ).
  dependsOn(core_jvm, script)
  