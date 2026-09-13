/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl._
import cats.effect._

// scalafmt: { maxColumn = 160 }
object LocalMethodCapture {

  def captured = Handle.allowF[IO, String] { implicit h =>
    def boom: IO[Unit] = h.raise("boom")

    boom.attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
  }

  def chainedCapture = Handle.allowF[IO, String] { implicit h =>
    def boom: IO[Unit]     = h.raise("boom")
    def indirect: IO[Unit] = boom

    indirect.recover(_ => ()) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
  }

  def normal = Handle.allowF[IO, String] { _ =>
    def safe: IO[Unit] = IO.unit
    def recursive(remaining: Int): IO[Unit] =
      if (remaining <= 0) IO.unit else recursive(remaining - 1)

    safe.attempt *> recursive(1).attempt
  }

}
