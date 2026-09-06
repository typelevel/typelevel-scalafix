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

  private final class LocalDefinitions(val bySymbol: Map[Symbol, Term])

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

  private val ParameterlessErrorHandlingMethods =
    Set("attempt", "attemptNarrow", "attemptT", "voidError")

  private val PartialErrorHandlingMethods =
    Set("adaptErr", "adaptError", "onError", "recover", "recoverWith")

  private val SubmarineClassSupertypes_M =
    SymbolMatcher.exact("java/lang/RuntimeException#") +
      SymbolMatcher.exact("java/lang/Exception#") +
      SymbolMatcher.exact("java/lang/Throwable#") +
      SymbolMatcher.exact("java/lang/Object#") +
      SymbolMatcher.exact("scala/Any#") +
      SymbolMatcher.exact("scala/AnyRef#")

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
    implicit val localDefinitions: LocalDefinitions = new LocalDefinitions(
      doc.tree
        .collect {
          case d: Defn.Def if d.parent.exists(_.isInstanceOf[Term.Block]) =>
            List(d.symbol -> d.body)

          case d: Defn.Val if d.parent.exists(_.isInstanceOf[Term.Block]) =>
            d.pats match {
              case List(p: Pat.Var) => List(p.symbol -> d.rhs)
              case _                => Nil
            }
        }
        .flatten
        .toMap
    )

    doc.tree.collect {
      // syntax, e.g. `raise.onError { e => ??? }`
      case t @ Term.Apply.After_4_6_0(Term.Select(qual, method @ Term.Name(name)), _)
          if Syntax_M.matches(t.symbol.owner) &&
            ErrorHandlingMethods(name) &&
            handlerMayObserveSubmarine(t, name) &&
            producedByRaise(qual) =>
        Patch.lint(new MTLSubmarine.SubmarineErrorHandlingDiagnostic(method, Some(name)))

      // syntax, e.g. `raise.voidError`
      case t @ Term.Select(qual, method @ Term.Name(name))
          if Syntax_M.matches(t.symbol.owner) &&
            ParameterlessErrorHandlingMethods(name) &&
            handlerMayObserveSubmarine(t, name) &&
            producedByRaise(qual) =>
        Patch.lint(new MTLSubmarine.SubmarineErrorHandlingDiagnostic(method, Some(name)))

      // direct, e.g. `Async[F].onError(raise) { e => ??? }`
      case t @ Term.Apply.After_4_6_0(_, _)
          if isOwner(t.symbol, Direct_M) &&
            calleeName(t)
              .exists(name => ErrorHandlingMethods(name) && handlerMayObserveSubmarine(t, name)) &&
            protectedEffectArg(t).exists(producedByRaise(_)) =>
        Patch.lint(
          new MTLSubmarine.SubmarineErrorHandlingDiagnostic(
            calleeNameTerm(t).getOrElse(t),
            calleeName(t)
          )
        )

      // IO direct methods, e.g. `raise.onError { e => ??? }`
      case t @ Term.Apply.After_4_6_0(sel @ Term.Select(qual, method @ Term.Name(name)), _)
          if IO_M.matches(sel) &&
            handlerMayObserveSubmarine(t, name) &&
            producedByRaise(qual) =>
        Patch.lint(new MTLSubmarine.SubmarineErrorHandlingDiagnostic(method, Some(name)))

      case t @ Term.Select(qual, method @ Term.Name(name))
          if IO_M.matches(t) &&
            ParameterlessErrorHandlingMethods(name) &&
            handlerMayObserveSubmarine(t, name) &&
            producedByRaise(qual) =>
        Patch.lint(new MTLSubmarine.SubmarineErrorHandlingDiagnostic(method, Some(name)))
    }.asPatch
  }

  private def handlerMayObserveSubmarine(
    term: Term,
    method: String
  )(implicit doc: SemanticDocument): Boolean =
    if (method == "attemptNarrow")
      narrowErrorType(term).forall(typePatternMayMatchSubmarine)
    else if (PartialErrorHandlingMethods(method))
      partialHandlerArgument(term).exists(partialFunctionMayMatchSubmarine)
    else true

  private def narrowErrorType(term: Term): Option[Type] = {
    @annotation.tailrec
    def fromCall(t: Term): Option[Type] =
      t match {
        case Term.Apply.After_4_6_0(fun, _)      => fromCall(fun)
        case Term.ApplyType.After_4_6_0(_, tpes) => tpes.values.headOption
        case Term.Select(_, _) | Term.Name(_)    => fromParent(t)
        case _                                   => None
      }

    def fromParent(t: Term): Option[Type] =
      t.parent.collect { case Term.ApplyType.After_4_6_0(_, tpes) =>
        tpes.values.headOption
      }.flatten

    fromCall(term)
  }

  private def partialHandlerArgument(
    term: Term
  )(implicit doc: SemanticDocument): Option[Term] =
    appliedCall(term).flatMap { case (_, _, argumentLists) =>
      calleeSymbol(term).flatMap { symbol =>
        if (isOwner(symbol, Direct_M))
          argumentLists.drop(1).headOption.flatMap(_.headOption)
        else argumentLists.headOption.flatMap(_.headOption)
      }
    }

  private def partialFunctionMayMatchSubmarine(
    term: Term
  )(implicit doc: SemanticDocument): Boolean =
    term match {
      case Term.PartialFunction(cases) => cases.exists(c => patternMayMatchSubmarine(c.pat))
      case _                           => true
    }

  private def patternMayMatchSubmarine(pat: Pat)(implicit doc: SemanticDocument): Boolean =
    pat match {
      case Pat.Wildcard()        => true
      case Pat.Typed(_, tpe)     => typePatternMayMatchSubmarine(tpe)
      case Pat.Alternative(a, b) => patternMayMatchSubmarine(a) || patternMayMatchSubmarine(b)
      case Pat.Bind(_, nested)   => patternMayMatchSubmarine(nested)
      case _: Lit                => false
      case _                     => true
    }

  private def typePatternMayMatchSubmarine(
    tpe: Type
  )(implicit doc: SemanticDocument): Boolean = {
    val symbol = tpe.symbol

    SubmarineClassSupertypes_M.matches(symbol) ||
    doc.info(symbol).forall(info => !info.isClass)
  }

  private def producedByRaise(
    term: Term,
    visitedLocalDefinitions: Set[Symbol] = Set.empty
  )(implicit doc: SemanticDocument, localDefinitions: LocalDefinitions): Boolean =
    term match {
      case Term.Ascribe(expr, _) => producedByRaise(expr, visitedLocalDefinitions)
      case Term.Block(stats) =>
        stats.lastOption
          .collect { case result: Term => producedByRaise(result, visitedLocalDefinitions) }
          .getOrElse(false)
      case Term.If.After_4_4_0(_, thenBranch, elseBranch, _) =>
        producedByRaise(thenBranch, visitedLocalDefinitions) ||
        producedByRaise(elseBranch, visitedLocalDefinitions)
      case Term.Match.After_4_9_9(_, cases, _) =>
        cases.exists(c => producedByRaise(c.body, visitedLocalDefinitions))
      case _ =>
        methodRequiresImplicitRaise(term) ||
        originatesFromRaiseOperation(term) ||
        originatesFromLocalDefinition(term, visitedLocalDefinitions) ||
        originatesFromPropagation(term, visitedLocalDefinitions) ||
        originatesFromForComprehension(term, visitedLocalDefinitions)
    }

  private def originatesFromLocalDefinition(
    term: Term,
    visitedLocalDefinitions: Set[Symbol]
  )(implicit doc: SemanticDocument, localDefinitions: LocalDefinitions): Boolean =
    calleeSymbol(term).exists { symbol =>
      !visitedLocalDefinitions(symbol) &&
      localDefinitions.bySymbol
        .get(symbol)
        .exists(producedByRaise(_, visitedLocalDefinitions + symbol))
    }

  private def originatesFromRaiseOperation(term: Term)(implicit doc: SemanticDocument) =
    calleeSymbol(term).exists(sym => isOwner(sym, RaiseAll_M))

  private def originatesFromPropagation(
    term: Term,
    visitedLocalDefinitions: Set[Symbol]
  )(implicit doc: SemanticDocument, localDefinitions: LocalDefinitions): Boolean =
    appliedCall(term).exists { case (receiver, method, argumentLists) =>
      calleeSymbol(term).exists { symbol =>
        val isCatsOperation = symbol.value.startsWith("cats/")
        val isDirect        = isOwner(symbol, PropagationDirect_M)
        val firstArguments  = argumentLists.headOption.getOrElse(Nil)

        if (!isCatsOperation) false
        else if (UnaryPropagationMethods(method)) {
          val source = if (isDirect) firstArguments.headOption else Some(receiver)
          source.exists(producedByRaise(_, visitedLocalDefinitions))
        } else if (BinaryPropagationMethods(method)) {
          val sources = if (isDirect) firstArguments.take(2) else receiver :: firstArguments.take(1)
          sources.exists(producedByRaise(_, visitedLocalDefinitions))
        } else if (FlatMapPropagationMethods(method)) {
          val source = if (isDirect) firstArguments.headOption else Some(receiver)
          val callback =
            if (isDirect) argumentLists.drop(1).headOption.flatMap(_.headOption)
            else firstArguments.headOption

          source.exists(producedByRaise(_, visitedLocalDefinitions)) ||
          callback.exists(callbackProducesRaise(_, visitedLocalDefinitions))
        } else false
      }
    }

  private def callbackProducesRaise(
    term: Term,
    visitedLocalDefinitions: Set[Symbol]
  )(implicit doc: SemanticDocument, localDefinitions: LocalDefinitions): Boolean =
    term match {
      case Term.Function.After_4_6_0(_, body: Term) =>
        producedByRaise(body, visitedLocalDefinitions)
      case other => producedByRaise(other, visitedLocalDefinitions)
    }

  private def originatesFromForComprehension(
    term: Term,
    visitedLocalDefinitions: Set[Symbol]
  )(implicit doc: SemanticDocument, localDefinitions: LocalDefinitions): Boolean = {
    def generatorsProduceRaise(enumerators: List[Enumerator]): Boolean =
      enumerators.exists {
        case Enumerator.Generator(_, rhs) => producedByRaise(rhs, visitedLocalDefinitions)
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

  private def calleeName(term: Term): Option[String] =
    calleeNameTerm(term).map(_.value)

  @annotation.tailrec
  private def calleeNameTerm(term: Term): Option[Term.Name] =
    term match {
      case Term.Apply.After_4_6_0(fun, _)     => calleeNameTerm(fun)
      case Term.ApplyType.After_4_6_0(fun, _) => calleeNameTerm(fun)
      case Term.Select(_, method: Term.Name)  => Some(method)
      case method: Term.Name                  => Some(method)
      case _                                  => None
    }

}

object MTLSubmarine {

  final class SubmarineErrorHandlingDiagnostic(
    tree: Tree,
    method: Option[String]
  ) extends Diagnostic {

    override def message: String = {
      val operation = method.fold("error-handling methods")(m => s"the error-handling method `$m`")
      s"Avoid calling $operation " +
        s"on expressions that require `cats.mtl.Raise[F, *]`. " +
        s"Errors raised through `Raise` may be represented by the traceless " +
        s"`cats.mtl.Handle.Submarine` exception. Handling that exception with " +
        s"`ApplicativeError`, `MonadError`, or `IO` can bypass marker-aware typed error handling. " +
        s"Use `cats.mtl.Handle[F, *].handle` or `cats.mtl.Handle[F, *].handleWith` " +
        s"instead."
    }

    def position: Position = tree.pos

    override def categoryID: String = "mtlSubmarineErrorHandling"
  }

}
