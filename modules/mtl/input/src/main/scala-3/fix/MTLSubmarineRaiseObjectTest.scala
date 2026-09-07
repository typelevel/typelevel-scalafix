/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl.*
import cats.effect.*
import cats.syntax.all.*

// scalafmt: { maxColumn = 160 }
object RaiseObject {

  def applicativeErrorSyntax[F[_]: Async](using r: Raise[F, String]): F[Unit] = {
    Raise.raise[F, String, Unit]("").handleError(_ => ())                // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").handleErrorWith(_ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").attempt                             // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").attemptNarrow[RuntimeException]     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").attemptT.value                      // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").recover(_ => ())                    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").recoverWith(_ => Async[F].unit)     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").redeem(_ => (), _ => ())            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").onError(_ => Async[F].unit)         // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").orElse(Async[F].unit)               // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").adaptErr(_ => new Exception(""))    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").orRaise(new Exception(""))          // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").voidError                           // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    // ensure other methods aren't affected
    Raise.raise[F, String, Unit]("").tupleRight("")
    Raise.raise[F, String, Unit]("").void
  }

  def monadErrorSyntax[F[_]: Async](using r: Raise[F, String]): F[Unit] = {
    Raise.raise[F, String, Unit]("").ensure(new Exception(""))(_ => false)
    Raise.raise[F, String, Unit]("").ensureOr(_ => new Exception(""))(_ => false)
    Raise.raise[F, String, Unit]("").redeemWith(_ => Async[F].unit, _ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").attemptTap(_ => Async[F].unit)                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, Unit]("").adaptError(_ => new Exception(""))                 // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[F, String, String]("").reject(_ => new Exception(""))
    // ensure other methods aren't affected
    Raise.raise[F, String, Unit]("").tupleRight("")
    Raise.raise[F, String, Unit]("").void
  }

  def applicativeErrorDirect[F[_]: Async](using r: Raise[F, String]): F[Unit] = {
    Async[F].handleError(Raise.raise[F, String, Unit](""))(_ => ())                  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].handleErrorWith(Raise.raise[F, String, Unit](""))(_ => Async[F].unit)   // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].attempt(Raise.raise[F, String, Unit](""))                               // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].attemptNarrow[RuntimeException, Unit](Raise.raise[F, String, Unit]("")) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].attemptT(Raise.raise[F, String, Unit]("")).value                        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].recover(Raise.raise[F, String, Unit](""))(_ => ())                      // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].recoverWith(Raise.raise[F, String, Unit](""))(_ => Async[F].unit)       // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].redeem(Raise.raise[F, String, Unit](""))(_ => (), _ => ())              // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].onError(Raise.raise[F, String, Unit](""))(_ => Async[F].unit)           // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].adaptError(Raise.raise[F, String, Unit](""))(_ => new Exception(""))    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].voidError(Raise.raise[F, String, Unit](""))                             // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    // ensure other methods aren't affected
    Async[F].tupleRight(Raise.raise[F, String, Unit](""), "")
    Async[F].void(Raise.raise[F, String, Unit](""))
  }

  def monadErrorDirect[F[_]: Async](using r: Raise[F, String]): F[Unit] = {
    Async[F].ensure(Raise.raise[F, String, Unit](""))(new Exception(""))(_ => false)
    Async[F].ensureOr(Raise.raise[F, String, Unit](""))(_ => new Exception(""))(_ => false)
    Async[F].redeemWith(Raise.raise[F, String, Unit](""))(_ => Async[F].unit, _ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].attemptTap(Raise.raise[F, String, Unit](""))(_ => Async[F].unit)                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    // ensure other methods aren't affected
    Async[F].tupleRight(Raise.raise[F, String, Unit](""), "")
    Async[F].void(Raise.raise[F, String, Unit](""))
  }

  def io(using r: Raise[IO, String]): IO[Unit] = {
    // applicative error
    Raise.raise[IO, String, Unit]("").handleError(_ => ())             // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, Unit]("").handleErrorWith(_ => IO.unit)    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, Unit]("").attempt                          // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, Unit]("").attemptNarrow[RuntimeException]  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, Unit]("").attemptT.value                   // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, Unit]("").recover(_ => ())                 // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, Unit]("").recoverWith(_ => IO.unit)        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, Unit]("").redeem(_ => (), _ => ())         // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, Unit]("").onError(_ => IO.unit)            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, Unit]("").orElse(IO.unit)                  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, Unit]("").adaptErr(_ => new Exception("")) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, Unit]("").orRaise(new Exception(""))       // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, Unit]("").voidError                        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    // monad error
    Raise.raise[IO, String, Unit]("").ensure(new Exception(""))(_ => false)
    Raise.raise[IO, String, Unit]("").ensureOr(_ => new Exception(""))(_ => false)
    Raise.raise[IO, String, Unit]("").redeemWith(_ => IO.unit, _ => IO.unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, Unit]("").attemptTap(_ => IO.unit)               // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, Unit]("").adaptError(_ => new Exception(""))     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Raise.raise[IO, String, String]("").reject(_ => new Exception(""))
    // ensure other methods aren't affected
    Raise.raise[IO, String, Unit]("").void
    Raise.raise[IO, String, Unit]("").product(Raise.raise[IO, String, Unit](""))
    Raise.raise[IO, String, Unit]("").debug()
  }

}
