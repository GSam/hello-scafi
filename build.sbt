val scafi_core  = "it.unibo.scafi" %% "scafi-core"  % "1.6.0"
val scafi_simulator  = "it.unibo.scafi" %% "scafi-simulator-gui"  % "1.6.0"
val scafi_sim  = "it.unibo.scafi" %% "scafi-simulator"  % "1.6.0"

ThisBuild / scalaVersion := "2.13.12"
ThisBuild / organization := "com.example"

lazy val hello = (project in file("."))
  .settings(
    name := "Hello",
    libraryDependencies ++= Seq(scafi_core, scafi_simulator, scafi_sim)
  )
