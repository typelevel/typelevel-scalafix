package fix

import cats.effect.*
import java.util.UUID

object IORandomUUIDTests {
  IO.randomUUID
  IO.randomUUID
  IO.randomUUID
  val test = IO.randomUUID
  for {
    ioa <- IO.randomUUID
    iob = IO.randomUUID
  } yield (ioa, iob)
  def generateUUID1(): IO[UUID] = {
    IO.randomUUID
  }
  def generateUUID2(): IO[UUID] = {
    IO.randomUUID
  }
  def generateUUID3(): IO[UUID] = {
    IO.randomUUID
  }
  def generateUUID4(): IO[(UUID, IO[UUID])] = {
    for {
      ioa <- IO.randomUUID
      iob = IO.randomUUID
    } yield (ioa, iob)
  }
}
