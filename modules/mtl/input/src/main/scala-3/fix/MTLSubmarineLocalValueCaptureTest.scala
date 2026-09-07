/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl.*
import cats.effect.*

// scalafmt: { maxColumn = 160 }
object LocalValueCapture {

  def captured = Handle.allow[String] {
    val h                = summon[Handle[IO, String]]
    val direct: IO[Unit] = h.raise("boom")

    direct.attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
  }

  def capturedMethod = Handle.allow[String] {
    val h = summon[Handle[IO, String]]

    def boom: IO[Unit]        = h.raise("boom")
    lazy val action: IO[Unit] = boom

    action.recover(_ => ()) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
  }

  def normal = Handle.allow[String] {
    val safe: IO[Unit] = IO.unit

    safe.attempt
  }

}
