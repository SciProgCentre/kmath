@file:Suppress("NOTHING_TO_INLINE")

package space.kscience.kmath.torch

import kotlin.time.measureTime

internal inline fun <T, PrimitiveArrayType, TorchTensorType : TorchTensorOverField<T>,
        TorchTensorAlgebraType : TorchTensorPartialDivisionAlgebra<T, PrimitiveArrayType, TorchTensorType>>
        TorchTensorAlgebraType.benchmarkMatMul(
    scale: Int,
    numWarmUp: Int,
    numIter: Int,
    fieldName: String,
    device: space.kscience.kmath.torch.Device = space.kscience.kmath.torch.Device.CPU
): Unit {
    println("Benchmarking $scale x $scale $fieldName matrices on $device: ")
    setSeed(SEED)
    val lhs = randNormal(shape = intArrayOf(scale, scale), device = device)
    val rhs = randNormal(shape = intArrayOf(scale, scale), device = device)
    repeat(numWarmUp) { lhs dotAssign rhs }
    val measuredTime = measureTime { repeat(numIter) { lhs dotAssign rhs } }
    println("   ${measuredTime / numIter} p.o. with $numIter iterations")
}

