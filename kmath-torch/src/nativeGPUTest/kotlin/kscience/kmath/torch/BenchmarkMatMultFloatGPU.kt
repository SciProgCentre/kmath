package kscience.kmath.torch

import kotlin.test.Test

class BenchmarkMatMultFloatGPU {
    @Test
    fun benchmarkMatMult20() =
        benchmarkingMatMultFloat(20, 10, 100000,
            device = TorchDevice.TorchCUDA(0))

    @Test
    fun benchmarkMatMult200() =
        benchmarkingMatMultFloat(200, 10, 10000,
            device = TorchDevice.TorchCUDA(0))

    @Test
    fun benchmarkMatMult2000() =
        benchmarkingMatMultFloat(2000, 10, 1000,
            device = TorchDevice.TorchCUDA(0))
}