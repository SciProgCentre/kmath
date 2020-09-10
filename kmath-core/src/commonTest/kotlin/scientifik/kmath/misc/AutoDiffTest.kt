package scientifik.kmath.misc

import scientifik.kmath.operations.RealField
import scientifik.kmath.structures.asBuffer
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AutoDiffTest {
    fun Variable(int: Int): Variable<Double> = Variable(int.toDouble())

    fun deriv(body: AutoDiffField<Double, RealField>.() -> Variable<Double>): DerivationResult<Double> =
        RealField.deriv(body)

    @Test
    fun testPlusX2() {
        val x = Variable(3) // diff w.r.t this x at 3
        val y = deriv { x + x }
        assertEquals(6.0, y.value) //    y  = x + x = 6
        assertEquals(2.0, y.deriv(x)) // dy/dx = 2
    }

    @Test
    fun testPlus() {
        // two variables
        val x = Variable(2)
        val y = Variable(3)
        val z = deriv { x + y }
        assertEquals(5.0, z.value) //    z  = x + y = 5
        assertEquals(1.0, z.deriv(x)) // dz/dx = 1
        assertEquals(1.0, z.deriv(y)) // dz/dy = 1
    }

    @Test
    fun testMinus() {
        // two variables
        val x = Variable(7)
        val y = Variable(3)
        val z = deriv { x - y }
        assertEquals(4.0, z.value)  //    z  = x - y = 4
        assertEquals(1.0, z.deriv(x))  // dz/dx = 1
        assertEquals(-1.0, z.deriv(y)) // dz/dy = -1
    }

    @Test
    fun testMulX2() {
        val x = Variable(3) // diff w.r.t this x at 3
        val y = deriv { x * x }
        assertEquals(9.0, y.value) //    y  = x * x = 9
        assertEquals(6.0, y.deriv(x)) // dy/dx = 2 * x = 7
    }

    @Test
    fun testSqr() {
        val x = Variable(3)
        val y = deriv { sqr(x) }
        assertEquals(9.0, y.value) //    y  = x ^ 2 = 9
        assertEquals(6.0, y.deriv(x)) // dy/dx = 2 * x = 7
    }

    @Test
    fun testSqrSqr() {
        val x = Variable(2)
        val y = deriv { sqr(sqr(x)) }
        assertEquals(16.0, y.value) //     y = x ^ 4   = 16
        assertEquals(32.0, y.deriv(x)) // dy/dx = 4 * x^3 = 32
    }

    @Test
    fun testX3() {
        val x = Variable(2) // diff w.r.t this x at 2
        val y = deriv { x * x * x }
        assertEquals(8.0, y.value)  //    y  = x * x * x = 8
        assertEquals(12.0, y.deriv(x)) // dy/dx = 3 * x * x = 12
    }

    @Test
    fun testDiv() {
        val x = Variable(5)
        val y = Variable(2)
        val z = deriv { x / y }
        assertEquals(2.5, z.value)   //     z =  x / y   = 2.5
        assertEquals(0.5, z.deriv(x))   // dz/dx =  1 / y   = 0.5
        assertEquals(-1.25, z.deriv(y)) // dz/dy = -x / y^2 = -1.25
    }

    @Test
    fun testPow3() {
        val x = Variable(2) // diff w.r.t this x at 2
        val y = deriv { pow(x, 3) }
        assertEquals(8.0, y.value)  //    y  = x ^ 3     = 8
        assertEquals(12.0, y.deriv(x)) // dy/dx = 3 * x ^ 2 = 12
    }

    @Test
    fun testPowFull() {
        val x = Variable(2)
        val y = Variable(3)
        val z = deriv { pow(x, y) }
        assertApprox(8.0, z.value)           //     z = x ^ y = 8
        assertApprox(12.0, z.deriv(x))          // dz/dx = y * x ^ (y - 1) = 12
        assertApprox(8.0 * kotlin.math.ln(2.0), z.deriv(y)) // dz/dy = x ^ y * ln(x)
    }

    @Test
    fun testFromPaper() {
        val x = Variable(3)
        val y = deriv { 2 * x + x * x * x }
        assertEquals(33.0, y.value)  //     y = 2 * x + x * x * x = 33
        assertEquals(29.0, y.deriv(x))  // dy/dx = 2 + 3 * x * x = 29
    }

    @Test
    fun testInnerVariable() {
        val x = Variable(1)
        val y = deriv {
            Variable(1) * x
        }
        assertEquals(1.0, y.value)          //     y = x ^ n = 1
        assertEquals(1.0, y.deriv(x)) // dy/dx = n * x ^ (n - 1) = n - 1
    }

    @Test
    fun testLongChain() {
        val n = 10_000
        val x = Variable(1)
        val y = deriv {
            var res = Variable(1)
            for (i in 1..n) res *= x
            res
        }
        assertEquals(1.0, y.value)          //     y = x ^ n = 1
        assertEquals(n.toDouble(), y.deriv(x)) // dy/dx = n * x ^ (n - 1) = n - 1
    }

    @Test
    fun testExample() {
        val x = Variable(2)
        val y = deriv { sqr(x) + 5 * x + 3 }
        assertEquals(17.0, y.value) // the value of result (y)
        assertEquals(9.0, y.deriv(x))  // dy/dx
    }

    @Test
    fun testSqrt() {
        val x = Variable(16)
        val y = deriv { sqrt(x) }
        assertEquals(4.0, y.value)     //     y = x ^ 1/2 = 4
        assertEquals(1.0 / 8, y.deriv(x)) // dy/dx = 1/2 / x ^ 1/4 = 1/8
    }

    @Test
    fun testSin() {
        val x = Variable(PI / 6)
        val y = deriv { sin(x) }
        assertApprox(0.5, y.value)           //    y = sin(PI/6) = 0.5
        assertApprox(kotlin.math.sqrt(3.0) / 2, y.deriv(x)) // dy/dx = cos(PI/6) = sqrt(3)/2
    }

    @Test
    fun testCos() {
        val x = Variable(PI / 6)
        val y = deriv { cos(x) }
        assertApprox(kotlin.math.sqrt(3.0) / 2, y.value) //     y =  cos(PI/6) = sqrt(3)/2
        assertApprox(-0.5, y.deriv(x))          // dy/dx = -sin(PI/6) = -0.5
    }

    @Test
    fun testDivGrad() {
        val x = Variable(1.0)
        val y = Variable(2.0)
        val res = deriv { x * x + y * y }
        assertEquals(6.0, res.div())
        assertTrue(res.grad(x, y).contentEquals(doubleArrayOf(2.0, 4.0).asBuffer()))
    }

    private fun assertApprox(a: Double, b: Double) {
        if ((a - b) > 1e-10) assertEquals(a, b)
    }
}
