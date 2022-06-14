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

class UnusedShowInterpolator extends SemanticRule("TypelevelUnusedShowInterpolator") {
  val ShowSym = SymbolMatcher.exact("cats/Show.ShowInterpolator#show().")

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case interp @ Term.Interpolate(fn, _, Nil) if ShowSym.matches(fn) =>
        Patch.lint(UnusedShowInterpolatorDiagnostic(interp))
    }.asPatch
  }
}

final case class UnusedShowInterpolatorDiagnostic(t: Tree) extends Diagnostic {
  override def message: String    = "This show interpolator contains no interpolated variables."
  override def position: Position = t.pos
  override def categoryID: String = "unusedShowInterpolator"
}
