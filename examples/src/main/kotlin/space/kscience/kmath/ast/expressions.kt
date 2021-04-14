package space.kscience.kmath.ast

import space.kscience.kmath.expressions.MstField
import space.kscience.kmath.expressions.interpret
import space.kscience.kmath.misc.Symbol.Companion.x
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.bindSymbol
import space.kscience.kmath.operations.invoke

fun main() {
    val expr = MstField {
        val x = bindSymbol(x)
        x * 2.0 + number(2.0) / x - 16.0
    }

    repeat(10000000) {
        expr.interpret(DoubleField, x to 1.0)
    }
}