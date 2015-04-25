lazy val Test = "test"

lazy val commonSettings = Seq(
  organization := "org.monkeynuthead.riak",
  version := "0.1.0",
  scalaVersion := "2.11.6",
  libraryDependencies ++= Seq(
    "junit" % "junit" % "4.12" % Test,
    "org.scalatest" %% "scalatest" % "2.2.4" % Test
  )
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "riak-scala-client-example",
    libraryDependencies ++= Seq(
      "com.scalapenos" %% "riak-scala-client" % "0.9.5"
    )
  )