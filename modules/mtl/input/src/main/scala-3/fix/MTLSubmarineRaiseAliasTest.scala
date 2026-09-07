/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl.*
import cats.effect.*
import cats.syntax.all.*

// scalafmt: { maxColumn = 160 }
object RaiseAlias {

  type AliasedRaise[F[_], E]       = Raise[F, E]
  type ChainedRaiseAlias[F[_], E]  = AliasedRaise[F, E]
  type AliasedHandle[F[_], E]      = Handle[F, E]
  type ChainedHandleAlias[F[_], E] = AliasedHandle[F, E]

  def aliasedRaise[F[_]](using r: AliasedRaise[F, String]): F[Unit] =
    r.raise("something went wrong")

  def chainedRaise[F[_]](using r: ChainedRaiseAlias[F, String]): F[Unit] =
    r.raise("something went wrong")

  def aliasedHandle[F[_]](using h: AliasedHandle[F, String]): F[Unit] =
    h.raise("something went wrong")

  def chainedHandle[F[_]](using h: ChainedHandleAlias[F, String]): F[Unit] =
    h.raise("something went wrong")

  def aliasedRaiseHandling[F[_]: Async](using r: AliasedRaise[F, String]): F[Unit] =
    aliasedRaise[F].attempt.void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

  def chainedRaiseHandling[F[_]: Async](using r: ChainedRaiseAlias[F, String]): F[Unit] =
    Async[F].attempt(chainedRaise[F]).void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

  def aliasedHandleHandling[F[_]: Async](using h: AliasedHandle[F, String]): F[Unit] =
    aliasedHandle[F].recover(_ => ()).void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

  def chainedHandleHandling[F[_]: Async](using h: ChainedHandleAlias[F, String]): F[Unit] =
    Async[F].handleError(chainedHandle[F])(_ => ()).void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

}
