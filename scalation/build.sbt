name := "scalation"

version := "1.0"

scalaVersion := "2.9.3-RC2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies <+= scalaVersion { "org.scala-lang" % "scala-swing" % _ }

libraryDependencies <+= scalaVersion { "org.scala-lang" % "scala-compiler" % _ }

scalacOptions ++= Seq("-unchecked", "-deprecation")

unmanagedSourceDirectories in Compile := Seq(file("src"))

sourceDirectories in Compile := Seq(file("src"))

