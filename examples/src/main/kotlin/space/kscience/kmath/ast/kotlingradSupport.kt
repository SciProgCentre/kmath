package space.kscience.kmath.ast

import space.kscience.kmath.asm.compileToExpression
import space.kscience.kmath.expressions.derivative
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.kotlingrad.toDiffExpression
import space.kscience.kmath.misc.symbol
import space.kscience.kmath.operations.DoubleField

/**
 * In this example, x^2-4*x-44 function is differentiated with Kotlin∇, and the autodiff result is compared with
 * valid derivative.
 */
fun main() {
    val x by symbol

    val actualDerivative = "x^2-4*x-44".parseMath()
        .toDiffExpression(DoubleField)
        .derivative(x)


    val expectedDerivative = "2*x-4".parseMath().compileToExpression(DoubleField)
    assert(actualDerivative("x" to 123.0) == expectedDerivative("x" to 123.0))
}
