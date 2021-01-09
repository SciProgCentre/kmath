package kscience.kmath.torch

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
}