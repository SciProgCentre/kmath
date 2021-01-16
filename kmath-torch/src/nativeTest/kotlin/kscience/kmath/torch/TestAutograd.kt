package kscience.kmath.torch

import kotlin.test.*

internal fun testingAutoGrad(dim: Int, device: Device = Device.CPU): Unit {
    TorchTensorRealAlgebra {
        setSeed(SEED)

        val tensorX = randNormal(shape = intArrayOf(dim), device = device)
        val randFeatures = randNormal(shape = intArrayOf(dim, dim), device = device)
        val tensorSigma = randFeatures + randFeatures.transpose(0, 1)
        val tensorMu = randNormal(shape = intArrayOf(dim), device = device)

        val expressionAtX = withGradAt(tensorX, { x ->
            0.5 * (x dot (tensorSigma dot x)) + (tensorMu dot x) + 25.9
        })

        val gradientAtX = expressionAtX.grad(tensorX, retainGraph = true)
        val hessianAtX = expressionAtX hess tensorX
        val expectedGradientAtX = (tensorSigma dot tensorX) + tensorMu

        val error = (gradientAtX - expectedGradientAtX).abs().sum().value() +
                (hessianAtX - tensorSigma).abs().sum().value()
        assertTrue(error < TOLERANCE)
    }
}

internal fun testingBatchedAutoGrad(
    bath: IntArray,
    dim: Int,
    device: Device = Device.CPU
): Unit {
    TorchTensorRealAlgebra {
        setSeed(SEED)

        val tensorX = randNormal(shape = bath + intArrayOf(1, dim), device = device)
        val randFeatures = randNormal(shape = bath + intArrayOf(dim, dim), device = device)
        val tensorSigma = randFeatures + randFeatures.transpose(-2, -1)
        val tensorMu = randNormal(shape = bath + intArrayOf(1, dim), device = device)

        val expressionAtX = withGradAt(tensorX, { x ->
            val xt = x.transpose(-1, -2)
            0.5 * (x dot (tensorSigma dot xt)) + (tensorMu dot xt) + 58.2
        })
        expressionAtX.sumAssign()

        val gradientAtX = expressionAtX grad tensorX
        val expectedGradientAtX = (tensorX dot tensorSigma) + tensorMu

        val error = (gradientAtX - expectedGradientAtX).abs().sum().value()
        assertTrue(error < TOLERANCE)
    }
}


internal class TestAutograd {
    @Test
    fun testAutoGrad() = testingAutoGrad(dim = 100)

    @Test
    fun testBatchedAutoGrad() = testingBatchedAutoGrad(bath = intArrayOf(2, 10), dim = 30)
}