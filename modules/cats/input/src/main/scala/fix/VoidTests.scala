/*
rule = TypelevelVoid
 */
package fix

import cats.Parallel
import cats.Foldable
import cats.Reducible
import cats.data.NonEmptyList
import cats.syntax.all._

object VoidTests {
  val sequence         = List(Option(1)).sequence_
  val traverse         = List(1).traverse_(i => Option(i))
  val nonEmptySequence = NonEmptyList.one(Option(1)).nonEmptySequence_
  val nonEmptyTraverse = NonEmptyList.one(1).nonEmptyTraverse_(i => Option(i))

  val typeclassSequence = Foldable[List].sequence_(List(Option(1)))
  val typeclassTraverse = Foldable[List].traverse_(List(1))(i => Option(i))
  val typeclassNonEmptySequence = Reducible[NonEmptyList].nonEmptySequence_(
    NonEmptyList.one(Option(1))
  )
  val typeclassNonEmptyTraverse =
    Reducible[NonEmptyList].nonEmptyTraverse_(NonEmptyList.one(1))(i => Option(i))

  val parSequence = Parallel.parSequence_(List(Right(1): Either[String, Int]))
  val parTraverse = Parallel.parTraverse_(List(1))(i => Right(i): Either[String, Int])
  val parNonEmptySequence = Parallel.parNonEmptySequence_(
    NonEmptyList.one(Right(1): Either[String, Int])
  )
  val parNonEmptyTraverse =
    Parallel.parNonEmptyTraverse_(NonEmptyList.one(1))(i => Right(i): Either[String, Int])

  def `sequence_`(value: Int): Int = value
  val custom                       = `sequence_`(1)
}
