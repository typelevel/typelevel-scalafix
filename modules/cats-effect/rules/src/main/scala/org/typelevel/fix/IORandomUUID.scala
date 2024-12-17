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

import scala.meta.*
import scalafix.v1.*

class IORandomUUID extends SemanticRule("TypelevelIORandomUUID") {
  def checkSignature(parent: Stat, tree: Term)(implicit doc: SemanticDocument): Patch = {
    tree.symbol.info.flatMap { info =>
      info.signature match {
        case MethodSignature(_, _, TypeRef(_, sym, _))
            if IOSym.matches(sym) && containsJavaUUID(parent) =>
          Some(Patch.replaceTree(parent, "IO.randomUUID"))
        case _ =>
          None
      }
    }.asPatch
  }

  def containsJavaUUID(outer: Stat)(implicit doc: SemanticDocument): Boolean = {
    outer.children.exists(_.children.map(_.symbol).exists(JavaUUIDSym.matches))
  }

  def checkDiscardedStat(outer: Stat)(implicit doc: SemanticDocument): Patch = {
    def checkInner(stat: Stat): Patch = {
      lazy val self: PartialFunction[Stat, Patch] = {
        case Term.Apply.Initial(fn @ Term.Name(_), _)
            if IOCompanionSym.matches(fn) && containsJavaUUID(outer) =>
          Patch.replaceTree(outer, "IO.randomUUID").atomic
        case Term.Apply.Initial(Term.Select(_, method), _) =>
          checkSignature(outer, method)
      }

      self.lift(stat).asPatch
    }

    checkInner(outer)
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.Block(body) =>
        body.map(checkDiscardedStat).asPatch
      case Defn.Val(_, _, _, expr) =>
        checkDiscardedStat(expr)
      case Template.Initial(_, _, _, stats) =>
        stats.map(checkDiscardedStat).asPatch
      case Term.ForYield.After_4_9_9(enums, _) =>
        enums.collect {
          case Enumerator.Generator(_, rhs) =>
            checkDiscardedStat(rhs)
          case Enumerator.Val(_, rhs) =>
            checkDiscardedStat(rhs)
        }.asPatch
    }.asPatch
  }

  val IOSym          = SymbolMatcher.exact("cats/effect/IO#")
  val IOCompanionSym = SymbolMatcher.exact("cats/effect/IO.")
  val JavaUUIDSym    = SymbolMatcher.exact("java/util/UUID#randomUUID().")
}
