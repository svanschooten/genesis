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
    // Add your project dependencies here,
    jdbc,
    anorm
  )

  val main = play.Project(appName, appVersion, appDependencies, settings = jacoco_settings).settings(
    // Add your own project settings here
    parallelExecution     in jacoco.Config := false,
    jacoco.reportFormats  in jacoco.Config := Seq(XMLReport("utf-8"), HTMLReport("utf-8")),
    jacoco.excludes       in jacoco.Config := Seq("views.*", "controllers.Reverse*", "controllers.javascript.*", "controllers.ref.*", "Routes*")
  )

}
