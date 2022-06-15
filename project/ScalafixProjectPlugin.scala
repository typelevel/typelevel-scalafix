import sbt._, Keys._

import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._
import org.typelevel.sbt._
import scalafix.sbt._
import ScalafixPlugin.autoImport._
import ScalafixTestkitPlugin.autoImport._
import TypelevelSettingsPlugin.autoImport._

object ScalafixProjectPlugin extends AutoPlugin {
  object autoImport {
    lazy val V                        = _root_.scalafix.sbt.BuildInfo
    def scalafixProject(name: String) = ScalafixProject(name)
  }
}

class ScalafixProject private (
  val name: String,
  val rules: Project,
  val input: Project,
  val output: Project,
  val tests: Project
) extends CompositeProject {

  lazy val componentProjects = Seq(all, rules, input, output, tests)

  lazy val all = Project(name, file(s"target/$name-aggregate"))
    .aggregate(rules, input, output, tests)
    .enablePlugins(NoPublishPlugin)

  def rulesSettings(ss: Def.SettingsDefinition*): ScalafixProject =
    rulesConfigure(_.settings(ss: _*))

  def inputSettings(ss: Def.SettingsDefinition*): ScalafixProject =
    inputConfigure(_.settings(ss: _*))

  def outputSettings(ss: Def.SettingsDefinition*): ScalafixProject =
    outputConfigure(_.settings(ss: _*))

  def testsSettings(ss: Def.SettingsDefinition*): ScalafixProject =
    testsConfigure(_.settings(ss: _*))

  def rulesConfigure(transforms: (Project => Project)*): ScalafixProject =
    new ScalafixProject(
      name,
      rules.configure(transforms: _*),
      input,
      output,
      tests
    )

  def inputConfigure(transforms: (Project => Project)*): ScalafixProject =
    new ScalafixProject(
      name,
      rules,
      input.configure(transforms: _*),
      output,
      tests
    )

  def outputConfigure(transforms: (Project => Project)*): ScalafixProject =
    new ScalafixProject(
      name,
      rules,
      input,
      output.configure(transforms: _*),
      tests
    )

  def testsConfigure(transforms: (Project => Project)*): ScalafixProject =
    new ScalafixProject(
      name,
      rules,
      input,
      output,
      tests.configure(transforms: _*)
    )

}

object ScalafixProject {
  def apply(name: String): ScalafixProject = {

    lazy val rules = Project(s"$name-rules", file(s"modules/$name/rules"))
      .settings(
        moduleName := s"typelevel-scalafix-$name",
        libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % _root_.scalafix.sbt.BuildInfo.scalafixVersion
      )

    lazy val input = Project(s"$name-input", file(s"modules/$name/input"))
      .settings(headerSources / excludeFilter := AllPassFilter, tlFatalWarnings := false)
      .enablePlugins(NoPublishPlugin)

    lazy val output = Project(s"$name-output", file(s"modules/$name/output"))
      .settings(headerSources / excludeFilter := AllPassFilter, tlFatalWarnings := false)
      .enablePlugins(NoPublishPlugin)

    lazy val tests = Project(s"$name-tests", file(s"modules/$name/tests"))
      .settings(
        scalafixTestkitOutputSourceDirectories := (output / Compile / unmanagedSourceDirectories).value,
        scalafixTestkitInputSourceDirectories := (input / Compile / unmanagedSourceDirectories).value,
        scalafixTestkitInputClasspath     := (input / Compile / fullClasspath).value,
        scalafixTestkitInputScalacOptions := (input / Compile / scalacOptions).value,
        scalafixTestkitInputScalaVersion  := (input / Compile / scalaVersion).value
      )
      .dependsOn(rules)
      .enablePlugins(NoPublishPlugin, ScalafixTestkitPlugin)

    new ScalafixProject(name, rules, input, output, tests)
  }
}
