package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.TensorStructure


internal inline fun checkEmptyShape(shape: IntArray): Unit =
    check(shape.isNotEmpty()) {
        "Illegal empty shape provided"
    }

internal inline fun checkEmptyDoubleBuffer(buffer: DoubleArray): Unit =
    check(buffer.isNotEmpty()) {
        "Illegal empty buffer provided"
    }

internal inline fun checkBufferShapeConsistency(shape: IntArray, buffer: DoubleArray): Unit =
    check(buffer.size == shape.reduce(Int::times)) {
        "Inconsistent shape ${shape.toList()} for buffer of size ${buffer.size} provided"
    }


internal inline fun <T> checkShapesCompatible(a: TensorStructure<T>, b: TensorStructure<T>): Unit =
    check(a.shape contentEquals b.shape) {
        "Incompatible shapes ${a.shape.toList()} and ${b.shape.toList()} "
    }


internal inline fun checkTranspose(dim: Int, i: Int, j: Int): Unit =
    check((i < dim) and (j < dim)) {
        "Cannot transpose $i to $j for a tensor of dim $dim"
    }

internal inline fun <T> checkView(a: TensorStructure<T>, shape: IntArray): Unit =
    check(a.shape.reduce(Int::times) == shape.reduce(Int::times))


internal inline fun checkSquareMatrix(shape: IntArray): Unit {
    val n = shape.size
    check(n >= 2) {
        "Expected tensor with 2 or more dimensions, got size $n instead"
    }
    check(shape[n - 1] == shape[n - 2]) {
        "Tensor must be batches of square matrices, but they are ${shape[n - 1]} by ${shape[n - 1]} matrices"
    }
}

internal inline fun DoubleTensorAlgebra.checkSymmetric(
    tensor: TensorStructure<Double>, epsilon: Double = 1e-6
): Unit =
    check(tensor.eq(tensor.transpose(), epsilon)) {
        "Tensor is not symmetric about the last 2 dimensions at precision $epsilon"
    }

internal inline fun DoubleLinearOpsTensorAlgebra.checkPositiveDefinite(
    tensor: DoubleTensor, epsilon: Double = 1e-6
): Unit {
    checkSymmetric(tensor, epsilon)
    for (mat in tensor.matrixSequence())
        check(mat.asTensor().detLU().value() > 0.0) {
            "Tensor contains matrices which are not positive definite ${mat.asTensor().detLU().value()}"
        }
}