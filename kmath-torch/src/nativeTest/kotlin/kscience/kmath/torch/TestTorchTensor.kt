package kscience.kmath.torch

import kotlinx.cinterop.*
import kotlin.test.*

internal fun testingCopyFromArray(device: TorchDevice = TorchDevice.TorchCPU): Unit {
    TorchTensorRealAlgebra {
        val array = (1..24).map { 10.0 * it * it }.toDoubleArray()
        val shape = intArrayOf(2, 3, 4)
        val tensor = copyFromArray(array, shape = shape, device = device)
        val copyOfTensor = tensor.copy()
        tensor[intArrayOf(0, 0)] = 0.1
        assertTrue(copyOfTensor.copyToArray() contentEquals array)
        assertEquals(0.1, tensor[intArrayOf(0, 0)])
    }
}


class TestTorchTensor {

    @Test
    fun testCopyFromArray() = testingCopyFromArray()

    @Test
    fun testCopyLessDataTransferOnCPU() = memScoped {
        val data = allocArray<DoubleVar>(1)
        data[0] = 1.0
        TorchTensorRealAlgebra {
            val tensor = fromBlob(data, intArrayOf(1))
            assertEquals(tensor[intArrayOf(0)], 1.0)
            data[0] = 2.0
            assertEquals(tensor[intArrayOf(0)], 2.0)
            val tensorData = tensor.getData()
            tensorData[0] = 3.0
            println(assertEquals(tensor[intArrayOf(0)], 3.0))
        }
        assertEquals(data[0], 3.0)
    }

    @Test
    fun testRequiresGrad() = TorchTensorRealAlgebra {
        val tensor = randNormal(intArrayOf(3))
        assertTrue(!tensor.requiresGrad)
        tensor.requiresGrad = true
        assertTrue(tensor.requiresGrad)
        tensor.requiresGrad = false
        assertTrue(!tensor.requiresGrad)
        tensor.requiresGrad = true
        val detachedTensor = tensor.detachFromGraph()
        assertTrue(!detachedTensor.requiresGrad)
    }
}