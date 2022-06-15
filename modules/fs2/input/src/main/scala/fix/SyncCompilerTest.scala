/*
rule = TypelevelSyncCompiler
 */

package fix

import cats.effect._
import fs2._

object Fs2SyncCompilerTest {
  def usesSyncInnocently[F[_]](implicit F: Sync[F]) = F.delay(println("hi"))
  def usesSyncCompiler[F[_]](implicit F: Sync[F]) =
    Stream(1, 2, 3).covary[F].compile.drain // assert: TypelevelSyncCompiler.syncCompiler
  def usesConcurrentCompiler[F[_]](implicit F: Concurrent[F]) =
    Stream(1, 2, 3).covary[F].compile.drain
}
