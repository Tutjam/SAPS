name := "SAPS"

version := "1.0"

lazy val `saps` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(jdbc, ehcache, ws, specs2 % Test, guice,
  // URL DETECTOR  //
  "org.nibor.autolink" % "autolink" % "0.11.0",
  // Tests
  "org.scalactic" %% "scalactic" % "3.2.15",
  "org.scalatest" %% "scalatest" % "3.2.15" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test",
  "org.scalatestplus" %% "mockito-1-10" % "3.1.0.0" % Test)
