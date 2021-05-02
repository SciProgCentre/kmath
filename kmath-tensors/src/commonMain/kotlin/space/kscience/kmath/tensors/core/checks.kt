package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.algebras.DoubleLinearOpsTensorAlgebra
import space.kscience.kmath.tensors.core.algebras.DoubleTensorAlgebra


internal fun checkEmptyShape(shape: IntArray) =
    check(shape.isNotEmpty()) {
        "Illegal empty shape provided"
    }

internal fun checkEmptyDoubleBuffer(buffer: DoubleArray) =
    check(buffer.isNotEmpty()) {
        "Illegal empty buffer provided"
    }

internal fun checkBufferShapeConsistency(shape: IntArray, buffer: DoubleArray) =
    check(buffer.size == shape.reduce(Int::times)) {
        "Inconsistent shape ${shape.toList()} for buffer of size ${buffer.size} provided"
    }

internal fun <T> checkShapesCompatible(a: Tensor<T>, b: Tensor<T>) =
    check(a.shape contentEquals b.shape) {
        "Incompatible shapes ${a.shape.toList()} and ${b.shape.toList()} "
    }

internal fun checkTranspose(dim: Int, i: Int, j: Int) =
    check((i < dim) and (j < dim)) {
        "Cannot transpose $i to $j for a tensor of dim $dim"
    }

internal fun <T> checkView(a: Tensor<T>, shape: IntArray) =
    check(a.shape.reduce(Int::times) == shape.reduce(Int::times))

internal fun checkSquareMatrix(shape: IntArray) {
    val n = shape.size
    check(n >= 2) {
        "Expected tensor with 2 or more dimensions, got size $n instead"
    }
    check(shape[n - 1] == shape[n - 2]) {
        "Tensor must be batches of square matrices, but they are ${shape[n - 1]} by ${shape[n - 1]} matrices"
    }
}

internal fun DoubleTensorAlgebra.checkSymmetric(
    tensor: Tensor<Double>, epsilon: Double = 1e-6
) =
    check(tensor.eq(tensor.transpose(), epsilon)) {
        "Tensor is not symmetric about the last 2 dimensions at precision $epsilon"
    }

internal fun DoubleLinearOpsTensorAlgebra.checkPositiveDefinite(tensor: DoubleTensor, epsilon: Double = 1e-6) {
    checkSymmetric(tensor, epsilon)
    for (mat in tensor.matrixSequence())
        check(mat.asTensor().detLU().value() > 0.0) {
            "Tensor contains matrices which are not positive definite ${mat.asTensor().detLU().value()}"
        }
}