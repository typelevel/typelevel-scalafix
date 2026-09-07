/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl._
import cats.effect._
import cats.effect.implicits._
import cats.syntax.all._

// scalafmt: { maxColumn = 160 }
object EffectPropagation {

  def methodRaise[F[_]](implicit r: Raise[F, String]): F[Unit] =
    r.raise("something went wrong")

  def syntax[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    methodRaise[F].map(identity).attempt                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[F].void.recover(_ => ())                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[F].as(()).attempt                            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    (Async[F].unit *> methodRaise[F]).attempt                // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    (methodRaise[F] <* Async[F].unit).attempt                // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    (Async[F].unit >> methodRaise[F]).attempt                // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[F].product(Async[F].unit).attempt            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].unit.product(methodRaise[F]).attempt            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[F].productL(Async[F].unit).attempt           // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].unit.productR(methodRaise[F]).attempt           // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].unit.map2(methodRaise[F])((_, _) => ()).attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[F].fproduct(identity).attempt                // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[F].fproductLeft(identity).attempt            // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[F].tupleLeft(()).attempt                     // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[F].tupleRight(()).attempt                    // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[F]
      .as((_: Unit) => ())
      .<&>(Async[F].unit)
      .attempt                                          // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    methodRaise[F].flatMap(_ => Async[F].unit).attempt  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].unit.flatMap(_ => methodRaise[F]).attempt  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].unit.flatTap(_ => methodRaise[F]).attempt  // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    (Async[F].unit >>= (_ => methodRaise[F])).attempt   // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].unit.mproduct(_ => methodRaise[F]).attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].unit.map(identity).attempt
    (for {
      _ <- Async[F].unit
      _ <- methodRaise[F]
    } yield ()).attempt.void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
  }

  def direct[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    Async[F].attempt(Async[F].map(methodRaise[F])(identity)).void               // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].attempt(Async[F].flatMap(Async[F].unit)(_ => methodRaise[F])).void // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    Async[F].attempt(Async[F].map(Async[F].unit)(identity)).void
    Async[F].attempt(Async[F].flatMap(Async[F].unit)(_ => Async[F].unit)).void
  }

  def safeSyntax[F[_]: Async]: F[Unit] = {
    Async[F].unit.map(identity).attempt
    Async[F].unit.void.recover(_ => ())
    Async[F].unit.as(()).attempt
    (Async[F].unit *> Async[F].unit).attempt
    (Async[F].unit <* Async[F].unit).attempt
    (Async[F].unit >> Async[F].unit).attempt
    Async[F].unit.product(Async[F].unit).attempt
    Async[F].unit.productL(Async[F].unit).attempt
    Async[F].unit.productR(Async[F].unit).attempt
    Async[F].unit.map2(Async[F].unit)((_, _) => ()).attempt
    Async[F].unit.fproduct(identity).attempt
    Async[F].unit.fproductLeft(identity).attempt
    Async[F].unit.tupleLeft(()).attempt
    Async[F].unit.tupleRight(()).attempt
    Async[F].pure((_: Unit) => ()).<&>(Async[F].unit).attempt
    Async[F].unit.flatMap(_ => Async[F].unit).attempt
    Async[F].unit.flatTap(_ => Async[F].unit).attempt
    (Async[F].unit >>= (_ => Async[F].unit)).attempt
    Async[F].unit.mproduct(_ => Async[F].unit).attempt
    (for {
      _ <- Async[F].unit
      _ <- Async[F].unit
    } yield ()).attempt.void
  }

  def expressionWrappers[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    (methodRaise[F]: F[Unit]).attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    (((methodRaise[F]))).attempt      // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    (Async[F].unit: F[Unit]).attempt.void
  }

  def conditional[F[_]: Async](condition: Boolean)(implicit r: Raise[F, String]): F[Unit] = {
    (if (condition) Async[F].unit else methodRaise[F]).attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    (if (condition) methodRaise[F] else Async[F].unit).attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    (if (condition) Async[F].unit else Async[F].unit).attempt.void
  }

  def matched[F[_]: Async](condition: Boolean)(implicit r: Raise[F, String]): F[Unit] = {
    (condition match {
      case true  => Async[F].unit
      case false => methodRaise[F]
    }).attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    (condition match {
      case true  => methodRaise[F]
      case false => Async[F].unit
    }).attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    (condition match {
      case true  => Async[F].unit
      case false => Async[F].unit
    }).attempt.void
  }

  def blocks[F[_]: Async](implicit r: Raise[F, String]): F[Unit] = {
    ({
      Async[F].unit
      methodRaise[F]
    }).attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
    ({
      methodRaise[F]
      Async[F].unit
    }).attempt.void
  }

}
