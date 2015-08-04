lazy val ComTypeSafeAkka = "com.typesafe.akka"
lazy val AkkaVersion = "2.4-SNAPSHOT"

lazy val root = (project in file(".")).
  settings(
    organization := "org.monkeynuthead",
    name := "akka-persistence-playground",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.11.6",
    resolvers ++= Seq(
      "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/",
      "Typesafe Snapshot" at "https://repo.typesafe.com/typesafe/snapshots/"
    ),
    libraryDependencies ++= Seq(
      ComTypeSafeAkka %% "akka-actor" % AkkaVersion,
      ComTypeSafeAkka %% "akka-remote" % AkkaVersion,
      ComTypeSafeAkka %% "akka-persistence-experimental" % AkkaVersion,
      "org.iq80.leveldb" % "leveldb" % "0.7",
      "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.7"
    )
  )