package kscience.kmath.torch

import kotlin.test.Test
import kotlin.time.measureTime

internal fun benchmarkingRandNormal(
    samples: Int,
    numWarmUp: Int,
    numIter: Int,
    device: Device = Device.CPU): Unit
{
    TorchTensorFloatAlgebra{
        println("Benchmarking generation of $samples Normal samples on $device: ")
        setSeed(SEED)
        val shape = intArrayOf(samples)
        val tensor = randNormal(shape = shape, device = device)
        repeat(numWarmUp) { tensor.randNormalAssign() }
        val measuredTime = measureTime { repeat(numIter) { tensor.randNormalAssign() } }
        println("   ${measuredTime / numIter} p.o. with $numIter iterations")
    }
}
internal fun benchmarkingRandUniform(
    samples: Int,
    numWarmUp: Int,
    numIter: Int,
    device: Device = Device.CPU): Unit
{
    TorchTensorFloatAlgebra{
        println("Benchmarking generation of $samples Uniform samples on $device: ")
        setSeed(SEED)
        val shape = intArrayOf(samples)
        val tensor = randUniform(shape = shape, device = device)
        repeat(numWarmUp) { tensor.randUniformAssign() }
        val measuredTime = measureTime { repeat(numIter) { tensor.randUniformAssign() } }
        println("   ${measuredTime / numIter} p.o. with $numIter iterations")
    }
}

internal fun benchmarkingRandIntegral(
    samples: Int,
    numWarmUp: Int,
    numIter: Int,
    device: Device = Device.CPU): Unit
{
    TorchTensorIntAlgebra {
        println("Benchmarking generation of $samples integer [0,100] samples on $device: ")
        setSeed(SEED)
        val shape = intArrayOf(samples)
        val tensor = randIntegral(0,100, shape = shape, device = device)
        repeat(numWarmUp) { tensor.randIntegralAssign(0,100) }
        val measuredTime = measureTime { repeat(numIter) { tensor.randIntegralAssign(0,100) } }
        println("   ${measuredTime / numIter} p.o. with $numIter iterations")
    }
}


internal class BenchmarkRandomGenerators {

    @Test
    fun benchmarkRandNormal1() =
        benchmarkingRandNormal(10, 10, 100000)

    @Test
    fun benchmarkRandUniform1() =
        benchmarkingRandUniform(10, 10, 100000)

    @Test
    fun benchmarkRandIntegral1() =
        benchmarkingRandIntegral(10, 10, 100000)

    @Test
    fun benchmarkRandNormal3() =
        benchmarkingRandNormal(1000, 10, 10000)

    @Test
    fun benchmarkRandUniform3() =
        benchmarkingRandUniform(1000, 10, 10000)

    @Test
    fun benchmarkRandIntegral3() =
        benchmarkingRandIntegral(1000, 10, 10000)

    @Test
    fun benchmarkRandNormal5() =
        benchmarkingRandNormal(100000, 5, 100)

    @Test
    fun benchmarkRandUniform5() =
        benchmarkingRandUniform(100000, 5, 100)

    @Test
    fun benchmarkRandIntegral5() =
        benchmarkingRandIntegral(100000, 5, 100)

    @Test
    fun benchmarkRandNormal7() =
        benchmarkingRandNormal(10000000, 3, 20)

    @Test
    fun benchmarkRandUniform7() =
        benchmarkingRandUniform(10000000, 3, 20)

    @Test
    fun benchmarkRandIntegral7() =
        benchmarkingRandIntegral(10000000, 3, 20)

}