/*
rule = TypelevelHttp4sLiteralsSyntax
 */

package fix

import org.http4s.Uri
import org.http4s.implicits._

object LiteralsSyntaxWithExistingImplicitsImportTests {
  Uri.unsafeFromString("foo.com")
}
