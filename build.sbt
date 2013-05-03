name := "scalation"

version := "0.9"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq("org.scala-lang" % "scala-swing" % "2.10.0", "org.scala-lang" % "scala-actors" % "2.10.0")

unmanagedSourceDirectories in Compile <++= baseDirectory { base =>
  Seq(
    base / "src/examples/scala"
  )
}