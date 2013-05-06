import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "genesis"
  val appVersion      = "1.0-SNAPSHOT"

val appDependencies = Seq(
  jdbc,
  anorm,
  "com.github.nikita-volkov" % "sorm" % "0.3.7",
  "com.h2database" % "h2" % "1.3.168",
  "postgresql" % "postgresql" % "9.1-901.jdbc4"
)

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
  )

}
