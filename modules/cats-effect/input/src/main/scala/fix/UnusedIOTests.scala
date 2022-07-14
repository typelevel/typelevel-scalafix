/*
rule = TypelevelUnusedIO
 */
package fix

import cats.data.EitherT
import cats.data.OptionT
import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.syntax.all._
import java.util.concurrent.TimeoutException
import scala.concurrent.duration._

object UnusedIOTests {
  def usedAssign = {

    { val x = 1 }
    {}

  }

  def unusedIOCompanion = {
    IO.println("foo") /* assert: TypelevelUnusedIO.unusedIO
    ^^^^^^^^^^^^^^^^^
    This IO expression is not used. */
    IO.println("bar")
  }

  def usedIOCompanion = {
    IO.println("foo") >>
      IO.println("bar")
  }

  def unusedDefApply = {
    def foo = IO.pure("foo")

    foo /* assert: TypelevelUnusedIO.unusedIO
    ^^^
    This IO expression is not used. */

    IO.println("bar")
  }

  def usedDefApply = {
    def foo = IO.pure("foo")

    foo >>
      IO.println("bar")
  }

  def unusedNullaryDefApply = {
    def foo() = IO.pure("foo")

    foo() /* assert: TypelevelUnusedIO.unusedIO
    ^^^^^
    This IO expression is not used. */

    IO.println("bar")
  }

  def usedNullaryDefApply = {
    def foo() = IO.pure("foo")

    foo() >>
      IO.println("bar")
  }

  def unusedRef = {
    val foo = IO.println("foo")

    foo /* assert: TypelevelUnusedIO.unusedIO
    ^^^
    This IO expression is not used. */

    IO.println("bar")
  }

  def usedRef = {
    val foo = IO.println("foo")

    foo >>
      IO.println("bar")
  }

  def unusedDefWithSelect = {
    def foo() = IO.pure("foo")

    foo().map(_.length) /* assert: TypelevelUnusedIO.unusedIO
    ^^^^^^^^^^^^^^^^^^^
    This IO expression is not used. */

    IO.println("bar")
  }

  def usedDefWithSelect = {
    def foo() = IO.pure("foo")

    foo().map(_.length()) >>
      IO.println("bar")
  }

  def unusedInfix = {
    IO.pure("foo") >> IO.println("bar") /* assert: TypelevelUnusedIO.unusedIO
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    This IO expression is not used. */
    IO.println("baz")
  }

  def usedInfix = {
    IO.pure("foo") >> IO.println("bar") >>
      IO.println("baz")
  }

  def unusedTypeApply = {
    def never[A] = IO.never[A]

    never[Int] /* assert: TypelevelUnusedIO.unusedIO
    ^^^^^^^^^^
    This IO expression is not used. */

    IO.println("bar")
  }

  def usedTypeApply = {
    def never[A] = IO.never[A]

    never[Int] >>
      IO.println("bar")
  }

  def unusedMatch = {
    1 match { /* assert: TypelevelUnusedIO.unusedIO
    ^
    This IO expression is not used. */
      case _ =>
        IO.println("foo")
    }

    IO.println("bar")
  }

  def unusedForYieldVal = {
    for {
      _ <- IO.println("foo")
      _ = IO.println("bar") /* assert: TypelevelUnusedIO.unusedIO
          ^^^^^^^^^^^^^^^^^
          This IO expression is not used. */
    } yield "baz"
  }

  def unusedForYieldBlock = {
    for {
      _ <- IO.println("foo")
      _ = { /* assert: TypelevelUnusedIO.unusedIO
          ^
          This IO expression is not used. */
        IO.println("bar")
      }
    } yield "baz"
  }

  def unusedMatchCase = {
    1 match {
      case _ =>
        IO.println("foo") /* assert: TypelevelUnusedIO.unusedIO
        ^^^^^^^^^^^^^^^^^
        This IO expression is not used. */
        IO.println("bar")
    }
  }

  def usedMatchCase = {
    1 match {
      case _ =>
        IO.println("foo") >>
          IO.println("bar")
    }
  }

  def unusedObjectBody = {
    object unusedIO {
      IO.println("foo") /* assert: TypelevelUnusedIO.unusedIO
      ^^^^^^^^^^^^^^^^^
      This IO expression is not used. */
    }
  }

  def unusedPrimaryConstructor = {
    class unusedIO {
      IO.println("foo") /* assert: TypelevelUnusedIO.unusedIO
      ^^^^^^^^^^^^^^^^^
      This IO expression is not used. */
    }
  }

  def unusedSecondaryConstructor = {
    class unusedIO {
      def this(i: Int) = {
        this()
        IO.println("foo") /* assert: TypelevelUnusedIO.unusedIO
        ^^^^^^^^^^^^^^^^^
        This IO expression is not used. */
      }
    }
  }

  def unusedWhileStat = {
    while (true) IO.println("foo") /* assert: TypelevelUnusedIO.unusedIO
                 ^^^^^^^^^^^^^^^^^
                 This IO expression is not used. */
  }

  def unusedForStat = {
    for (i <- (0 to 10)) IO.println("foo") /* assert: TypelevelUnusedIO.unusedIO
                         ^^^^^^^^^^^^^^^^^
                         This IO expression is not used. */
  }

  def unusedTryFinally = {
    try println("foo")
    finally IO.println("bar") /* assert: TypelevelUnusedIO.unusedIO
            ^^^^^^^^^^^^^^^^^
            This IO expression is not used. */
  }

  def usedTryFinally = {
    try println("foo")
    finally IO.println("bar").unsafeRunSync()
  }

  def unusedStringInterpolation = {
    s"${IO.pure("foo")}" /* assert: TypelevelUnusedIO.unusedIO
        ^^^^^^^^^^^^^^
        This IO expression is not used. */
  }

  def usedStringInterpolation = {
    s"${IO.pure("foo").unsafeRunSync()}"
  }

  def unusedIOApply = {
    for {
      a <- IO.pure(42L)
      _ = IO(println("hello")) /* assert: TypelevelUnusedIO.unusedIO
          ^^^^^^^^^^^^^^^^^^^^
          This IO expression is not used. */
      _ = IO { println("hello") } /* assert: TypelevelUnusedIO.unusedIO
          ^^^^^^^^^^^^^^^^^^^^^^^
          This IO expression is not used. */
      _ = IO({ println("hello") }) /* assert: TypelevelUnusedIO.unusedIO
          ^^^^^^^^^^^^^^^^^^^^^^^^
          This IO expression is not used. */
    } yield ()
  }

  def unusedEmptyIO = {
    for {
      a <- IO.pure(42L)
      _ = IO {} /* assert: TypelevelUnusedIO.unusedIO
          ^^^^^
          This IO expression is not used. */
    } yield ()
  }

  // TODO: Implement checks using inferred type information
  // This is tricky to do because the inferred types in the
  // call to `.value` are bound in the previous expression.
  // This would require keeping track of the inferred types from
  // expression to expression.
  def unusedTransformers = {
    OptionT(IO.some(1)).value
    EitherT(IO.println("foo").attempt).value
    IO.println("foo")
  }

  // TODO: Implement checks using inferred type information
  def unusedExtension = {
    IO.println("foo").timeout(50.millis).attemptNarrow[TimeoutException]

    IO.println("bar")
  }
}
