package kscience.kmath.torch

import kotlin.test.*


class TestTorchTensorAlgebraGPU {

    @Test
    fun testScalarProduct() =
        testingScalarProduct(device = Device.CUDA(0))

    @Test
    fun testMatrixMultiplication() =
        testingMatrixMultiplication(device = Device.CUDA(0))

    @Test
    fun testLinearStructure() =
        testingLinearStructure(device = Device.CUDA(0))

    @Test
    fun testTensorTransformations() =
        testingTensorTransformations(device = Device.CUDA(0))

    @Test
    fun testBatchedSVD() =
        testingBatchedSVD(device = Device.CUDA(0))

    @Test
    fun testBatchedSymEig() =
        testingBatchedSymEig(device = Device.CUDA(0))

}
