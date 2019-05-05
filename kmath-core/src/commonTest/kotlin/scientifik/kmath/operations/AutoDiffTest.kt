package scientifik.kmath.operations

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class AutoDiffTest {
    @Test
    fun testPlusX2() {
        val x = ValueWithDeriv(3) // diff w.r.t this x at 3
        val y = deriv { x + x }
        assertEquals(6.0, y.x) //    y  = x + x = 6
        assertEquals(2.0, x.d) // dy/dx = 2
    }

    @Test
    fun testPlus() {
        // two variables
        val x = ValueWithDeriv(2)
        val y = ValueWithDeriv(3)
        val z = deriv { x + y }
        assertEquals(5.0, z.x) //    z  = x + y = 5
        assertEquals(1.0, x.d) // dz/dx = 1
        assertEquals(1.0, y.d) // dz/dy = 1
    }

    @Test
    fun testMinus() {
        // two variables
        val x = ValueWithDeriv(7)
        val y = ValueWithDeriv(3)
        val z = deriv { x - y }
        assertEquals(4.0, z.x)  //    z  = x - y = 4
        assertEquals(1.0, x.d)  // dz/dx = 1
        assertEquals(-1.0, y.d) // dz/dy = -1
    }

    @Test
    fun testMulX2() {
        val x = ValueWithDeriv(3) // diff w.r.t this x at 3
        val y = deriv { x * x }
        assertEquals(9.0, y.x) //    y  = x * x = 9
        assertEquals(6.0, x.d) // dy/dx = 2 * x = 7
    }

    @Test
    fun testSqr() {
        val x = ValueWithDeriv(3)
        val y = deriv { sqr(x) }
        assertEquals(9.0, y.x) //    y  = x ^ 2 = 9
        assertEquals(6.0, x.d) // dy/dx = 2 * x = 7
    }

    @Test
    fun testSqrSqr() {
        val x = ValueWithDeriv(2)
        val y = deriv { sqr(sqr(x)) }
        assertEquals(16.0, y.x) //     y = x ^ 4   = 16
        assertEquals(32.0, x.d) // dy/dx = 4 * x^3 = 32
    }

    @Test
    fun testX3() {
        val x = ValueWithDeriv(2) // diff w.r.t this x at 2
        val y = deriv { x * x * x }
        assertEquals(8.0, y.x)  //    y  = x * x * x = 8
        assertEquals(12.0, x.d) // dy/dx = 3 * x * x = 12
    }

    @Test
    fun testDiv() {
        val x = ValueWithDeriv(5)
        val y = ValueWithDeriv(2)
        val z = deriv { x / y }
        assertEquals(2.5, z.x)   //     z =  x / y   = 2.5
        assertEquals(0.5, x.d)   // dz/dx =  1 / y   = 0.5
        assertEquals(-1.25, y.d) // dz/dy = -x / y^2 = -1.25
    }

    @Test
    fun testPow3() {
        val x = ValueWithDeriv(2) // diff w.r.t this x at 2
        val y = deriv { pow(x, 3) }
        assertEquals(8.0, y.x)  //    y  = x ^ 3     = 8
        assertEquals(12.0, x.d) // dy/dx = 3 * x ^ 2 = 12
    }

    @Test
    fun testPowFull() {
        val x = ValueWithDeriv(2)
        val y = ValueWithDeriv(3)
        val z = deriv { pow(x, y) }
        assertApprox(8.0, z.x)           //     z = x ^ y = 8
        assertApprox(12.0, x.d)          // dz/dx = y * x ^ (y - 1) = 12
        assertApprox(8.0 * kotlin.math.ln(2.0), y.d) // dz/dy = x ^ y * ln(x)
    }

    @Test
    fun testFromPaper() {
        val x = ValueWithDeriv(3)
        val y = deriv { 2 * x + x * x * x }
        assertEquals(33.0, y.x)  //     y = 2 * x + x * x * x = 33
        assertEquals(29.0, x.d)  // dy/dx = 2 + 3 * x * x = 29
    }

    @Test
    fun testLongChain() {
        val n = 10_000
        val x = ValueWithDeriv(1)
        val y = deriv {
            var pow = ValueWithDeriv(1)
            for (i in 1..n) pow *= x
            pow
        }
        assertEquals(1.0, y.x)          //     y = x ^ n = 1
        assertEquals(n.toDouble(), x.d) // dy/dx = n * x ^ (n - 1) = n - 1
    }

    @Test
    fun testExample() {
        val x = ValueWithDeriv(2)
        val y = deriv { sqr(x) + 5 * x + 3 }
        assertEquals(17.0, y.x) // the value of result (y)
        assertEquals(9.0, x.d)  // dy/dx
    }

    @Test
    fun testSqrt() {
        val x = ValueWithDeriv(16)
        val y = deriv { sqrt(x) }
        assertEquals(4.0, y.x)     //     y = x ^ 1/2 = 4
        assertEquals(1.0 / 8, x.d) // dy/dx = 1/2 / x ^ 1/4 = 1/8
    }

    @Test
    fun testSin() {
        val x = ValueWithDeriv(PI / 6)
        val y = deriv { sin(x) }
        assertApprox(0.5, y.x)           //    y = sin(PI/6) = 0.5
        assertApprox(kotlin.math.sqrt(3.0) / 2, x.d) // dy/dx = cos(PI/6) = sqrt(3)/2
    }

    @Test
    fun testCos() {
        val x = ValueWithDeriv(PI / 6)
        val y = deriv { cos(x) }
        assertApprox(kotlin.math.sqrt(3.0) / 2, y.x) //     y =  cos(PI/6) = sqrt(3)/2
        assertApprox(-0.5, x.d)          // dy/dx = -sin(PI/6) = -0.5
    }

    private fun assertApprox(a: Double, b: Double) {
        if ((a - b) > 1e-10) assertEquals(a, b)
    }

}