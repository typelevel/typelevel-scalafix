lazy val V = _root_.scalafix.sbt.BuildInfo

ThisBuild / tlBaseVersion := "0.1"

ThisBuild / crossScalaVersions := Seq(V.scala213, V.scala212)
ThisBuild / scalaVersion       := (ThisBuild / crossScalaVersions).value.head

lazy val CatsVersion       = "2.7.0"
lazy val CatsEffectVersion = "3.3.11"

ThisBuild / developers ++= List(
  tlGitHubDev("DavidGregory084", "David Gregory")
)

ThisBuild / semanticdbEnabled          := true
ThisBuild / semanticdbVersion          := scalafixSemanticdb.revision
ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)

lazy val `typelevel-scalafix` = project
  .in(file("."))
  .aggregate(cats, catsEffect)
  .enablePlugins(NoPublishPlugin)

// typelevel/cats Scalafix rules
lazy val cats =
  aggregateModule("cats", catsRules, catsInput, catsOutput, catsTests)

lazy val catsRules = rulesModule("cats")

lazy val catsInput = inputModule("cats")
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % CatsVersion
    )
  )

lazy val catsOutput = outputModule("cats")

lazy val catsTests = testsModule("cats", catsRules, catsInput, catsOutput)

// typelevel/cats-effect Scalafix rules
lazy val catsEffect =
  aggregateModule(
    "cats-effect",
    catsEffectRules,
    catsEffectInput,
    catsEffectOutput,
    catsEffectTests
  )

lazy val catsEffectRules = rulesModule("cats-effect")

lazy val catsEffectInput = inputModule("cats-effect")
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core"   % CatsVersion,
      "org.typelevel" %% "cats-effect" % CatsEffectVersion
    )
  )

lazy val catsEffectOutput = outputModule("cats-effect")

lazy val catsEffectTests =
  testsModule("cats-effect", catsEffectRules, catsEffectInput, catsEffectOutput)

// Project definition helpers
def rulesModule(name: String) =
  Project(s"$name-rules", file(s"modules/$name/rules"))
    .settings(
      moduleName                             := s"typelevel-scalafix-$name",
      libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
    )

def aggregateModule(
  name: String,
  rulesMod: Project,
  inputMod: Project,
  outputMod: Project,
  testsMod: Project
) = Project(name, file(s"target/$name-aggregate"))
  .aggregate(rulesMod, inputMod, outputMod, testsMod)
  .enablePlugins(NoPublishPlugin)

def inputModule(name: String) =
  Project(s"$name-input", file(s"modules/$name/input"))
    .settings(headerSources / excludeFilter := AllPassFilter, tlFatalWarnings := false)
    .enablePlugins(NoPublishPlugin)

def outputModule(name: String) =
  Project(s"$name-output", file(s"modules/$name/output"))
    .settings(headerSources / excludeFilter := AllPassFilter, tlFatalWarnings := false)
    .enablePlugins(NoPublishPlugin)

def testsModule(
  name: String,
  rulesMod: Project,
  inputMod: Project,
  outputMod: Project
) = Project(s"$name-tests", file(s"modules/$name/tests"))
  .settings(
    scalafixTestkitOutputSourceDirectories := (outputMod / Compile / unmanagedSourceDirectories).value,
    scalafixTestkitInputSourceDirectories := (inputMod / Compile / unmanagedSourceDirectories).value,
    scalafixTestkitInputClasspath     := (inputMod / Compile / fullClasspath).value,
    scalafixTestkitInputScalacOptions := (inputMod / Compile / scalacOptions).value,
    scalafixTestkitInputScalaVersion  := (inputMod / Compile / scalaVersion).value
  )
  .dependsOn(rulesMod)
  .enablePlugins(NoPublishPlugin, ScalafixTestkitPlugin)
