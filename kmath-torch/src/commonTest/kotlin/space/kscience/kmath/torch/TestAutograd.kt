@file:Suppress("NOTHING_TO_INLINE")

package space.kscience.kmath.torch

import kotlin.test.assertTrue

internal inline fun <TorchTensorType : TorchTensorOverField<Float>,
        TorchTensorAlgebraType : TorchTensorPartialDivisionAlgebra<Float, FloatArray, TorchTensorType>>
        TorchTensorAlgebraType.testingAutoGrad(device: Device = Device.CPU): Unit {
    setSeed(SEED)
    val dim = 3
    val tensorX = randNormal(shape = intArrayOf(dim), device = device)
    val randFeatures = randNormal(shape = intArrayOf(dim, dim), device = device)
    val tensorSigma = randFeatures + randFeatures.transpose(0, 1)
    val tensorMu = randNormal(shape = intArrayOf(dim), device = device)

    val expressionAtX = withGradAt(tensorX, { x ->
        0.5f * (x dot (tensorSigma dot x)) + (tensorMu dot x) + 25.9f
    })

    val gradientAtX = expressionAtX.grad(tensorX, retainGraph = true)
    val hessianAtX = expressionAtX hess tensorX
    val expectedGradientAtX = (tensorSigma dot tensorX) + tensorMu

    val error = (gradientAtX - expectedGradientAtX).abs().sum().value() +
            (hessianAtX - tensorSigma).abs().sum().value()
    assertTrue(error < TOLERANCE)
}

internal inline fun <TorchTensorType : TorchTensorOverField<Float>,
        TorchTensorAlgebraType : TorchTensorPartialDivisionAlgebra<Float, FloatArray, TorchTensorType>>
        TorchTensorAlgebraType.testingBatchedAutoGrad(device: Device = Device.CPU): Unit {
    setSeed(SEED)
    val batch = intArrayOf(2)
    val dim = 2
    val tensorX = randNormal(shape = batch + intArrayOf(1, dim), device = device)
    val randFeatures = randNormal(shape = batch + intArrayOf(dim, dim), device = device)
    val tensorSigma = randFeatures + randFeatures.transpose(-2, -1)
    val tensorMu = randNormal(shape = batch + intArrayOf(1, dim), device = device)

    val expressionAtX = withGradAt(tensorX, { x ->
        val xt = x.transpose(-1, -2)
        0.5f * (x dot (tensorSigma dot xt)) + (tensorMu dot xt) + 58.2f
    })
    expressionAtX.sumAssign()

    val gradientAtX = expressionAtX grad tensorX
    val expectedGradientAtX = (tensorX dot tensorSigma) + tensorMu

    val error = (gradientAtX - expectedGradientAtX).abs().sum().value()
    assertTrue(error < TOLERANCE)
}

