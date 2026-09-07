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

  private final class Call(
    val receiver: Option[Term],
    val method: Term.Name,
    val argumentLists: List[List[Term]],
    val typeArguments: List[Type],
    val callee: Term
  ) {
    def name: String                                   = method.value
    def symbol(implicit doc: SemanticDocument): Symbol = method.symbol
  }

  private final class HandlerCall(
    val call: Call,
    val protectedEffect: Term,
    val handler: Option[Term]
  )

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

  private val UnaryPropagationMethods =
    Set("map", "void", "as", "fproduct", "fproductLeft", "tupleLeft", "tupleRight")
  private val BinaryPropagationMethods =
    Set(
      "product",
      "productL",
      "productR",
      "map2",
      "<&>",
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

    def diagnostic(term: Term): Option[Patch] =
      handlerCall(term)
        .filter(handlerMayObserveSubmarine)
        .filter(call => producedByRaise(call.protectedEffect))
        .map { handlerCall =>
          val call = handlerCall.call
          Patch.lint(
            new MTLSubmarine.SubmarineErrorHandlingDiagnostic(call.method, Some(call.name))
          )
        }

    doc.tree
      .collect {
        case t: Term.Apply => diagnostic(t)
        case t: Term.ApplyType
            if normalizedCall(t).exists(call => ParameterlessErrorHandlingMethods(call.name)) =>
          diagnostic(t)
        case t: Term.Select
            if !t.parent.exists(_.isInstanceOf[Term.ApplyType]) &&
              normalizedCall(t).exists(call => ParameterlessErrorHandlingMethods(call.name)) =>
          diagnostic(t)
      }
      .flatten
      .asPatch
  }

  private def handlerMayObserveSubmarine(
    handlerCall: HandlerCall
  )(implicit doc: SemanticDocument): Boolean =
    if (handlerCall.call.name == "attemptNarrow")
      narrowErrorType(handlerCall.call).forall(typePatternMayMatchSubmarine)
    else if (PartialErrorHandlingMethods(handlerCall.call.name))
      handlerCall.handler.exists(partialFunctionMayMatchSubmarine)
    else true

  private def narrowErrorType(call: Call): Option[Type] =
    call.typeArguments.headOption

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
        val call = normalizedCall(term)
        methodRequiresImplicitRaise(call) ||
        originatesFromRaiseOperation(call) ||
        originatesFromLocalDefinition(call, visitedLocalDefinitions) ||
        originatesFromPropagation(call, visitedLocalDefinitions) ||
        originatesFromForComprehension(term, visitedLocalDefinitions)
    }

  private def originatesFromLocalDefinition(
    call: Option[Call],
    visitedLocalDefinitions: Set[Symbol]
  )(implicit doc: SemanticDocument, localDefinitions: LocalDefinitions): Boolean =
    call.exists { call =>
      val symbol = call.symbol
      !visitedLocalDefinitions(symbol) &&
      localDefinitions.bySymbol
        .get(symbol)
        .exists(producedByRaise(_, visitedLocalDefinitions + symbol))
    }

  private def originatesFromRaiseOperation(call: Option[Call])(implicit doc: SemanticDocument) =
    call.exists(call => isOwner(call.symbol, RaiseAll_M))

  private def originatesFromPropagation(
    call: Option[Call],
    visitedLocalDefinitions: Set[Symbol]
  )(implicit doc: SemanticDocument, localDefinitions: LocalDefinitions): Boolean =
    call.exists { call =>
      call.receiver.exists { receiver =>
        val isCatsOperation = call.symbol.value.startsWith("cats/")
        val isDirect        = isOwner(call.symbol, PropagationDirect_M)
        val firstArguments  = call.argumentLists.headOption.getOrElse(Nil)

        if (!isCatsOperation) false
        else if (UnaryPropagationMethods(call.name)) {
          val source = if (isDirect) firstArguments.headOption else Some(receiver)
          source.exists(producedByRaise(_, visitedLocalDefinitions))
        } else if (BinaryPropagationMethods(call.name)) {
          val sources = if (isDirect) firstArguments.take(2) else receiver :: firstArguments.take(1)
          sources.exists(producedByRaise(_, visitedLocalDefinitions))
        } else if (FlatMapPropagationMethods(call.name)) {
          val source = if (isDirect) firstArguments.headOption else Some(receiver)
          val callback =
            if (isDirect) call.argumentLists.drop(1).headOption.flatMap(_.headOption)
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

  private def handlerCall(term: Term)(implicit doc: SemanticDocument): Option[HandlerCall] =
    normalizedCall(term)
      .filter(call => ErrorHandlingMethods(call.name))
      .flatMap { call =>
        val isDirect = isOwner(call.symbol, Direct_M)
        val isSyntax = Syntax_M.matches(call.symbol.owner)
        val isIO     = IO_M.matches(call.callee)

        if (isDirect)
          call.argumentLists.headOption.flatMap(_.headOption).map { protectedEffect =>
            val handler = call.argumentLists.drop(1).headOption.flatMap(_.headOption)
            new HandlerCall(call, protectedEffect, handler)
          }
        else if (isSyntax || isIO)
          call.receiver.map { protectedEffect =>
            val handler = call.argumentLists.headOption.flatMap(_.headOption)
            new HandlerCall(call, protectedEffect, handler)
          }
        else None
      }

  private def normalizedCall(term: Term): Option[Call] = {
    @annotation.tailrec
    def loop(
      t: Term,
      argumentLists: List[List[Term]],
      typeArguments: List[Type]
    ): Option[Call] =
      t match {
        case Term.Apply.After_4_6_0(fun, args) =>
          loop(fun, args :: argumentLists, typeArguments)

        case Term.ApplyType.After_4_6_0(fun, tpes) =>
          loop(fun, argumentLists, tpes.values.toList ::: typeArguments)

        case Term.ApplyInfix.Initial(receiver, method: Term.Name, _, args) =>
          Some(new Call(Some(receiver), method, List(args), typeArguments, method))

        case select @ Term.Select(receiver, method: Term.Name) =>
          Some(new Call(Some(receiver), method, argumentLists, typeArguments, select))

        case method: Term.Name =>
          Some(new Call(None, method, argumentLists, typeArguments, method))

        case _ =>
          None
      }

    loop(term, Nil, Nil)
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

  private def methodRequiresImplicitRaise(
    call: Option[Call]
  )(implicit doc: SemanticDocument): Boolean =
    call.flatMap(call => doc.info(call.symbol)).exists { info =>
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
