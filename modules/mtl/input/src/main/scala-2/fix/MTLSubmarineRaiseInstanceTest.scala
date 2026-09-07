/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl._
import cats.effect._
import cats.syntax.all._


// scalafmt: { maxColumn = 160 }
object RaiseInstance {

  def applicativeErrorSyntax[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    // ApplicativeError syntax
    r.raise[String, Unit]("").handleError(_ => ())                // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").handleErrorWith(_ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").attempt                             // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").attemptNarrow[RuntimeException]     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").attemptT.value                      // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").recover(_ => ())                    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").recoverWith(_ => Async[F].unit)     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").redeem(_ => (), _ => ())            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").onError(_ => Async[F].unit)         // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").orElse(Async[F].unit)               // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").adaptErr(_ => new Exception(""))    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").orRaise(new Exception(""))          // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").voidError                           // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    // ensure other methods aren't affected
    r.raise[String, Unit]("").tupleRight("")
    r.raise[String, Unit]("").void
  }

  def monadErrorSyntax[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    r.raise[String, Unit]("").ensure(new Exception(""))(_ => false)
    r.raise[String, Unit]("").ensureOr(_ => new Exception(""))(_ => false)
    r.raise[String, Unit]("").redeemWith(_ => Async[F].unit, _ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").attemptTap(_ => Async[F].unit)                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").adaptError(_ => new Exception(""))                 // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, String]("").reject(_ => new Exception(""))
    // ensure other methods aren't affected
    r.raise[String, Unit]("").tupleRight("")
    r.raise[String, Unit]("").void
  }

  def applicativeErrorDirect[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    Async[F].handleError(r.raise[String, Unit](""))(_ => ())                  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].handleErrorWith(r.raise[String, Unit](""))(_ => Async[F].unit)   // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].attempt(r.raise[String, Unit](""))                               // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].attemptNarrow[RuntimeException, Unit](r.raise[String, Unit]("")) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].attemptT(r.raise[String, Unit]("")).value                        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].recover(r.raise[String, Unit](""))(_ => ())                      // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].recoverWith(r.raise[String, Unit](""))(_ => Async[F].unit)       // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].redeem(r.raise[String, Unit](""))(_ => (), _ => ())              // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].onError(r.raise[String, Unit](""))(_ => Async[F].unit)           // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].adaptError(r.raise[String, Unit](""))(_ => new Exception(""))    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].voidError(r.raise[String, Unit](""))                             // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    // ensure other methods aren't affected
    Async[F].tupleRight(r.raise[String, Unit](""), "")
    Async[F].void(r.raise[String, Unit](""))
  }

  def monadErrorDirect[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    Async[F].ensure(r.raise[String, Unit](""))(new Exception(""))(_ => false)
    Async[F].ensureOr(r.raise[String, Unit](""))(_ => new Exception(""))(_ => false)
    Async[F].redeemWith(r.raise[String, Unit](""))(_ => Async[F].unit, _ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].attemptTap(r.raise[String, Unit](""))(_ => Async[F].unit)                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    // ensure other methods aren't affected
    Async[F].tupleRight(r.raise[String, Unit](""), "")
    Async[F].void(r.raise[String, Unit](""))
  }

  def io(implicit r: Raise[IO, String]): IO[Unit] = {
    // applicative error
    r.raise[String, Unit]("").handleError(_ => ())             // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").handleErrorWith(_ => IO.unit)    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").attempt                          // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").attemptNarrow[RuntimeException]  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").attemptT.value                   // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").recover(_ => ())                 // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").recoverWith(_ => IO.unit)        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").redeem(_ => (), _ => ())         // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").onError(_ => IO.unit)            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").orElse(IO.unit)                  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").adaptErr(_ => new Exception("")) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").orRaise(new Exception(""))       // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").voidError                        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    // monad error
    r.raise[String, Unit]("").ensure(new Exception(""))(_ => false)
    r.raise[String, Unit]("").ensureOr(_ => new Exception(""))(_ => false)
    r.raise[String, Unit]("").redeemWith(_ => IO.unit, _ => IO.unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").attemptTap(_ => IO.unit)               // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, Unit]("").adaptError(_ => new Exception(""))     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    r.raise[String, String]("").reject(_ => new Exception(""))
    // ensure other methods aren't affected
    r.raise[String, Unit]("").void
    r.raise[String, Unit]("").debug()
  }

}
