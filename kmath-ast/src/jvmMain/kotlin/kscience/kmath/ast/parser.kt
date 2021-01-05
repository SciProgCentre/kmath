// TODO move to common when https://github.com/h0tk3y/better-parse/pull/33 is merged

package kscience.kmath.ast

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
import kscience.kmath.operations.FieldOperations
import kscience.kmath.operations.PowerOperations
import kscience.kmath.operations.RingOperations
import kscience.kmath.operations.SpaceOperations

/**
 * better-parse implementation of grammar defined in the ArithmeticsEvaluator.g4.
 *
 * @author Alexander Nozik and Iaroslav Postovalov
 */
public object ArithmeticsEvaluator : Grammar<MST>() {
    // TODO replace with "...".toRegex() when better-parse 0.4.1 is released
    private val num: Token by regexToken("[\\d.]+(?:[eE][-+]?\\d+)?")
    private val id: Token by regexToken("[a-z_A-Z][\\da-z_A-Z]*")
    private val lpar: Token by literalToken("(")
    private val rpar: Token by literalToken(")")
    private val comma: Token by literalToken(",")
    private val mul: Token by literalToken("*")
    private val pow: Token by literalToken("^")
    private val div: Token by literalToken("/")
    private val minus: Token by literalToken("-")
    private val plus: Token by literalToken("+")
    private val ws: Token by regexToken("\\s+", ignore = true)

    private val number: Parser<MST> by num use { MST.Numeric(text.toDouble()) }
    private val singular: Parser<MST> by id use { MST.Symbolic(text) }

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
        .or(-minus and parser(ArithmeticsEvaluator::term) map { MST.Unary(SpaceOperations.MINUS_OPERATION, it) })
        .or(-lpar and parser(ArithmeticsEvaluator::subSumChain) and -rpar)

    private val powChain: Parser<MST> by leftAssociative(term = term, operator = pow) { a, _, b ->
        MST.Binary(PowerOperations.POW_OPERATION, a, b)
    }

    private val divMulChain: Parser<MST> by leftAssociative(
        term = powChain,
        operator = div or mul use TokenMatch::type
    ) { a, op, b ->
        if (op == div)
            MST.Binary(FieldOperations.DIV_OPERATION, a, b)
        else
            MST.Binary(RingOperations.TIMES_OPERATION, a, b)
    }

    private val subSumChain: Parser<MST> by leftAssociative(
        term = divMulChain,
        operator = plus or minus use TokenMatch::type
    ) { a, op, b ->
        if (op == plus)
            MST.Binary(SpaceOperations.PLUS_OPERATION, a, b)
        else
            MST.Binary(SpaceOperations.MINUS_OPERATION, a, b)
    }

    override val rootParser: Parser<MST> by subSumChain
}

/**
 * Tries to parse the string into [MST] using [ArithmeticsEvaluator]. Returns [ParseResult] representing expression or error.
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
