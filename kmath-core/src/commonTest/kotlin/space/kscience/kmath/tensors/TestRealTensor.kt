package space.kscience.kmath.tensors


import space.kscience.kmath.structures.array
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestRealTensor {

    @Test
    fun valueTest() = RealTensorAlgebra {
        val value = 12.5
        val tensor = RealTensor(intArrayOf(1), doubleArrayOf(value))
        assertEquals(tensor.value(), value)
    }

    @Test
    fun stridesTest(){
        val tensor = RealTensor(intArrayOf(2,2), doubleArrayOf(3.5,5.8,58.4,2.4))
        assertEquals(tensor[intArrayOf(0,1)], 5.8)
        assertTrue(tensor.elements().map{ it.second }.toList().toDoubleArray() contentEquals tensor.buffer.array)
    }
}