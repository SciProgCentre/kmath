package kscience.kmath.torch

import kotlin.test.*


internal class TestAutogradGPU {
    @Test
    fun testAutoGrad() = testingAutoGrad(dim = 3, device = TorchDevice.TorchCUDA(0))

    @Test
    fun testBatchedAutoGrad() = testingBatchedAutoGrad(
        bath = intArrayOf(2), dim=3, device = TorchDevice.TorchCUDA(0))
}