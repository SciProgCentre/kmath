package space.kscience.kmath.tensors.core

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
    fun testValue() = DoubleTensorAlgebra {
        val value = 12.5
        val tensor = fromArray(intArrayOf(1), doubleArrayOf(value))
        assertEquals(tensor.valueOrNull()!!, value)
    }

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