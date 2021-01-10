package kscience.kmath.torch

import kotlin.test.*


internal class TestUtilsGPU {

    @Test
    fun testCudaAvailable() {
        assertTrue(cudaAvailable())
    }

    @Test
    fun testSetSeed() = testingSetSeed(TorchDevice.TorchCUDA(0))

}
