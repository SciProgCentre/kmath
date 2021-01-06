package kscience.kmath.torch

import kscience.kmath.structures.asBuffer

import kotlinx.cinterop.memScoped
import kotlin.test.*

class TestTorchTensorGPU {

    @Test
    fun cudaAvailability() {
        assertTrue(cudaAvailable())
    }

    @Test
    fun floatGPUTensorLayout() = memScoped {
        val array = (1..8).map { it * 2f }.toList().toFloatArray()
        val shape = intArrayOf(2, 2, 2)
        val tensor = TorchTensor.copyFromFloatArrayToGPU(this, array, shape, 0)
        tensor.elements().forEach {
            assertEquals(tensor[it.first], it.second)
        }
        assertTrue(tensor.buffer.contentEquals(array.asBuffer()))
    }
}