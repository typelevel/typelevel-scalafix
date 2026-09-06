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

class MTLSubmarine extends SemanticRule("TypelevelMTLSubmarine") {

  private final class LocalMethodBodies(val bySymbol: Map[Symbol, Term])

  private val Syntax_M =
    SymbolMatcher.exact("cats/syntax/ApplicativeErrorOps#") +
      SymbolMatcher.exact("cats/syntax/ApplicativeErrorFUnitOps#") +
      SymbolMatcher.exact("cats/syntax/MonadErrorOps#")

  private val Direct_M =
    SymbolMatcher.exact("cats/ApplicativeError#") +
      SymbolMatcher.exact("cats/MonadError#")

  private val PropagationDirect_M =
    SymbolMatcher.exact("cats/Functor#") +
      SymbolMatcher.exact("cats/FlatMap#") +
      SymbolMatcher.exact("cats/Apply#") +
      SymbolMatcher.exact("cats/Applicative#") +
      SymbolMatcher.exact("cats/Monad#") +
      SymbolMatcher.exact("cats/Semigroupal#")

  private val UnaryPropagationMethods = Set("map", "void", "as")
  private val BinaryPropagationMethods =
    Set(
      "product",
      "productL",
      "productR",
      "map2",
      ">>",
      "*>",
      "<*"
    )
  private val FlatMapPropagationMethods = Set("flatMap", ">>=", "flatTap", "mproduct")

  private val ErrorHandlingMethods = Set(
    "adaptErr",
    "adaptError",
    "attempt",
    "attemptNarrow",
    "attemptT",
    "attemptTap",
    "handleError",
    "handleErrorWith",
    "onError",
    "orElse",
    "orRaise",
    "recover",
    "recoverWith",
    "redeem",
    "redeemWith",
    "voidError"
  )

  private val Raise_M =
    SymbolMatcher.exact("cats/mtl/Raise#")

  private val Handle_M =
    SymbolMatcher.exact("cats/mtl/Handle#")

  private val RaiseAll_M =
    SymbolMatcher.exact("cats/mtl/Raise#") +
      SymbolMatcher.exact("cats/mtl/Raise.") +
      SymbolMatcher.exact("cats/mtl/syntax/RaiseOps#")

  private val ContextFunction_M =
    (1 to 22).map(i => SymbolMatcher.exact(s"scala/ContextFunction$i#")).reduce(_ + _)

  private val IO_M =
    SymbolMatcher.normalized("cats/effect/IO#handleError().") +
      SymbolMatcher.normalized("cats/effect/IO#handleErrorWith().") +
      SymbolMatcher.normalized("cats/effect/IO#recover().") +
      SymbolMatcher.normalized("cats/effect/IO#recoverWith().") +
      SymbolMatcher.normalized("cats/effect/IO#attempt().") +
      SymbolMatcher.normalized("cats/effect/IO#redeem().") +
      SymbolMatcher.normalized("cats/effect/IO#redeemWith().") +
      SymbolMatcher.normalized("cats/effect/IO#adaptError().") +
      SymbolMatcher.normalized("cats/effect/IO#onError().") +
      SymbolMatcher.normalized("cats/effect/IO#orElse().") +
      SymbolMatcher.normalized("cats/effect/IO#voidError().") +
      SymbolMatcher.normalized("cats/effect/IO#attemptTap().")

  override def fix(implicit doc: SemanticDocument): Patch = {
    implicit val localMethodBodies: LocalMethodBodies = new LocalMethodBodies(
      doc.tree.collect {
        case d: Defn.Def if d.parent.exists(_.isInstanceOf[Term.Block]) =>
          d.symbol -> d.body
      }.toMap
    )

    doc.tree.collect {
      // syntax, e.g. `raise.onError { e => ??? }`
      case t @ Term.Apply.After_4_6_0(Term.Select(qual, Term.Name(name)), _)
          if Syntax_M.matches(t.symbol.owner) &&
            ErrorHandlingMethods(name) &&
            producedByRaise(qual) =>
        Patch.lint(new MTLSubmarine.SubmarineErrorHandlingDiagnostic(t, Some(name)))

      // syntax, e.g. `raise.voidError`
      case t @ Term.Select(qual, Term.Name(name))
          if Syntax_M.matches(t.symbol.owner) &&
            ErrorHandlingMethods(name) &&
            producedByRaise(qual) =>
        Patch.lint(new MTLSubmarine.SubmarineErrorHandlingDiagnostic(t, Some(name)))

      // direct, e.g. `Async[F].onError(raise) { e => ??? }`
      case t @ Term.Apply.After_4_6_0(_, _)
          if isOwner(t.symbol, Direct_M) &&
            calleeName(t).exists(ErrorHandlingMethods) &&
            protectedEffectArg(t).exists(producedByRaise(_)) =>
        Patch.lint(new MTLSubmarine.SubmarineErrorHandlingDiagnostic(t, None))

      // IO direct methods, e.g. `raise.onError { e => ??? }`
      case t @ Term.Apply.After_4_6_0(sel @ Term.Select(qual, Term.Name(name)), _)
          if IO_M.matches(sel) && producedByRaise(qual) =>
        Patch.lint(new MTLSubmarine.SubmarineErrorHandlingDiagnostic(t, Some(name)))

      case t @ Term.Select(qual, Term.Name(name)) if IO_M.matches(t) && producedByRaise(qual) =>
        Patch.lint(new MTLSubmarine.SubmarineErrorHandlingDiagnostic(t, Some(name)))
    }.asPatch
  }

  private def producedByRaise(
    term: Term,
    visitedLocalMethods: Set[Symbol] = Set.empty
  )(implicit doc: SemanticDocument, localMethodBodies: LocalMethodBodies): Boolean =
    term match {
      case Term.Ascribe(expr, _) => producedByRaise(expr, visitedLocalMethods)
      case Term.Block(stats) =>
        stats.lastOption
          .collect { case result: Term => producedByRaise(result, visitedLocalMethods) }
          .getOrElse(false)
      case Term.If.After_4_4_0(_, thenBranch, elseBranch, _) =>
        producedByRaise(thenBranch, visitedLocalMethods) ||
          producedByRaise(elseBranch, visitedLocalMethods)
      case Term.Match.After_4_9_9(_, cases, _) =>
        cases.exists(c => producedByRaise(c.body, visitedLocalMethods))
      case _ =>
        methodRequiresImplicitRaise(term) ||
        originatesFromRaiseOperation(term) ||
        originatesFromLocalMethod(term, visitedLocalMethods) ||
        originatesFromPropagation(term, visitedLocalMethods) ||
        originatesFromForComprehension(term, visitedLocalMethods)
    }

  private def originatesFromLocalMethod(
    term: Term,
    visitedLocalMethods: Set[Symbol]
  )(implicit doc: SemanticDocument, localMethodBodies: LocalMethodBodies): Boolean =
    calleeSymbol(term).exists { symbol =>
      !visitedLocalMethods(symbol) &&
      localMethodBodies.bySymbol
        .get(symbol)
        .exists(producedByRaise(_, visitedLocalMethods + symbol))
    }

  private def originatesFromRaiseOperation(term: Term)(implicit doc: SemanticDocument) =
    calleeSymbol(term).exists(sym => isOwner(sym, RaiseAll_M))

  private def originatesFromPropagation(
    term: Term,
    visitedLocalMethods: Set[Symbol]
  )(implicit doc: SemanticDocument, localMethodBodies: LocalMethodBodies): Boolean =
    appliedCall(term).exists { case (receiver, method, argumentLists) =>
      calleeSymbol(term).exists { symbol =>
        val isCatsOperation = symbol.value.startsWith("cats/")
        val isDirect        = isOwner(symbol, PropagationDirect_M)
        val firstArguments  = argumentLists.headOption.getOrElse(Nil)

        if (!isCatsOperation) false
        else if (UnaryPropagationMethods(method)) {
          val source = if (isDirect) firstArguments.headOption else Some(receiver)
          source.exists(producedByRaise(_, visitedLocalMethods))
        } else if (BinaryPropagationMethods(method)) {
          val sources = if (isDirect) firstArguments.take(2) else receiver :: firstArguments.take(1)
          sources.exists(producedByRaise(_, visitedLocalMethods))
        } else if (FlatMapPropagationMethods(method)) {
          val source = if (isDirect) firstArguments.headOption else Some(receiver)
          val callback =
            if (isDirect) argumentLists.drop(1).headOption.flatMap(_.headOption)
            else firstArguments.headOption

          source.exists(producedByRaise(_, visitedLocalMethods)) ||
          callback.exists(callbackProducesRaise(_, visitedLocalMethods))
        } else false
      }
    }

  private def callbackProducesRaise(
    term: Term,
    visitedLocalMethods: Set[Symbol]
  )(implicit doc: SemanticDocument, localMethodBodies: LocalMethodBodies): Boolean =
    term match {
      case Term.Function.After_4_6_0(_, body: Term) =>
        producedByRaise(body, visitedLocalMethods)
      case other => producedByRaise(other, visitedLocalMethods)
    }

  private def originatesFromForComprehension(
    term: Term,
    visitedLocalMethods: Set[Symbol]
  )(implicit doc: SemanticDocument, localMethodBodies: LocalMethodBodies): Boolean = {
    def generatorsProduceRaise(enumerators: List[Enumerator]): Boolean =
      enumerators.exists {
        case Enumerator.Generator(_, rhs) => producedByRaise(rhs, visitedLocalMethods)
        case _                            => false
      }

    term match {
      case Term.For.After_4_9_9(enumerators, _)      => generatorsProduceRaise(enumerators)
      case Term.ForYield.After_4_9_9(enumerators, _) => generatorsProduceRaise(enumerators)
      case _                                         => false
    }
  }

  private def appliedCall(term: Term): Option[(Term, String, List[List[Term]])] = {
    @annotation.tailrec
    def loop(t: Term, argumentLists: List[List[Term]]): Option[(Term, String, List[List[Term]])] =
      t match {
        case Term.Apply.After_4_6_0(fun, args) =>
          loop(fun, args :: argumentLists)

        case Term.ApplyType.After_4_6_0(fun, _) =>
          loop(fun, argumentLists)

        case Term.ApplyInfix.Initial(receiver, Term.Name(method), _, args) =>
          Some((receiver, method, List(args)))

        case Term.Select(receiver, Term.Name(method)) =>
          Some((receiver, method, argumentLists))

        case _ =>
          None
      }

    loop(term, Nil)
  }

  private def protectedEffectArg(term: Term): Option[Term] = {
    @annotation.tailrec
    def loop(t: Term, candidate: Option[Term]): Option[Term] =
      t match {
        case Term.Apply.After_4_6_0(fun, args) =>
          loop(fun, args.headOption.orElse(candidate))

        case Term.ApplyType.After_4_6_0(fun, _) =>
          loop(fun, candidate)

        case _ =>
          candidate
      }

    loop(term, None)
  }

  private def isOwner(sym: Symbol, matcher: SymbolMatcher): Boolean = {
    @annotation.tailrec
    def loop(s: Symbol): Boolean =
      if (s == Symbol.None) false
      else {
        val o = s.owner
        if (matcher.matches(o)) true
        else loop(o)
      }
    loop(sym)
  }

  private def methodRequiresImplicitRaise(qual: Term)(implicit doc: SemanticDocument): Boolean =
    calleeSymbol(qual).flatMap(sym => doc.info(sym)).exists { info =>
      info.signature match {
        case m: MethodSignature =>
          requiresRaiseViaParams(m) || requiresRaiseViaContextFunction(m)

        case ValueSignature(tpe) =>
          requiresRaiseViaContextFunction(tpe)

        case _ =>
          false
      }
    }

  // any implicit parameter whose type constructor is Raise
  private def requiresRaiseViaParams(m: MethodSignature)(implicit doc: SemanticDocument): Boolean =
    m.parameterLists.exists(_.exists { p =>
      doc.info(p.symbol).exists { pi =>
        val isUsingOrImplicit = pi.isImplicit
        isUsingOrImplicit && paramIsRaise(pi.signature)
      }
    })

  // Scala 3: handle def f: Raise[F, E] ?=> R
  private def requiresRaiseViaContextFunction(
    m: MethodSignature
  )(implicit doc: SemanticDocument): Boolean =
    requiresRaiseViaContextFunction(m.returnType)

  private def requiresRaiseViaContextFunction(
    tpe: SemanticType
  )(implicit doc: SemanticDocument): Boolean =
    tpe match {
      case TypeRef(_, sym, args) if ContextFunction_M.matches(sym) =>
        args.exists(typeIsRaise)

      case _ =>
        false
    }

  private def paramIsRaise(sig: Signature)(implicit doc: SemanticDocument): Boolean =
    sig match {
      case ValueSignature(tpe) => typeIsRaise(tpe)
      case _                   => false
    }

  private def typeIsRaise(tpe: SemanticType)(implicit doc: SemanticDocument): Boolean =
    typeIsRaise(tpe, Set.empty)

  private def typeIsRaise(
    tpe: SemanticType,
    visited: Set[Symbol]
  )(implicit doc: SemanticDocument): Boolean =
    tpe match {
      // matches Raise[F, E] or Handle[F, E] for any F and E
      case TypeRef(_, sym, _) if Raise_M.matches(sym) || Handle_M.matches(sym) =>
        true

      case TypeRef(_, sym, _) =>
        !visited(sym) && doc.info(sym).exists {
          _.signature match {
            case t: TypeSignature =>
              typeIsRaise(t.upperBound, visited + sym)

            case _ =>
              false
          }
        }

      case AnnotatedType(_, underlying) =>
        typeIsRaise(underlying, visited)

      case _ =>
        false
    }

  private def calleeSymbol(term: Term)(implicit doc: SemanticDocument): Option[Symbol] =
    term match {
      case Term.Apply.After_4_6_0(fun, _)       => calleeSymbol(fun)
      case Term.ApplyType.After_4_6_0(fun, _)   => calleeSymbol(fun)
      case Term.ApplyInfix.Initial(_, op, _, _) => Some(op.symbol)
      case Term.Select(_, n)                    => Some(n.symbol)
      case n: Term.Name                         => Some(n.symbol)
      case Term.Block(stats) =>
        stats.lastOption.collect { case t: Term => t }.flatMap(calleeSymbol)

      case Term.If.After_4_4_0(_, thn, els, _) =>
        calleeSymbol(thn).orElse(calleeSymbol(els))

      case Term.Match.After_4_9_9(_, cases, _) =>
        cases.view.flatMap(c => calleeSymbol(c.body)).headOption

      case _ => None
    }

  @annotation.tailrec
  private def calleeName(term: Term): Option[String] =
    term match {
      case Term.Apply.After_4_6_0(fun, _)        => calleeName(fun)
      case Term.ApplyType.After_4_6_0(fun, _)    => calleeName(fun)
      case Term.Select(_, Term.Name(methodName)) => Some(methodName)
      case Term.Name(methodName)                 => Some(methodName)
      case _                                     => None
    }

}

object MTLSubmarine {

  final class SubmarineErrorHandlingDiagnostic(
    tree: Tree,
    method: Option[String]
  ) extends Diagnostic {

    override def message: String = {
      val suchAs = method.fold("")(m => s", such as `$m`,")
      s"Avoid calling error-handling methods$suchAs " +
        s"on expressions that require `cats.mtl.Raise[F, *]`. " +
        s"Errors raised through `Raise` represented by a traceless exception type `cats.mtl.Handle#Submarine`. " +
        s"Handling them with `ApplicativeError`, `MonadError`, or `IO` error-handling " +
        s"methods is not always desirable. " +
        s"Use `cats.mtl.Handle[F, *].handle` or `cats.mtl.Handle[F, *].handleWith` " +
        s"to manage these cases explicitly."
    }

    def position: Position = tree.pos

    override def categoryID: String = "mtlSubmarineErrorHandling"
  }

}
