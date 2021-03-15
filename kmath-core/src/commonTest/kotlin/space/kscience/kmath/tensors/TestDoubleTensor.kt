package space.kscience.kmath.tensors


import space.kscience.kmath.structures.toDoubleArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestDoubleTensor {

    @Test
    fun valueTest() = DoubleTensorAlgebra {
        val value = 12.5
        val tensor = DoubleTensor(intArrayOf(1), doubleArrayOf(value))
        assertEquals(tensor.value(), value)
    }

    @Test
    fun stridesTest(){
        val tensor = DoubleTensor(intArrayOf(2,2), doubleArrayOf(3.5,5.8,58.4,2.4))
        assertEquals(tensor[intArrayOf(0,1)], 5.8)
        assertTrue(tensor.elements().map{ it.second }.toList().toDoubleArray() contentEquals tensor.buffer.toDoubleArray())
    }

    @Test
    fun getTest() = DoubleTensorAlgebra {
        val tensor = DoubleTensor(intArrayOf(2,2), doubleArrayOf(3.5,5.8,58.4,2.4))
        assertTrue(tensor[0].elements().map{ it.second }.toList().toDoubleArray() contentEquals doubleArrayOf(3.5,5.8))
    }
}