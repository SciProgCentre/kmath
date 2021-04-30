package space.kscience.kmath.tensors.core

import space.kscience.kmath.operations.invoke
import space.kscience.kmath.tensors.core.algebras.BroadcastDoubleTensorAlgebra
import space.kscience.kmath.tensors.core.algebras.DoubleTensorAlgebra
import kotlin.test.Test
import kotlin.test.assertTrue

internal class TestBroadcasting {

    @Test
    fun broadcastShapes() = DoubleTensorAlgebra.invoke {
        assertTrue(
            broadcastShapes(
                intArrayOf(2, 3), intArrayOf(1, 3), intArrayOf(1, 1, 1)
            ) contentEquals intArrayOf(1, 2, 3)
        )

        assertTrue(
            broadcastShapes(
                intArrayOf(6, 7), intArrayOf(5, 6, 1), intArrayOf(7), intArrayOf(5, 1, 7)
            ) contentEquals intArrayOf(5, 6, 7)
        )
    }

    @Test
    fun broadcastTo() = DoubleTensorAlgebra.invoke {
        val tensor1 = fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor2 = fromArray(intArrayOf(1, 3), doubleArrayOf(10.0, 20.0, 30.0))

        val res = broadcastTo(tensor2, tensor1.shape)
        assertTrue(res.shape contentEquals intArrayOf(2, 3))
        assertTrue(res.buffer.array() contentEquals doubleArrayOf(10.0, 20.0, 30.0, 10.0, 20.0, 30.0))
    }

    @Test
    fun broadcastTensors() = DoubleTensorAlgebra.invoke {
        val tensor1 = fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor2 = fromArray(intArrayOf(1, 3), doubleArrayOf(10.0, 20.0, 30.0))
        val tensor3 = fromArray(intArrayOf(1, 1, 1), doubleArrayOf(500.0))

        val res = broadcastTensors(tensor1, tensor2, tensor3)

        assertTrue(res[0].shape contentEquals intArrayOf(1, 2, 3))
        assertTrue(res[1].shape contentEquals intArrayOf(1, 2, 3))
        assertTrue(res[2].shape contentEquals intArrayOf(1, 2, 3))

        assertTrue(res[0].buffer.array() contentEquals doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        assertTrue(res[1].buffer.array() contentEquals doubleArrayOf(10.0, 20.0, 30.0, 10.0, 20.0, 30.0))
        assertTrue(res[2].buffer.array() contentEquals doubleArrayOf(500.0, 500.0, 500.0, 500.0, 500.0, 500.0))
    }

    @Test
    fun broadcastOuterTensors() = DoubleTensorAlgebra.invoke {
        val tensor1 = fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor2 = fromArray(intArrayOf(1, 3), doubleArrayOf(10.0, 20.0, 30.0))
        val tensor3 = fromArray(intArrayOf(1, 1, 1), doubleArrayOf(500.0))

        val res = broadcastOuterTensors(tensor1, tensor2, tensor3)

        assertTrue(res[0].shape contentEquals intArrayOf(1, 2, 3))
        assertTrue(res[1].shape contentEquals intArrayOf(1, 1, 3))
        assertTrue(res[2].shape contentEquals intArrayOf(1, 1, 1))

        assertTrue(res[0].buffer.array() contentEquals doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        assertTrue(res[1].buffer.array() contentEquals doubleArrayOf(10.0, 20.0, 30.0))
        assertTrue(res[2].buffer.array() contentEquals doubleArrayOf(500.0))
    }

    @Test
    fun broadcastOuterTensorsShapes() = DoubleTensorAlgebra.invoke {
        val tensor1 = fromArray(intArrayOf(2, 1, 3, 2, 3), DoubleArray(2 * 1 * 3 * 2 * 3) {0.0})
        val tensor2 = fromArray(intArrayOf(4, 2, 5, 1, 3, 3), DoubleArray(4 * 2 * 5 * 1 * 3 * 3) {0.0})
        val tensor3 = fromArray(intArrayOf(1, 1), doubleArrayOf(500.0))

        val res = broadcastOuterTensors(tensor1, tensor2, tensor3)

        assertTrue(res[0].shape contentEquals intArrayOf(4, 2, 5, 3, 2, 3))
        assertTrue(res[1].shape contentEquals intArrayOf(4, 2, 5, 3, 3, 3))
        assertTrue(res[2].shape contentEquals intArrayOf(4, 2, 5, 3, 1, 1))
    }

    @Test
    fun minusTensor() = BroadcastDoubleTensorAlgebra.invoke {
        val tensor1 = fromArray(intArrayOf(2, 3), doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0))
        val tensor2 = fromArray(intArrayOf(1, 3), doubleArrayOf(10.0, 20.0, 30.0))
        val tensor3 = fromArray(intArrayOf(1, 1, 1), doubleArrayOf(500.0))

        val tensor21 = tensor2 - tensor1
        val tensor31 = tensor3 - tensor1
        val tensor32 = tensor3 - tensor2

        assertTrue(tensor21.shape contentEquals intArrayOf(2, 3))
        assertTrue(tensor21.buffer.array() contentEquals doubleArrayOf(9.0, 18.0, 27.0, 6.0, 15.0, 24.0))

        assertTrue(tensor31.shape contentEquals intArrayOf(1, 2, 3))
        assertTrue(
            tensor31.buffer.array()
                    contentEquals doubleArrayOf(499.0, 498.0, 497.0, 496.0, 495.0, 494.0)
        )

        assertTrue(tensor32.shape contentEquals intArrayOf(1, 1, 3))
        assertTrue(tensor32.buffer.array() contentEquals doubleArrayOf(490.0, 480.0, 470.0))
    }

}