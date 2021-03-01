@file:Suppress("NOTHING_TO_INLINE")

package space.kscience.kmath.torch

import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal inline fun <TorchTensorType : TorchTensorOverField<Float>,
        TorchTensorAlgebraType : TorchTensorPartialDivisionAlgebra<Float, FloatArray, TorchTensorType>>
        TorchTensorAlgebraType.testingCopying(device: Device = Device.CPU): Unit {
    val array = (1..24).map { 10f * it * it }.toFloatArray()
    val shape = intArrayOf(2, 3, 4)
    val tensor = copyFromArray(array, shape = shape, device = device)
    val copyOfTensor = tensor.copy()
    tensor[intArrayOf(1, 2, 3)] = 0.1f
    assertTrue(copyOfTensor.copyToArray() contentEquals array)
    assertEquals(0.1f, tensor[intArrayOf(1, 2, 3)])
    if(device != Device.CPU){
        val normalCpu = randNormal(intArrayOf(2, 3))
        val normalGpu = normalCpu.copyToDevice(device)
        assertTrue(normalCpu.copyToArray() contentEquals normalGpu.copyToArray())

        val uniformGpu = randUniform(intArrayOf(3,2),device)
        val uniformCpu = uniformGpu.copyToDevice(Device.CPU)
        assertTrue(uniformGpu.copyToArray() contentEquals uniformCpu.copyToArray())
    }
}

internal inline fun <T, PrimitiveArrayType, TorchTensorType : TorchTensorOverField<T>,
        TorchTensorAlgebraType : TorchTensorPartialDivisionAlgebra<T, PrimitiveArrayType, TorchTensorType>>
        TorchTensorAlgebraType.testingRequiresGrad(): Unit {
    val tensor = randNormal(intArrayOf(3))
    assertTrue(!tensor.requiresGrad)
    tensor.requiresGrad = true
    assertTrue(tensor.requiresGrad)
    tensor.requiresGrad = false
    assertTrue(!tensor.requiresGrad)
    tensor.requiresGrad = true
    val detachedTensor = tensor.detachFromGraph()
    assertTrue(!detachedTensor.requiresGrad)
}

internal inline fun <TorchTensorType : TorchTensor<Int>,
        TorchTensorAlgebraType : TorchTensorAlgebra<Int, IntArray, TorchTensorType>>
        TorchTensorAlgebraType.testingViewWithNoCopy(device: Device = Device.CPU) {
    val tensor = copyFromArray(intArrayOf(1, 2, 3, 4, 5, 6), shape = intArrayOf(6), device)
    val viewTensor = tensor.view(intArrayOf(2, 3))
    assertTrue(viewTensor.shape contentEquals intArrayOf(2, 3))
    viewTensor[intArrayOf(0, 0)] = 10
    assertEquals(tensor[intArrayOf(0)], 10)
}


