package org.typelevel.fix

import scalafix.v1._
import scala.meta._

class MapSequence extends SemanticRule("MapSequence") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // fa.map(f).sequence
      case tree @ DotSequence(seq, DotMap(map)) =>
        Patch.lint(MapSequenceDiagnostic(tree))
      // fa.map(f).sequence_
      case tree @ DotSequence_(seq, DotMap(map)) =>
        Patch.lint(MapSequence_Diagnostic(tree))
      // F.sequence(fa.map(f))
      case tree @ Sequence(seq, DotMap(map)) =>
        Patch.lint(MapSequenceDiagnostic(tree))
      // F.sequence_(fa.map(f))
      case tree @ Sequence_(seq, DotMap(map)) =>
        Patch.lint(MapSequence_Diagnostic(tree))
      // F.map(fa)(f).sequence
      case tree @ DotSequence(seq, Map(map)) =>
        Patch.lint(MapSequenceDiagnostic(tree))
      // F.map(fa)(f).sequence_
      case tree @ DotSequence_(seq, Map(map)) =>
        Patch.lint(MapSequence_Diagnostic(tree))
      // F.sequence(F.map(fa)(f))
      case tree @ Sequence(seq, Map(map)) =>
        Patch.lint(MapSequenceDiagnostic(tree))
      // F.sequence_(F.map(fa)(f))
      case tree @ Sequence_(seq, Map(map)) =>
        Patch.lint(MapSequence_Diagnostic(tree))
    }.asPatch
  }
}

object Sequence {
  val SequenceSym = SymbolMatcher.exact("cats/Traverse#sequence().")

  def unapply(term: Term)(implicit doc: SemanticDocument): Option[(Term.Select, Term)] =
    term match {
      case Term.Apply(
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
      case Term.Apply(
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
    case Term.Apply(
          Term.Apply(
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
  def unapply(term: Term)(implicit doc: SemanticDocument): Option[Term.Select] = term match {
    case Term.Apply(select @ Term.Select(_, Term.Name("map")), _) =>
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
