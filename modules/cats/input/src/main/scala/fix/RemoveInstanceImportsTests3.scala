/*
rule = TypelevelCatsRemoveInstanceImports
 */
package fix

import cats._
import cats.implicits._

object RemoveInstanceImportsTests3 {

  def doSomethingMonadic[F[_]: Monad](x: F[Int]): F[String] =
    for {
      a <- x
      b <- Monad[F].pure("hi")
      c <- Monad[F].pure("hey")
    } yield a.toString + b + c

}
