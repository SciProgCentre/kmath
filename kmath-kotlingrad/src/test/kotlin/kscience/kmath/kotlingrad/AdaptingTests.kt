package kscience.kmath.kotlingrad

import edu.umontreal.kotlingrad.api.*
import kscience.kmath.asm.compile
import kscience.kmath.ast.MstAlgebra
import kscience.kmath.ast.MstExpression
import kscience.kmath.ast.parseMath
import kscience.kmath.expressions.invoke
import kscience.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

internal class AdaptingTests {
    @Test
    fun symbol() {
        val c1 = MstAlgebra.symbol("x")
        assertTrue(c1.toSVar<KMathNumber<Double, RealField>>().name == "x")
        val c2 = "kitten".parseMath().toSFun<KMathNumber<Double, RealField>>()
        if (c2 is SVar) assertTrue(c2.name == "kitten") else fail()
    }

    @Test
    fun number() {
        val c1 = MstAlgebra.number(12354324)
        assertTrue(c1.toSConst<DReal>().doubleValue == 12354324.0)
        val c2 = "0.234".parseMath().toSFun<KMathNumber<Double, RealField>>()
        if (c2 is SConst) assertTrue(c2.doubleValue == 0.234) else fail()
        val c3 = "1e-3".parseMath().toSFun<KMathNumber<Double, RealField>>()
        if (c3 is SConst) assertEquals(0.001, c3.value) else fail()
    }

    @Test
    fun simpleFunctionShape() {
        val linear = "2*x+16".parseMath().toSFun<KMathNumber<Double, RealField>>()
        if (linear !is Sum) fail()
        if (linear.left !is Prod) fail()
        if (linear.right !is SConst) fail()
    }

    @Test
    fun simpleFunctionDerivative() {
        val x = MstAlgebra.symbol("x").toSVar<KMathNumber<Double, RealField>>()
        val quadratic = "x^2-4*x-44".parseMath().toSFun<KMathNumber<Double, RealField>>()
        val actualDerivative = MstExpression(RealField, quadratic.d(x).toMst()).compile()
        val expectedDerivative = MstExpression(RealField, "2*x-4".parseMath()).compile()
        assertEquals(actualDerivative("x" to 123.0), expectedDerivative("x" to 123.0))
    }

    @Test
    fun moreComplexDerivative() {
        val x = MstAlgebra.symbol("x").toSVar<KMathNumber<Double, RealField>>()
        val composition = "-sqrt(sin(x^2)-cos(x)^2-16*x)".parseMath().toSFun<KMathNumber<Double, RealField>>()
        val actualDerivative = MstExpression(RealField, composition.d(x).toMst()).compile()

        val expectedDerivative = MstExpression(
            RealField,
            "-(2*x*cos(x^2)+2*sin(x)*cos(x)-16)/(2*sqrt(sin(x^2)-16*x-cos(x)^2))".parseMath()
        ).compile()

        assertEquals(actualDerivative("x" to 0.1), expectedDerivative("x" to 0.1))
    }
}
