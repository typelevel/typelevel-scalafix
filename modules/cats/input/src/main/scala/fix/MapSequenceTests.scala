/*
rule = TypelevelMapSequence
 */
package fix

import cats._
import cats.data.Const
import cats.data.NonEmptyList
import cats.syntax.all._

object MapSequenceTests {
  def listMapSequence = {
    List(1, 2, 3).map(Either.right[Unit, Int]).sequence /* assert: TypelevelMapSequence.mapSequence
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence can be replaced by .traverse(f) */
  }

  def nelMapSequence = {
    NonEmptyList /* assert: TypelevelMapSequence.mapSequence
    ^
    .map(f).sequence can be replaced by .traverse(f) */
      .one(1)
      .map(Const.apply[Int, String])
      .sequence
  }

  def fMapSequence[F[_], G[_]](implicit
    F: Traverse[F] with Applicative[F],
    G: Applicative[G]
  ) = {
    F.pure(1).map(i => G.pure(i)).sequence /* assert: TypelevelMapSequence.mapSequence
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence can be replaced by .traverse(f) */

    F.sequence(F.pure(1).map(i => G.pure(i))) /* assert: TypelevelMapSequence.mapSequence
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence can be replaced by .traverse(f) */

    F.map(F.pure(1))(i => G.pure(i)).sequence /* assert: TypelevelMapSequence.mapSequence
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence can be replaced by .traverse(f) */

    F.sequence(F.map(F.pure(1))(i => G.pure(i))) /* assert: TypelevelMapSequence.mapSequence
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence can be replaced by .traverse(f) */
  }

  def listMapSequence_ = {
    List(1, 2, 3) /* assert: TypelevelMapSequence.mapSequence_
    ^
    .map(f).sequence_ can be replaced by .traverse_(f) */
      .map(Either.right[Unit, Int])
      .sequence_
  }

  def nelMapSequence_ = {
    NonEmptyList /* assert: TypelevelMapSequence.mapSequence_
    ^
    .map(f).sequence_ can be replaced by .traverse_(f) */
      .one(1)
      .map(Const.of[String](_))
      .sequence_
  }

  def fMapSequence_[F[_], G[_]](implicit
    F: Traverse[F] with Applicative[F],
    G: Applicative[G]
  ) = {
    F.pure(1).map(i => G.pure(i)).sequence_ /* assert: TypelevelMapSequence.mapSequence_
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence_ can be replaced by .traverse_(f) */

    F.sequence_(F.pure(1).map(i => G.pure(i))) /* assert: TypelevelMapSequence.mapSequence_
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence_ can be replaced by .traverse_(f) */

    F.map(F.pure(1))(i => G.pure(i)).sequence_ /* assert: TypelevelMapSequence.mapSequence_
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence_ can be replaced by .traverse_(f) */

    F.sequence_(F.map(F.pure(1))(i => G.pure(i))) /* assert: TypelevelMapSequence.mapSequence_
    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(f).sequence_ can be replaced by .traverse_(f) */
  }
}
