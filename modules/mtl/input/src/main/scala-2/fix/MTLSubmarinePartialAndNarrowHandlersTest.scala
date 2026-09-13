/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl._
import cats.effect._
import cats.syntax.all._

import java.io.IOException

// scalafmt: { maxColumn = 160 }
object PartialAndNarrowHandlers {

  def methodRaise[F[_]](implicit r: Raise[F, String]): F[Unit] =
    r.raise("boom")

  def precise[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    methodRaise[F].attemptNarrow[RuntimeException] // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[F].attemptNarrow[IOException]
    Async[F].attemptNarrow[IOException, Unit](methodRaise[F])
    methodRaise[F].recover { case _: RuntimeException => () } // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[F].recover { case _: IOException => () }
    Async[F].recover(methodRaise[F]) { case _: IOException => () }
    methodRaise[F].recover { case _ => () } // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
  }

  def unknown[F[_]: Async](handler: PartialFunction[Throwable, Unit])(implicit
    r: Raise[F, String]
  ): F[Unit] =
    methodRaise[F].recover(handler) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

}
