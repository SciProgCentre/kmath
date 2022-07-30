/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core


import space.kscience.kmath.operations.invoke
import space.kscience.kmath.tensors.core.internal.array
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class TestDoubleTensorAlgebra {

    @Test
    fun testDoublePlus() = DoubleTensorAlgebra {
        val tensor = fromArray(intArrayOf(2), doubleArrayOf(1.0, 2.0))
        val res = 10.0 + tensor
        assertTrue(res.mutableBuffer.array() contentEquals doubleArrayOf(11.0, 12.0))
    }

    @Test
    fun testDoubleDiv() = DoubleTensorAlgebra {
        val tensor = fromArray(intArrayOf(2), doubleArrayOf(2.0, 4.0))
        val res = 2.0/tensor
        assertTrue(res.mutableBuffer.array() contentEquals doubleArrayOf(1.0, 0.5))
    }

    @Test
    fun testDivDouble() = DoubleTensorAlgebra {
        val tensor = fromArray(intArrayOf(2), doubleArrayOf(10.0, 5.0))
        val res = tensor / 2.5
        assertTrue(res.mutableBuffer.array() contentEquals doubleArrayOf(4.0, 2.0))
    }

    @Test
    fun testTranspose1x1() = DoubleTensorAlgebra {
        val tensor = fromArray(intArrayOf(1), doubleArrayOf(0.0))
        val res = tensor.transpose(0, 0)

        assertTrue(res.mutableBuffer.array() contentEquals doubleArrayOf(0.0))
        assertTrue(res.shape contentEquals intArrayOf(1))
    }

    @Test
    fun testTranspose3x2() = DoubleTensorAlgebra {
        val tensor = fromArray(intArrayOf(3, 2), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val res = tensor.transpose(1, 0)

        assertTrue(res.mutableBuffer.array() contentEquals doubleArrayOf(1.0, 3.0, 5.0, 2.0, 4.0, 6.0))
        assertTrue(res.shape contentEquals intArrayOf(2, 3))
    }

    @Test
    fun testTranspose1x2x3() = DoubleTensorAlgebra {
        val tensor = fromArray(intArrayOf(1, 2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val res01 = tensor.transpose(0, 1)
        val res02 = tensor.transpose(-3, 2)
        val res12 = tensor.transpose()

        assertTrue(res01.shape contentEquals intArrayOf(2, 1, 3))
        assertTrue(res02.shape contentEquals intArrayOf(3, 2, 1))
        assertTrue(res12.shape contentEquals intArrayOf(1, 3, 2))

        assertTrue(res01.mutableBuffer.array() contentEquals doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        assertTrue(res02.mutableBuffer.array() contentEquals doubleArrayOf(1.0, 4.0, 2.0, 5.0, 3.0, 6.0))
        assertTrue(res12.mutableBuffer.array() contentEquals doubleArrayOf(1.0, 4.0, 2.0, 5.0, 3.0, 6.0))
    }

    @Test
    fun testLinearStructure() = DoubleTensorAlgebra {
        val shape = intArrayOf(3)
        val tensorA = full(value = -4.5, shape = shape)
        val tensorB = full(value = 10.9, shape = shape)
        val tensorC = full(value = 789.3, shape = shape)
        val tensorD = full(value = -72.9, shape = shape)
        val tensorE = full(value = 553.1, shape = shape)
        val result = 15.8 * tensorA - 1.5 * tensorB * (-tensorD) + 0.02 * tensorC / tensorE - 39.4

        val expected = fromArray(
            shape,
            (1..3).map {
                15.8 * (-4.5) - 1.5 * 10.9 * 72.9 + 0.02 * 789.3 / 553.1 - 39.4
            }.toDoubleArray()
        )

        val assignResult = zeros(shape)
        tensorA *= 15.8
        tensorB *= 1.5
        tensorB *= -tensorD
        tensorC *= 0.02
        tensorC /= tensorE
        assignResult += tensorA
        assignResult -= tensorB
        assignResult += tensorC
        assignResult += -39.4

        assertTrue(expected.mutableBuffer.array() contentEquals result.mutableBuffer.array())
        assertTrue(expected.mutableBuffer.array() contentEquals assignResult.mutableBuffer.array())
    }

    @Test
    fun testDot() = DoubleTensorAlgebra {
        val tensor1 = fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor11 = fromArray(intArrayOf(3, 2), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor2 = fromArray(intArrayOf(3), doubleArrayOf(10.0, 20.0, 30.0))
        val tensor3 = fromArray(intArrayOf(1, 1, 3), doubleArrayOf(-1.0, -2.0, -3.0))
        val tensor4 = fromArray(intArrayOf(2, 3, 3), (1..18).map { it.toDouble() }.toDoubleArray())
        val tensor5 = fromArray(intArrayOf(2, 3, 3), (1..18).map { 1 + it.toDouble() }.toDoubleArray())

        val res12 = tensor1.dot(tensor2)
        assertTrue(res12.mutableBuffer.array() contentEquals doubleArrayOf(140.0, 320.0))
        assertTrue(res12.shape contentEquals intArrayOf(2))

        val res32 = tensor3.dot(tensor2)
        assertTrue(res32.mutableBuffer.array() contentEquals doubleArrayOf(-140.0))
        assertTrue(res32.shape contentEquals intArrayOf(1, 1))

        val res22 = tensor2.dot(tensor2)
        assertTrue(res22.mutableBuffer.array() contentEquals doubleArrayOf(1400.0))
        assertTrue(res22.shape contentEquals intArrayOf(1))

        val res11 = tensor1.dot(tensor11)
        assertTrue(res11.mutableBuffer.array() contentEquals doubleArrayOf(22.0, 28.0, 49.0, 64.0))
        assertTrue(res11.shape contentEquals intArrayOf(2, 2))

        val res45 = tensor4.dot(tensor5)
        assertTrue(res45.mutableBuffer.array() contentEquals doubleArrayOf(
            36.0, 42.0, 48.0, 81.0, 96.0, 111.0, 126.0, 150.0, 174.0,
            468.0, 501.0, 534.0, 594.0, 636.0, 678.0, 720.0, 771.0, 822.0
        ))
        assertTrue(res45.shape contentEquals intArrayOf(2, 3, 3))

        val oneDimTensor1 = fromArray(intArrayOf(3), doubleArrayOf(1.0, 2.0, 3.0))
        val oneDimTensor2 = fromArray(intArrayOf(3), doubleArrayOf(4.0, 5.0, 6.0))
        val resOneDimTensors = oneDimTensor1.dot(oneDimTensor2)
        assertTrue(resOneDimTensors.mutableBuffer.array() contentEquals doubleArrayOf(32.0))
        assertTrue(resOneDimTensors.shape contentEquals intArrayOf(1))

        val twoDimTensor1 = fromArray(intArrayOf(2, 2), doubleArrayOf(1.0, 2.0, 3.0, 4.0))
        val twoDimTensor2 = fromArray(intArrayOf(2, 2), doubleArrayOf(5.0, 6.0, 7.0, 8.0))
        val resTwoDimTensors = twoDimTensor1.dot(twoDimTensor2)
        assertTrue(resTwoDimTensors.mutableBuffer.array() contentEquals doubleArrayOf(19.0, 22.0, 43.0, 50.0))
        assertTrue(resTwoDimTensors.shape contentEquals intArrayOf(2, 2))

        val oneDimTensor3 = fromArray(intArrayOf(2), doubleArrayOf(1.0, 2.0))
        val resOneDimTensorOnTwoDimTensor = oneDimTensor3.dot(twoDimTensor1)
        assertTrue(resOneDimTensorOnTwoDimTensor.mutableBuffer.array() contentEquals doubleArrayOf(7.0, 10.0))
        assertTrue(resOneDimTensorOnTwoDimTensor.shape contentEquals intArrayOf(2))

        val resTwoDimTensorOnOneDimTensor = twoDimTensor1.dot(oneDimTensor3)
        assertTrue(resTwoDimTensorOnOneDimTensor.mutableBuffer.array() contentEquals doubleArrayOf(5.0, 11.0))
        assertTrue(resTwoDimTensorOnOneDimTensor.shape contentEquals intArrayOf(2))
    }

    @Test
    fun testDiagonalEmbedding() = DoubleTensorAlgebra {
        val tensor1 = fromArray(intArrayOf(3), doubleArrayOf(10.0, 20.0, 30.0))
        val tensor2 = fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor3 = zeros(intArrayOf(2, 3, 4, 5))

        assertTrue(diagonalEmbedding(tensor3, 0, 3, 4).shape contentEquals
                intArrayOf(2, 3, 4, 5, 5))
        assertTrue(diagonalEmbedding(tensor3, 1, 3, 4).shape contentEquals
                intArrayOf(2, 3, 4, 6, 6))
        assertTrue(diagonalEmbedding(tensor3, 2, 0, 3).shape contentEquals
                intArrayOf(7, 2, 3, 7, 4))

        val diagonal1 = diagonalEmbedding(tensor1, 0, 1, 0)
        assertTrue(diagonal1.shape contentEquals intArrayOf(3, 3))
        assertTrue(diagonal1.mutableBuffer.array() contentEquals
                doubleArrayOf(10.0, 0.0, 0.0, 0.0, 20.0, 0.0, 0.0, 0.0, 30.0))

        val diagonal1Offset = diagonalEmbedding(tensor1, 1, 1, 0)
        assertTrue(diagonal1Offset.shape contentEquals intArrayOf(4, 4))
        assertTrue(diagonal1Offset.mutableBuffer.array() contentEquals
                doubleArrayOf(0.0, 0.0, 0.0, 0.0, 10.0, 0.0, 0.0, 0.0, 0.0, 20.0, 0.0, 0.0, 0.0, 0.0, 30.0, 0.0))

        val diagonal2 = diagonalEmbedding(tensor2, 1, 0, 2)
        assertTrue(diagonal2.shape contentEquals intArrayOf(4, 2, 4))
        assertTrue(diagonal2.mutableBuffer.array() contentEquals
                doubleArrayOf(
                    0.0, 1.0, 0.0, 0.0, 0.0, 4.0, 0.0, 0.0,
                    0.0, 0.0, 2.0, 0.0, 0.0, 0.0, 5.0, 0.0,
                    0.0, 0.0, 0.0, 3.0, 0.0, 0.0, 0.0, 6.0,
                    0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0))
    }

    @Test
    fun testEq() = DoubleTensorAlgebra {
        val tensor1 = fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor2 = fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor3 = fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 5.0))

        assertTrue(tensor1 eq tensor1)
        assertTrue(tensor1 eq tensor2)
        assertFalse(tensor1.eq(tensor3))

    }
}
