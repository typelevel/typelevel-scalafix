/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl._
import cats.effect._
import cats.syntax.all._

// scalafmt: { maxColumn = 160 }
object RaiseObject {

  def generic[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    Raise.raise[F, String, Unit]("").attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].recover(Raise.raise[F, String, Unit]("")) { case _ => () } // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").void
  }

  def io(implicit r: Raise[IO, String]): IO[Unit] = {
    Raise.raise[IO, String, Unit]("").onError(_ => IO.unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, String]("").reject(_ => new Exception(""))
    Raise.raise[IO, String, Unit]("").debug()
  }

}
