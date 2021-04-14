package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.TensorAlgebra
import space.kscience.kmath.tensors.TensorStructure
import kotlin.math.abs


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

internal inline fun DoubleTensorAlgebra.checkSymmetric(tensor: DoubleTensor, epsilon: Double = 1e-6): Unit =
    check(tensor.eq(tensor.transpose(), epsilon)) {
        "Tensor is not symmetric about the last 2 dimensions at precision $epsilon"
    }

internal inline fun DoubleLinearOpsTensorAlgebra.checkPositiveDefinite(
    tensor: DoubleTensor, epsilon: Double = 1e-6): Unit {
    checkSymmetric(tensor, epsilon)
    for( mat in tensor.matrixSequence())
        check(mat.asTensor().detLU().value() > 0.0){
            "Tensor contains matrices which are not positive definite ${mat.asTensor().detLU().value()}"
        }
}

internal inline fun DoubleLinearOpsTensorAlgebra.checkNonSingularMatrix(tensor: DoubleTensor): Unit {
    for( mat in tensor.matrixSequence()) {
        val detTensor = mat.asTensor().detLU()
        check(!(detTensor.eq(detTensor.zeroesLike()))){
            "Tensor contains matrices which are singular"
        }
    }
}