import sbt._
import Keys._

object Dependencies {

  object Version {
		val circe = "0.8.0"
  }

  val common = Seq()

  object Library {
    val `geojson-circe` = Seq(
			"io.circe" %% "circe-core" % Version.circe,
			"io.circe" %% "circe-generic" % Version.circe,
			"io.circe" %% "circe-generic-extras" % Version.circe,
			"io.circe" %% "circe-parser" % Version.circe,
			"io.circe" %% "circe-java8" % Version.circe
    )
  }

  object Testing {
    val common = Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  }
}
