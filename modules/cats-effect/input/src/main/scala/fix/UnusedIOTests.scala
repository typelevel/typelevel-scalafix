/*
 * Copyright 2022 Typelevel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fix

import cats.data.EitherT
import cats.data.OptionT
import cats.effect._
import cats.effect.unsafe.implicits.global
import scala.util.control.NonFatal

object UnusedIOTests {
  def unusedIOCompanion = {
    IO.println("foo") /* assert: UnusedIO.unusedIO
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

    foo /* assert: UnusedIO.unusedIO
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

    foo() /* assert: UnusedIO.unusedIO
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

    foo /* assert: UnusedIO.unusedIO
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

    foo().map(_.length) /* assert: UnusedIO.unusedIO
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
    IO.pure("foo") >> IO.println("bar") /* assert: UnusedIO.unusedIO
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

    never[Int] /* assert: UnusedIO.unusedIO
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
    1 match { /* assert: UnusedIO.unusedIO
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
      _ = IO.println("bar") /* assert: UnusedIO.unusedIO
          ^^^^^^^^^^^^^^^^^
          This IO expression is not used. */
    } yield "baz"
  }

  def unusedForYieldBlock = {
    for {
      _ <- IO.println("foo")
      _ = { /* assert: UnusedIO.unusedIO
          ^
          This IO expression is not used. */
        IO.println("bar")
      }
    } yield "baz"
  }

  def unusedMatchCase = {
    1 match {
      case _ =>
        IO.println("foo") /* assert: UnusedIO.unusedIO
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
      IO.println("foo") /* assert: UnusedIO.unusedIO
      ^^^^^^^^^^^^^^^^^
      This IO expression is not used. */
    }
  }

  def unusedPrimaryConstructor = {
    class unusedIO {
      IO.println("foo") /* assert: UnusedIO.unusedIO
      ^^^^^^^^^^^^^^^^^
      This IO expression is not used. */
    }
  }

  def unusedSecondaryConstructor = {
    class unusedIO {
      def this(i: Int) = {
        this()
        IO.println("foo") /* assert: UnusedIO.unusedIO
        ^^^^^^^^^^^^^^^^^
        This IO expression is not used. */
      }
    }
  }

  def unusedWhileStat = {
    while (true) IO.println("foo") /* assert: UnusedIO.unusedIO
                 ^^^^^^^^^^^^^^^^^
                 This IO expression is not used. */
  }

  def unusedForStat = {
    for (i <- (0 to 10)) IO.println("foo") /* assert: UnusedIO.unusedIO
                         ^^^^^^^^^^^^^^^^^
                         This IO expression is not used. */
  }

  def unusedTryFinally = {
    try println("foo")
    finally IO.println("bar") /* assert: UnusedIO.unusedIO
            ^^^^^^^^^^^^^^^^^
            This IO expression is not used. */
  }

  def usedTryFinally = {
    try println("foo")
    finally IO.println("bar").unsafeRunSync()
  }

  def unusedStringInterpolation = {
    s"${IO.pure("foo")}" /* assert: UnusedIO.unusedIO
        ^^^^^^^^^^^^^^
        This IO expression is not used. */
  }

  def usedStringInterpolation = {
    s"${IO.pure("foo").unsafeRunSync()}"
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
}
