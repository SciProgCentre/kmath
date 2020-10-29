package kscience.kmath.ast

import kscience.kmath.asm.compile
import kscience.kmath.expressions.invoke
import kscience.kmath.expressions.symbol
import kscience.kmath.kotlingrad.DifferentiableMstExpression
import kscience.kmath.operations.RealField

/**
 * In this example, x^2-4*x-44 function is differentiated with Kotlin∇, and the autodiff result is compared with
 * valid derivative.
 */
fun main() {
    val x by symbol

    val actualDerivative = DifferentiableMstExpression(RealField, "x^2-4*x-44".parseMath())
        .derivativeOrNull(listOf(x))
        .compile()

    val expectedDerivative = MstExpression(RealField, "2*x-4".parseMath()).compile()
    assert(actualDerivative("x" to 123.0) == expectedDerivative("x" to 123.0))
}
