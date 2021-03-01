package space.kscience.kmath.ast

import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.operations.RealField

fun main() {
    val expr = RealField.mstInField {
        val x = bindSymbol("x")
        x * 2.0 + 2.0 / x - 16.0
    }

    repeat(10000000){
        expr.invoke("x" to 1.0)
    }
}