import Dependencies._

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "snap"

lazy val root = (project in file("."))
  .settings(
    name := "blink-snap",
    libraryDependencies += catsEffect,
    libraryDependencies += scalaTest % Test
  )
