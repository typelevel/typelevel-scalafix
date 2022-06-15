# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Early Semantic Versioning](https://docs.scala-lang.org/overviews/core/binary-compatibility-for-library-authors.html#recommended-versioning-scheme) in addition to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.4]

### Fixed

* [#19](https://github.com/typelevel/typelevel-scalafix/issues/19) - an issue which occurred when traversing empty statement blocks.

## [0.1.3]

### Added

* [#17](https://github.com/typelevel/typelevel-scalafix/issues/17) - implemented a `TypelevelUnusedShowInterpolator` rule that detects usages of the `show` interpolator that do not interpolate any variables.

## [0.1.2]

### Added

* Introduce a `typelevel-scalafix` aggregate module that depends on all rule modules.

## [0.1.1]

### Fixed

* [#14](https://github.com/typelevel/typelevel-scalafix/issues/14) - a bug where usage of the `IO.apply` method was not recognised when discarded, since it represented a special case in the Scalameta AST.

## [0.1.0] - 2022-06-07

The initial release of this project, containing linting rules to detect `.map(f).sequence` function call chains, and to detect discarded IO expressions.

[Unreleased]: https://github.com/typelevel/typelevel-scalafix/compare/v0.1.4...HEAD
[0.1.4]: https://github.com/typelevel/typelevel-scalafix/compare/v0.1.3...v0.1.4
[0.1.3]: https://github.com/typelevel/typelevel-scalafix/compare/v0.1.2...v0.1.3
[0.1.2]: https://github.com/typelevel/typelevel-scalafix/compare/v0.1.1...v0.1.2
[0.1.1]: https://github.com/typelevel/typelevel-scalafix/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/typelevel/typelevel-scalafix/releases/tag/v0.1.0
