/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl.*
import cats.effect.*
import cats.syntax.all.*

// scalafmt: { maxColumn = 160 }
object RaiseObject {

  def generic[F[_]: Async](using Raise[F, String]): F[Unit] = {
    Raise.raise[F, String, Unit]("").attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].recover(Raise.raise[F, String, Unit]("")) { case _ => () } // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").void
  }

  def io(using Raise[IO, String]): IO[Unit] = {
    Raise.raise[IO, String, Unit]("").onError(_ => IO.unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, String]("").reject(_ => new Exception(""))
    Raise.raise[IO, String, Unit]("").debug()
  }

}
