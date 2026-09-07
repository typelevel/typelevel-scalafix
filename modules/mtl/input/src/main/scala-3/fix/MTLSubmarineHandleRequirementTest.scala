/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl.*
import cats.effect.*
import cats.syntax.all.*

// scalafmt: { maxColumn = 160 }
object HandleRequirement {

  def method[F[_]](using h: Handle[F, String]): F[Unit] =
    h.raise("something went wrong")

  def contextFunctionMethod[F[_]]: Handle[F, String] ?=> F[Unit] =
    summon[Handle[F, String]].raise("something went wrong")

  def normalContextFunctionMethod[F[_]]: Async[F] ?=> F[Unit] =
    summon[Async[F]].unit

  def applicativeErrorSyntax[F[_]: Async](using h: Handle[F, String]): F[Unit] =
    method[F].attempt.void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

  def applicativeErrorDirect[F[_]: Async](using h: Handle[F, String]): F[Unit] =
    Async[F].attempt(method[F]).void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

  def io(using h: Handle[IO, String]): IO[Unit] =
    method[IO].attempt.void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

  def contextFunction = Handle.allow[String] {
    contextFunctionMethod[IO].attempt.void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
  }

  def normalContextFunction(using Async[IO]): IO[Unit] =
    normalContextFunctionMethod[IO].attempt.void

}
