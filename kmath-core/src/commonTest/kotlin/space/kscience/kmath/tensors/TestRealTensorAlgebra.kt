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

    @Test
    fun transpose1x1() = RealTensorAlgebra {
        val tensor = RealTensor(intArrayOf(1), doubleArrayOf(0.0))
        val res = tensor.transpose(0, 0)

        assertTrue(res.buffer.array contentEquals doubleArrayOf(0.0))
        assertTrue(res.shape contentEquals intArrayOf(1))
    }

    @Test
    fun transpose3x2() = RealTensorAlgebra {
        val tensor = RealTensor(intArrayOf(3, 2), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val res = tensor.transpose(1, 0)

        assertTrue(res.buffer.array contentEquals doubleArrayOf(1.0, 3.0, 5.0, 2.0, 4.0, 6.0))
        assertTrue(res.shape contentEquals intArrayOf(2, 3))
    }

    @Test
    fun transpose1x2x3() = RealTensorAlgebra {
        val tensor = RealTensor(intArrayOf(1, 2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val res01 = tensor.transpose(0, 1)
        val res02 = tensor.transpose(0, 2)
        val res12 = tensor.transpose(1, 2)

        assertTrue(res01.shape contentEquals intArrayOf(2, 1, 3))
        assertTrue(res02.shape contentEquals intArrayOf(3, 2, 1))
        assertTrue(res12.shape contentEquals intArrayOf(1, 3, 2))

        assertTrue(res01.buffer.array contentEquals doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        assertTrue(res02.buffer.array contentEquals doubleArrayOf(1.0, 4.0, 2.0, 5.0, 3.0, 6.0))
        assertTrue(res12.buffer.array contentEquals doubleArrayOf(1.0, 4.0, 2.0, 5.0, 3.0, 6.0))
    }

    @Test
    fun transposeAssign1x2() = RealTensorAlgebra {
        val tensor = RealTensor(intArrayOf(1,2), doubleArrayOf(1.0, 2.0))
        tensor.transposeAssign(0, 1)

        assertTrue(tensor.buffer.array contentEquals doubleArrayOf(1.0, 2.0))
        assertTrue(tensor.shape contentEquals intArrayOf(2, 1))
    }

    @Test
    fun transposeAssign2x3() = RealTensorAlgebra {
        val tensor = RealTensor(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        tensor.transposeAssign(1, 0)

        assertTrue(tensor.buffer.array contentEquals doubleArrayOf(1.0, 4.0, 2.0, 5.0, 3.0, 6.0))
        assertTrue(tensor.shape contentEquals intArrayOf(3, 2))
    }
}