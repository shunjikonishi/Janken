name := "janken"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.typesafe" %% "play-plugins-redis" % "2.1.1",
  "org.webjars" %% "webjars-play" % "2.2.1",
  "org.webjars" % "bootstrap" % "3.0.3",
  "org.webjars" % "jquery" % "1.10.2-1"
)     

play.Project.playScalaSettings

resolvers += "org.sedis" at "http://pk11-scratch.googlecode.com/svn/trunk"
