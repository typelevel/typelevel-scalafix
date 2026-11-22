package fix

import cats.Parallel
import cats.Foldable
import cats.Reducible
import cats.data.NonEmptyList
import cats.syntax.all._

object VoidTests {
  val sequence         = List(Option(1)).sequenceVoid
  val traverse         = List(1).traverseVoid(i => Option(i))
  val nonEmptySequence = NonEmptyList.one(Option(1)).nonEmptySequenceVoid
  val nonEmptyTraverse = NonEmptyList.one(1).nonEmptyTraverseVoid(i => Option(i))

  val typeclassSequence = Foldable[List].sequenceVoid(List(Option(1)))
  val typeclassTraverse = Foldable[List].traverseVoid(List(1))(i => Option(i))
  val typeclassNonEmptySequence = Reducible[NonEmptyList].nonEmptySequenceVoid(
    NonEmptyList.one(Option(1))
  )
  val typeclassNonEmptyTraverse =
    Reducible[NonEmptyList].nonEmptyTraverseVoid(NonEmptyList.one(1))(i => Option(i))

  val parSequence = Parallel.parSequenceVoid(List(Right(1): Either[String, Int]))
  val parTraverse = Parallel.parTraverseVoid(List(1))(i => Right(i): Either[String, Int])
  val parNonEmptySequence = Parallel.parNonEmptySequenceVoid(
    NonEmptyList.one(Right(1): Either[String, Int])
  )
  val parNonEmptyTraverse =
    Parallel.parNonEmptyTraverseVoid(NonEmptyList.one(1))(i => Right(i): Either[String, Int])

  def `sequence_`(value: Int): Int = value
  val custom                       = `sequence_`(1)
}
