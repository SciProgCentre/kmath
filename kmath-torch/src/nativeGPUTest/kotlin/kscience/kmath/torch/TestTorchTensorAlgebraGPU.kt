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
    fun testTensorTransformations() =
        testingTensorTransformations(device = TorchDevice.TorchCUDA(0))

    @Test
    fun testBatchedSVD() =
        testingBatchedSVD(device = TorchDevice.TorchCUDA(0))

    @Test
    fun testBatchedSymEig() =
        testingBatchedSymEig(device = TorchDevice.TorchCUDA(0))

}
