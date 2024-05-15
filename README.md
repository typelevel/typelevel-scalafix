# Scalafix rules for Typelevel projects

[![Continuous Integration](https://github.com/typelevel/typelevel-scalafix/actions/workflows/ci.yml/badge.svg)](https://github.com/typelevel/typelevel-scalafix/actions/workflows/ci.yml)
[![License](https://img.shields.io/github/license/typelevel/typelevel-scalafix.svg)](https://opensource.org/licenses/Apache-2.0)
[![Discord](https://img.shields.io/discord/632277896739946517.svg?label=&logo=discord&logoColor=ffffff&color=404244&labelColor=6A7EC2)](https://discord.gg/D7wY3aH7BQ)
[![Maven Central](https://img.shields.io/maven-central/v/org.typelevel/typelevel-scalafix_2.13)](https://search.maven.org/artifact/org.typelevel/typelevel-scalafix_2.13)

This is a set of Scalafix rules to provide automated rewrites and linting for code which makes use of [Typelevel](https://github.com/typelevel) libraries.

## Installation

Follow the instructions to set up [Scalafix](https://scalacenter.github.io/scalafix/docs/users/installation.html) in your project.

Then you can add the *typelevel-scalafix* rules to your sbt project using the `scalafixDependencies` key.

```scala
// To add all Scalafix rules
ThisBuild / scalafixDependencies += "org.typelevel" %% "typelevel-scalafix" % "0.2.0"

// To add only cats Scalafix rules
ThisBuild / scalafixDependencies += "org.typelevel" %% "typelevel-scalafix-cats" % "0.2.0"
// To add only cats-effect Scalafix rules
ThisBuild / scalafixDependencies += "org.typelevel" %% "typelevel-scalafix-cats-effect" % "0.2.0"
// To add only fs2 Scalafix rules
ThisBuild / scalafixDependencies += "org.typelevel" %% "typelevel-scalafix-fs2" % "0.2.0"
// To add only http4s Scalafix rules
ThisBuild / scalafixDependencies += "org.typelevel" %% "typelevel-scalafix-http4s" % "0.2.0"
```

## Usage

Once enabled, you can configure the rules that will run when you use the `scalafix` command by adding them to your .scalafix.conf file, or run individual rules by providing them as arguments to the `scalafix` command.

```
// .scalafix.conf
rules = [
  TypelevelUnusedIO
  TypelevelMapSequence
  TypelevelAs
  TypelevelUnusedShowInterpolator
  TypelevelFs2SyncCompiler
  TypelevelHttp4sLiteralsSyntax
  TypelevelIORandomUUID
]
```

```
// in the sbt shell
> scalafix TypelevelUnusedIO
```

### Scala compatibility

Not all rules function with Scala 3 yet.

| Rule                            | 2.13.x             | 3.x                |
|---------------------------------|--------------------|--------------------|
| TypelevelUnusedIO               | :white_check_mark: | :x:                |
| TypelevelIORandomUUID           | :white_check_mark: | :white_check_mark: |
| TypelevelMapSequence            | :white_check_mark: | :white_check_mark: |
| TypelevelAs                     | :white_check_mark: | :white_check_mark: |
| TypelevelUnusedShowInterpolator | :white_check_mark: | :x:                |
| TypelevelFs2SyncCompiler        | :white_check_mark: | :x:                |
| TypelevelHttp4sLiteralsSyntax   | :white_check_mark: | :white_check_mark: |

## Rules for cats

### TypelevelMapSequence

This rule detects call sequences like `.map(f).sequence` and `.map(f).sequence_`, since they can be replaced by `.traverse(f)` and `.traverse_(f)` respectively.

**Examples**

```scala
NonEmptyList.one(1).map(Const.apply[Int, String]).sequence /*
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
.map(f).sequence can be replaced by .traverse(f) */
```

### TypelevelAs

This rule detects call sequences like `.map(_ => ())` and `.map(_ => <some literal>)`, since they can be replaced by `.void` and `.as(<some literal>)` respectively.

**Examples**

```scala
List(1, 2, 3).map(_ => ()) /* assert: TypelevelAs.as
^^^^^^^^^^^^^^^^^^^^^^^^^^
.map(_ => ()) can be replaced by .void */
```

**Limitations**

At the moment this rule is only applied to applications of `map` where the argument function returns a literal value.

This is because it's not clear whether any given variable in a Scala program has been evaluated yet.

For example, in the expression `.map(_ => someVariable)`, if `someVariable` is a `lazy val` refactoring to use `.as` could change the behaviour of the program, since `as` evaluates its argument strictly.

### TypelevelUnusedShowInterpolator

This rule detects usages of the cats `show` interpolator that do not interpolate any variables.

**Examples**

```scala
val foo = show"bar" /* assert: TypelevelUnusedShowInterpolator.unusedShowInterpolator
          ^^^^^^^^^
          This show interpolator contains no interpolated variables. */
```

## Rules for cats-effect

### TypelevelUnusedIO

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

### TypelevelIORandomUUID

This rule detects usages of UUID.randomUUID wrapped in an IO or IO.blocking call and rewrites them automatically to IO.randomUUID calls.

**Examples**

```scala
val test = IO(UUID.randomUUID())
```

would be rewritten to
```scala
val test = IO.randomUUID
```

This rule works on variable declarations, usaged within methods as well as for comprehensions.

## Rules for fs2

### TypelevelFs2SyncCompiler

This rule detects usages of the fs2 `Sync` compiler which can have surprising semantics with regard to (lack of) interruption (e.g., [typelevel/fs2#2371](https://github.com/typelevel/fs2/issues/2371)).

To use this rule, you'll need to enable synthetics by adding the following to your `build.sbt`:
```scala
ThisBuild / semanticdbOptions += "-P:semanticdb:synthetics:on"
```


**Examples**

```scala
def countChunks[F[_]: Sync, A](stream: Stream[F, A]): F[Long] =
  stream.chunks.as(1L).compile.foldMonoid /*
  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  FS2's Sync compiler should be avoided due to its surprising semantics.
  Usually this means a Sync constraint needs to be changed to Concurrent or upgraded to Async. */
```

## Rules for http4s

### TypelevelHttp4sLiteralsSyntax

This rule rewrites uses of `Uri.unsafeFromString("...")` and friends with `uri"..."`.

## Conduct

Participants are expected to follow the [Scala Code of Conduct](https://www.scala-lang.org/conduct/) while discussing the project on GitHub and any other venues associated with the project.

## License

All code in this repository is licensed under the Apache License, Version 2.0.  See [LICENSE](./LICENSE).
