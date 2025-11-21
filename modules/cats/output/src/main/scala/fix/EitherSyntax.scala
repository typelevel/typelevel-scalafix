package fix

import cats.syntax.either._
object EitherSyntax {

  def shouldBeReplaced = {
    Either.unit
  }

  def shouldBeIgnored = {
    Right(1)
  }

}
