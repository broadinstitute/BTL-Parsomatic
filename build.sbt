name := "Parsomatic"

version := "1.0"

scalaVersion := "2.11.8"


version := "1.0"

organization := "org.broadinstitute"

libraryDependencies ++= Seq(
  "org.broadinstitute" %% "mdtypes" % "1.2",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "ch.qos.logback" %  "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.github.scopt" %% "scopt" % "3.5.0"
)

// Needed to get play iteratees and others
resolvers ++= Seq(
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.sonatypeRepo("public")
)

