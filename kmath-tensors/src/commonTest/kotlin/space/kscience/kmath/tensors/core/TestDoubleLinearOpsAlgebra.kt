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

    @Test
    fun svd1d() = DoubleLinearOpsTensorAlgebra {
        val tensor2 = fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))

        val res = svd1d(tensor2)

        assertTrue(res.shape contentEquals intArrayOf(2))
        assertTrue { abs(abs(res.buffer.array()[res.bufferStart]) -  0.386) < 0.01}
        assertTrue { abs(abs(res.buffer.array()[res.bufferStart + 1]) -  0.922) < 0.01}
    }

    @Test
    fun svd() = DoubleLinearOpsTensorAlgebra {
        val epsilon = 1e-10
        fun test_tensor(tensor: DoubleTensor) {
            val svd = tensor.svd()

            val tensorSVD = svd.first
                .dot(
                    diagonalEmbedding(svd.second, 0, 0, 1)
                        .dot(svd.third.transpose(0, 1))
                )

            for ((x1, x2) in tensor.buffer.array() zip tensorSVD.buffer.array()) {
                assertTrue { abs(x1 - x2) < epsilon }
            }
        }
        test_tensor(fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)))
        test_tensor(fromArray(intArrayOf(2, 2), doubleArrayOf(-1.0, 0.0, 239.0, 238.0)))

    }
}
