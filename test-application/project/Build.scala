import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "test-application"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    
    "de.corux" %% "play-messages" % "2.0"
  )
  
  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers += "corux-releases" at "http://tomcat.corux.de/nexus/content/repositories/releases/",
    resolvers += "corux-snapshots" at "http://tomcat.corux.de/nexus/content/repositories/snaphots/"
  )

}
