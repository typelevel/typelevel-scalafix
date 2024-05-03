/*
rule = TypelevelIORandomUUID
 */
package fix

import cats.effect.*
import java.util.UUID

object IORandomUUIDTests {
  IO.randomUUID
  IO(UUID.randomUUID())
  IO.blocking(UUID.randomUUID())
  val test = IO(UUID.randomUUID())
  for {
    ioa <- IO(UUID.randomUUID())
    iob = IO(UUID.randomUUID())
  } yield (ioa, iob)
  def generateUUID1(): IO[UUID] = {
    IO.blocking(UUID.randomUUID())
  }
  def generateUUID2(): IO[UUID] = {
    IO.randomUUID
  }
  def generateUUID3(): IO[UUID] = {
    IO(UUID.randomUUID())
  }
  def generateUUID4(): IO[(UUID, IO[UUID])] = {
    for {
      ioa <- IO(UUID.randomUUID())
      iob = IO(UUID.randomUUID())
    } yield (ioa, iob)
  }
}
