package space.kscience.kmath.tensors.core

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestDoubleLinearOpsTensorAlgebra {

    private val eps = 1e-5

    private fun Double.epsEqual(other: Double): Boolean {
        return abs(this - other) < eps
    }

    fun DoubleArray.epsEqual(other: DoubleArray, eps: Double = 1e-5): Boolean {
        for ((elem1, elem2) in this.asSequence().zip(other.asSequence())) {
            if (abs(elem1 - elem2) > eps) {
                return false
            }
        }
        return true
    }

    @Test
    fun testDetLU() = DoubleLinearOpsTensorAlgebra {
        val tensor = fromArray(
            intArrayOf(2, 2, 2),
            doubleArrayOf(
                1.0, 3.0,
                1.0, 2.0,
                1.5, 1.0,
                10.0, 2.0
            )
        )

        val expectedShape = intArrayOf(2, 1)
        val expectedBuffer = doubleArrayOf(
            -1.0,
            -7.0
        )
        val detTensor = tensor.detLU()

        assertTrue { detTensor.shape contentEquals expectedShape }
        assertTrue { detTensor.buffer.array().epsEqual(expectedBuffer) }
    }

    @Test
    fun testInvLU() = DoubleLinearOpsTensorAlgebra {
        val tensor = fromArray(
            intArrayOf(2, 2, 2),
            doubleArrayOf(
                1.0, 0.0,
                0.0, 2.0,
                1.0, 1.0,
                1.0, 0.0
            )
        )

        val expectedShape = intArrayOf(2, 2, 2)
        val expectedBuffer = doubleArrayOf(
            1.0, 0.0,
            0.0, 0.5,
            0.0, 1.0,
            1.0, -1.0
        )

        val invTensor = tensor.invLU()
        assertTrue { invTensor.shape contentEquals expectedShape }
        assertTrue { invTensor.buffer.array().epsEqual(expectedBuffer) }
    }

    @Test
    fun testScalarProduct() = DoubleLinearOpsTensorAlgebra {
        val a = fromArray(intArrayOf(3), doubleArrayOf(1.8, 2.5, 6.8))
        val b = fromArray(intArrayOf(3), doubleArrayOf(5.5, 2.6, 6.4))
        assertEquals(a.dot(b).value(), 59.92)
    }

    @Test
    fun testQR() = DoubleLinearOpsTensorAlgebra {
        val shape = intArrayOf(2, 2, 2)
        val buffer = doubleArrayOf(
            1.0, 3.0,
            1.0, 2.0,
            1.5, 1.0,
            10.0, 2.0
        )

        val tensor = fromArray(shape, buffer)

        val (q, r) = tensor.qr()

        assertTrue { q.shape contentEquals shape }
        assertTrue { r.shape contentEquals shape }

        assertTrue { q.dot(r).buffer.array().epsEqual(buffer) }

        //todo check orthogonality/upper triang.
    }

    @Test
    fun testLU() = DoubleLinearOpsTensorAlgebra {
        val shape = intArrayOf(2, 2, 2)
        val buffer = doubleArrayOf(
            1.0, 3.0,
            1.0, 2.0,
            1.5, 1.0,
            10.0, 2.0
        )
        val tensor = fromArray(shape, buffer)

        val (lu, pivots) = tensor.lu()

        // todo check lu

        val (p, l, u) = luPivot(lu, pivots)

        assertTrue { p.shape contentEquals shape }
        assertTrue { l.shape contentEquals shape }
        assertTrue { u.shape contentEquals shape }

        assertTrue { p.dot(tensor).buffer.array().epsEqual(l.dot(u).buffer.array()) }
    }
}
