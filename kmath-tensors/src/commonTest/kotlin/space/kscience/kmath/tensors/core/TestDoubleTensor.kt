/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.nd.DefaultStrides
import space.kscience.kmath.nd.MutableBufferND
import space.kscience.kmath.nd.as1D
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.toDoubleArray
import space.kscience.kmath.tensors.core.internal.array
import space.kscience.kmath.tensors.core.internal.asTensor
import space.kscience.kmath.tensors.core.internal.matrixSequence
import space.kscience.kmath.tensors.core.internal.toBufferedTensor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class TestDoubleTensor {

    @Test
    fun testFullLike() = DoubleTensorAlgebra {
        val shape = intArrayOf(2, 3)
        val buffer = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
        val tensor = DoubleTensor(shape, buffer)
        val value = 12.5
        assertTrue { tensor.fullLike(value) eq DoubleTensor(shape, buffer.map { value }.toDoubleArray() ) }
    }

    @Test
    fun testOnesLike() = DoubleTensorAlgebra {
        val shape = intArrayOf(2, 3)
        val buffer = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
        val tensor = DoubleTensor(shape, buffer)
        assertTrue { tensor.onesLike() eq DoubleTensor(shape, buffer.map { 1.0 }.toDoubleArray() ) }
    }

    @Test
    fun testRowsByIndices() = DoubleTensorAlgebra {
        val shape = intArrayOf(2, 2)
        val buffer = doubleArrayOf(1.0, 2.0, -3.0, 4.0)
        val tensor = fromArray(shape, buffer)
        assertTrue { tensor.rowsByIndices(intArrayOf(0)) eq DoubleTensor(intArrayOf(1, 2), doubleArrayOf(1.0, 2.0)) }
        assertTrue { tensor.rowsByIndices(intArrayOf(0, 1)) eq tensor }
    }

    @Test
    fun testTimes() = DoubleTensorAlgebra {
        val shape = intArrayOf(2, 2)
        val buffer = doubleArrayOf(1.0, 2.0, -3.0, 4.0)
        val tensor = DoubleTensor(shape, buffer)
        val value = 3
        assertTrue { tensor.times(value).toBufferedTensor() eq DoubleTensor(shape, buffer.map { x -> value * x }.toDoubleArray()) }
        val buffer2 = doubleArrayOf(7.0, -8.0, -5.0, 2.0)
        val tensor2 = DoubleTensor(shape, buffer2)
        assertTrue {tensor.times(tensor2).toBufferedTensor() eq DoubleTensor(shape, doubleArrayOf(7.0, -16.0, 15.0, 8.0)) }
    }

    @Test
    fun testValue() = DoubleTensorAlgebra {
        val value = 12.5
        val tensor = fromArray(intArrayOf(1), doubleArrayOf(value))
        assertEquals(tensor.value(), value)
    }

    @OptIn(PerformancePitfall::class)
    @Test
    fun testStrides() = DoubleTensorAlgebra {
        val tensor = fromArray(intArrayOf(2, 2), doubleArrayOf(3.5, 5.8, 58.4, 2.4))
        assertEquals(tensor[intArrayOf(0, 1)], 5.8)
        assertTrue(
            tensor.elements().map { it.second }.toList().toDoubleArray() contentEquals tensor.mutableBuffer.toDoubleArray()
        )
    }

    @Test
    fun testGet() = DoubleTensorAlgebra {
        val tensor = fromArray(intArrayOf(1, 2, 2), doubleArrayOf(3.5, 5.8, 58.4, 2.4))
        val matrix = tensor[0].as2D()
        assertEquals(matrix[0, 1], 5.8)

        val vector = tensor[0][1].as1D()
        assertEquals(vector[0], 58.4)

        matrix[0, 1] = 77.89
        assertEquals(tensor[intArrayOf(0, 0, 1)], 77.89)

        vector[0] = 109.56
        assertEquals(tensor[intArrayOf(0, 1, 0)], 109.56)

        tensor.matrixSequence().forEach {
            val a = it.asTensor()
            val secondRow = a[1].as1D()
            val secondColumn = a.transpose(0, 1)[1].as1D()
            assertEquals(secondColumn[0], 77.89)
            assertEquals(secondRow[1], secondColumn[1])
        }
    }

    @Test
    fun testNoBufferProtocol() {

        // create buffer
        val doubleArray = DoubleBuffer(doubleArrayOf(1.0, 2.0, 3.0))

        // create ND buffers, no data is copied
        val ndArray = MutableBufferND(DefaultStrides(intArrayOf(3)), doubleArray)

        // map to tensors
        val bufferedTensorArray = ndArray.toBufferedTensor() // strides are flipped so data copied
        val tensorArray = bufferedTensorArray.asTensor() // data not contiguous so copied again

        val tensorArrayPublic = ndArray.toDoubleTensor() // public API, data copied twice
        val sharedTensorArray = tensorArrayPublic.toDoubleTensor() // no data copied by matching type

        assertTrue(tensorArray.mutableBuffer.array() contentEquals sharedTensorArray.mutableBuffer.array())

        tensorArray[intArrayOf(0)] = 55.9
        assertEquals(tensorArrayPublic[intArrayOf(0)], 1.0)

        tensorArrayPublic[intArrayOf(0)] = 55.9
        assertEquals(sharedTensorArray[intArrayOf(0)], 55.9)
        assertEquals(bufferedTensorArray[intArrayOf(0)], 1.0)

        bufferedTensorArray[intArrayOf(0)] = 55.9
        assertEquals(ndArray[intArrayOf(0)], 1.0)

    }
}
