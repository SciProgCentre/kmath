package kscience.kmath.torch

import kscience.kmath.linear.RealMatrixContext
import kscience.kmath.operations.invoke
import kscience.kmath.structures.Matrix
import kotlin.math.abs
import kotlin.math.exp
import kotlin.test.*

internal fun testingScalarProduct(device: TorchDevice = TorchDevice.TorchCPU): Unit {
    TorchTensorRealAlgebra {
        val lhs = randUniform(shape = intArrayOf(3), device = device)
        val rhs = randUniform(shape = intArrayOf(3), device = device)
        val product = lhs dot rhs
        var expected = 0.0
        lhs.elements().forEach {
            expected += it.second * rhs[it.first]
        }
        assertTrue(abs(expected - product.value()) < TOLERANCE)
    }
}

internal fun testingMatrixMultiplication(device: TorchDevice = TorchDevice.TorchCPU): Unit {
    TorchTensorRealAlgebra {
        setSeed(SEED)

        val lhsTensor = randNormal(shape = intArrayOf(3, 3), device = device)
        val rhsTensor = randNormal(shape = intArrayOf(3, 3), device = device)
        val product = lhsTensor dot rhsTensor

        val expected: Matrix<Double> = RealMatrixContext {
            val lhs = produce(3, 3) { i, j -> lhsTensor[intArrayOf(i, j)] }
            val rhs = produce(3, 3) { i, j -> rhsTensor[intArrayOf(i, j)] }
            lhs dot rhs
        }

        val lhsTensorCopy = lhsTensor.copy()
        val rhsTensorCopy = rhsTensor.copy()

        lhsTensorCopy dotAssign rhsTensor
        lhsTensor dotRightAssign rhsTensorCopy

        var error: Double = 0.0
        product.elements().forEach {
            error += abs(expected[it.first] - it.second) +
                    abs(expected[it.first] - lhsTensorCopy[it.first]) +
                    abs(expected[it.first] - rhsTensorCopy[it.first])
        }
        assertTrue(error < TOLERANCE)
    }
}

internal fun testingLinearStructure(device: TorchDevice = TorchDevice.TorchCPU): Unit {
    TorchTensorRealAlgebra {
        val shape = intArrayOf(3)
        val tensorA = full(value = -4.5, shape = shape, device = device)
        val tensorB = full(value = 10.9, shape = shape, device = device)
        val tensorC = full(value = 789.3, shape = shape, device = device)
        val tensorD = full(value = -72.9, shape = shape, device = device)
        val tensorE = full(value = 553.1, shape = shape, device = device)
        val result = 15.8 * tensorA - 1.5 * tensorB * (-tensorD) + 0.02 * tensorC / tensorE - 39.4
        val expected = copyFromArray(
            array = (1..3).map {
                15.8 * (-4.5) - 1.5 * 10.9 * 72.9 + 0.02 * 789.3 / 553.1 - 39.4
            }
                .toDoubleArray(),
            shape = shape,
            device = device
        )

        val assignResult = full(value = 0.0, shape = shape, device = device)
        tensorA *= 15.8
        tensorB *= 1.5
        tensorB *= -tensorD
        tensorC *= 0.02
        tensorC /= tensorE
        assignResult += tensorA
        assignResult -= tensorB
        assignResult += tensorC
        assignResult += -39.4

        val error = (expected - result).abs().sum().value() +
                (expected - assignResult).abs().sum().value()
        assertTrue(error < TOLERANCE)
    }
}

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


internal class TestTorchTensorAlgebra {

    @Test
    fun testScalarProduct() = testingScalarProduct()

    @Test
    fun testMatrixMultiplication() = testingMatrixMultiplication()

    @Test
    fun testLinearStructure() = testingLinearStructure()

    @Test
    fun testAutoGrad() = testingAutoGrad(dim = 100)

}