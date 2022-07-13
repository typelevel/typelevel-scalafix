/*
rule = As
 */
package fix

object AsTests {

  def listMapInt = {
    List(1, 2, 3).map(_ => 1) /* assert: As.as
    ^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(_ => f) can be replaced by .as(f) */
  }

  def listMapUnit = {
    List(1, 2, 3).map(_ => ()) /* assert: As.as
    ^^^^^^^^^^^^^^^^^^^^^^^^^^
    .map(_ => ()) can be replaced by .void */
  }

  def shouldBeIgnored = {
    List(1, 2, 3).map(i => i)
    List(1, 2, 3).map(println(_))
  }

}
