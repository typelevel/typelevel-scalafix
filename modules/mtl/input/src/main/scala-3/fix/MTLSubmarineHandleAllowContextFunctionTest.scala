/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl.*
import cats.effect.*
import cats.syntax.all.*

// scalafmt: { maxColumn = 160 }
object HandleAllowContextFunction {

  def methodRaise[F[_]]: Raise[F, String] ?=> F[Unit] = r ?=> r.raise("something went wrong")

  def method[F[_]: Async]: F[Unit] =
    Async[F].unit

  def applicativeErrorSyntax[F[_]: Async] = Handle.allow[String] {
    for {
      _ <- method[F].handleError(_ => ())
      _ <- method[F].handleErrorWith(_ => Async[F].unit)
      _ <- method[F].attempt
      _ <- method[F].attemptNarrow[RuntimeException]
      _ <- method[F].attemptT.value
      _ <- method[F].recover { case _ => () }
      _ <- method[F].recoverWith { case _ => Async[F].unit }
      _ <- method[F].redeem(_ => (), _ => ())
      _ <- method[F].onError { case _ => Async[F].unit }
      _ <- method[F].orElse(Async[F].unit)
      _ <- method[F].adaptErr { case _ => new Exception("") }
      _ <- method[F].orRaise(new Exception(""))
      _ <- method[F].voidError
    } yield ()
  }

  def applicativeErrorDirect[F[_]: Async] = Handle.allow[String] {
    for {
      _ <- Async[F].handleError(method[F])(_ => ())
      _ <- Async[F].handleErrorWith(method[F])(_ => Async[F].unit)
      _ <- Async[F].attempt(method[F])
      _ <- Async[F].attemptNarrow[RuntimeException, Unit](method[F])
      _ <- Async[F].attemptT(method[F]).value
      _ <- Async[F].recover(method[F]) { case _ => () }
      _ <- Async[F].recoverWith(method[F]) { case _ => Async[F].unit }
      _ <- Async[F].redeem(method[F])(_ => (), _ => ())
      _ <- Async[F].onError(method[F]) { case _ => Async[F].unit }
      _ <- Async[F].adaptError(method[F]) { case _ => new Exception("") }
      _ <- Async[F].voidError(method[F])
    } yield ()
  }

  def monadErrorDirect[F[_]: Async] = Handle.allow[String] {
    for {
      _ <- Async[F].ensure(method[F])(new Exception(""))(_ => false)
      _ <- Async[F].ensureOr(method[F])(_ => new Exception(""))(_ => false)
      _ <- Async[F].rethrow(method[F].map(_ => Either.right[Throwable, Unit](())))
      _ <- Async[F].redeemWith(method[F])(_ => Async[F].unit, _ => Async[F].unit)
      _ <- Async[F].attemptTap(method[F])(_ => Async[F].unit)
    } yield ()
  }

  def monadErrorSyntax[F[_]: Async] = Handle.allow[String] {
    for {
      _ <- method[F].ensure(new Exception(""))(_ => false)
      _ <- method[F].ensureOr(_ => new Exception(""))(_ => false)
      _ <- method[F].map(_ => Either.right[Throwable, Unit](())).rethrow
      _ <- method[F].redeemWith(_ => Async[F].unit, _ => Async[F].unit)
      _ <- method[F].attemptTap(_ => Async[F].unit)
      _ <- method[F].adaptError { case _ => new Exception("") }
      _ <- method[F].as("").reject { case _ => new Exception("") }
    } yield ()
  }

  def raiseWithApplicativeErrorSyntax[F[_]: Async] = Handle.allow[String] {
    for {
      _ <- methodRaise[F].handleError(_ => ())                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].handleErrorWith(_ => Async[F].unit)      // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].attempt                                  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].attemptNarrow[RuntimeException]          // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].attemptT.value                           // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].recover { case _ => () }                 // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].recoverWith { case _ => Async[F].unit }  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].redeem(_ => (), _ => ())                 // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].onError { case _ => Async[F].unit }      // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].orElse(Async[F].unit)                    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].adaptErr { case _ => new Exception("") } // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].orRaise(new Exception(""))               // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].voidError                                // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      // ensure other methods aren't affected
      _ <- methodRaise[F].void
      _ <- methodRaise[F].tupleRight("")
    } yield ()
  }

  def raiseWithApplicativeErrorDirect[F[_]: Async] = Handle.allow[String] {
    for {
      _ <- Async[F].handleError(methodRaise[F])(_ => ())                       // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- Async[F].handleErrorWith(methodRaise[F])(_ => Async[F].unit)        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- Async[F].attempt(methodRaise[F])                                    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- Async[F].attemptNarrow[RuntimeException, Unit](methodRaise[F])      // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- Async[F].attemptT(methodRaise[F]).value                             // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- Async[F].recover(methodRaise[F]) { case _ => () }                   // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- Async[F].recoverWith(methodRaise[F]) { case _ => Async[F].unit }    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- Async[F].redeem(methodRaise[F])(_ => (), _ => ())                   // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- Async[F].onError(methodRaise[F]) { case _ => Async[F].unit }        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- Async[F].adaptError(methodRaise[F]) { case _ => new Exception("") } // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- Async[F].voidError(methodRaise[F])                                  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      // ensure other methods aren't affected
      _ <- Async[F].void(methodRaise[F])
      _ <- Async[F].tupleRight(methodRaise[F], "")
    } yield ()
  }

  def raiseWithMonadErrorDirect[F[_]: Async] = Handle.allow[String] {
    for {
      _ <- Async[F].ensure(methodRaise[F])(new Exception(""))(_ => false)
      _ <- Async[F].ensureOr(methodRaise[F])(_ => new Exception(""))(_ => false)
      _ <- Async[F].redeemWith(methodRaise[F])(_ => Async[F].unit, _ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- Async[F].attemptTap(methodRaise[F])(_ => Async[F].unit)                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      // ensure other methods aren't affected
      _ <- Async[F].void(methodRaise[F])
      _ <- Async[F].tupleRight(methodRaise[F], "")
    } yield ()
  }

  def raiseWithMonadErrorSyntax[F[_]: Async] = Handle.allow[String] {
    for {
      _ <- methodRaise[F].ensure(new Exception(""))(_ => false)
      _ <- methodRaise[F].ensureOr(_ => new Exception(""))(_ => false)
      _ <- methodRaise[F].redeemWith(_ => Async[F].unit, _ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].attemptTap(_ => Async[F].unit)                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].adaptError { case _ => new Exception("") }         // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[F].as("").reject { case _ => new Exception("") }
      // ensure other methods aren't affected
      _ <- methodRaise[F].void
      _ <- methodRaise[F].tupleRight("")
    } yield ()
  }

  def withIO = Handle.allow[String] {
    for {
      // applicative error
      _ <- method[IO].handleError(_ => ())
      _ <- method[IO].handleErrorWith(_ => IO.unit)
      _ <- method[IO].attempt
      _ <- method[IO].attemptNarrow[RuntimeException]
      _ <- method[IO].attemptT.value
      _ <- method[IO].recover { case _ => () }
      _ <- method[IO].recoverWith { case _ => IO.unit }
      _ <- method[IO].redeem(_ => (), _ => ())
      _ <- method[IO].onError { case _ => IO.unit }
      _ <- method[IO].orElse(IO.unit)
      _ <- method[IO].adaptErr { case _ => new Exception("") }
      _ <- method[IO].orRaise(new Exception(""))
      _ <- method[IO].voidError
      // monad error
      _ <- method[IO].ensure(new Exception(""))(_ => false)
      _ <- method[IO].ensureOr(_ => new Exception(""))(_ => false)
      _ <- method[IO].redeemWith(_ => IO.unit, _ => IO.unit)
      _ <- method[IO].attemptTap(_ => IO.unit)
      _ <- method[IO].adaptError { case _ => new Exception("") }
      _ <- method[IO].as("").reject { case _ => new Exception("") }
    } yield ()
  }

  def raiseWithIO = Handle.allow[String] {
    for {
      // applicative error
      _ <- methodRaise[IO].handleError(_ => ())                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].handleErrorWith(_ => IO.unit)            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].attempt                                  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].attemptNarrow[RuntimeException]          // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].attemptT.value                           // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].recover { case _ => () }                 // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].recoverWith { case _ => IO.unit }        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].redeem(_ => (), _ => ())                 // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].onError { case _ => IO.unit }            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].orElse(IO.unit)                          // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].adaptErr { case _ => new Exception("") } // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].orRaise(new Exception(""))               // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].voidError                                // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      // monad error
      _ <- methodRaise[IO].ensure(new Exception(""))(_ => false)
      _ <- methodRaise[IO].ensureOr(_ => new Exception(""))(_ => false)
      _ <- methodRaise[IO].redeemWith(_ => IO.unit, _ => IO.unit)     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].attemptTap(_ => IO.unit)                   // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].adaptError { case _ => new Exception("") } // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      _ <- methodRaise[IO].as("").reject { case _ => new Exception("") }
      // ensure other methods aren't affected
      _ <- methodRaise[IO].void
      _ <- methodRaise[IO].debug()
    } yield ()
  }

}
