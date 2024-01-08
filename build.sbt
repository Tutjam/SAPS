name := "SAPS"

version := "1.0"

lazy val `saps` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(jdbc, ehcache, ws, specs2 % Test, guice)
