/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl._
import cats.effect._
import cats.syntax.all._

// scalafmt: { maxColumn = 160 }
object HandleAllowF {

  def methodRaise[F[_]](implicit r: Raise[F, String]): F[Unit] =
    r.raise("something went wrong")

  def method[F[_]: Async]: F[Unit] =
    Async[F].unit

  def safeGeneric[F[_]: Async] = Handle.allowF[F, String] { _ =>
    for {
      _ <- method[F].attempt
      _ <- Async[F].recover(method[F]) { case _ => () }
      _ <- method[F].ensure(new Exception(""))(_ => false)
    } yield ()
  }

  def attemptedGeneric[F[_]: Async]: F[Either[String, Unit]] =
    Handle
      .allowF[F, String] { implicit h =>
        methodRaise[F]
      }
      .attempt

  def dangerousGeneric[F[_]: Async] = Handle.allowF[F, String] { implicit h =>
    for {
      _ <- methodRaise[F].attempt                            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- Async[F].recover(methodRaise[F]) { case _ => () } // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].ensure(new Exception(""))(_ => false)
      _ <- methodRaise[F].void
    } yield ()
  }

  def io = Handle.allowF[IO, String] { implicit h =>
    method[IO].onError(_ => IO.unit)
    methodRaise[IO].onError(_ => IO.unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[IO].debug()
  }

}
