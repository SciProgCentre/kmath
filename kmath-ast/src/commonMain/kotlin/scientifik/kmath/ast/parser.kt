package scientifik.kmath.ast

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.grammar.tryParseToEnd
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.h0tk3y.betterParse.parser.ParseResult
import com.github.h0tk3y.betterParse.parser.Parser
import scientifik.kmath.operations.FieldOperations
import scientifik.kmath.operations.PowerOperations
import scientifik.kmath.operations.RingOperations
import scientifik.kmath.operations.SpaceOperations

/**
 * TODO move to common
 */
object ArithmeticsEvaluator : Grammar<MST>() {
    private val num: Token by token("[\\d.]+(?:[eE]-?\\d+)?".toRegex())
    private val id: Token by token("[a-z_][\\da-z_]*".toRegex())
    private val lpar: Token by token("\\(".toRegex())
    private val rpar: Token by token("\\)".toRegex())
    private val comma: Token by token(",".toRegex())
    private val mul: Token by token("\\*".toRegex())
    private val pow: Token by token("\\^".toRegex())
    private val div: Token by token("/".toRegex())
    private val minus: Token by token("-".toRegex())
    private val plus: Token by token("\\+".toRegex())
    private val ws: Token by token("\\s+".toRegex(), ignore = true)

    private val number: Parser<MST> by num use { MST.Numeric(text.toDouble()) }
    private val singular: Parser<MST> by id use { MST.Symbolic(text) }

    private val unaryFunction: Parser<MST> by (id and skip(lpar) and parser(::subSumChain) and skip(rpar))
        .map { (id, term) -> MST.Unary(id.text, term) }

    private val binaryFunction: Parser<MST> by id
        .and(skip(lpar))
        .and(parser(::subSumChain))
        .and(skip(comma))
        .and(parser(::subSumChain))
        .and(skip(rpar))
        .map { (id, left, right) -> MST.Binary(id.text, left, right) }

    private val term: Parser<MST> by number
        .or(binaryFunction)
        .or(unaryFunction)
        .or(singular)
        .or(skip(minus) and parser(::term) map { MST.Unary(SpaceOperations.MINUS_OPERATION, it) })
        .or(skip(lpar) and parser(::subSumChain) and skip(rpar))

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

fun String.tryParseMath(): ParseResult<MST> = ArithmeticsEvaluator.tryParseToEnd(this)
fun String.parseMath(): MST = ArithmeticsEvaluator.parseToEnd(this)
