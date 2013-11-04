organization := "org.indyscala"

name := "akka-io-demo"

scalacOptions in ThisBuild ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.3"
)
