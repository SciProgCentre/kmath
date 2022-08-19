/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.bindSymbol
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.asBuffer
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SimpleAutoDiffTest {

    fun dx(
        xBinding: Pair<Symbol, Double>,
        body: SimpleAutoDiffField<Double, DoubleField>.(x: AutoDiffValue<Double>) -> AutoDiffValue<Double>,
    ): DerivationResult<Double> = DoubleField.simpleAutoDiff(xBinding) { body(bindSymbol(xBinding.first)) }

    fun dxy(
        xBinding: Pair<Symbol, Double>,
        yBinding: Pair<Symbol, Double>,
        body: SimpleAutoDiffField<Double, DoubleField>.(x: AutoDiffValue<Double>, y: AutoDiffValue<Double>) -> AutoDiffValue<Double>,
    ): DerivationResult<Double> = DoubleField.simpleAutoDiff(xBinding, yBinding) {
        body(bindSymbol(xBinding.first), bindSymbol(yBinding.first))
    }

    fun diff(block: SimpleAutoDiffField<Double, DoubleField>.() -> AutoDiffValue<Double>): SimpleAutoDiffExpression<Double, DoubleField> {
        return SimpleAutoDiffExpression(DoubleField, block)
    }

    val x by symbol
    val y by symbol
    val z by symbol

    @Test
    fun testPlusX2() {
        val y = DoubleField.simpleAutoDiff(x to 3.0) {
            // diff w.r.t this x at 3
            val x = bindSymbol(x)
            x + x
        }
        assertEquals(6.0, y.value) //    y  = x + x = 6
        assertEquals(2.0, y.derivative(x)) // dy/dx = 2
    }

    @Test
    fun testPlusX2Expr() {
        val expr = diff {
            val x = bindSymbol(x)
            x + x
        }
        assertEquals(6.0, expr(x to 3.0)) //    y  = x + x = 6
        assertEquals(2.0, expr.derivative(x)(x to 3.0)) // dy/dx = 2
    }


    @Test
    fun testPlus() {
        // two variables
        val z = DoubleField.simpleAutoDiff(x to 2.0, y to 3.0) {
            val x = bindSymbol(x)
            val y = bindSymbol(y)
            x + y
        }
        assertEquals(5.0, z.value) //    z  = x + y = 5
        assertEquals(1.0, z.derivative(x)) // dz/dx = 1
        assertEquals(1.0, z.derivative(y)) // dz/dy = 1
    }

    @Test
    fun testMinus() {
        // two variables
        val z = DoubleField.simpleAutoDiff(x to 7.0, y to 3.0) {
            val x = bindSymbol(x)
            val y = bindSymbol(y)

            x - y
        }
        assertEquals(4.0, z.value)  //    z  = x - y = 4
        assertEquals(1.0, z.derivative(x))  // dz/dx = 1
        assertEquals(-1.0, z.derivative(y)) // dz/dy = -1
    }

    @Test
    fun testMulX2() {
        val y = dx(x to 3.0) { x ->
            // diff w.r.t this x at 3
            x * x
        }
        assertEquals(9.0, y.value) //    y  = x * x = 9
        assertEquals(6.0, y.derivative(x)) // dy/dx = 2 * x = 7
    }

    @Test
    fun testSqr() {
        val y = dx(x to 3.0) { x -> sqr(x) }
        assertEquals(9.0, y.value) //    y  = x ^ 2 = 9
        assertEquals(6.0, y.derivative(x)) // dy/dx = 2 * x = 7
    }

    @Test
    fun testSqrSqr() {
        val y = dx(x to 2.0) { x -> sqr(sqr(x)) }
        assertEquals(16.0, y.value) //     y = x ^ 4   = 16
        assertEquals(32.0, y.derivative(x)) // dy/dx = 4 * x^3 = 32
    }

    @Test
    fun testX3() {
        val y = dx(x to 2.0) { x ->
            // diff w.r.t this x at 2
            x * x * x
        }
        assertEquals(8.0, y.value)  //    y  = x * x * x = 8
        assertEquals(12.0, y.derivative(x)) // dy/dx = 3 * x * x = 12
    }

    @Test
    fun testDiv() {
        val z = dxy(x to 5.0, y to 2.0) { x, y ->
            x / y
        }
        assertEquals(2.5, z.value)   //     z =  x / y   = 2.5
        assertEquals(0.5, z.derivative(x))   // dz/dx =  1 / y   = 0.5
        assertEquals(-1.25, z.derivative(y)) // dz/dy = -x / y^2 = -1.25
    }

    @Test
    fun testPow3() {
        val y = dx(x to 2.0) { x ->
            // diff w.r.t this x at 2
            pow(x, 3)
        }
        assertEquals(8.0, y.value)  //    y  = x ^ 3     = 8
        assertEquals(12.0, y.derivative(x)) // dy/dx = 3 * x ^ 2 = 12
    }

    @Test
    fun testPowFull() {
        val z = dxy(x to 2.0, y to 3.0) { x, y ->
            pow(x, y)
        }
        assertApprox(8.0, z.value)           //     z = x ^ y = 8
        assertApprox(12.0, z.derivative(x))          // dz/dx = y * x ^ (y - 1) = 12
        assertApprox(8.0 * kotlin.math.ln(2.0), z.derivative(y)) // dz/dy = x ^ y * ln(x)
    }

    @Test
    fun testFromPaper() {
        val y = dx(x to 3.0) { x -> 2 * x + x * x * x }
        assertEquals(33.0, y.value)  //     y = 2 * x + x * x * x = 33
        assertEquals(29.0, y.derivative(x))  // dy/dx = 2 + 3 * x * x = 29
    }

    @Test
    fun testInnerVariable() {
        val y = dx(x to 1.0) { x ->
            const(1.0) * x
        }
        assertEquals(1.0, y.value)          //     y = x ^ n = 1
        assertEquals(1.0, y.derivative(x)) // dy/dx = n * x ^ (n - 1) = n - 1
    }

    @Test
    fun testLongChain() {
        val n = 10_000
        val y = dx(x to 1.0) { x ->
            var res = const(1.0)
            for (i in 1..n) res *= x
            res
        }
        assertEquals(1.0, y.value)          //     y = x ^ n = 1
        assertEquals(n.toDouble(), y.derivative(x)) // dy/dx = n * x ^ (n - 1) = n - 1
    }

    @Test
    fun testExample() {
        val y = dx(x to 2.0) { x -> sqr(x) + 5 * x + 3 }
        assertEquals(17.0, y.value) // the value of result (y)
        assertEquals(9.0, y.derivative(x))  // dy/dx
    }

    @Test
    fun testSqrt() {
        val y = dx(x to 16.0) { x -> sqrt(x) }
        assertEquals(4.0, y.value)     //     y = x ^ 1/2 = 4
        assertEquals(1.0 / 8, y.derivative(x)) // dy/dx = 1/2 / x ^ 1/4 = 1/8
    }

    @Test
    fun testSin() {
        val y = dx(x to PI / 6.0) { x -> sin(x) }
        assertApprox(0.5, y.value) // y = sin(PI/6) = 0.5
        assertApprox(sqrt(3.0) / 2, y.derivative(x)) // dy/dx = cos(pi/6) = sqrt(3)/2
    }

    @Test
    fun testCos() {
        val y = dx(x to PI / 6) { x -> cos(x) }
        assertApprox(sqrt(3.0) / 2, y.value) //y = cos(pi/6) = sqrt(3)/2
        assertApprox(-0.5, y.derivative(x)) // dy/dx = -sin(pi/6) = -0.5
    }

    @Test
    fun testTan() {
        val y = dx(x to PI / 6) { x -> tan(x) }
        assertApprox(1.0 / sqrt(3.0), y.value) // y = tan(pi/6) = 1/sqrt(3)
        assertApprox(4.0 / 3.0, y.derivative(x)) // dy/dx = sec(pi/6)^2 = 4/3
    }

    @Test
    fun testAsin() {
        val y = dx(x to PI / 6) { x -> asin(x) }
        assertApprox(kotlin.math.asin(PI / 6.0), y.value) // y = asin(pi/6)
        assertApprox(6.0 / sqrt(36 - PI * PI), y.derivative(x)) // dy/dx = 6/sqrt(36-pi^2)
    }

    @Test
    fun testAcos() {
        val y = dx(x to PI / 6) { x -> acos(x) }
        assertApprox(kotlin.math.acos(PI / 6.0), y.value) // y = acos(pi/6)
        assertApprox(-6.0 / sqrt(36.0 - PI * PI), y.derivative(x)) // dy/dx = -6/sqrt(36-pi^2)
    }

    @Test
    fun testAtan() {
        val y = dx(x to PI / 6) { x -> atan(x) }
        assertApprox(kotlin.math.atan(PI / 6.0), y.value) // y = atan(pi/6)
        assertApprox(36.0 / (36.0 + PI * PI), y.derivative(x)) // dy/dx = 36/(36+pi^2)
    }

    @Test
    fun testSinh() {
        val y = dx(x to 0.0) { x -> sinh(x) }
        assertApprox(kotlin.math.sinh(0.0), y.value) // y = sinh(0)
        assertApprox(kotlin.math.cosh(0.0), y.derivative(x)) // dy/dx = cosh(0)
    }

    @Test
    fun testCosh() {
        val y = dx(x to 0.0) { x -> cosh(x) }
        assertApprox(1.0, y.value) //y = cosh(0)
        assertApprox(0.0, y.derivative(x)) // dy/dx = sinh(0)
    }

    @Test
    fun testTanh() {
        val y = dx(x to 1.0) { x -> tanh(x) }
        assertApprox((E * E - 1) / (E * E + 1), y.value) // y = tanh(pi/6)
        assertApprox(1.0 / kotlin.math.cosh(1.0).pow(2), y.derivative(x)) // dy/dx = sech(pi/6)^2
    }

    @Test
    fun testAsinh() {
        val y = dx(x to PI / 6) { x -> asinh(x) }
        assertApprox(kotlin.math.asinh(PI / 6.0), y.value) // y = asinh(pi/6)
        assertApprox(6.0 / sqrt(36 + PI * PI), y.derivative(x)) // dy/dx = 6/sqrt(pi^2+36)
    }

    @Test
    fun testAcosh() {
        val y = dx(x to PI / 6) { x -> acosh(x) }
        assertApprox(kotlin.math.acosh(PI / 6.0), y.value) // y = acosh(pi/6)
        assertApprox(-6.0 / sqrt(36.0 - PI * PI), y.derivative(x)) // dy/dx = -6/sqrt(36-pi^2)
    }

    @Test
    fun testAtanh() {
        val y = dx(x to PI / 6) { x -> atanh(x) }
        assertApprox(kotlin.math.atanh(PI / 6.0), y.value) // y = atanh(pi/6)
        assertApprox(-36.0 / (PI * PI - 36.0), y.derivative(x)) // dy/dx = -36/(pi^2-36)
    }

    @Test
    fun testDivGrad() {
        val res = dxy(x to 1.0, y to 2.0) { x, y -> x * x + y * y }
        assertEquals(6.0, res.div())
        assertTrue(Buffer.contentEquals(res.grad(x, y), doubleArrayOf(2.0, 4.0).asBuffer()))
    }

    private fun assertApprox(a: Double, b: Double) {
        if ((a - b) > 1e-10) assertEquals(a, b)
    }
}
