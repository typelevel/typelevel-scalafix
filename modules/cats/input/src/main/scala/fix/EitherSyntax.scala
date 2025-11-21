/*
rule = TypelevelEitherSyntax
 */
package fix

object EitherSyntax {

  def shouldBeReplaced = {
    Right(())
  }

  def shouldBeIgnored = {
    Right(1)
  }

}
