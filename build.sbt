name := "janken"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "net.debasishg" % "redisclient_2.10" % "2.11",
  "org.webjars" %% "webjars-play" % "2.2.1",
  "org.webjars" % "jquery" % "1.10.2-1",
  "org.webjars" % "bootstrap" % "3.0.3"
)     

play.Project.playScalaSettings

