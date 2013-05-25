import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play-messages"
  val appVersion      = "2.0"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    
    "commons-io" % "commons-io" % "2.4"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    organization := "de.corux",
    
    // global template imports
    templatesImport += "controllers.playmessages.routes"
  )

}
