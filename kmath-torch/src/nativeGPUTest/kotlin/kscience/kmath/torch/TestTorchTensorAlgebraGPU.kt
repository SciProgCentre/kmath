package kscience.kmath.torch

import kotlin.test.*


class TestTorchTensorAlgebraGPU {

    @Test
    fun testScalarProduct() =
        testingScalarProduct(device = TorchDevice.TorchCUDA(0))

    @Test
    fun testMatrixMultiplication() =
        testingMatrixMultiplication(device = TorchDevice.TorchCUDA(0))

    @Test
    fun testLinearStructure() =
        testingLinearStructure(device = TorchDevice.TorchCUDA(0))

    @Test
    fun testAutoGrad() = testingAutoGrad(dim = 3, device = TorchDevice.TorchCUDA(0))
}
