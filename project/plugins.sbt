// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects 
addSbtPlugin("play" % "sbt-plugin" % "2.1.1")

// Use JaCoCo plugin
addSbtPlugin("de.johoop" % "jacoco4sbt" % "1.2.4")

addSbtPlugin("com.github.scct" % "sbt-scct" % "0.2")

libraryDependencies ++= Seq(
  "org.jacoco" % "org.jacoco.core" % "0.5.9.201207300726" artifacts(Artifact("org.jacoco.core", "jar", "jar")),
  "org.jacoco" % "org.jacoco.report" % "0.5.9.201207300726" artifacts(Artifact("org.jacoco.report", "jar", "jar")))
