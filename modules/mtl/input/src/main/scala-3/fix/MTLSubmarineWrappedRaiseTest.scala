/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl.*
import cats.effect.*
import cats.syntax.all.*

// scalafmt: { maxColumn = 160 }
object WrappedRaise {

  trait Wrapper[A]

  def normal[F[_]: Async](using wrapper: Wrapper[Raise[F, String]]): F[Unit] =
    Async[F].unit

  def handlingNormal[F[_]: Async](using wrapper: Wrapper[Raise[F, String]]): F[Unit] =
    normal[F].attempt.void

}
