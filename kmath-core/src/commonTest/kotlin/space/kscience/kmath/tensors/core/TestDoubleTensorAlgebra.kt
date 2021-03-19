package space.kscience.kmath.tensors.core


import kotlin.test.Test
import kotlin.test.assertTrue

class TestDoubleTensorAlgebra {

    @Test
    fun doublePlus() = DoubleTensorAlgebra {
        val tensor = fromArray(intArrayOf(2), doubleArrayOf(1.0, 2.0))
        val res = 10.0 + tensor
        assertTrue(res.buffer.array() contentEquals doubleArrayOf(11.0, 12.0))
    }

    @Test
    fun transpose1x1() = DoubleTensorAlgebra {
        val tensor = fromArray(intArrayOf(1), doubleArrayOf(0.0))
        val res = tensor.transpose(0, 0)

        assertTrue(res.buffer.array() contentEquals doubleArrayOf(0.0))
        assertTrue(res.shape contentEquals intArrayOf(1))
    }

    @Test
    fun transpose3x2() = DoubleTensorAlgebra {
        val tensor = fromArray(intArrayOf(3, 2), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val res = tensor.transpose(1, 0)

        assertTrue(res.buffer.array() contentEquals doubleArrayOf(1.0, 3.0, 5.0, 2.0, 4.0, 6.0))
        assertTrue(res.shape contentEquals intArrayOf(2, 3))
    }

    @Test
    fun transpose1x2x3() = DoubleTensorAlgebra {
        val tensor = fromArray(intArrayOf(1, 2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val res01 = tensor.transpose(0, 1)
        val res02 = tensor.transpose(0, 2)
        val res12 = tensor.transpose(1, 2)

        assertTrue(res01.shape contentEquals intArrayOf(2, 1, 3))
        assertTrue(res02.shape contentEquals intArrayOf(3, 2, 1))
        assertTrue(res12.shape contentEquals intArrayOf(1, 3, 2))

        assertTrue(res01.buffer.array() contentEquals doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        assertTrue(res02.buffer.array() contentEquals doubleArrayOf(1.0, 4.0, 2.0, 5.0, 3.0, 6.0))
        assertTrue(res12.buffer.array() contentEquals doubleArrayOf(1.0, 4.0, 2.0, 5.0, 3.0, 6.0))
    }

    @Test
    fun linearStructure() = DoubleTensorAlgebra {
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

        assertTrue(expected.buffer.array() contentEquals result.buffer.array())
        assertTrue(expected.buffer.array() contentEquals assignResult.buffer.array())
    }

}