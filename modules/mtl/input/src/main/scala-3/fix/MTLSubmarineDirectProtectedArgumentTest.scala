/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl.*
import cats.effect.*

// scalafmt: { maxColumn = 160 }
object DirectProtectedArgument {

  def raiseDependentRecovery[F[_]](error: Throwable)(using r: Raise[F, String]): F[Unit] =
    r.raise(error.getMessage)

  def normalSource[F[_]: Async](using r: Raise[F, String]): F[Unit] = {
    Async[F].handleErrorWith(Async[F].unit)(raiseDependentRecovery[F])
    Async[F].redeemWith(Async[F].unit)(raiseDependentRecovery[F], _ => Async[F].unit)
  }

}
