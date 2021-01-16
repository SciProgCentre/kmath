package kscience.kmath.torch

import kotlin.test.Test

class BenchmarkMatMultGPU {
    @Test
    fun benchmarkMatMultFloat20() =
        benchmarkingMatMultFloat(20, 10, 100000,
            device = Device.CUDA(0))

    @Test
    fun benchmarkMatMultFloat200() =
        benchmarkingMatMultFloat(200, 10, 10000,
            device = Device.CUDA(0))

    @Test
    fun benchmarkMatMultFloat2000() =
        benchmarkingMatMultFloat(2000, 10, 1000,
            device = Device.CUDA(0))
}