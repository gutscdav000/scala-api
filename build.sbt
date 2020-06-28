name := "scala-api"

version := "0.1"
scalaVersion := "2.13.2"

val scalaTest = "org.scalatest" %% "scalatest" % "3.1.0" % "test"
val doobieVersion = "0.8.8"
val tapirVersion = "0.12.15"
//val http4sVersion = "0.20.22"
val http4sVersion = "0.21.4"
val tsecVersion = "0.2.0"
val circeVersion = "0.13.0"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-quill" % doobieVersion,
  "org.tpolecat" %% "doobie-hikari" % doobieVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s" % tapirVersion,
  "dev.zio" %% "zio" % "1.0.0-RC17",
  "dev.zio" %% "zio-interop-cats" % "2.0.0.0-RC10",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "io.github.jmcardon" %% "tsec-password" % tsecVersion,
  "io.github.jmcardon" %% "tsec-cipher-jca" % tsecVersion,
  "io.github.jmcardon" % "tsec-mac_2.13" % tsecVersion,
  "io.github.jmcardon" % "tsec-http4s_2.13" % tsecVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" % "circe-core_2.13" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe"  %% "circe-parser"   % circeVersion,
  "io.circe" %% "circe-optics" % circeVersion,
  "org.http4s" % "http4s-client_2.13" % http4sVersion,
  "org.http4s" % "http4s-dsl_2.13" % http4sVersion,
  "org.http4s" % "http4s-blaze-server_2.13" % http4sVersion,
  "org.http4s" % "http4s-blaze-server_2.13" % http4sVersion,
  "org.http4s" % "http4s-blaze-client_2.13" % http4sVersion,
  "org.http4s" % "http4s-json4s-jackson_2.13" % http4sVersion,
  "org.http4s" % "http4s-json4s_2.13" % http4sVersion,
  "io.circe" % "circe-derivation_2.13" % "0.13.0-M4",
  "org.json4s" % "json4s-native_2.13" % "3.7.0-M4",
  "org.reactormonk" % "cryptobits_2.13" % "1.3",
//  "joda-time" % "joda-time" % "2.10.6",
  scalaTest
)

resolvers ++= Seq(
  "Typesafe" at "https://repo.typesafe.com/typesafe/releases/",
  "Java.net Maven2 Repository" at "https://download.java.net/maven/2/"
)

//addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
//scalacOptions ++= Seq("-Ypartial-unification")