/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.ParseResult
import com.github.h0tk3y.betterParse.parser.Parser
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.FieldOps
import space.kscience.kmath.operations.GroupOps
import space.kscience.kmath.operations.PowerOperations
import space.kscience.kmath.operations.RingOps
import kotlin.math.floor

/**
 * better-parse implementation of grammar defined in the ArithmeticsEvaluator.g4.
 *
 * @author Alexander Nozik
 * @author Iaroslav Postovalov
 */
public object ArithmeticsEvaluator : Grammar<MST>() {
    private val num: Token by regexToken("[\\d.]+(?:[eE][-+]?\\d+)?".toRegex())
    private val id: Token by regexToken("[a-z_A-Z][\\da-z_A-Z]*".toRegex())
    private val lpar: Token by literalToken("(")
    private val rpar: Token by literalToken(")")
    private val comma: Token by literalToken(",")
    private val mul: Token by literalToken("*")
    private val pow: Token by literalToken("^")
    private val div: Token by literalToken("/")
    private val minus: Token by literalToken("-")
    private val plus: Token by literalToken("+")

    @Suppress("unused")
    private val ws: Token by regexToken("\\s+".toRegex(), ignore = true)

    // TODO Rewrite as custom parser to handle numbers with better precision. Currently, numbers like 1e10 are handled while they could be stored as longs without precision loss.
    private val number: Parser<MST> by num use {
        val d = text.toDoubleOrNull()

        MST.Numeric(
            if (d == null || d == floor(d) && !d.isInfinite()) {
                text.toLongOrNull() ?: text.toDouble()
            } else
                d
        )
    }

    private val singular: Parser<MST> by id use { Symbol(text) }

    private val unaryFunction: Parser<MST> by (id and -lpar and parser(ArithmeticsEvaluator::subSumChain) and -rpar)
        .map { (id, term) -> MST.Unary(id.text, term) }

    private val binaryFunction: Parser<MST> by id
        .and(-lpar)
        .and(parser(ArithmeticsEvaluator::subSumChain))
        .and(-comma)
        .and(parser(ArithmeticsEvaluator::subSumChain))
        .and(-rpar)
        .map { (id, left, right) -> MST.Binary(id.text, left, right) }

    private val term: Parser<MST> by number
        .or(binaryFunction)
        .or(unaryFunction)
        .or(singular)
        .or(-minus and parser(ArithmeticsEvaluator::term) map { MST.Unary(GroupOps.MINUS_OPERATION, it) })
        .or(-lpar and parser(ArithmeticsEvaluator::subSumChain) and -rpar)

    private val powChain: Parser<MST> by leftAssociative(term = term, operator = pow) { a, _, b ->
        MST.Binary(PowerOperations.POW_OPERATION, a, b)
    }

    private val divMulChain: Parser<MST> by leftAssociative(
        term = powChain,
        operator = div or mul use TokenMatch::type
    ) { a, op, b ->
        if (op == div)
            MST.Binary(FieldOps.DIV_OPERATION, a, b)
        else
            MST.Binary(RingOps.TIMES_OPERATION, a, b)
    }

    private val subSumChain: Parser<MST> by leftAssociative(
        term = divMulChain,
        operator = plus or minus use TokenMatch::type
    ) { a, op, b ->
        if (op == plus)
            MST.Binary(GroupOps.PLUS_OPERATION, a, b)
        else
            MST.Binary(GroupOps.MINUS_OPERATION, a, b)
    }

    override val rootParser: Parser<MST> by subSumChain
}

/**
 * Tries to parse the string into [MST] using [ArithmeticsEvaluator]. Returns [ParseResult] representing expression or
 * error.
 *
 * @receiver the string to parse.
 * @return the [MST] node.
 */
public fun String.tryParseMath(): ParseResult<MST> = ArithmeticsEvaluator.tryParseToEnd(this)

/**
 * Parses the string into [MST] using [ArithmeticsEvaluator].
 *
 * @receiver the string to parse.
 * @return the [MST] node.
 */
public fun String.parseMath(): MST = ArithmeticsEvaluator.parseToEnd(this)
