@file:Suppress("NOTHING_TO_INLINE")

package space.kscience.kmath.torch

import kotlin.test.assertEquals

internal val SEED = 987654
internal val TOLERANCE = 1e-6

internal inline fun <T, PrimitiveArrayType, TorchTensorType : TorchTensor<T>,
        TorchTensorAlgebraType : TorchTensorAlgebra<T, PrimitiveArrayType, TorchTensorType>>
        TorchTensorAlgebraType.withCuda(block: TorchTensorAlgebraType.(Device) -> Unit): Unit {
    this.block(Device.CPU)
    if (cudaAvailable()) this.block(Device.CUDA(0))
}

internal inline fun <T, PrimitiveArrayType, TorchTensorType : TorchTensor<T>,
        TorchTensorAlgebraType : TorchTensorAlgebra<T, PrimitiveArrayType, TorchTensorType>>
        TorchTensorAlgebraType.testingSetNumThreads(): Unit {
    val numThreads = 2
    setNumThreads(numThreads)
    assertEquals(numThreads, getNumThreads())
}

internal inline fun <TorchTensorType : TorchTensorOverField<Float>,
        TorchTensorAlgebraType : TorchTensorPartialDivisionAlgebra<Float, FloatArray, TorchTensorType>>
        TorchTensorAlgebraType.testingSetSeed(device: Device = Device.CPU): Unit {
    setSeed(SEED)
    val integral = randIntegral(0, 100, IntArray(0), device = device).value()
    val normal = randNormal(IntArray(0), device = device).value()
    val uniform = randUniform(IntArray(0), device = device).value()
    setSeed(SEED)
    val nextIntegral = randIntegral(0, 100, IntArray(0), device = device).value()
    val nextNormal = randNormal(IntArray(0), device = device).value()
    val nextUniform = randUniform(IntArray(0), device = device).value()
    assertEquals(normal, nextNormal)
    assertEquals(uniform, nextUniform)
    assertEquals(integral, nextIntegral)
}