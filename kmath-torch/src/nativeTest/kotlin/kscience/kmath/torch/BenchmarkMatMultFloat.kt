package kscience.kmath.torch

import kotlin.test.Test
import kotlin.time.measureTime

internal fun benchmarkingMatMultFloat(
    scale: Int,
    numWarmUp: Int,
    numIter: Int,
    device: TorchDevice = TorchDevice.TorchCPU
): Unit {
    TorchTensorFloatAlgebra {
        println("Benchmarking $scale x $scale matrices over Float's on $device: ")
        setSeed(SEED)
        val lhs = randNormal(shape = intArrayOf(scale, scale), device = device)
        val rhs = randNormal(shape = intArrayOf(scale, scale), device = device)
        repeat(numWarmUp) { lhs dotAssign rhs }
        val measuredTime = measureTime { repeat(numIter) { lhs dotAssign rhs } }
        println("   ${measuredTime / numIter} p.o. with $numIter iterations")
    }
}

internal class BenchmarkMatMultFloat {

    @Test
    fun benchmarkMatMult20() =
        benchmarkingMatMultFloat(20, 10, 100000)

    @Test
    fun benchmarkMatMult200() =
        benchmarkingMatMultFloat(200, 10, 10000)

    @Test
    fun benchmarkMatMult2000() =
        benchmarkingMatMultFloat(2000, 3, 20)
}