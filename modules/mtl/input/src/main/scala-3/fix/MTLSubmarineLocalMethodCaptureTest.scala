/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl.*
import cats.effect.*

// scalafmt: { maxColumn = 160 }
object LocalMethodCapture {

  def captured = Handle.allow[String] {
    val h = summon[Handle[IO, String]]

    def boom: IO[Unit] = h.raise("boom")

    boom.attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
  }

  def chainedCapture = Handle.allow[String] {
    val h = summon[Handle[IO, String]]

    def boom: IO[Unit]     = h.raise("boom")
    def indirect: IO[Unit] = boom

    indirect.recover(_ => ()) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
  }

  def normal = Handle.allow[String] {
    def safe: IO[Unit] = IO.unit
    def recursive(remaining: Int): IO[Unit] =
      if remaining <= 0 then IO.unit else recursive(remaining - 1)

    safe.attempt *> recursive(1).attempt
  }

}
