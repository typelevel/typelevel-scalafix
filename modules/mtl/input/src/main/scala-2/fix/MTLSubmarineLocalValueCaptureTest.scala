/*
rule = TypelevelMTLSubmarine
 */
package fix

import cats.mtl._
import cats.effect._


// scalafmt: { maxColumn = 160 }
object LocalValueCapture {

  def captured = Handle.allowF[IO, String] { implicit h =>
    val direct: IO[Unit] = h.raise("boom")

    direct.attempt // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
  }

  def capturedMethod = Handle.allowF[IO, String] { implicit h =>
    def boom: IO[Unit]        = h.raise("boom")
    lazy val action: IO[Unit] = boom

    action.recover(_ => ()) // assert: TypelevelMTLSubmarine.mtlSubmarineErrorHandling
  }

  def normal = Handle.allowF[IO, String] { _ =>
    val safe: IO[Unit] = IO.unit

    safe.attempt
  }

}
