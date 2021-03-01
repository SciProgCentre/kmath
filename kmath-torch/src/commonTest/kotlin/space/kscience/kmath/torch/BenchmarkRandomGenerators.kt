@file:Suppress("NOTHING_TO_INLINE")

package space.kscience.kmath.torch

import kotlin.time.measureTime

internal inline fun <TorchTensorType : TorchTensorOverField<Float>,
        TorchTensorAlgebraType : TorchTensorPartialDivisionAlgebra<Float, FloatArray, TorchTensorType>>
        TorchTensorAlgebraType.benchmarkRand(
    samples: Int,
    numWarmUp: Int,
    numIter: Int,
    device: Device,
    distName: String,
    initBock: TorchTensorAlgebraType.(IntArray, Device) -> TorchTensorType,
    runBlock: TorchTensorAlgebraType.(TorchTensorType) -> Unit
): Unit{
    println("Benchmarking generation of $samples $distName samples on $device: ")
    setSeed(SEED)
    val shape = intArrayOf(samples)
    val tensor = this.initBock(shape,device)
    repeat(numWarmUp) { this.runBlock(tensor) }
    val measuredTime = measureTime { repeat(numIter) { this.runBlock(tensor) } }
    println("   ${measuredTime / numIter} p.o. with $numIter iterations")
}

internal inline fun <TorchTensorType : TorchTensorOverField<Float>,
        TorchTensorAlgebraType : TorchTensorPartialDivisionAlgebra<Float, FloatArray, TorchTensorType>>
        TorchTensorAlgebraType.benchmarkRandNormal(
    samples: Int,
    numWarmUp: Int,
    numIter: Int,
    device: Device = Device.CPU): Unit{
    benchmarkRand(
        samples,
        numWarmUp,
        numIter,
        device,
        "Normal",
        {sh, dc -> randNormal(shape = sh, device = dc)},
        {ten -> ten.randNormalAssign() }
    )
}

internal inline fun <TorchTensorType : TorchTensorOverField<Float>,
        TorchTensorAlgebraType : TorchTensorPartialDivisionAlgebra<Float, FloatArray, TorchTensorType>>
        TorchTensorAlgebraType.benchmarkRandUniform(
    samples: Int,
    numWarmUp: Int,
    numIter: Int,
    device: Device = Device.CPU): Unit{
    benchmarkRand(
        samples,
        numWarmUp,
        numIter,
        device,
        "Uniform",
        {sh, dc -> randUniform(shape = sh, device = dc)},
        {ten -> ten.randUniformAssign() }
    )
}

internal inline fun <TorchTensorType : TorchTensorOverField<Float>,
        TorchTensorAlgebraType : TorchTensorPartialDivisionAlgebra<Float, FloatArray, TorchTensorType>>
        TorchTensorAlgebraType.benchmarkRandIntegral(
    samples: Int,
    numWarmUp: Int,
    numIter: Int,
    device: Device = Device.CPU): Unit{
    benchmarkRand(
        samples,
        numWarmUp,
        numIter,
        device,
        "integer [0,100]",
        {sh, dc -> randIntegral(0, 100, shape = sh, device = dc)},
        {ten -> ten.randIntegralAssign(0, 100) }
    )
}

internal inline fun <TorchTensorType : TorchTensorOverField<Float>,
        TorchTensorAlgebraType : TorchTensorPartialDivisionAlgebra<Float, FloatArray, TorchTensorType>>
        TorchTensorAlgebraType.benchmarkingRand1(): Unit {
    benchmarkRandNormal(10, 10, 100000)
    benchmarkRandUniform(10, 10, 100000)
    benchmarkRandIntegral(10, 10, 100000)
    if(cudaAvailable()) {
        benchmarkRandNormal(10, 10, 100000, device = Device.CUDA(0))
        benchmarkRandUniform(10, 10, 100000, device = Device.CUDA(0))
        benchmarkRandIntegral(10, 10, 100000, device = Device.CUDA(0))
    }

}

internal inline fun <TorchTensorType : TorchTensorOverField<Float>,
        TorchTensorAlgebraType : TorchTensorPartialDivisionAlgebra<Float, FloatArray, TorchTensorType>>
        TorchTensorAlgebraType.benchmarkingRand3(): Unit {
    benchmarkRandNormal(1000, 10, 10000)
    benchmarkRandUniform(1000, 10, 10000)
    benchmarkRandIntegral(1000, 10, 10000)
    if(cudaAvailable()) {
        benchmarkRandNormal(1000, 10, 100000, device = Device.CUDA(0))
        benchmarkRandUniform(1000, 10, 100000, device = Device.CUDA(0))
        benchmarkRandIntegral(1000, 10, 100000, device = Device.CUDA(0))
    }
}

internal inline fun <TorchTensorType : TorchTensorOverField<Float>,
        TorchTensorAlgebraType : TorchTensorPartialDivisionAlgebra<Float, FloatArray, TorchTensorType>>
        TorchTensorAlgebraType.benchmarkingRand5(): Unit {
    benchmarkRandNormal(100000, 5, 100)
    benchmarkRandUniform(100000, 5, 100)
    benchmarkRandIntegral(100000, 5, 100)
    if(cudaAvailable()){
        benchmarkRandNormal(100000, 10, 100000, device = Device.CUDA(0))
        benchmarkRandUniform(100000, 10, 100000, device = Device.CUDA(0))
        benchmarkRandIntegral(100000, 10, 100000, device = Device.CUDA(0))
    }
}

internal inline fun <TorchTensorType : TorchTensorOverField<Float>,
        TorchTensorAlgebraType : TorchTensorPartialDivisionAlgebra<Float, FloatArray, TorchTensorType>>
        TorchTensorAlgebraType.benchmarkingRand7(): Unit {
    benchmarkRandNormal(10000000, 3, 20)
    benchmarkRandUniform(10000000, 3, 20)
    benchmarkRandIntegral(10000000, 3, 20)
    if(cudaAvailable()){
        benchmarkRandNormal(10000000, 10, 10000, device = Device.CUDA(0))
        benchmarkRandUniform(10000000, 10, 10000, device = Device.CUDA(0))
        benchmarkRandIntegral(10000000, 10, 10000, device = Device.CUDA(0))
    }
}