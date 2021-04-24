package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.DefaultStrides
import space.kscience.kmath.nd.MutableBufferND
import space.kscience.kmath.nd.as1D
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.asMutableBuffer
import space.kscience.kmath.structures.toDoubleArray
import space.kscience.kmath.tensors.core.algebras.DoubleTensorAlgebra
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestDoubleTensor {

    @Test
    fun valueTest() = DoubleTensorAlgebra {
        val value = 12.5
        val tensor = fromArray(intArrayOf(1), doubleArrayOf(value))
        assertEquals(tensor.value(), value)
    }

    @Test
    fun stridesTest() = DoubleTensorAlgebra {
        val tensor = fromArray(intArrayOf(2,2), doubleArrayOf(3.5,5.8,58.4,2.4))
        assertEquals(tensor[intArrayOf(0,1)], 5.8)
        assertTrue(tensor.elements().map{ it.second }.toList().toDoubleArray() contentEquals tensor.buffer.toDoubleArray())
    }

    @Test
    fun getTest() = DoubleTensorAlgebra {
        val tensor = fromArray(intArrayOf(1,2,2), doubleArrayOf(3.5,5.8,58.4,2.4))
        val matrix = tensor[0].as2D()
        assertEquals(matrix[0,1], 5.8)

        val vector = tensor[0][1].as1D()
        assertEquals(vector[0], 58.4)

        matrix[0,1] = 77.89
        assertEquals(tensor[intArrayOf(0,0,1)], 77.89)

        vector[0] = 109.56
        assertEquals(tensor[intArrayOf(0,1,0)], 109.56)

        tensor.matrixSequence().forEach {
            val a = it.asTensor()
            val secondRow = a[1].as1D()
            val secondColumn = a.transpose(0,1)[1].as1D()
            assertEquals(secondColumn[0], 77.89)
            assertEquals(secondRow[1], secondColumn[1])
        }
    }

    @Test
    fun bufferProtocol() {

        // create buffers
        val doubleBuffer = DoubleBuffer(doubleArrayOf(1.0,2.0,3.0))
        val doubleList = MutableList(3, doubleBuffer::get)

        // create ND buffers
        val ndBuffer = MutableBufferND(DefaultStrides(intArrayOf(3)), doubleBuffer)
        val ndList = MutableBufferND(DefaultStrides(intArrayOf(3)), doubleList.asMutableBuffer())

        // map to tensors
        val bufferedTensorBuffer = ndBuffer.toBufferedTensor() // strides are flipped
        val tensorBuffer = bufferedTensorBuffer.asTensor() // no data copied

        val bufferedTensorList = ndList.toBufferedTensor() // strides are flipped
        val tensorList = bufferedTensorList.asTensor() // data copied

        tensorBuffer[intArrayOf(0)] = 55.9
        assertEquals(ndBuffer[intArrayOf(0)], 55.9)
        assertEquals(doubleBuffer[0], 55.9)

        tensorList[intArrayOf(0)] = 55.9
        assertEquals(ndList[intArrayOf(0)], 1.0)
        assertEquals(doubleList[0], 1.0)

        ndList[intArrayOf(0)] = 55.9
        assertEquals(doubleList[0], 55.9)
    }
}