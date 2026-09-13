/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl.*
import cats.effect.*
import cats.syntax.all.*

// scalafmt: { maxColumn = 160 }
object ContextFunctionValue {

  def valueHandling[F[_]: Async](using Raise[F, String]): F[Unit] = {
    val action: Raise[F, String] ?=> F[Unit] = r ?=> r.raise("something went wrong")

    action.attempt.void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
  }

  def parameterHandling[F[_]: Async](
    action: Raise[F, String] ?=> F[Unit]
  )(using Raise[F, String]): F[Unit] =
    action.recover(_ => ()).void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

  def normalParameter[F[_]: Async](action: Async[F] ?=> F[Unit]): F[Unit] =
    action.attempt.void

}
