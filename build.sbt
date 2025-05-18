scalaVersion := "3.3.6"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
)

version := "4.0.0"

assembly / mainClass := Some("FECharMaker.fecc")