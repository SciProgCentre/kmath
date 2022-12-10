/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core.internal

import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.contentEquals
import space.kscience.kmath.nd.linearSize
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.DoubleTensor
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra
import space.kscience.kmath.tensors.core.asDoubleTensor
import space.kscience.kmath.tensors.core.detLU


internal fun checkNotEmptyShape(shape: ShapeND) =
    check(shape.size > 0) {
        "Illegal empty shape provided"
    }

internal fun checkEmptyDoubleBuffer(buffer: DoubleArray) = check(buffer.isNotEmpty()) {
    "Illegal empty buffer provided"
}

internal fun checkBufferShapeConsistency(shape: ShapeND, buffer: DoubleArray) =
    check(buffer.size == shape.linearSize) {
        "Inconsistent shape $shape for buffer of size ${buffer.size} provided"
    }

@PublishedApi
internal fun <T> checkShapesCompatible(a: StructureND<T>, b: StructureND<T>): Unit =
    check(a.shape contentEquals b.shape) {
        "Incompatible shapes ${a.shape} and ${b.shape} "
    }

internal fun checkTranspose(dim: Int, i: Int, j: Int) =
    check((i < dim) and (j < dim)) {
        "Cannot transpose $i to $j for a tensor of dim $dim"
    }

internal fun <T> checkView(a: Tensor<T>, shape: ShapeND) =
    check(a.shape.linearSize == shape.linearSize)

internal fun checkSquareMatrix(shape: ShapeND) {
    val n = shape.size
    check(n >= 2) {
        "Expected tensor with 2 or more dimensions, got size $n instead"
    }
    check(shape[n - 1] == shape[n - 2]) {
        "Tensor must be batches of square matrices, but they are ${shape[n - 1]} by ${shape[n - 1]} matrices"
    }
}

internal fun DoubleTensorAlgebra.checkSymmetric(
    tensor: Tensor<Double>, epsilon: Double = 1e-6,
) = check(tensor.eq(tensor.transposed(), epsilon)) {
    "Tensor is not symmetric about the last 2 dimensions at precision $epsilon"
}

internal fun DoubleTensorAlgebra.checkPositiveDefinite(tensor: DoubleTensor, epsilon: Double = 1e-6) {
    checkSymmetric(tensor, epsilon)
    for (mat in tensor.matrixSequence())
        check(detLU(mat.asDoubleTensor()).value() > 0.0) {
            "Tensor contains matrices which are not positive definite ${detLU(mat.asDoubleTensor()).value()}"
        }
}