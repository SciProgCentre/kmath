package space.kscience.kmath.tensors

import space.kscience.kmath.structures.array
import kotlin.test.Test
import kotlin.test.assertTrue

class TestRealTensorAlgebra {

    @Test
    fun doublePlus() = RealTensorAlgebra {
        val tensor = RealTensor(intArrayOf(2), doubleArrayOf(1.0, 2.0))
        val res = 10.0 + tensor
        assertTrue(res.buffer.array contentEquals doubleArrayOf(11.0,12.0))
    }

}