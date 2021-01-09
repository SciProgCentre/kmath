package kscience.kmath.torch

import kscience.kmath.linear.RealMatrixContext
import kscience.kmath.operations.invoke
import kscience.kmath.structures.Matrix
import kotlin.math.abs
import kotlin.test.*

internal fun testingScalarProduct(device: TorchDevice = TorchDevice.TorchCPU): Unit {
    TorchTensorRealAlgebra {
        val lhs = randUniform(shape = intArrayOf(10), device = device)
        val rhs = randUniform(shape = intArrayOf(10), device = device)
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

        val lhsTensor = randNormal(shape = intArrayOf(20, 20), device = device)
        val rhsTensor = randNormal(shape = intArrayOf(20, 20), device = device)
        val product = lhsTensor dot rhsTensor

        val expected: Matrix<Double> = RealMatrixContext {
            val lhs = produce(20, 20) { i, j -> lhsTensor[intArrayOf(i, j)] }
            val rhs = produce(20, 20) { i, j -> rhsTensor[intArrayOf(i, j)] }
            lhs dot rhs
        }

        var error: Double = 0.0
        product.elements().forEach {
            error += abs(expected[it.first] - it.second)
        }
        assertTrue(error < TOLERANCE)
    }
}

internal class TestTorchTensorAlgebra {

    @Test
    fun testScalarProduct() = testingScalarProduct()

    @Test
    fun testMatrixMultiplication() = testingMatrixMultiplication()

}