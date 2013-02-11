import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

<<<<<<< HEAD
    val appName         = "codestory-play-scala"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.scalatest" %% "scalatest" % "2.0.M5" % "test",
      "net.java.dev.eval" % "eval" % "0.5",
      "io.backchat.jerkson" %% "jerkson" % "0.7.0"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here  
        testOptions in Test := Nil
=======
    val appName         = "myFirstApp"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
>>>>>>> With scala
    )

}
