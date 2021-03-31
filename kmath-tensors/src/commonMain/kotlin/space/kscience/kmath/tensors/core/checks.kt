package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.TensorAlgebra
import space.kscience.kmath.tensors.TensorStructure


internal inline fun <T, TensorType : TensorStructure<T>,
        TorchTensorAlgebraType : TensorAlgebra<T, TensorType>>
        TorchTensorAlgebraType.checkEmptyShape(shape: IntArray): Unit =
    check(shape.isNotEmpty()) {
        "Illegal empty shape provided"
    }

internal inline fun <TensorType : TensorStructure<Double>,
        TorchTensorAlgebraType : TensorAlgebra<Double, TensorType>>
        TorchTensorAlgebraType.checkEmptyDoubleBuffer(buffer: DoubleArray): Unit =
    check(buffer.isNotEmpty()) {
        "Illegal empty buffer provided"
    }

internal inline fun <TensorType : TensorStructure<Double>,
        TorchTensorAlgebraType : TensorAlgebra<Double, TensorType>>
        TorchTensorAlgebraType.checkBufferShapeConsistency(shape: IntArray, buffer: DoubleArray): Unit =
    check(buffer.size == shape.reduce(Int::times)) {
        "Inconsistent shape ${shape.toList()} for buffer of size ${buffer.size} provided"
    }


internal inline fun <T, TensorType : TensorStructure<T>,
        TorchTensorAlgebraType : TensorAlgebra<T, TensorType>>
        TorchTensorAlgebraType.checkShapesCompatible(a: TensorType, b: TensorType): Unit =
    check(a.shape contentEquals b.shape) {
        "Incompatible shapes ${a.shape.toList()} and ${b.shape.toList()} "
    }


internal inline fun <T, TensorType : TensorStructure<T>,
        TorchTensorAlgebraType : TensorAlgebra<T, TensorType>>
        TorchTensorAlgebraType.checkTranspose(dim: Int, i: Int, j: Int): Unit =
    check((i < dim) and (j < dim)) {
        "Cannot transpose $i to $j for a tensor of dim $dim"
    }

internal inline fun <T, TensorType : TensorStructure<T>,
        TorchTensorAlgebraType : TensorAlgebra<T, TensorType>>
        TorchTensorAlgebraType.checkView(a: TensorType, shape: IntArray): Unit =
    check(a.shape.reduce(Int::times) == shape.reduce(Int::times))

internal inline fun <T, TensorType : TensorStructure<T>,
        TorchTensorAlgebraType : TensorAlgebra<T, TensorType>>
        TorchTensorAlgebraType.checkSquareMatrix(shape: IntArray): Unit {
    val n = shape.size
    check(n >= 2) {
        "Expected tensor with 2 or more dimensions, got size $n instead"
    }
    check(shape[n - 1] == shape[n - 2]) {
        "Tensor must be batches of square matrices, but they are ${shape[n - 1]} by ${shape[n - 1]} matrices"
    }
}