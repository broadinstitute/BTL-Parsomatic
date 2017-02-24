name := "Parsomatic"

version := "2017.3.0"
scalaVersion := "2.11.8"

organization := "org.broadinstitute"

libraryDependencies ++= Seq(
  "org.broadinstitute" %% "mdtypes" % "1.21",
  "org.broadinstitute" %% "mdreport" % "2017.2.3",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "ch.qos.logback" %  "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "org.json4s" %% "json4s-jackson" % "3.4.2",
  "com.lambdaworks" %% "jacks" % "2.3.3",
  "com.github.scopt" %% "scopt" % "3.5.0"
)

// Needed to get play iteratees and others
resolvers ++= Seq(
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.sonatypeRepo("public")
)

