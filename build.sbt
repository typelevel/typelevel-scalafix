import sbt.internal.ProjectMatrix
lazy val V = _root_.scalafix.sbt.BuildInfo

ThisBuild / tlBaseVersion := "0.0"

// Cannot support Scala 3.x until Scalafix supports 3.x for semantic rules
// lazy val scala3Version = "3.1.2"
lazy val rulesCrossVersions = Seq(V.scala213, V.scala212)

lazy val CatsVersion       = "2.7.0"
lazy val CatsEffectVersion = "3.3.11"

ThisBuild / developers ++= List(
  tlGitHubDev("DavidGregory084", "David Gregory")
)

ThisBuild / semanticdbEnabled          := true
ThisBuild / semanticdbVersion          := scalafixSemanticdb.revision
ThisBuild / scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(scalaVersion.value)

lazy val root = (project in file("."))
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

lazy val catsTestAggregate = testsAggregateModule("cats", catsTests)

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

lazy val catsEffectTestsAggregate = testsAggregateModule("cats-effect", catsEffectTests)

lazy val catsEffectTests =
  testsModule("cats-effect", catsEffectRules, catsEffectInput, catsEffectOutput)

// Project definition helpers
def rulesModule(name: String) = ProjectMatrix(name, file(s"modules/$name/rules"))
  .settings(
    moduleName                             := s"typelevel-scalafix-$name",
    libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
  )
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(rulesCrossVersions)

def aggregateModule(
  name: String,
  rulesMod: ProjectMatrix,
  inputMod: ProjectMatrix,
  outputMod: ProjectMatrix,
  testsMod: ProjectMatrix
) =
  Project(name, file(s"target/$name-aggregate"))
    .aggregate(
      rulesMod.projectRefs ++
        inputMod.projectRefs ++
        outputMod.projectRefs ++
        testsMod.projectRefs: _*
    )
    .settings(
      publish / skip := true
    )

def inputModule(name: String) = ProjectMatrix(s"$name-input", file(s"modules/$name/input"))
  .settings(publish / skip := true)
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(scalaVersions = rulesCrossVersions /*:+ scala3Version*/ )

def outputModule(name: String) = ProjectMatrix(s"$name-output", file(s"modules/$name/output"))
  .settings(publish / skip := true)
  .defaultAxes(VirtualAxis.jvm)
  .jvmPlatform(scalaVersions = rulesCrossVersions /*:+ scala3Version*/ )

def testsAggregateModule(name: String, testsMod: ProjectMatrix) =
  Project(s"$name-tests", file(s"target/$name-tests-aggregate"))
    .aggregate(testsMod.projectRefs: _*)
    .enablePlugins(NoPublishPlugin)

def testsModule(
  name: String,
  rulesMod: ProjectMatrix,
  inputMod: ProjectMatrix,
  outputMod: ProjectMatrix
) = ProjectMatrix(s"$name-tests", file(s"modules/$name/tests"))
  .settings(
    scalafixTestkitOutputSourceDirectories :=
      TargetAxis
        .resolve(outputMod, Compile / unmanagedSourceDirectories)
        .value,
    scalafixTestkitInputSourceDirectories :=
      TargetAxis
        .resolve(inputMod, Compile / unmanagedSourceDirectories)
        .value,
    scalafixTestkitInputClasspath :=
      TargetAxis.resolve(inputMod, Compile / fullClasspath).value,
    scalafixTestkitInputScalacOptions :=
      TargetAxis.resolve(inputMod, Compile / scalacOptions).value,
    scalafixTestkitInputScalaVersion :=
      TargetAxis.resolve(inputMod, Compile / scalaVersion).value
  )
  .defaultAxes(
    rulesCrossVersions.map(VirtualAxis.scalaABIVersion) :+ VirtualAxis.jvm: _*
  )
  /*.jvmPlatform(
    scalaVersions = Seq(scala3Version),
    axisValues = Seq(TargetAxis(scala3Version)),
    settings = Seq()
  )*/
  .jvmPlatform(
    scalaVersions = Seq(V.scala213),
    axisValues = Seq(TargetAxis(V.scala213)),
    settings = Seq()
  )
  .jvmPlatform(
    scalaVersions = Seq(V.scala212),
    axisValues = Seq(TargetAxis(V.scala212)),
    settings = Seq()
  )
  .dependsOn(rulesMod)
  .enablePlugins(NoPublishPlugin, ScalafixTestkitPlugin)
