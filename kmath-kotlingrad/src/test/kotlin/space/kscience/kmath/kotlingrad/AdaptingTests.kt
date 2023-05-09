/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.kotlingrad

import ai.hypergraph.kotlingrad.api.*
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.asm.compileToExpression
import space.kscience.kmath.ast.parseMath
import space.kscience.kmath.expressions.MstNumericAlgebra
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.operations.DoubleField
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@OptIn(UnstableKMathAPI::class)
internal class AdaptingTests {
    @Test
    fun symbol() {
        assertEquals(x.identity, x.toSVar<KMathNumber<Double, DoubleField>>().name)
        val c2 = "kitten".parseMath().toSFun<KMathNumber<Double, DoubleField>>()
        if (c2 is SVar<*>) assertTrue(c2.name == "kitten") else fail()
    }

    @Test
    fun number() {
        val c1 = MstNumericAlgebra.number(12354324)
        assertTrue(c1.toSConst<DReal>().doubleValue == 12354324.0)
        val c2 = "0.234".parseMath().toSFun<KMathNumber<Double, DoubleField>>()
        if (c2 is SConst<*>) assertTrue(c2.doubleValue == 0.234) else fail()
        val c3 = "1e-3".parseMath().toSFun<KMathNumber<Double, DoubleField>>()
        if (c3 is SConst<*>) assertEquals(0.001, c3.value) else fail()
    }

    @Test
    fun simpleFunctionShape() {
        val linear = "2*x+16".parseMath().toSFun<KMathNumber<Double, DoubleField>>()
        if (linear !is Sum<*>) fail()
        if (linear.left !is Prod<*>) fail()
        if (linear.right !is SConst<*>) fail()
    }

    @Test
    fun simpleFunctionDerivative() {
        val xSVar = x.toSVar<KMathNumber<Double, DoubleField>>()
        val quadratic = "x^2-4*x-44".parseMath().toSFun<KMathNumber<Double, DoubleField>>()
        val actualDerivative = quadratic.d(xSVar).toMst().compileToExpression(DoubleField)
        val expectedDerivative = "2*x-4".parseMath().compileToExpression(DoubleField)
        assertEquals(actualDerivative(x to 123.0), expectedDerivative(x to 123.0))
    }

    @Test
    fun moreComplexDerivative() {
        val xSVar = x.toSVar<KMathNumber<Double, DoubleField>>()
        val composition = "-sqrt(sin(x^2)-cos(x)^2-16*x)".parseMath().toSFun<KMathNumber<Double, DoubleField>>()
        val actualDerivative = composition.d(xSVar).toMst().compileToExpression(DoubleField)

        val expectedDerivative = "-(2*x*cos(x^2)+2*sin(x)*cos(x)-16)/(2*sqrt(sin(x^2)-16*x-cos(x)^2))"
            .parseMath()
            .compileToExpression(DoubleField)

        assertEquals(actualDerivative(x to -0.1), expectedDerivative(x to -0.1))
    }
}
