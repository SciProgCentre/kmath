package kscience.kmath.torch

import kotlin.test.Test

class BenchmarkRandomGeneratorsGPU {
    @Test
    fun benchmarkRandNormal1() =
        benchmarkingRandNormal(10, 10, 100000,
            device = TorchDevice.TorchCUDA(0))
    @Test
    fun benchmarkRandUniform1() =
        benchmarkingRandUniform(10, 10, 100000,
            device = TorchDevice.TorchCUDA(0))

    @Test
    fun benchmarkRandIntegral1() =
        benchmarkingRandIntegral(10, 10, 100000,
            device = TorchDevice.TorchCUDA(0))

    @Test
    fun benchmarkRandNormal3() =
        benchmarkingRandNormal(1000, 10, 100000,
            device = TorchDevice.TorchCUDA(0))

    @Test
    fun benchmarkRandUniform3() =
        benchmarkingRandUniform(1000, 10, 100000,
            device = TorchDevice.TorchCUDA(0))

    @Test
    fun benchmarkRandIntegral3() =
        benchmarkingRandIntegral(1000, 10, 100000,
            device = TorchDevice.TorchCUDA(0))

    @Test
    fun benchmarkRandNormal5() =
        benchmarkingRandNormal(100000, 10, 100000,
            device = TorchDevice.TorchCUDA(0))

    @Test
    fun benchmarkRandUniform5() =
        benchmarkingRandUniform(100000, 10, 100000,
            device = TorchDevice.TorchCUDA(0))

    @Test
    fun benchmarkRandIntegral5() =
        benchmarkingRandIntegral(100000, 10, 100000,
            device = TorchDevice.TorchCUDA(0))

    @Test
    fun benchmarkRandNormal7() =
        benchmarkingRandNormal(10000000, 10, 10000,
            device = TorchDevice.TorchCUDA(0))

    @Test
    fun benchmarkRandUniform7() =
        benchmarkingRandUniform(10000000, 10, 10000,
            device = TorchDevice.TorchCUDA(0))

    @Test
    fun benchmarkRandIntegral7() =
        benchmarkingRandIntegral(10000000, 10, 10000,
            device = TorchDevice.TorchCUDA(0))
}