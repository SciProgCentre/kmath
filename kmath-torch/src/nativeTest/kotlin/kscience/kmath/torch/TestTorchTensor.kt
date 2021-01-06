package kscience.kmath.torch

import kscience.kmath.structures.asBuffer

import kotlinx.cinterop.memScoped
import kotlin.test.*


internal class TestTorchTensor {

    @Test
    fun intTensorLayout() = memScoped {
        val array = (1..24).toList().toIntArray()
        val shape = intArrayOf(3, 2, 4)
        val tensor = TorchTensor.copyFromIntArray(scope = this, array = array, shape = shape)
        tensor.elements().forEach {
            assertEquals(tensor[it.first], it.second)
        }
        assertTrue(tensor.buffer.contentEquals(array.asBuffer()))
    }

    @Test
    fun floatTensorLayout() = memScoped {
        val array = (1..10).map { it + 50f }.toList().toFloatArray()
        val shape = intArrayOf(10)
        val tensor = TorchTensor.copyFromFloatArray(this, array, shape)
        tensor.elements().forEach {
            assertEquals(tensor[it.first], it.second)
        }
        assertTrue(tensor.buffer.contentEquals(array.asBuffer()))
    }

}