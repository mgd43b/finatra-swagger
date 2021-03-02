inThisBuild(List(
  organization := "com.jakehschwartz",
  homepage := Some(url("https://github.com/jakehschwartz/finatra-swagger")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(id="jakehschwartz", name="Jake Schwartz", email="jakehschwartz@gmail.com", url=url("https://www.jakehschwartz.com")),
    Developer(id="xiaodongw", name="Xiaodong Wang", email="xiaodongw79@gmail.com", url=url("https://github.com/xiaodongw"))
  )
))

name := "finatra-swagger"

scalaVersion := "2.13.1"

crossScalaVersions := Seq("2.11.12", "2.12.12", "2.13.1")

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val twitterReleaseVersion = "21.2.0"
lazy val jacksonVersion = "2.11.2"

lazy val swaggerUIVersion = SettingKey[String]("swaggerUIVersion")

swaggerUIVersion := "3.44.0"

enablePlugins(BuildInfoPlugin)
buildInfoPackage := "com.jakehschwartz.finatra.swagger"
buildInfoKeys := Seq[BuildInfoKey](name, version, swaggerUIVersion)

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra-http" % twitterReleaseVersion,
  "io.swagger" % "swagger-core" % "1.6.2",
  "io.swagger" %% "swagger-scala-module" % "1.0.6",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  "org.webjars" % "swagger-ui" % swaggerUIVersion.value,
  "net.bytebuddy" % "byte-buddy" % "1.10.19",
  "org.scalatest" %% "scalatest" % "3.1.3" % Test
)

val examplesTestLibs = Seq(
  "com.twitter" %% "finatra-http" % twitterReleaseVersion % "test" classifier "tests",
  "com.twitter" %% "inject-app" % twitterReleaseVersion % "test" classifier "tests",
  "com.twitter" %% "inject-core" % twitterReleaseVersion % "test" classifier "tests",
  "com.twitter" %% "inject-modules" % twitterReleaseVersion % "test" classifier "tests",
  "com.twitter" %% "inject-server" % twitterReleaseVersion % "test" classifier "tests",
  "org.scalatestplus" %% "mockito-1-10" % "3.1.0.0" % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.scalatest" %% "scalatest" % "3.1.4"  % Test,
  "org.mockito" % "mockito-all" % "1.10.19"  % Test
)

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:existentials",
  "-language:implicitConversions"
)

//pomIncludeRepository := { _ => false }
//
//pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toCharArray())
//
//scmInfo := Some(
//  ScmInfo(
//    browseUrl = url("https://github.com/jakehschwartz/finatra-swagger"),
//    connection = "https://github.com/jakehschwartz/finatra-swagger.git"
//  )
//)

lazy val root = Project("finatra-swagger", file("."))

lazy val example = Project("hello-world-example", file("examples/hello-world"))
  .dependsOn(root)
  .settings(libraryDependencies ++= examplesTestLibs)
  .settings(
    parallelExecution in Test := true,
    testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oDF"),
    javaOptions ++= Seq(
      "-Xss8M",
      "-Xms512M",
      "-Xmx2G"
    ),
    javaOptions in Test ++= Seq(
      "-Dlog.service.output=/dev/stdout",
      "-Dlog.access.output=/dev/stdout",
      "-Dlog_level=DEBUG")
  )
