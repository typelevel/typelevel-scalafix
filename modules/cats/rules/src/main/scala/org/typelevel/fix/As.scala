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

class As extends SemanticRule("TypelevelAs") {
  override def fix(implicit doc: SemanticDocument): Patch =
    doc.tree.collect {
      // fa.map(_ => ())
      case tree @ AnonymousMap(Lit.Unit()) => Patch.lint(VoidDiagnostic(tree))
      // fa.map(_ => 1)
      case tree @ AnonymousMap(_: Lit) => Patch.lint(AsDiagnostic(tree))
      // fa.as(())
      case tree @ As(Lit.Unit()) => Patch.lint(AsUnitDiagnostic(tree))
    }.asPatch

}

object AnonymousMap {
  def unapply(term: Term): Option[Term] = term match {
    case Term.Apply.Initial(
          Term.Select(_, Term.Name("map")),
          Term.Function.Initial(List(Term.Param(_, Name.Placeholder(), _, _)), returnType) :: Nil
        ) =>
      Some(returnType)
    case _ => None
  }
}

object As {
  def unapply(term: Term): Option[Term] = term match {
    case Term.Apply.Initial(
          Term.Select(_, Term.Name("as")),
          List(value)
        ) =>
      Some(value)
    case _ => None
  }
}

final case class AsDiagnostic(t: Tree) extends Diagnostic {
  override def message: String    = ".map(_ => f) can be replaced by .as(f)"
  override def position: Position = t.pos
  override def categoryID: String = "as"
}

final case class VoidDiagnostic(t: Tree) extends Diagnostic {
  override def message: String    = ".map(_ => ()) can be replaced by .void"
  override def position: Position = t.pos
  override def categoryID: String = "as"
}

final case class AsUnitDiagnostic(t: Tree) extends Diagnostic {
  override def message: String    = ".as(()) can be replaced by .void"
  override def position: Position = t.pos
  override def categoryID: String = "as"
}
