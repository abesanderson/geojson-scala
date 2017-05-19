/*
* NOTE: When resolving depenency conflicts, Maven uses a "nearest definition" strategy while SBT uses
* a "latest version" strategy.  In an effort to keep the dependencies in-sync between build systems,
* this build definition will "force" each defined dependency.
*
* NOTE: Dependency forcing "is not" inherited by a "dependsOn" declaration
*/
import Dependencies._

releaseVersionBump := sbtrelease.Version.Bump.Bugfix

lazy val root = (project in file(".")).
  settings(
    publishArtifact := false,
    publishTo := Some(Resolver.file("file", file("/tmp"))),
    Common.settings,
    Common.disableTests
  ).
  aggregate(`geojson`, `geojson-circe`)

lazy val geojson = (project in file("geojson")).
  settings(
    name := "geojson",
    Common.settings,
    libraryDependencies ++= (common ++ Testing.common).map(_.force())
  )

lazy val `geojson-circe` = (project in file("geojson-circe")).
  settings(
    name := "geojson-circe",
    Common.settings,
    libraryDependencies ++= (Library.`geojson-circe` ++ common ++ Testing.common).map(_.force())
  ).
  dependsOn(geojson)
