/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl._
import cats.effect._
import cats.syntax.all._


// scalafmt: { maxColumn = 160 }
object HandleRequirement {

  def method[F[_]](implicit h: Handle[F, String]): F[Unit] =
    h.raise("something went wrong")

  def applicativeErrorSyntax[F[_]: Async](implicit h: Handle[F, String]): F[Unit] =
    method[F].attempt.void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

  def applicativeErrorDirect[F[_]: Async](implicit h: Handle[F, String]): F[Unit] =
    Async[F].attempt(method[F]).void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

  def io(implicit h: Handle[IO, String]): IO[Unit] =
    method[IO].attempt.void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

}
