package kscience.kmath.torch

import kotlin.test.*

internal fun testingAutoGrad(dim: Int, device: TorchDevice = TorchDevice.TorchCPU): Unit {
    TorchTensorRealAlgebra {
        setSeed(SEED)
        val tensorX = randNormal(shape = intArrayOf(dim), device = device)
        tensorX.requiresGrad = true
        val randFeatures = randNormal(shape = intArrayOf(dim, dim), device = device)
        val tensorSigma = randFeatures + randFeatures.transpose(0,1)
        val tensorMu = randNormal(shape = intArrayOf(dim), device = device)

        val expressionAtX =
            0.5 * (tensorX dot (tensorSigma dot tensorX)) + (tensorMu dot tensorX) + 25.9

        val gradientAtX = expressionAtX grad tensorX
        val expectedGradientAtX = (tensorSigma dot tensorX) + tensorMu

        val error = (gradientAtX - expectedGradientAtX).abs().sum().value()
        assertTrue(error < TOLERANCE)
    }
}

internal class TestAutograd {
    @Test
    fun testAutoGrad() = testingAutoGrad(dim = 100)
}