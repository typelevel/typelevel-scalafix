/*
 * Copyright 2022 Typelevel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.typelevel.fix

import scalafix.v1._
import scala.meta._

class CatsRemoveInstanceImports extends SemanticRule("TypelevelCatsRemoveInstanceImports") {

  override def fix(implicit doc: SemanticDocument): Patch = doc.tree.collect {
    // e.g. "import cats.instances.int._" or "import cats.instances.all._"
    case i @ Import(
          Importer(Term.Select(Term.Select(Name("cats"), Name("instances")), _), _) :: _
        ) =>
      removeImportLine(doc)(i)

    // "import cats.implicits._"
    case i @ Import(Importer(Term.Select(Name("cats"), Name("implicits")), _) :: _) =>
      try {

        def treeContainsFunctionCallMatching(f: Symbol => Boolean): Boolean =
          i.parent.fold(false)(treeContainsFunctionApplicationSymbolMatches(f))

        def usesImplicitConversion = treeContainsFunctionCallMatching(catsKernelConversion)
        def usesSyntax             = treeContainsFunctionCallMatching(catsSyntax)

        if (usesImplicitConversion) {
          // the import is used to enable an implicit conversion,
          // so we have to keep it
          Patch.empty
        } else if (usesSyntax) {
          // the import is used to enable an extension method,
          // so replace it with "import cats.syntax.all._"
          Patch.replaceTree(i, "import cats.syntax.all._")
        } else {
          // the import is only used to import instances,
          // so it's safe to remove
          removeImportLine(doc)(i)
        }
      } catch {
        case e: scalafix.v1.MissingSymbolException =>
          // see https://github.com/typelevel/cats/pull/3566#issuecomment-684007028
          // and https://github.com/scalacenter/scalafix/issues/1123
          doc.input match {
            case Input.File(path, _) =>
              println(
                s"Skipping rewrite of 'import cats.implicits._' in file ${path.syntax} because we ran into a Scalafix bug. $e"
              )
            case _ =>
              println(
                s"Skipping rewrite of 'import cats.implicits._' because we ran into a Scalafix bug. $e"
              )
          }
          e.printStackTrace()
          Patch.empty
      }
  }.asPatch

  // Recursively searches for a specific SemanticTree in the terms that this tree is comprised of
  private def treeContainsFunctionApplicationSymbolMatches(
    f: Symbol => Boolean
  )(t: Tree)(implicit doc: SemanticDocument): Boolean =
    t match {
      case t: Term =>
        t.synthetics.exists {
          case ApplyTree(fn, _) => fn.symbol.fold(false)(f)
          case _                => false
        } || t.children.exists(treeContainsFunctionApplicationSymbolMatches(f))
      case t => t.children.exists(treeContainsFunctionApplicationSymbolMatches(f))
    }

  private def catsKernelConversion(symbol: Symbol): Boolean =
    symbol.value.contains("cats/kernel") // && symbol.value.contains("Conversion")

  private def catsSyntax(symbol: Symbol): Boolean = symbol.value
    .contains("cats") && (symbol.value.contains("syntax") || symbol.value.contains("Ops"))

  private def removeImportLine(doc: SemanticDocument)(i: Import): Patch =
    Patch.removeTokens(i.tokens) + removeWhitespaceAndNewlineBefore(doc)(i.tokens.start)

  private def removeWhitespaceAndNewlineBefore(doc: SemanticDocument)(index: Int): Patch =
    Patch.removeTokens(
      doc.tokens
        .take(index)
        .takeRightWhile(t => t.is[Token.Space] || t.is[Token.Tab] || t.is[Token.LF])
    )

}
