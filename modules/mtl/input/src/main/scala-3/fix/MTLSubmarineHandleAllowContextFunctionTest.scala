/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl.*
import cats.effect.*
import cats.syntax.all.*

// scalafmt: { maxColumn = 160 }
object HandleAllowContextFunction {

  def methodRaise[F[_]]: Raise[F, String] ?=> F[Unit] =
    summon[Raise[F, String]].raise("something went wrong")

  def method[F[_]: Async]: F[Unit] =
    Async[F].unit

  def safeGeneric[F[_]: Async] = Handle.allow[String] {
    method[F].attempt
  }

  def dangerousGeneric[F[_]: Async] = Handle.allow[String] {
    methodRaise[F].attempt                        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].handleError(methodRaise[F])(_ => ()) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[F].ensure(new Exception(""))(_ => false)
    methodRaise[F].void
  }

  def io = Handle.allow[String] {
    methodRaise[IO].onError(_ => IO.unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[IO].debug()
  }

}
