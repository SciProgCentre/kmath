package kscience.kmath.torch

import kotlin.test.*


internal class TestAutogradGPU {
    @Test
    fun testAutoGrad() = testingAutoGrad(dim = 3, device = TorchDevice.TorchCUDA(0))
}