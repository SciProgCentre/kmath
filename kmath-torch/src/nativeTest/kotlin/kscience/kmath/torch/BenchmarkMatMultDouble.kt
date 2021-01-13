package kscience.kmath.torch

import kotlin.test.Test
import kotlin.time.measureTime

internal fun benchmarkingMatMultDouble(
    scale: Int,
    numWarmUp: Int,
    numIter: Int,
    device: TorchDevice = TorchDevice.TorchCPU
): Unit {
    TorchTensorRealAlgebra {
        println("Benchmarking $scale x $scale matrices over Double's on $device: ")
        setSeed(SEED)
        val lhs = randNormal(shape = intArrayOf(scale, scale), device = device)
        val rhs = randNormal(shape = intArrayOf(scale, scale), device = device)
        repeat(numWarmUp) { lhs dotAssign rhs }
        val measuredTime = measureTime { repeat(numIter) { lhs dotAssign rhs } }
        println("   ${measuredTime / numIter} p.o. with $numIter iterations")
    }
}

internal class BenchmarkMatMultDouble {

    @Test
    fun benchmarkMatMult20() =
        benchmarkingMatMultDouble(20, 10, 100000)

    @Test
    fun benchmarkMatMult200() =
        benchmarkingMatMultDouble(200, 10, 10000)

    @Test
    fun benchmarkMatMult2000() =
        benchmarkingMatMultDouble(2000, 3, 20)

}