/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl._
import cats.mtl.syntax.raise._
import cats.effect._
import cats.syntax.all._


// scalafmt: { maxColumn = 160 }
object RaiseSyntax {

  def applicativeErrorSyntax[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    // ApplicativeError syntax
    "".raise[F, Unit].handleError(_ => ())                // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].handleErrorWith(_ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].attempt                             // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].attemptNarrow[RuntimeException]     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].attemptT.value                      // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].recover(_ => ())                    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].recoverWith(_ => Async[F].unit)     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].redeem(_ => (), _ => ())            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].onError(_ => Async[F].unit)         // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].orElse(Async[F].unit)               // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].adaptErr(_ => new Exception(""))    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].orRaise(new Exception(""))          // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].voidError                           // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    // ensure other methods aren't affected
    "".raise[F, Unit].tupleRight("")
    "".raise[F, Unit].void
  }

  def monadErrorSyntax[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    "".raise[F, Unit].ensure(new Exception(""))(_ => false)
    "".raise[F, Unit].ensureOr(_ => new Exception(""))(_ => false)
    "".raise[F, Unit].redeemWith(_ => Async[F].unit, _ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].attemptTap(_ => Async[F].unit)                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, Unit].adaptError(_ => new Exception(""))                 // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[F, String].reject(_ => new Exception(""))
    // ensure other methods aren't affected
    "".raise[F, Unit].tupleRight("")
    "".raise[F, Unit].void
  }

  def applicativeErrorDirect[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    Async[F].handleError("".raise[F, Unit])(_ => ())                  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].handleErrorWith("".raise[F, Unit])(_ => Async[F].unit)   // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].attempt("".raise[F, Unit])                               // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].attemptNarrow[RuntimeException, Unit]("".raise[F, Unit]) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].attemptT("".raise[F, Unit]).value                        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].recover("".raise[F, Unit])(_ => ())                      // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].recoverWith("".raise[F, Unit])(_ => Async[F].unit)       // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].redeem("".raise[F, Unit])(_ => (), _ => ())              // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].onError("".raise[F, Unit])(_ => Async[F].unit)           // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].adaptError("".raise[F, Unit])(_ => new Exception(""))    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].voidError("".raise[F, Unit])                             // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    // ensure other methods aren't affected
    Async[F].tupleRight("".raise[F, Unit], "")
    Async[F].void("".raise[F, Unit])
  }

  def monadErrorDirect[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    Async[F].ensure("".raise[F, Unit])(new Exception(""))(_ => false)
    Async[F].ensureOr("".raise[F, Unit])(_ => new Exception(""))(_ => false)
    Async[F].redeemWith("".raise[F, Unit])(_ => Async[F].unit, _ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].attemptTap("".raise[F, Unit])(_ => Async[F].unit)                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    // ensure other methods aren't affected
    Async[F].tupleRight("".raise[F, Unit], "")
    Async[F].void("".raise[F, Unit])
  }

  def io(implicit r: Raise[IO, String]): IO[Unit] = {
    // applicative error
    "".raise[IO, Unit].handleError(_ => ())             // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, Unit].handleErrorWith(_ => IO.unit)    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, Unit].attempt                          // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, Unit].attemptNarrow[RuntimeException]  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, Unit].attemptT.value                   // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, Unit].recover(_ => ())                 // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, Unit].recoverWith(_ => IO.unit)        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, Unit].redeem(_ => (), _ => ())         // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, Unit].onError(_ => IO.unit)            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, Unit].orElse(IO.unit)                  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, Unit].adaptErr(_ => new Exception("")) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, Unit].orRaise(new Exception(""))       // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, Unit].voidError                        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    // monad error
    "".raise[IO, Unit].ensure(new Exception(""))(_ => false)
    "".raise[IO, Unit].ensureOr(_ => new Exception(""))(_ => false)
    "".raise[IO, Unit].redeemWith(_ => IO.unit, _ => IO.unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, Unit].attemptTap(_ => IO.unit)               // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, Unit].adaptError(_ => new Exception(""))     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    "".raise[IO, String].reject(_ => new Exception(""))
    // ensure other methods aren't affected
    "".raise[IO, Unit].void
    "".raise[IO, Unit].debug()
  }

}
