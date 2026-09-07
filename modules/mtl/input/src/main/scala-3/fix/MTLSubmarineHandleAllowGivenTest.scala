/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl.*
import cats.effect.*
import cats.syntax.all.*

// scalafmt: { maxColumn = 160 }
object HandleAllowGiven {

  def methodRaise[F[_]](using r: Raise[F, String]): F[Unit] =
    r.raise("something went wrong")

  def method[F[_]: Async]: F[Unit] =
    Async[F].unit

  def safeGeneric[F[_]: Async] = Handle.allow[String] {
    for {
      _ <- method[F].attempt
      _ <- Async[F].recover(method[F]) { case _ => () }
      _ <- method[F].ensure(new Exception(""))(_ => false)
    } yield ()
  }

  def dangerousGeneric[F[_]: Async] = Handle.allow[String] {
    for {
      _ <- methodRaise[F].attempt                            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- Async[F].recover(methodRaise[F]) { case _ => () } // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].ensure(new Exception(""))(_ => false)
      _ <- methodRaise[F].void
    } yield ()
  }

  def io = Handle.allow[String] {
    method[IO].onError(_ => IO.unit)
    methodRaise[IO].onError(_ => IO.unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[IO].debug()
  }

}
