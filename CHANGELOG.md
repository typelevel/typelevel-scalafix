# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Early Semantic Versioning](https://docs.scala-lang.org/overviews/core/binary-compatibility-for-library-authors.html#recommended-versioning-scheme) in addition to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.5]

### Added

* [#22](https://github.com/typelevel/typelevel-scalafix/pull/22) - an FS2 linting module was added and the `SyncCompiler` rule originally written for FS2 was ported into this repo.

* [#30](https://github.com/typelevel/typelevel-scalafix/pull/30) - an http4s linting module was added and the `LiteralsSyntax` rule originally written for http4s was ported into this repo.

* [#41](https://github.com/typelevel/typelevel-scalafix/pull/41) - a `TypelevelAs` rule to detect opportunities to rewrite `.map(_ => x)` sequences to `.as(x)` or `.void` was added.

### Changed

* [#21](https://github.com/typelevel/typelevel-scalafix/pull/21/files) - a build plugin for the Scalafix rule project structure was extracted, simplifying the sbt build.

* [#26](https://github.com/typelevel/typelevel-scalafix/pull/26) - scalafmt was upgraded from version 3.4.3 -> 3.5.8

* [#27](https://github.com/typelevel/typelevel-scalafix/pull/27) - cats was upgraded from version 2.7.0 -> 2.8.0

* [#39](https://github.com/typelevel/typelevel-scalafix/pull/39) - sbt was upgraded from version 1.6.2 -> 1.7.1

* Several patch version dependency updates were applied - see the [diff][0.1.5] for more details.

### Fixed

* [#34](https://github.com/typelevel/typelevel-scalafix/issues/34) - an issue with the `UnusedIO` lint where an exception was thrown when traversing `throw` statements.

## [0.1.4] - 2022-06-15

### Fixed

* [#19](https://github.com/typelevel/typelevel-scalafix/issues/19) - an issue which occurred when traversing empty statement blocks.

## [0.1.3] - 2022-06-14

### Added

* [#17](https://github.com/typelevel/typelevel-scalafix/issues/17) - implemented a `TypelevelUnusedShowInterpolator` rule that detects usages of the `show` interpolator that do not interpolate any variables.

## [0.1.2] - 2022-06-09

### Added

* Introduce a `typelevel-scalafix` aggregate module that depends on all rule modules.

## [0.1.1] - 2022-06-08

### Fixed

* [#14](https://github.com/typelevel/typelevel-scalafix/issues/14) - a bug where usage of the `IO.apply` method was not recognised when discarded, since it represented a special case in the Scalameta AST.

## [0.1.0] - 2022-06-07

The initial release of this project, containing linting rules to detect `.map(f).sequence` function call chains, and to detect discarded IO expressions.

[Unreleased]: https://github.com/typelevel/typelevel-scalafix/compare/v0.1.5...HEAD
[0.1.5]: https://github.com/typelevel/typelevel-scalafix/compare/v0.1.4...v0.1.5
[0.1.4]: https://github.com/typelevel/typelevel-scalafix/compare/v0.1.3...v0.1.4
[0.1.3]: https://github.com/typelevel/typelevel-scalafix/compare/v0.1.2...v0.1.3
[0.1.2]: https://github.com/typelevel/typelevel-scalafix/compare/v0.1.1...v0.1.2
[0.1.1]: https://github.com/typelevel/typelevel-scalafix/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/typelevel/typelevel-scalafix/releases/tag/v0.1.0
