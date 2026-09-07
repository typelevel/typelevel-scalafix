/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl.*
import cats.mtl.syntax.raise.*
import cats.effect.*
import cats.syntax.all.*

// scalafmt: { maxColumn = 160 }
object RaiseSyntax {

  def generic[F[_]: Async](using Raise[F, String]): F[Unit] = {
    "".raise[F, Unit].attempt                        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].handleError("".raise[F, Unit])(_ => ()) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].void
  }

  def io(using Raise[IO, String]): IO[Unit] = {
    "".raise[IO, Unit].onError(_ => IO.unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, String].reject(_ => new Exception(""))
    "".raise[IO, Unit].debug()
  }

}
