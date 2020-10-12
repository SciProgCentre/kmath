package kscience.kmath.ast

import edu.umontreal.kotlingrad.experimental.DoublePrecision
import kscience.kmath.asm.compile
import kscience.kmath.ast.kotlingrad.mst
import kscience.kmath.ast.kotlingrad.sFun
import kscience.kmath.ast.kotlingrad.sVar
import kscience.kmath.expressions.invoke
import kscience.kmath.operations.RealField

/**
 * In this example, x^2-4*x-44 function is differentiated with Kotlin∇, and the autodiff result is compared with
 * valid derivative.
 */
fun main() {
    val proto = DoublePrecision.prototype
    val x by MstAlgebra.symbol("x").sVar(proto)
    val quadratic = "x^2-4*x-44".parseMath().sFun(proto)
    val actualDerivative = MstExpression(RealField, quadratic.d(x).mst()).compile()
    val expectedDerivative = MstExpression(RealField, "2*x-4".parseMath()).compile()
    assert(actualDerivative("x" to 123.0) == expectedDerivative("x" to 123.0))
}
