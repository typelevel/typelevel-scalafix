ThisBuild / tlBaseVersion := "0.1"

ThisBuild / crossScalaVersions := Seq(V.scala213, V.scala212)
ThisBuild / scalaVersion       := (ThisBuild / crossScalaVersions).value.head

lazy val CatsVersion       = "2.8.0"
lazy val CatsEffectVersion = "3.3.13"
lazy val Fs2Version        = "3.2.8"
lazy val Http4sVersion     = "0.23.13"

ThisBuild / developers ++= List(
  tlGitHubDev("DavidGregory084", "David Gregory")
)

ThisBuild / semanticdbEnabled          := true
ThisBuild / semanticdbVersion          := scalafixSemanticdb.revision
ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)

lazy val `typelevel-scalafix` = project
  .in(file("."))
  .aggregate(`typelevel-scalafix-rules`, cats.all, catsEffect.all, fs2.all, http4s.all)
  .enablePlugins(NoPublishPlugin)

lazy val `typelevel-scalafix-rules` = project
  .in(file("target/rules-aggregate"))
  .dependsOn(cats.rules, catsEffect.rules, fs2.rules, http4s.rules)
  .settings(
    moduleName := "typelevel-scalafix",
    tlVersionIntroduced ++= List("2.12", "2.13").map(_ -> "0.1.2").toMap,
    // TODO: Should be removed if/when https://github.com/lightbend/mima/issues/702 is fixed
    mimaPreviousArtifacts := Set.empty
  )

// typelevel/cats Scalafix rules
lazy val cats = scalafixProject("cats")
  .inputSettings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % CatsVersion
    )
  )

// typelevel/cats-effect Scalafix rules
lazy val catsEffect = scalafixProject("cats-effect")
  .inputSettings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core"   % CatsVersion,
      "org.typelevel" %% "cats-effect" % CatsEffectVersion
    )
  )

// typelevel/fs2 Scalafix rules
lazy val fs2 = scalafixProject("fs2")
  .rulesSettings(
    tlVersionIntroduced ++= List("2.12", "2.13").map(_ -> "0.1.5").toMap
  )
  .inputSettings(
    semanticdbOptions += "-P:semanticdb:synthetics:on",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core"   % CatsVersion,
      "org.typelevel" %% "cats-effect" % CatsEffectVersion,
      "co.fs2"        %% "fs2-core"    % Fs2Version
    )
  )

// http4s/http4s Scalafix rules
lazy val http4s = scalafixProject("http4s")
  .rulesSettings(
    tlVersionIntroduced ++= List("2.12", "2.13").map(_ -> "0.1.5").toMap
  )
  .inputSettings(
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-core" % Http4sVersion
    )
  )
  .outputSettings(
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-core" % Http4sVersion
    )
  )
