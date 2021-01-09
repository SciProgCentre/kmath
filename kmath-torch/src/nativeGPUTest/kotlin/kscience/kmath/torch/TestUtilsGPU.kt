package kscience.kmath.torch

import kotlin.test.*


internal class TestUtilsGPU {

    @Test
    fun testCudaAvailable() {
        assertTrue(cudaAvailable())
    }

    @Test
    fun testSetSeed() = testingSetSeed(TorchDevice.TorchCUDA(0))

    @Test
    fun testReadmeFactory() = TorchTensorRealAlgebra {

        val realTensor: TorchTensorReal = copyFromArray(
            array = (1..10).map { it + 50.0 }.toList().toDoubleArray(),
            shape = intArrayOf(2,5)
        )
        println(realTensor)

        val gpuRealTensor: TorchTensorReal = copyFromArray(
            array = (1..8).map { it * 2.5 }.toList().toDoubleArray(),
            shape = intArrayOf(2, 2, 2),
            device = TorchDevice.TorchCUDA(0)
        )
        println(gpuRealTensor)
    }

}
