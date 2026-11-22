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

class Void extends SemanticRule("TypelevelVoid") {
  private val replacements: scala.collection.immutable.Map[String, (String, SymbolMatcher)] =
    scala.collection.immutable.Map(
      "sequence_" -> Tuple2(
        "sequenceVoid",
        SymbolMatcher.exact("cats/Foldable#sequence_().") +
          SymbolMatcher.exact("cats/syntax/NestedFoldableOps#sequence_().")
      ),
      "traverse_" -> Tuple2(
        "traverseVoid",
        SymbolMatcher.exact("cats/Foldable#traverse_().") +
          SymbolMatcher.exact("cats/Foldable.Ops#traverse_().")
      ),
      "nonEmptySequence_" -> Tuple2(
        "nonEmptySequenceVoid",
        SymbolMatcher.exact("cats/Reducible#nonEmptySequence_().") +
          SymbolMatcher.exact("cats/Reducible.Ops#nonEmptySequence_().")
      ),
      "nonEmptyTraverse_" -> Tuple2(
        "nonEmptyTraverseVoid",
        SymbolMatcher.exact("cats/Reducible#nonEmptyTraverse_().") +
          SymbolMatcher.exact("cats/Reducible.Ops#nonEmptyTraverse_().")
      ),
      "parSequence_" -> Tuple2(
        "parSequenceVoid",
        SymbolMatcher.exact("cats/Parallel.parSequence_().")
      ),
      "parTraverse_" -> Tuple2(
        "parTraverseVoid",
        SymbolMatcher.exact("cats/Parallel.parTraverse_().")
      ),
      "parNonEmptySequence_" -> Tuple2(
        "parNonEmptySequenceVoid",
        SymbolMatcher.exact("cats/Parallel.parNonEmptySequence_().")
      ),
      "parNonEmptyTraverse_" -> Tuple2(
        "parNonEmptyTraverseVoid",
        SymbolMatcher.exact("cats/Parallel.parNonEmptyTraverse_().")
      )
    )

  override def description: String =
    "Rewrites Cats calls to the deprecated underscore methods to their `Void` counterparts."

  override def fix(implicit doc: SemanticDocument): Patch =
    doc.tree.collect {
      case methodName: Term.Name
          if replacements
            .get(methodName.value)
            .exists { case (_, matcher) => matcher.matches(methodName.symbol) } =>
        Patch.replaceTree(methodName, replacements(methodName.value)._1)
    }.asPatch
}
