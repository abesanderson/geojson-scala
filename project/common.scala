import sbt._
import Keys._

object Common {

  val settings = Seq(
    scalaVersion := "2.11.8",
    organization := "com.github.abesanderson",
    updateOptions := updateOptions.value.withLatestSnapshots(true),
    mappings in (Compile, packageSrc) ~= { _.filterNot(_._2.matches(".*\\.conf$"))},
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    fork in Test := true
  )

  val disableTests = Seq(
    test := {},
    testOnly := {},
    testQuick := {}
  )
}

