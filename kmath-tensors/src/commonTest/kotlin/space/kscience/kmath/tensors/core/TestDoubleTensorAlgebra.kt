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

    @Test
    fun dot() = DoubleTensorAlgebra {
        val tensor1 = fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor11 = fromArray(intArrayOf(3, 2), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor2 = fromArray(intArrayOf(3), doubleArrayOf(10.0, 20.0, 30.0))
        val tensor3 = fromArray(intArrayOf(1, 1, 3), doubleArrayOf(-1.0, -2.0, -3.0))

        val res12 = tensor1.dot(tensor2)
        assertTrue(res12.buffer.array() contentEquals doubleArrayOf(140.0, 320.0))
        assertTrue(res12.shape contentEquals intArrayOf(2))

        val res32 = tensor3.dot(tensor2)
        assertTrue(res32.buffer.array() contentEquals doubleArrayOf(-140.0))
        assertTrue(res32.shape contentEquals intArrayOf(1, 1))

        val res22 = tensor2.dot(tensor2)
        assertTrue(res22.buffer.array() contentEquals doubleArrayOf(1400.0))
        assertTrue(res22.shape contentEquals intArrayOf(1))

        val res11 = tensor1.dot(tensor11)
        assertTrue(res11.buffer.array() contentEquals doubleArrayOf(22.0, 28.0, 49.0, 64.0))
        assertTrue(res11.shape contentEquals intArrayOf(2, 2))

        var tensor4 = fromArray(intArrayOf(10, 3, 4), DoubleArray(10 * 3 * 4) {0.0})
        var tensor5 = fromArray(intArrayOf(10, 4, 5), DoubleArray(10 * 4 * 5) {0.0})
        assertTrue(tensor4.dot(tensor5).shape contentEquals intArrayOf(10, 3, 5))

        tensor4 = fromArray(intArrayOf(10, 3, 4), DoubleArray(10 * 3 * 4) {0.0})
        tensor5 = fromArray(intArrayOf(4, 5), DoubleArray(4 * 5) {0.0})
        assertTrue(tensor4.dot(tensor5).shape contentEquals intArrayOf(10, 3, 5))

        tensor4 = fromArray(intArrayOf(4, 2, 1, 3, 8, 1), DoubleArray(4 * 2 * 1 * 3 * 8 * 1) {0.0})
        tensor5 = fromArray(intArrayOf(5, 1, 2, 8, 3, 1, 5), DoubleArray(5 * 1 * 2 * 8 * 3 * 1 * 5) {0.0})
        assertTrue(tensor4.dot(tensor5).shape contentEquals intArrayOf(5, 4, 2, 8, 3, 8, 5))
    }

    @Test
    fun testContentEqual() = DoubleTensorAlgebra {
        //TODO()
    }
}