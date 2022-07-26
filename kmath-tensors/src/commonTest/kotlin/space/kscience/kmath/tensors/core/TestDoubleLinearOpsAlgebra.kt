/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.operations.invoke
import space.kscience.kmath.tensors.core.internal.array
import space.kscience.kmath.tensors.core.internal.svd1d
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class TestDoubleLinearOpsTensorAlgebra {

    @Test
    fun testDetLU() = DoubleTensorAlgebra {
        val tensor = fromArray(
            intArrayOf(2, 2, 2),
            doubleArrayOf(
                1.0, 3.0,
                1.0, 2.0,
                1.5, 1.0,
                10.0, 2.0
            )
        )

        val expectedTensor = fromArray(
            intArrayOf(2, 1),
            doubleArrayOf(
                -1.0,
                -7.0
            )
        )
        val detTensor = tensor.detLU()

        assertTrue(detTensor.eq(expectedTensor))

    }

    @Test
    fun testDet() = DoubleTensorAlgebra {
        val expectedValue = 0.019827417
        val m = fromArray(
            intArrayOf(3, 3), doubleArrayOf(
                2.1843, 1.4391, -0.4845,
                1.4391, 1.7772, 0.4055,
                -0.4845, 0.4055, 0.7519
            )
        )

        assertTrue { abs(m.det().value() - expectedValue) < 1e-5 }
    }

    @Test
    fun testDetSingle() = DoubleTensorAlgebra {
        val expectedValue = 48.151623
        val m = fromArray(
            intArrayOf(1, 1), doubleArrayOf(
                expectedValue
            )
        )

        assertTrue { abs(m.det().value() - expectedValue) < 1e-5 }
    }

    @Test
    fun testInvLU() = DoubleTensorAlgebra {
        val tensor = fromArray(
            intArrayOf(2, 2, 2),
            doubleArrayOf(
                1.0, 0.0,
                0.0, 2.0,
                1.0, 1.0,
                1.0, 0.0
            )
        )

        val expectedTensor = fromArray(
            intArrayOf(2, 2, 2), doubleArrayOf(
                1.0, 0.0,
                0.0, 0.5,
                0.0, 1.0,
                1.0, -1.0
            )
        )

        val invTensor = tensor.invLU()
        assertTrue(invTensor.eq(expectedTensor))
    }

    @Test
    fun testScalarProduct() = DoubleTensorAlgebra {
        val a = fromArray(intArrayOf(3), doubleArrayOf(1.8, 2.5, 6.8))
        val b = fromArray(intArrayOf(3), doubleArrayOf(5.5, 2.6, 6.4))
        assertEquals(a.dot(b).value(), 59.92)
    }

    @Test
    fun testQR() = DoubleTensorAlgebra {
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

        assertTrue((q dot r).eq(tensor))

    }

    @Test
    fun testLU() = DoubleTensorAlgebra {
        val shape = intArrayOf(2, 2, 2)
        val buffer = doubleArrayOf(
            1.0, 3.0,
            1.0, 2.0,
            1.5, 1.0,
            10.0, 2.0
        )
        val tensor = fromArray(shape, buffer)

        val (p, l, u) = tensor.lu()

        assertTrue { p.shape contentEquals shape }
        assertTrue { l.shape contentEquals shape }
        assertTrue { u.shape contentEquals shape }

        assertTrue((p dot tensor).eq(l dot u))
    }

    @Test
    fun testCholesky() = DoubleTensorAlgebra {
        val tensor = randomNormal(intArrayOf(2, 5, 5), 0)
        val sigma = (tensor dot tensor.transpose()) + diagonalEmbedding(
            fromArray(intArrayOf(2, 5), DoubleArray(10) { 0.1 })
        )
        val low = sigma.cholesky()
        val sigmChol = low dot low.transpose()
        assertTrue(sigma.eq(sigmChol))
    }

    @Test
    fun testSVD1D() = DoubleTensorAlgebra {
        val tensor2 = fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))

        val res = svd1d(tensor2)

        assertTrue(res.shape contentEquals intArrayOf(2))
        assertTrue { abs(abs(res.mutableBuffer.array()[res.bufferStart]) - 0.386) < 0.01 }
        assertTrue { abs(abs(res.mutableBuffer.array()[res.bufferStart + 1]) - 0.922) < 0.01 }
    }

    @Test
    fun testBatchedSymEig() = DoubleTensorAlgebra {
        val tensor = randomNormal(shape = intArrayOf(2, 3, 3), 0)
        val tensorSigma = tensor + tensor.transpose()
        val (tensorS, tensorV) = tensorSigma.symEig()
        val tensorSigmaCalc = tensorV dot (diagonalEmbedding(tensorS) dot tensorV.transpose())
        assertTrue(tensorSigma.eq(tensorSigmaCalc))
    }

    @Test
    fun testSVD() = DoubleTensorAlgebra{
        testSVDFor(fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)))
        testSVDFor(fromArray(intArrayOf(2, 2), doubleArrayOf(-1.0, 0.0, 239.0, 238.0)))
        val buffer1 = doubleArrayOf(
            1.000000, 2.000000, 3.000000,
            2.000000, 3.000000, 4.000000,
            3.000000, 4.000000, 5.000000,
            4.000000, 5.000000, 6.000000,
            5.000000, 6.000000, 7.000000
        )
        testSVDFor(fromArray(intArrayOf(5, 3), buffer1))
        val buffer2 = doubleArrayOf(
            1.0, 2.0, 3.0, 2.0, 3.0,
            4.0, 3.0, 4.0, 5.0, 4.0,
            5.0, 6.0, 5.0, 6.0, 7.0
        )
        testSVDFor(fromArray(intArrayOf(3, 5), buffer2))
    }

    @Test
    fun testBatchedSVD() = DoubleTensorAlgebra{
        val tensor1 = randomNormal(intArrayOf(2, 5, 3), 0)
        testSVDFor(tensor1)
        val tensor2 = DoubleTensorAlgebra.randomNormal(intArrayOf(30, 30, 30), 0)
        testSVDFor(tensor2)
    }

    @Test
    fun testSVDPowerMethod() = DoubleTensorAlgebra{
        testSVDPowerMethodFor(fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)))
        testSVDPowerMethodFor(fromArray(intArrayOf(2, 2), doubleArrayOf(-1.0, 0.0, 239.0, 238.0)))
    }

    @Test
    fun testBatchedSVDPowerMethod() = DoubleTensorAlgebra {
        val tensor1 = randomNormal(intArrayOf(2, 5, 3), 0)
        testSVDPowerMethodFor(tensor1)
        val tensor2 = DoubleTensorAlgebra.randomNormal(intArrayOf(30, 30, 30), 0)
        testSVDPowerMethodFor(tensor2)
    }

//    @Test
//    fun testSVDPowerMethodError() = DoubleTensorAlgebra{
//       val buffer = doubleArrayOf(
//            1.000000, 2.000000, 3.000000,
//            2.000000, 3.000000, 4.000000,
//            3.000000, 4.000000, 5.000000,
//            4.000000, 5.000000, 6.000000,
//            5.000000, 6.000000, 7.000000
//        )
//        testSVDPowerMethodFor(fromArray(intArrayOf(5, 3), buffer))
//    }
}

private fun DoubleTensorAlgebra.testSVDFor(tensor: DoubleTensor) {
    val svd = tensor.svd()

    val tensorSVD = svd.first
        .dot(
            diagonalEmbedding(svd.second)
                .dot(svd.third.transpose())
        )

    assertTrue(tensor.eq(tensorSVD))
}

private fun DoubleTensorAlgebra.testSVDPowerMethodFor(tensor: DoubleTensor, epsilon: Double = 1e-10) {
    val svd = tensor.svdPowerMethod()

    val tensorSVD = svd.first
        .dot(
            diagonalEmbedding(svd.second)
                .dot(svd.third.transpose())
        )

    assertTrue(tensor.eq(tensorSVD, epsilon))
}