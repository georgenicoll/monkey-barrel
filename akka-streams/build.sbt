lazy val junit = "junit" % "junit" % "4.12" % "test"
lazy val scalatest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"

val com_typesafe_akka = "com.typesafe.akka"
val akka_version = "2.3.10"
lazy val akka_actor = com_typesafe_akka %% "akka-actor" % akka_version
val akka_stream_version = "1.0-RC2"
lazy val akka_stream = com_typesafe_akka %% "akka-stream-experimental" % akka_stream_version


lazy val commonSettings = Seq(
    organization := "org.monkeynuthead.akka_streams",
    scalaVersion := "2.11.6"
)

lazy val root = (project in file(".")).
    settings(commonSettings: _*).
    settings(
        name := "akka-streams-cookbook",
        libraryDependencies ++= Seq(
            akka_actor, akka_stream,
            junit, scalatest
        )
    )

