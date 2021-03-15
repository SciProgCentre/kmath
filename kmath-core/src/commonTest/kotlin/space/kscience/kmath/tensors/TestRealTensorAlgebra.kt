package space.kscience.kmath.tensors


import kotlin.test.Test
import kotlin.test.assertTrue

class TestRealTensorAlgebra {

    @Test
    fun doublePlus() = RealTensorAlgebra {
        val tensor = DoubleTensor(intArrayOf(2), doubleArrayOf(1.0, 2.0))
        val res = 10.0 + tensor
        assertTrue(res.buffer.unsafeToDoubleArray() contentEquals doubleArrayOf(11.0,12.0))
    }

    @Test
    fun transpose1x1() = RealTensorAlgebra {
        val tensor = DoubleTensor(intArrayOf(1), doubleArrayOf(0.0))
        val res = tensor.transpose(0, 0)

        assertTrue(res.buffer.unsafeToDoubleArray() contentEquals doubleArrayOf(0.0))
        assertTrue(res.shape contentEquals intArrayOf(1))
    }

    @Test
    fun transpose3x2() = RealTensorAlgebra {
        val tensor = DoubleTensor(intArrayOf(3, 2), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val res = tensor.transpose(1, 0)

        assertTrue(res.buffer.unsafeToDoubleArray() contentEquals doubleArrayOf(1.0, 3.0, 5.0, 2.0, 4.0, 6.0))
        assertTrue(res.shape contentEquals intArrayOf(2, 3))
    }

    @Test
    fun transpose1x2x3() = RealTensorAlgebra {
        val tensor = DoubleTensor(intArrayOf(1, 2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val res01 = tensor.transpose(0, 1)
        val res02 = tensor.transpose(0, 2)
        val res12 = tensor.transpose(1, 2)

        assertTrue(res01.shape contentEquals intArrayOf(2, 1, 3))
        assertTrue(res02.shape contentEquals intArrayOf(3, 2, 1))
        assertTrue(res12.shape contentEquals intArrayOf(1, 3, 2))

        assertTrue(res01.buffer.unsafeToDoubleArray() contentEquals doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        assertTrue(res02.buffer.unsafeToDoubleArray() contentEquals doubleArrayOf(1.0, 4.0, 2.0, 5.0, 3.0, 6.0))
        assertTrue(res12.buffer.unsafeToDoubleArray() contentEquals doubleArrayOf(1.0, 4.0, 2.0, 5.0, 3.0, 6.0))
    }

    @Test
    fun broadcastShapes() = RealTensorAlgebra {
        assertTrue(broadcastShapes(
            intArrayOf(2, 3), intArrayOf(1, 3), intArrayOf(1, 1, 1)
        ) contentEquals intArrayOf(1, 2, 3))

        assertTrue(broadcastShapes(
            intArrayOf(6, 7), intArrayOf(5, 6, 1), intArrayOf(7,), intArrayOf(5, 1, 7)
        ) contentEquals intArrayOf(5, 6, 7))
    }

    @Test
    fun broadcastTensors() = RealTensorAlgebra {
        val tensor1 = DoubleTensor(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor2 = DoubleTensor(intArrayOf(1, 3), doubleArrayOf(10.0, 20.0, 30.0))
        val tensor3 = DoubleTensor(intArrayOf(1, 1, 1), doubleArrayOf(500.0))

        val res = broadcastTensors(tensor1, tensor2, tensor3)

        assertTrue(res[0].shape contentEquals intArrayOf(1, 2, 3))
        assertTrue(res[1].shape contentEquals intArrayOf(1, 2, 3))
        assertTrue(res[2].shape contentEquals intArrayOf(1, 2, 3))

        assertTrue(res[0].buffer.unsafeToDoubleArray() contentEquals doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        assertTrue(res[1].buffer.unsafeToDoubleArray() contentEquals doubleArrayOf(10.0, 20.0, 30.0, 10.0, 20.0, 30.0))
        assertTrue(res[2].buffer.unsafeToDoubleArray() contentEquals doubleArrayOf(500.0, 500.0, 500.0, 500.0, 500.0, 500.0))
    }

    @Test
    fun minusTensor() = RealTensorAlgebra {
        val tensor1 = DoubleTensor(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor2 = DoubleTensor(intArrayOf(1, 3), doubleArrayOf(10.0, 20.0, 30.0))
        val tensor3 = DoubleTensor(intArrayOf(1, 1, 1), doubleArrayOf(500.0))

        assertTrue((tensor2 - tensor1).shape contentEquals intArrayOf(2, 3))
        assertTrue((tensor2 - tensor1).buffer.unsafeToDoubleArray() contentEquals doubleArrayOf(9.0, 18.0, 27.0, 6.0, 15.0, 24.0))

        assertTrue((tensor3 - tensor1).shape contentEquals intArrayOf(1, 2, 3))
        assertTrue((tensor3 - tensor1).buffer.unsafeToDoubleArray()
                contentEquals doubleArrayOf(499.0, 498.0, 497.0, 496.0, 495.0, 494.0))

        assertTrue((tensor3 - tensor2).shape contentEquals intArrayOf(1, 1, 3))
        assertTrue((tensor3 - tensor2).buffer.unsafeToDoubleArray() contentEquals doubleArrayOf(490.0, 480.0, 470.0))
    }

}