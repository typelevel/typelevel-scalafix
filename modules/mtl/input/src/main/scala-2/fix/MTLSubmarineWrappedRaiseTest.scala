/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl._
import cats.effect._
import cats.syntax.all._


// scalafmt: { maxColumn = 160 }
object WrappedRaise {

  trait Wrapper[A]

  def normal[F[_]: Async](implicit wrapper: Wrapper[Raise[F, String]]): F[Unit] =
    Async[F].unit

  def handlingNormal[F[_]: Async](implicit wrapper: Wrapper[Raise[F, String]]): F[Unit] =
    normal[F].attempt.void

}
