package kscience.kmath.torch

import kscience.kmath.structures.asBuffer

import kotlinx.cinterop.memScoped
import kotlin.test.*


internal class TestTorchTensor {

    @Test
    fun intTensorLayout() = memScoped {
        val array = intArrayOf(7,8,9,2,6,5)
        val shape = intArrayOf(3,2)
        val tensor = TorchTensor.copyFromIntArray(scope=this, array=array, shape=shape)
        tensor.elements().forEach {
            assertEquals(tensor[it.first], it.second)
        }
        assertTrue(tensor.buffer.contentEquals(array.asBuffer()))
    }

    @Test
    fun floatTensorLayout() = memScoped {
        val array = floatArrayOf(7.5f,8.2f,9f,2.58f,6.5f,5f)
        val shape = intArrayOf(2,3)
        val tensor = TorchTensor.copyFromFloatArray(this, array, shape)
        tensor.elements().forEach {
            assertEquals(tensor[it.first], it.second)
        }
        assertTrue(tensor.buffer.contentEquals(array.asBuffer()))
    }

}