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
import scala.collection.mutable.ListBuffer

class EitherSyntax extends SemanticRule("TypelevelEitherSyntax") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    var docContainsImports: Boolean = false
    val patches: ListBuffer[Patch]  = ListBuffer.empty[Patch]

    doc.tree.traverse {
      case ImportCatsSyntaxAll(_) =>
        docContainsImports = true
      case ImportCatsSyntaxEither(_) =>
        docContainsImports = true
      case expr @ Term.Apply.After_4_6_0(Term.Name("Right"), Term.ArgClause(List(Lit.Unit()), _)) =>
        if (!docContainsImports)
          patches.addOne(
            Patch.addGlobalImport(
              Importer(
                Term.Select(
                  Term.Select(Term.Name("cats"), Term.Name("syntax")),
                  Term.Name("either")
                ),
                List(Importee.Wildcard())
              )
            )
          )
        patches.addOne(Patch.replaceTree(expr, "Either.unit"))
    }

    patches.asPatch
  }
}

object ImportCatsSyntaxAll {
  def unapply(t: Tree): Option[String] = t match {
    case Import(
          List(
            Importer(
              Term.Select(
                Term.Select(Term.Name("cats"), Term.Name("syntax")),
                Term.Name("all")
              ),
              List(Importee.Wildcard())
            )
          )
        ) =>
      Some("cats.syntax.all._")
    case _ => None
  }
}

object ImportCatsSyntaxEither {
  def unapply(t: Tree): Option[String] = t match {
    case Import(
          List(
            Importer(
              Term.Select(
                Term.Select(Term.Name("cats"), Term.Name("syntax")),
                Term.Name("either")
              ),
              importee
            )
          )
        ) =>
      if (
        importee.exists {
          case Importee.Wildcard() => true
          case _                   => false
        }
      ) Some("cats.syntax.either._")
      else if (
        importee.exists {
          case Importee.Name(Name.Indeterminate("catsSyntaxEitherObject")) => true
          case _                                                           => false
        }
      ) Some("cats.syntax.either.catsSyntaxEitherObject")
      else None
    case _ => None
  }
}
