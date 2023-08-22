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

class MapSequence extends SemanticRule("TypelevelMapSequence") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // fa.map(f).sequence
      case tree @ DotSequence(_, DotMap(_)) =>
        Patch.lint(MapSequenceDiagnostic(tree))
      // fa.map(f).sequence_
      case tree @ DotSequence_(_, DotMap(_)) =>
        Patch.lint(MapSequence_Diagnostic(tree))
      // F.sequence(fa.map(f))
      case tree @ Sequence(_, DotMap(_)) =>
        Patch.lint(MapSequenceDiagnostic(tree))
      // F.sequence_(fa.map(f))
      case tree @ Sequence_(_, DotMap(_)) =>
        Patch.lint(MapSequence_Diagnostic(tree))
      // F.map(fa)(f).sequence
      case tree @ DotSequence(_, Map(_)) =>
        Patch.lint(MapSequenceDiagnostic(tree))
      // F.map(fa)(f).sequence_
      case tree @ DotSequence_(_, Map(_)) =>
        Patch.lint(MapSequence_Diagnostic(tree))
      // F.sequence(F.map(fa)(f))
      case tree @ Sequence(_, Map(_)) =>
        Patch.lint(MapSequenceDiagnostic(tree))
      // F.sequence_(F.map(fa)(f))
      case tree @ Sequence_(_, Map(_)) =>
        Patch.lint(MapSequence_Diagnostic(tree))
    }.asPatch
  }
}

object Sequence {
  val SequenceSym = SymbolMatcher.exact("cats/Traverse#sequence().")

  def unapply(term: Term)(implicit doc: SemanticDocument): Option[(Term.Select, Term)] =
    term match {
      case Term.Apply.Initial(
            select @ Term.Select(_, seq @ Term.Name("sequence")),
            List(receiver)
          ) if SequenceSym.matches(seq) =>
        Some((select, receiver))
      case _ => None
    }
}

object Sequence_ {
  val SequenceSym = SymbolMatcher.exact("cats/Foldable#sequence_().")

  def unapply(term: Term)(implicit doc: SemanticDocument): Option[(Term.Select, Term)] =
    term match {
      case Term.Apply.Initial(
            select @ Term.Select(_, seq @ Term.Name("sequence_")),
            List(receiver)
          ) if SequenceSym.matches(seq) =>
        Some((select, receiver))
      case _ => None
    }
}

object Map {
  val MapSym = SymbolMatcher.exact("cats/Applicative#map().")

  def unapply(term: Term)(implicit doc: SemanticDocument): Option[Term.Select] = term match {
    case Term.Apply.After_4_6_0(
          Term.Apply.After_4_6_0(
            select @ Term.Select(_, map @ Term.Name("map")),
            _
          ),
          _
        ) if MapSym.matches(map) =>
      Some(select)
    case _ => None
  }
}

object DotSequence {
  val SequenceSym = SymbolMatcher.exact("cats/Traverse.Ops#sequence().")

  def unapply(term: Term)(implicit doc: SemanticDocument): Option[(Term.Select, Term)] =
    term match {
      case select @ Term.Select(
            receiver,
            seq @ Term.Name("sequence")
          ) if SequenceSym.matches(seq) =>
        Some((select, receiver))
      case _ => None
    }
}

object DotSequence_ {
  val SequenceSym = SymbolMatcher.exact("cats/syntax/NestedFoldableOps#sequence_().")

  def unapply(term: Term)(implicit doc: SemanticDocument): Option[(Term.Select, Term)] =
    term match {
      case select @ Term.Select(
            receiver,
            seq @ Term.Name("sequence_")
          ) if SequenceSym.matches(seq) =>
        Some((select, receiver))
      case _ => None
    }
}

object DotMap {
  def unapply(term: Term): Option[Term.Select] = term match {
    case Term.Apply.After_4_6_0(select @ Term.Select(_, Term.Name("map")), _) =>
      Some(select)
    case _ => None
  }
}

final case class MapSequenceDiagnostic(t: Tree) extends Diagnostic {
  override def message: String    = ".map(f).sequence can be replaced by .traverse(f)"
  override def position: Position = t.pos
  override def categoryID: String = "mapSequence"
}

final case class MapSequence_Diagnostic(t: Tree) extends Diagnostic {
  override def message: String    = ".map(f).sequence_ can be replaced by .traverse_(f)"
  override def position: Position = t.pos
  override def categoryID: String = "mapSequence_"
}
