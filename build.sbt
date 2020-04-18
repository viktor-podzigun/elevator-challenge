
// put here until this issue is resolved:
//  https://github.com/sbt/sbt/issues/5043
ThisBuild / useCoursier := false

val ideExcludedDirectories = SettingKey[Seq[File]]("ide-excluded-directories")

lazy val `elevator-challenge` = (project in file("."))
  .settings(
    organization := "org.scommons.viktor-podzigun",
    name := "elevator-challenge",
    description := "elevator coding challenge",
    scalaVersion := "2.12.10",
    scalacOptions ++= Seq(
      //see https://docs.scala-lang.org/overviews/compiler-options/index.html#Warning_Settings
      //"-Xcheckinit",
      "-Xfatal-warnings",
      "-Xlint:_",
      "-Ywarn-macros:after", // Only inspect expanded trees when generating unused symbol warnings
      "-explaintypes",
      "-unchecked",
      "-deprecation",
      "-feature"
    ),
    
    ideExcludedDirectories := {
      val base = baseDirectory.value
      List(
        base / ".idea",
        base / "target"
      )
    },
    
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    ),

    //when run tests with coverage: "sbt clean coverage test coverageReport"
    coverageMinimum := 80
  )
