package kscience.kmath.torch

import kotlin.test.*


internal class TestUtilsGPU {

    @Test
    fun testCudaAvailable() {
        assertTrue(cudaAvailable())
    }

    @Test
    fun testSetSeed() = testingSetSeed(Device.CUDA(0))

}
