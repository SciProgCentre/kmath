package space.kscience.kmath.tensors.core

import kotlin.math.abs
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestDoubleLinearOpsTensorAlgebra {

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
    fun testSVD1D() = DoubleLinearOpsTensorAlgebra {
        val tensor2 = fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))

        val res = svd1d(tensor2)

        assertTrue(res.shape contentEquals intArrayOf(2))
        assertTrue { abs(abs(res.buffer.array()[res.bufferStart]) -  0.386) < 0.01}
        assertTrue { abs(abs(res.buffer.array()[res.bufferStart + 1]) -  0.922) < 0.01}
    }

    @Test
    fun testSVD() = DoubleLinearOpsTensorAlgebra {
        testSVDFor(fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)))
        testSVDFor(fromArray(intArrayOf(2, 2), doubleArrayOf(-1.0, 0.0, 239.0, 238.0)))
    }

    @Test
    fun testBatchedSVD() = DoubleLinearOpsTensorAlgebra {
        val tensor = randNormal(intArrayOf(1, 15, 4, 7, 5, 3), 0)
        val (tensorU, tensorS, tensorV) = tensor.svd()
        val tensorSVD = tensorU dot (diagonalEmbedding(tensorS) dot tensorV)
        assertTrue(tensor.eq(tensorSVD))
    }

    @Test @Ignore
    fun testBatchedSymEig() = DoubleLinearOpsTensorAlgebra {
        val tensor = randNormal(shape = intArrayOf(5, 2, 2), 0)
        val tensorSigma = tensor + tensor.transpose(1, 2)
        val (tensorS, tensorV) = tensorSigma.symEig()
        val tensorSigmaCalc = tensorV dot (diagonalEmbedding(tensorS) dot tensorV.transpose(1, 2))
        assertTrue(tensorSigma.eq(tensorSigmaCalc))
    }


}

private inline fun Double.epsEqual(other: Double, eps: Double = 1e-5): Boolean {
    return abs(this - other) < eps
}

private inline fun DoubleArray.epsEqual(other: DoubleArray, eps: Double = 1e-5): Boolean {
    for ((elem1, elem2) in this.asSequence().zip(other.asSequence())) {
        if (abs(elem1 - elem2) > eps) {
            return false
        }
    }
    return true
}

private inline fun DoubleLinearOpsTensorAlgebra.testSVDFor(tensor: DoubleTensor, epsilon: Double = 1e-10): Unit {
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


