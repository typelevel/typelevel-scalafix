/*
rule = TypelevelCatsRemoveInstanceImports
 */
package fix

import cats.implicits._

object RemoveInstanceImportsTests2 {
  val x = "hello".some
}
