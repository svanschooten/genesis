import sbt._
import Keys._
import play.Project._

import de.johoop.jacoco4sbt._
import JacocoPlugin._

object ApplicationBuild extends Build {

  val appName         = "genesis"
  val appVersion      = "1.0-SNAPSHOT"

  lazy val jacoco_settings = Defaults.defaultSettings ++ Seq(jacoco.settings: _*)

val appDependencies = Seq(
  jdbc,
  anorm,
  "com.github.nikita-volkov" % "sorm" % "0.3.7",
  "com.h2database" % "h2" % "1.3.168",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "com.github.t3hnar" % "scala-bcrypt_2.10" % "2.1",
  "org.seleniumhq.selenium" % "selenium-java" % "2.32.0"
)

  val main = play.Project(appName, appVersion, appDependencies, settings = jacoco_settings).settings(
    // Add your own project settings here
    parallelExecution     in jacoco.Config := false,
    jacoco.reportFormats  in jacoco.Config := Seq(XMLReport("utf-8"), HTMLReport("utf-8")),
    jacoco.excludes 		  in jacoco.Config := Seq("controllers**","views**","scalation**","Routes*")
    )

}
