package kscience.kmath.torch


import kotlin.test.*
import kotlin.time.measureTime


class TestTorchTensorAlgebra {

    @Test
    fun swappingTensors() = TorchTensorFloatAlgebra {
        val tensorA = copyFromArray(floatArrayOf(1f, 2f, 3f), intArrayOf(3))
        val tensorB = tensorA.copy()
        val tensorC = copyFromArray(floatArrayOf(4f, 5f, 6f), intArrayOf(3))
        tensorA swap tensorC
        assertTrue(tensorB.asBuffer().contentEquals(tensorC.asBuffer()))
    }

    @Test
    fun dotOperation() = TorchTensorFloatAlgebra {
        setSeed(987654)
        var tensorA = randn(intArrayOf(1000, 1000))
        val tensorB = randn(intArrayOf(1000, 1000))
        measureTime {
            repeat(100) {
                TorchTensorFloatAlgebra {
                    tensorA swap (tensorA dot tensorB)
                }
            }
        }.also(::println)
        assertTrue(tensorA.shape contentEquals tensorB.shape)
    }


}

