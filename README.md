# Scalafix rules for Typelevel projects

[![Continuous Integration](https://github.com/typelevel/typelevel-scalafix/actions/workflows/ci.yml/badge.svg)](https://github.com/typelevel/typelevel-scalafix/actions/workflows/ci.yml)
[![License](https://img.shields.io/github/license/typelevel/typelevel-scalafix.svg)](https://opensource.org/licenses/Apache-2.0)
[![Discord](https://img.shields.io/discord/632277896739946517.svg?label=&logo=discord&logoColor=ffffff&color=404244&labelColor=6A7EC2)](https://discord.gg/D7wY3aH7BQ)

This is a set of Scalafix rules to apply automated rewrites and linting in [Typelevel](https://github.com/typelevel) projects.

## Installation

Follow the instructions to set up [Scalafix](https://scalacenter.github.io/scalafix/docs/users/installation.html) in your project.

Then you can add the *typelevel-scalafix* rules to your sbt project using the `scalafixDependencies` key.

```scala
// For cats Scalafix rules
ThisBuild / scalafixDependencies += "org.typelevel" %% "typelevel-scalafix-cats" % "0.1.0-SNAPSHOT"
// For cats-effect Scalafix rules
ThisBuild / scalafixDependencies += "org.typelevel" %% "typelevel-scalafix-cats-effect" % "0.1.0-SNAPSHOT"
```

## Usage

Once enabled, you can run the rules provided by adding them to your .scalafix.conf file, or by running individual rules using the `scalafix` command in sbt.

```
// .scalafix.conf
rules = [
  UnusedIO
  MapSequence
]
```

```
// in the sbt shell
> scalafix UnusedIO
```

## Rules for cats

### MapSequence

This rule detects call sequences like `.map(f).sequence` and `.map(f).sequence_`, since they can be replaced by `.traverse(f)` and `.traverse_(f)` respectively.

**Examples**

```scala
NonEmptyList.one(1).map(Const.apply[Int, String]).sequence /*
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
.map(f).sequence can be replaced by .traverse(f) */
```

## Rules for cats-effect

### UnusedIO

This rule detects discarded IO expressions, since those expressions will not run as part of an IO program unless they are composed into the final result.

**Examples**

```scala
{
  val foo = IO.println("foo")

  foo /*
  ^^^
  This IO expression is not used. */

  IO.println("bar")
}

for {
  _ <- IO.println("foo")
  _ = IO.println("bar") /*
      ^^^^^^^^^^^^^^^^^
      This IO expression is not used. */
} yield "baz"
```

**Limitations**

At the moment this rule is most useful in concrete usages of `IO`, since detecting discarded expressions in a generic context requires access to inferred type information.

This also causes problems with checking for discarded expressions that are using extension methods.

This means that expressions like the following currently won't be detected:

```scala
OptionT(IO.some(1)).value

EitherT(IO.println("foo").attempt).value

// .attemptNarrow is provided as a `cats.ApplicativeError` extension method
IO.println("foo").timeout(50.millis).attemptNarrow[TimeoutException]
```

## Conduct

Participants are expected to follow the [Scala Code of Conduct](https://www.scala-lang.org/conduct/) while discussing the project on GitHub and any other venues associated with the project.

## License

All code in this repository is licensed under the Apache License, Version 2.0.  See [LICENSE](./LICENSE).
