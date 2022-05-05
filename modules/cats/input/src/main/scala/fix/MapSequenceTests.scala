/*
rule = MapSequence
 */
package fix

import cats._
import cats.data.Const
import cats.data.NonEmptyList
import cats.syntax.all._

object MapSequenceTests {
  def listMapSequence = {
    List(1, 2, 3).map(Either.right[Unit, Int]).sequence /* assert: MapSequence.mapSequence
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence can be replaced by .traverse(f) */
  }

  def nelMapSequence = {
    NonEmptyList.one(1).map(Const.apply[Int, String]).sequence /* assert: MapSequence.mapSequence
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence can be replaced by .traverse(f) */
  }

  def fMapSequence[F[_], G[_]](implicit
    F: Traverse[F] with Applicative[F],
    G: Applicative[G]
  ) = {
    F.pure(1).map(i => G.pure(i)).sequence /* assert: MapSequence.mapSequence
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence can be replaced by .traverse(f) */

    F.sequence(F.pure(1).map(i => G.pure(i))) /* assert: MapSequence.mapSequence
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence can be replaced by .traverse(f) */

    F.map(F.pure(1))(i => G.pure(i)).sequence /* assert: MapSequence.mapSequence
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence can be replaced by .traverse(f) */

    F.sequence(F.map(F.pure(1))(i => G.pure(i))) /* assert: MapSequence.mapSequence
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence can be replaced by .traverse(f) */
  }

  def listMapSequence_ = {
    List(1, 2, 3).map(Either.right[Unit, Int]).sequence_ /* assert: MapSequence.mapSequence_
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence_ can be replaced by .traverse_(f) */
  }

  def nelMapSequence_ = {
    NonEmptyList.one(1).map(Const.of[String](_)).sequence_ /* assert: MapSequence.mapSequence_
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence_ can be replaced by .traverse_(f) */
  }

  def fMapSequence_[F[_], G[_]](implicit
    F: Traverse[F] with Applicative[F],
    G: Applicative[G]
  ) = {
    F.pure(1).map(i => G.pure(i)).sequence_ /* assert: MapSequence.mapSequence_
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence_ can be replaced by .traverse_(f) */

    F.sequence_(F.pure(1).map(i => G.pure(i))) /* assert: MapSequence.mapSequence_
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence_ can be replaced by .traverse_(f) */

    F.map(F.pure(1))(i => G.pure(i)).sequence_ /* assert: MapSequence.mapSequence_
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence_ can be replaced by .traverse_(f) */

    F.sequence_(F.map(F.pure(1))(i => G.pure(i))) /* assert: MapSequence.mapSequence_
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence_ can be replaced by .traverse_(f) */
  }
}
