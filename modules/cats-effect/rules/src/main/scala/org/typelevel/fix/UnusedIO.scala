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

class UnusedIO extends SemanticRule("TypelevelUnusedIO") {
  val IOSym          = SymbolMatcher.exact("cats/effect/IO#")
  val IOCompanionSym = SymbolMatcher.exact("cats/effect/IO.")

  override def isLinter: Boolean       = true
  override def isRewrite: Boolean      = false
  override def isExperimental: Boolean = true

  override def description: String = "Checks for unused IO expressions."

  def checkSignature(parent: Stat, tree: Term)(implicit doc: SemanticDocument): Patch = {
    tree.symbol.info.flatMap { info =>
      info.signature match {
        case ValueSignature(TypeRef(_, sym, _)) if IOSym.matches(sym) =>
          Some(Patch.lint(UnusedIODiagnostic(parent)))
        case ValueSignature(ByNameType(TypeRef(_, sym, _))) if IOSym.matches(sym) =>
          Some(Patch.lint(UnusedIODiagnostic(parent)))
        case MethodSignature(_, _, TypeRef(_, sym, _)) if IOSym.matches(sym) =>
          Some(Patch.lint(UnusedIODiagnostic(parent)))
        case _ =>
          None
      }
    }.asPatch
  }

  def checkDiscardedStat(outer: Stat)(implicit doc: SemanticDocument): Patch = {
    def checkInner(stat: Stat): Patch = {
      lazy val self: PartialFunction[Stat, Patch] = {
        case ref @ Term.Name(_) =>
          checkSignature(outer, ref)
        case Term.ApplyInfix.Initial(_, op, _, _) =>
          checkSignature(outer, op)
        case Term.Apply.Initial(fn @ Term.Name(_), _) if IOCompanionSym.matches(fn) =>
          Patch.lint(UnusedIODiagnostic(outer))
        case Term.Apply.Initial(fn @ Term.Name(_), _) =>
          checkSignature(outer, fn)
        case Term.ApplyUnary(fn @ Term.Name(_), _) =>
          checkSignature(outer, fn)
        case Term.ApplyUsing.Initial(fn @ Term.Name(_), _) =>
          checkSignature(outer, fn)
        case Term.Select(_, prop @ Term.Name(_)) =>
          checkSignature(outer, prop)
        case Term.Apply.Initial(Term.Select(_, method), _) =>
          checkSignature(outer, method)
        case Term.Annotate(expr, _) =>
          checkInner(expr)
        case Term.Ascribe(expr, _) =>
          checkInner(expr)
        case Term.Tuple(args) =>
          args.map(arg => checkSignature(arg, arg)).asPatch
        case Term.Try(expr, catchCases, _) =>
          checkInner(expr) ++ catchCases.collect { case Case(_, _, consequent) =>
            checkInner(consequent)
          }
        case Term.TryWithHandler(expr, catchCase, _) =>
          checkInner(expr) + checkInner(catchCase)
        case Term.Match.Initial(_, cases) =>
          cases.collect {
            case cse if self.isDefinedAt(cse.body) =>
              checkInner(cse.body)
          }.asPatch
        case Term.Block(stats) =>
          stats.collect {
            case st if self.isDefinedAt(st) =>
              checkInner(st)
          }.asPatch
        case Term.ApplyType.Initial(term, _) if self.isDefinedAt(term) =>
          checkInner(term)
      }

      self.lift(stat).asPatch
    }

    checkInner(outer)
  }

  def checkTree(tree: Tree)(implicit
    doc: SemanticDocument
  ): Patch = tree.collect {
    case Term.For(_, body) =>
      checkDiscardedStat(body)
    case Term.While(_, body) =>
      checkDiscardedStat(body)
    case Term.Block(stats) =>
      stats.dropRight(1).map(checkDiscardedStat).asPatch
    case Term.Interpolate(_, _, args) =>
      args.map(checkDiscardedStat).asPatch
    case Term.Try(_, _, Some(finalizer)) =>
      checkDiscardedStat(finalizer)
    case Term.TryWithHandler(_, _, Some(finalizer)) =>
      checkDiscardedStat(finalizer)
    case Template.Initial(_, _, _, stats) =>
      stats.map(checkDiscardedStat).asPatch
    case Ctor.Secondary.Initial(_, _, _, _, stats) =>
      stats.map(checkDiscardedStat).asPatch
    case Term.ForYield(enums, _) =>
      enums.collect { case Enumerator.Val(Pat.Wildcard(), rhs) =>
        checkDiscardedStat(rhs)
      }.asPatch
  }.asPatch

  override def fix(implicit doc: SemanticDocument): Patch =
    checkTree(doc.tree)
}

final case class UnusedIODiagnostic(t: Tree) extends Diagnostic {
  override def message: String    = "This IO expression is not used."
  override def position: Position = t.pos
  override def categoryID: String = "unusedIO"
}
