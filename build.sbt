name := "Parsomatic"

organization in ThisBuild := "org.broadinstitute"

version := "1.0"

scalaVersion in ThisBuild := "2.11.8"

libraryDependencies in ThisBuild ++= Seq(
	"org.broadinstitute" %% "mdtypes" % "1.0",
	"org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

// Needed to get play iteratees and others
resolvers in ThisBuild += "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/"
scalacOptions in ThisBuild ++= Seq("-feature")

