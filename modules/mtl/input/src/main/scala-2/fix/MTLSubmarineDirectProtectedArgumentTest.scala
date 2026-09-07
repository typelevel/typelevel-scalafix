/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl._
import cats.effect._


// scalafmt: { maxColumn = 160 }
object DirectProtectedArgument {

  def raiseDependentRecovery[F[_]](error: Throwable)(implicit r: Raise[F, String]): F[Unit] =
    r.raise(error.getMessage)

  def normalSource[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    Async[F].handleErrorWith(Async[F].unit)(raiseDependentRecovery[F])
    Async[F].redeemWith(Async[F].unit)(raiseDependentRecovery[F], _ => Async[F].unit)
  }

}
