package space.kscience.kmath.kotlingrad

import edu.umontreal.kotlingrad.api.*
import space.kscience.kmath.asm.compileToExpression
import space.kscience.kmath.ast.parseMath
import space.kscience.kmath.expressions.MstAlgebra
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.operations.DoubleField
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

internal class AdaptingTests {
    @Test
    fun symbol() {
        val c1 = MstAlgebra.bindSymbol("x")
        assertTrue(c1.toSVar<KMathNumber<Double, DoubleField>>().name == "x")
        val c2 = "kitten".parseMath().toSFun<KMathNumber<Double, DoubleField>>()
        if (c2 is SVar) assertTrue(c2.name == "kitten") else fail()
    }

    @Test
    fun number() {
        val c1 = MstAlgebra.number(12354324)
        assertTrue(c1.toSConst<DReal>().doubleValue == 12354324.0)
        val c2 = "0.234".parseMath().toSFun<KMathNumber<Double, DoubleField>>()
        if (c2 is SConst) assertTrue(c2.doubleValue == 0.234) else fail()
        val c3 = "1e-3".parseMath().toSFun<KMathNumber<Double, DoubleField>>()
        if (c3 is SConst) assertEquals(0.001, c3.value) else fail()
    }

    @Test
    fun simpleFunctionShape() {
        val linear = "2*x+16".parseMath().toSFun<KMathNumber<Double, DoubleField>>()
        if (linear !is Sum) fail()
        if (linear.left !is Prod) fail()
        if (linear.right !is SConst) fail()
    }

    @Test
    fun simpleFunctionDerivative() {
        val x = MstAlgebra.bindSymbol("x").toSVar<KMathNumber<Double, DoubleField>>()
        val quadratic = "x^2-4*x-44".parseMath().toSFun<KMathNumber<Double, DoubleField>>()
        val actualDerivative = quadratic.d(x).toMst().compileToExpression(DoubleField)
        val expectedDerivative = "2*x-4".parseMath().compileToExpression(DoubleField)
        assertEquals(actualDerivative("x" to 123.0), expectedDerivative("x" to 123.0))
    }

    @Test
    fun moreComplexDerivative() {
        val x = MstAlgebra.bindSymbol("x").toSVar<KMathNumber<Double, DoubleField>>()
        val composition = "-sqrt(sin(x^2)-cos(x)^2-16*x)".parseMath().toSFun<KMathNumber<Double, DoubleField>>()
        val actualDerivative = composition.d(x).toMst().compileToExpression(DoubleField)

        val expectedDerivative =
            "-(2*x*cos(x^2)+2*sin(x)*cos(x)-16)/(2*sqrt(sin(x^2)-16*x-cos(x)^2))".parseMath().compileToExpression(DoubleField)


        assertEquals(actualDerivative("x" to 0.1), expectedDerivative("x" to 0.1))
    }
}
