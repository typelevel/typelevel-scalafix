/*
rule = TypelevelUnusedShowInterpolator
 */
package fix

import cats.syntax.all._

object UnusedShowInterpolatorTests {
  def unusedShowInterpolator = {
    val foo = "foo"
    val a   = show"$foo"
    val b   = show"foo" /* assert: TypelevelUnusedShowInterpolator.unusedShowInterpolator
              ^^^^^^^^^
              This show interpolator contains no interpolated variables. */
    val c   = show"${foo}"
  }
}
