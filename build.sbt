name := "play-markdown"

version := "0.1"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.2",
  "org.scala-lang" % "scala-compiler" % "2.10.3",
  "com.typesafe.play" %% "templates-compiler" % "2.2.1",
  "com.typesafe.play" %% "templates" % "2.2.1",
  // pegdown
  "org.pegdown" % "pegdown" % "1.4.2"
)

resolvers += "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/"
