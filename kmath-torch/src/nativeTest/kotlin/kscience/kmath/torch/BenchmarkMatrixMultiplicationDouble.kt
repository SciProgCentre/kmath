package kscience.kmath.torch

import kotlin.test.Test
import kotlin.time.measureTime

internal fun benchmarkingDoubleMatrixMultiplication(
    scale: Int,
    numWarmUp: Int,
    numIter: Int,
    device: TorchDevice = TorchDevice.TorchCPU
): Unit {
    TorchTensorRealAlgebra {
        println("Benchmarking $scale x $scale matrices over Double's: ")
        setSeed(SEED)
        val lhs = randNormal(shape = intArrayOf(scale, scale), device = device)
        val rhs = randNormal(shape = intArrayOf(scale, scale), device = device)
        repeat(numWarmUp) { lhs dotAssign rhs }
        val measuredTime = measureTime { repeat(numIter) { lhs dotAssign rhs } }
        println("   ${measuredTime / numIter} p.o. with $numIter iterations")
    }
}

class BenchmarkMatrixMultiplicationDouble {

    @Test
    fun benchmarkMatrixMultiplication20() = benchmarkingDoubleMatrixMultiplication(20, 10, 100000)

    @Test
    fun benchmarkMatrixMultiplication200() = benchmarkingDoubleMatrixMultiplication(200, 10, 10000)

    @Test
    fun benchmarkMatrixMultiplication2000() = benchmarkingDoubleMatrixMultiplication(2000, 3, 20)

}