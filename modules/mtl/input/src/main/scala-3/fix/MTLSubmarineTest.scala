/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl.*
import cats.mtl.syntax.raise.*
import cats.effect.*
import cats.syntax.all.*

// scalafmt: { maxColumn = 160 }
object MTLSubmarineTest {

  object RaiseInstance {

    def applicativeErrorSyntax[F[_]: Async](using r: Raise[F, String]): F[Unit] = {
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

    def monadErrorSyntax[F[_]: Async](using r: Raise[F, String]): F[Unit] = {
      r.raise[String, Unit]("").ensure(new Exception(""))(_ => false)
      r.raise[String, Unit]("").ensureOr(_ => new Exception(""))(_ => false)
      r.raise[String, Unit]("").redeemWith(_ => Async[F].unit, _ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      r.raise[String, Unit]("").attemptTap(_ => Async[F].unit)                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      r.raise[String, Unit]("").adaptError(_ => new Exception(""))                 // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      r.raise[String, Unit]("").reject(_ => new Exception(""))
      // ensure other methods aren't affected
      r.raise[String, Unit]("").tupleRight("")
      r.raise[String, Unit]("").void
    }

    def applicativeErrorDirect[F[_]: Async](using r: Raise[F, String]): F[Unit] = {
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

    def monadErrorDirect[F[_]: Async](using r: Raise[F, String]): F[Unit] = {
      Async[F].ensure(r.raise[String, Unit](""))(new Exception(""))(_ => false)
      Async[F].ensureOr(r.raise[String, Unit](""))(_ => new Exception(""))(_ => false)
      Async[F].redeemWith(r.raise[String, Unit](""))(_ => Async[F].unit, _ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      Async[F].attemptTap(r.raise[String, Unit](""))(_ => Async[F].unit)                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      // ensure other methods aren't affected
      Async[F].tupleRight(r.raise[String, Unit](""), "")
      Async[F].void(r.raise[String, Unit](""))
    }

    def io(using r: Raise[IO, String]): IO[Unit] = {
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
      r.raise[String, Unit]("").reject(_ => new Exception(""))
      // ensure other methods aren't affected
      r.raise[String, Unit]("").void
      r.raise[String, Unit]("").product(r.raise[String, Unit](""))
      r.raise[String, Unit]("").debug()
    }

  }

  object HandleRequirement {

    def method[F[_]](using h: Handle[F, String]): F[Unit] =
      h.raise("something went wrong")

    def contextFunctionMethod[F[_]]: Handle[F, String] ?=> F[Unit] =
      summon[Handle[F, String]].raise("something went wrong")

    def applicativeErrorSyntax[F[_]: Async](using h: Handle[F, String]): F[Unit] =
      method[F].attempt.void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

    def applicativeErrorDirect[F[_]: Async](using h: Handle[F, String]): F[Unit] =
      Async[F].attempt(method[F]).void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

    def io(using h: Handle[IO, String]): IO[Unit] =
      method[IO].attempt.void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

    def contextFunction = Handle.allow[String] {
      contextFunctionMethod[IO].attempt.void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    }

  }

  object CustomRaiseSubtype {

    trait CustomRaise[F[_], E]   extends Raise[F, E]
    trait IndirectRaise[F[_], E] extends CustomRaise[F, E]
    trait CustomHandle[F[_], E]  extends Handle[F, E]

    def customRaise[F[_]](using r: CustomRaise[F, String]): F[Unit] =
      r.raise("something went wrong")

    def indirectRaise[F[_]](using r: IndirectRaise[F, String]): F[Unit] =
      r.raise("something went wrong")

    def customHandle[F[_]](using h: CustomHandle[F, String]): F[Unit] =
      h.raise("something went wrong")

    def customRaiseHandling[F[_]: Async](using r: CustomRaise[F, String]): F[Unit] =
      customRaise[F].attempt.void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

    def indirectRaiseHandling[F[_]: Async](using r: IndirectRaise[F, String]): F[Unit] =
      Async[F].attempt(indirectRaise[F]).void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

    def customHandleHandling[F[_]: Async](using h: CustomHandle[F, String]): F[Unit] =
      customHandle[F].handleErrorWith(_ => Async[F].unit).void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

  }

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

  object DirectProtectedArgument {

    def raiseDependentRecovery[F[_]](error: Throwable)(using r: Raise[F, String]): F[Unit] =
      r.raise(error.getMessage)

    def normalSource[F[_]: Async](using r: Raise[F, String]): F[Unit] = {
      Async[F].handleErrorWith(Async[F].unit)(raiseDependentRecovery[F])
      Async[F].redeemWith(Async[F].unit)(raiseDependentRecovery[F], _ => Async[F].unit)
    }

  }

  object ContextFunctionValue {

    def valueHandling[F[_]: Async](using Raise[F, String]): F[Unit] = {
      val action: Raise[F, String] ?=> F[Unit] = r ?=> r.raise("something went wrong")

      action.attempt.void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    }

    def parameterHandling[F[_]: Async](
      action: Raise[F, String] ?=> F[Unit]
    )(using Raise[F, String]): F[Unit] =
      action.recover(_ => ()).void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling

  }

  object EffectPropagation {

    def methodRaise[F[_]](using r: Raise[F, String]): F[Unit] =
      r.raise("something went wrong")

    def syntax[F[_]: Async](using r: Raise[F, String]): F[Unit] = {
      methodRaise[F].map(identity).attempt                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      methodRaise[F].void.recover(_ => ())                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      methodRaise[F].as(()).attempt                            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      (Async[F].unit *> methodRaise[F]).attempt                // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      (methodRaise[F] <* Async[F].unit).attempt                // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      (Async[F].unit >> methodRaise[F]).attempt                // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      methodRaise[F].product(Async[F].unit).attempt            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      Async[F].unit.product(methodRaise[F]).attempt            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      Async[F].unit.map2(methodRaise[F])((_, _) => ()).attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      methodRaise[F].flatMap(_ => Async[F].unit).attempt       // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      Async[F].unit.flatMap(_ => methodRaise[F]).attempt       // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      Async[F].unit.flatTap(_ => methodRaise[F]).attempt       // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      (Async[F].unit >>= (_ => methodRaise[F])).attempt        // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      Async[F].unit.mproduct(_ => methodRaise[F]).attempt      // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      Async[F].unit.map(identity).attempt
      (for { // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
        _ <- Async[F].unit
        _ <- methodRaise[F]
      } yield ()).attempt.void
    }

    def direct[F[_]: Async](using r: Raise[F, String]): F[Unit] = {
      Async[F].attempt(Async[F].map(methodRaise[F])(identity)).void               // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      Async[F].attempt(Async[F].flatMap(Async[F].unit)(_ => methodRaise[F])).void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    }

  }

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
      Raise.raise[F, String, Unit]("").reject(_ => new Exception(""))
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
      Raise.raise[IO, String, Unit]("").reject(_ => new Exception(""))
      // ensure other methods aren't affected
      Raise.raise[IO, String, Unit]("").void
      Raise.raise[IO, String, Unit]("").product(Raise.raise[IO, String, Unit](""))
      Raise.raise[IO, String, Unit]("").debug()
    }

  }

  object RaiseSyntax {

    def applicativeErrorSyntax[F[_]: Async](using Raise[F, String]): F[Unit] = {
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

    def monadErrorSyntax[F[_]: Async](using Raise[F, String]): F[Unit] = {
      "".raise[F, Unit].ensure(new Exception(""))(_ => false)
      "".raise[F, Unit].ensureOr(_ => new Exception(""))(_ => false)
      "".raise[F, Unit].redeemWith(_ => Async[F].unit, _ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      "".raise[F, Unit].attemptTap(_ => Async[F].unit)                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      "".raise[F, Unit].adaptError(_ => new Exception(""))                 // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      "".raise[F, Unit].reject(_ => new Exception(""))
      // ensure other methods aren't affected
      "".raise[F, Unit].tupleRight("")
      "".raise[F, Unit].void
    }

    def applicativeErrorDirect[F[_]: Async](using Raise[F, String]): F[Unit] = {
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

    def monadErrorDirect[F[_]: Async](using Raise[F, String]): F[Unit] = {
      Async[F].ensure("".raise[F, Unit])(new Exception(""))(_ => false)
      Async[F].ensureOr("".raise[F, Unit])(_ => new Exception(""))(_ => false)
      Async[F].redeemWith("".raise[F, Unit])(_ => Async[F].unit, _ => Async[F].unit) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      Async[F].attemptTap("".raise[F, Unit])(_ => Async[F].unit)                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
      // ensure other methods aren't affected
      Async[F].tupleRight("".raise[F, Unit], "")
      Async[F].void("".raise[F, Unit])
    }

    def io(using Raise[IO, String]): IO[Unit] = {
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
      "".raise[IO, Unit].reject(_ => new Exception(""))
      // ensure other methods aren't affected
      "".raise[IO, Unit].void
      "".raise[IO, Unit].debug()
    }

  }

  object HandleAllowGiven {

    def methodRaise[F[_]](using r: Raise[F, String]): F[Unit] =
      r.raise("something went wrong")

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
        _ <- method[F].reject { case _ => new Exception("") }
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
        _ <- methodRaise[F].product(methodRaise[F])
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
        _ <- Async[F].product(methodRaise[F], methodRaise[F])
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
        _ <- methodRaise[F].reject { case _ => new Exception("") }
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
        _ <- method[IO].reject { case _ => new Exception("") }
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
        _ <- methodRaise[IO].reject { case _ => new Exception("") }
        // ensure other methods aren't affected
        _ <- methodRaise[IO].void
        _ <- methodRaise[IO].debug()
      } yield ()
    }

  }

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
        _ <- method[F].reject { case _ => new Exception("") }
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
        _ <- methodRaise[F].reject { case _ => new Exception("") }
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
        _ <- method[IO].reject { case _ => new Exception("") }
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
        _ <- methodRaise[IO].reject { case _ => new Exception("") }
        // ensure other methods aren't affected
        _ <- methodRaise[IO].void
        _ <- methodRaise[IO].debug()
      } yield ()
    }

  }

}
