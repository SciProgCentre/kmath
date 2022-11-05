/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.contentEquals
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.tensors.core.internal.svd1d
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class TestDoubleLinearOpsTensorAlgebra {

    @Test
    fun testDetLU() = DoubleTensorAlgebra {
        val tensor = fromArray(
            ShapeND(2, 2, 2),
            doubleArrayOf(
                1.0, 3.0,
                1.0, 2.0,
                1.5, 1.0,
                10.0, 2.0
            )
        )

        val expectedTensor = fromArray(
            ShapeND(2, 1),
            doubleArrayOf(
                -1.0,
                -7.0
            )
        )
        val detTensor = detLU(tensor)

        assertTrue(detTensor.eq(expectedTensor))

    }

    @Test
    fun testDet() = DoubleTensorAlgebra {
        val expectedValue = 0.019827417
        val m = fromArray(
            ShapeND(3, 3), doubleArrayOf(
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
            ShapeND(1, 1), doubleArrayOf(
                expectedValue
            )
        )

        assertTrue { abs(m.det().value() - expectedValue) < 1e-5 }
    }

    @Test
    fun testInvLU() = DoubleTensorAlgebra {
        val tensor = fromArray(
            ShapeND(2, 2, 2),
            doubleArrayOf(
                1.0, 0.0,
                0.0, 2.0,
                1.0, 1.0,
                1.0, 0.0
            )
        )

        val expectedTensor = fromArray(
            ShapeND(2, 2, 2), doubleArrayOf(
                1.0, 0.0,
                0.0, 0.5,
                0.0, 1.0,
                1.0, -1.0
            )
        )

        val invTensor = invLU(tensor)
        assertTrue(invTensor.eq(expectedTensor))
    }

    @Test
    fun testScalarProduct() = DoubleTensorAlgebra {
        val a = fromArray(ShapeND(3), doubleArrayOf(1.8, 2.5, 6.8))
        val b = fromArray(ShapeND(3), doubleArrayOf(5.5, 2.6, 6.4))
        assertEquals(a.dot(b).value(), 59.92)
    }

    @Test
    fun testQR() = DoubleTensorAlgebra {
        val shape = ShapeND(2, 2, 2)
        val buffer = doubleArrayOf(
            1.0, 3.0,
            1.0, 2.0,
            1.5, 1.0,
            10.0, 2.0
        )

        val tensor = fromArray(shape, buffer)

        val (q, r) = qr(tensor)

        assertTrue { q.shape contentEquals shape }
        assertTrue { r.shape contentEquals shape }

        assertTrue((q matmul r).eq(tensor))

    }

    @Test
    fun testLU() = DoubleTensorAlgebra {
        val shape = ShapeND(2, 2, 2)
        val buffer = doubleArrayOf(
            1.0, 3.0,
            1.0, 2.0,
            1.5, 1.0,
            10.0, 2.0
        )
        val tensor = fromArray(shape, buffer)

        val (p, l, u) = lu(tensor)

        assertTrue { p.shape contentEquals shape }
        assertTrue { l.shape contentEquals shape }
        assertTrue { u.shape contentEquals shape }

        assertTrue((p matmul tensor).eq(l matmul u))
    }

    @Test
    fun testCholesky() = DoubleTensorAlgebra {
        val tensor = randomNormal(ShapeND(2, 5, 5), 0)
        val sigma = (tensor matmul tensor.transposed()) + diagonalEmbedding(
            fromArray(ShapeND(2, 5), DoubleArray(10) { 0.1 })
        )
        val low = cholesky(sigma)
        val sigmChol = low matmul low.transposed()
        assertTrue(sigma.eq(sigmChol))
    }

    @Test
    fun testSVD1D() = DoubleTensorAlgebra {
        val tensor2 = fromArray(ShapeND(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))

        val res = svd1d(tensor2)

        assertTrue(res.shape contentEquals ShapeND(2))
        assertTrue { abs(abs(res.source[0]) - 0.386) < 0.01 }
        assertTrue { abs(abs(res.source[1]) - 0.922) < 0.01 }
    }

    @Test
    fun testSVD() = DoubleTensorAlgebra {
        testSVDFor(fromArray(ShapeND(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)))
        testSVDFor(fromArray(ShapeND(2, 2), doubleArrayOf(-1.0, 0.0, 239.0, 238.0)))
    }

    @Test
    fun testBatchedSVD() = DoubleTensorAlgebra {
        val tensor = randomNormal(ShapeND(2, 5, 3), 0)
        val (tensorU, tensorS, tensorV) = svd(tensor)
        val tensorSVD = tensorU matmul (diagonalEmbedding(tensorS) matmul tensorV.transposed())
        assertTrue(tensor.eq(tensorSVD))
    }

    @Test
    fun testBatchedSymEig() = DoubleTensorAlgebra {
        val tensor = randomNormal(shape = ShapeND(2, 3, 3), 0)
        val tensorSigma = tensor + tensor.transposed()
        val (tensorS, tensorV) = symEig(tensorSigma)
        val tensorSigmaCalc = tensorV matmul (diagonalEmbedding(tensorS) matmul tensorV.transposed())
        assertTrue(tensorSigma.eq(tensorSigmaCalc))
    }


}


private fun DoubleTensorAlgebra.testSVDFor(tensor: DoubleTensor, epsilon: Double = 1e-10) {
    val svd = svd(tensor)

    val tensorSVD = svd.first
        .dot(
            diagonalEmbedding(svd.second)
                .dot(svd.third.transposed())
        )

    assertTrue(tensor.eq(tensorSVD, epsilon))
}
