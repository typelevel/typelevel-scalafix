/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl._
import cats.mtl.syntax.raise._
import cats.effect._
import cats.syntax.all._

// scalafmt: { maxColumn = 160 }
object RaiseSyntax {

  def generic[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    "".raise[F, Unit].attempt                        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].handleError("".raise[F, Unit])(_ => ()) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].void
  }

  def io(implicit r: Raise[IO, String]): IO[Unit] = {
    "".raise[IO, Unit].onError(_ => IO.unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, String].reject(_ => new Exception(""))
    "".raise[IO, Unit].debug()
  }

}
