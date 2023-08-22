package fix

import org.http4s.Uri

object LiteralsSyntaxWithSuppresionsTests {
  val s = "foo.com"

  // scalafix:off
  Uri.unsafeFromString("foo.com")
  Uri.unsafeFromString("""foo.com""")
  Uri.unsafeFromString("foo" + ".com")
  Uri.unsafeFromString(s)
  Uri.unsafeFromString(s"http://$s")

  Uri.Path.unsafeFromString("foo/bar")
  // scalafix:on
}
