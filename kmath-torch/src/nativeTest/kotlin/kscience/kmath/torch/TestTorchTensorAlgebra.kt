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
        val result = 15.8 * tensorA - 1.5 * tensorB + 0.02 * tensorC
        val expected = copyFromArray(
            array = (1..3).map { 15.8 * (-4.5) - 1.5 * 10.9 + 0.02 * 789.3 }.toDoubleArray(),
            shape = shape,
            device = device
        )

        val assignResult = full(value = 0.0, shape = shape, device = device)
        tensorA *= 15.8
        tensorB *= 1.5
        tensorC *= 0.02
        assignResult += tensorA
        assignResult -= tensorB
        assignResult += tensorC

        val error = (expected - result).abs().sum().value() +
                (expected - assignResult).abs().sum().value()
        assertTrue(error < TOLERANCE)
    }
}

internal fun testingAutoGrad(dim: Int, device: TorchDevice = TorchDevice.TorchCPU): Unit {
    TorchTensorRealAlgebra {
        setSeed(SEED)
        val x = randNormal(shape = intArrayOf(dim), device = device)
        x.requiresGrad = true
        val X = randNormal(shape = intArrayOf(dim,dim), device = device)
        val Q = X + X.transpose(0,1)
        val mu = randNormal(shape = intArrayOf(dim), device = device)
        val c = randNormal(shape = IntArray(0), device = device)
        val f = 0.5 * (x dot (Q dot x)) + (mu dot x) + c
        val gradf = f grad x
        val error = (gradf - ((Q dot x) + mu)).abs().sum().value()
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