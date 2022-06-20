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

import scalafix.v0._
import scalafix.syntax._
import scala.meta._
import scala.meta.contrib._

// ref: https://github.com/typelevel/cats/issues/3563
case class CatsRemoveInstanceImports(index: SemanticdbIndex)
  extends SemanticRule(index, "TypelevelCatsRemoveInstanceImports") {

  override def fix(ctx: RuleCtx): Patch = ctx.tree.collect {
    // e.g. "import cats.instances.int._" or "import cats.instances.all._"
    case i @ Import(Importer(Select(Select(Name("cats"), Name("instances")), _), _) :: _) =>
      removeImportLine(ctx)(i)

    // "import cats.implicits._"
    case i @ Import(Importer(Select(Name("cats"), Name("implicits")), _) :: _) =>
      val boundary = findLexicalBoundary(i)

      // Find all synthetics between the import statement and the end of the lexical boundary
      val lexicalStart = i.pos.end
      val lexicalEnd   = boundary.pos.end
      try {
        val relevantSynthetics =
          ctx.index.synthetics.filter(x =>
            x.position.start >= lexicalStart && x.position.end <= lexicalEnd
          )

        val usesImplicitConversion = relevantSynthetics.exists(containsImplicitConversion)
        val usesSyntax             = relevantSynthetics.exists(containsCatsSyntax)

        if (usesImplicitConversion) {
          // the import is used to enable an implicit conversion,
          // so we have to keep it
          Patch.empty
        } else if (usesSyntax) {
          // the import is used to enable an extension method,
          // so replace it with "import cats.syntax.all._"
          ctx.replaceTree(i, "import cats.syntax.all._")
        } else {
          // the import is only used to import instances,
          // so it's safe to remove
          removeImportLine(ctx)(i)
        }
      } catch {
        case e: scalafix.v1.MissingSymbolException =>
          // see https://github.com/typelevel/cats/pull/3566#issuecomment-684007028
          // and https://github.com/scalacenter/scalafix/issues/1123
          println(
            s"Skipping rewrite of 'import cats.implicits._' in file ${ctx.input.label} because we ran into a Scalafix bug. $e"
          )
          e.printStackTrace()
          Patch.empty
      }
  }.asPatch

  private def removeImportLine(ctx: RuleCtx)(i: Import): Patch =
    ctx.removeTokens(i.tokens) + removeWhitespaceAndNewlineBefore(ctx)(i.tokens.start)

  private def containsImplicitConversion(synthetic: Synthetic) =
    synthetic.names.exists(x => isCatsKernelConversion(x.symbol))

  private def isCatsKernelConversion(symbol: Symbol) =
    symbol.syntax.contains("cats/kernel") && symbol.syntax.contains("Conversion")

  private def containsCatsSyntax(synthetic: Synthetic) =
    synthetic.names.exists(x => isCatsSyntax(x.symbol))

  private def isCatsSyntax(symbol: Symbol) =
    symbol.syntax
      .contains("cats") && (symbol.syntax.contains("syntax") || symbol.syntax.contains("Ops"))

  private def findLexicalBoundary(t: Tree): Tree = {
    t.parent match {
      case Some(b: Term.Block) => b
      case Some(t: Template)   => t
      case Some(parent)        => findLexicalBoundary(parent)
      case None                => t
    }
  }

  private def removeWhitespaceAndNewlineBefore(ctx: RuleCtx)(index: Int): Patch = {
    val whitespaceAndNewlines = ctx.tokens
      .take(index)
      .takeRightWhile(t =>
        t.is[Token.Space] ||
          t.is[Token.Tab] ||
          t.is[Token.LF]
      )
    ctx.removeTokens(whitespaceAndNewlines)
  }

}
