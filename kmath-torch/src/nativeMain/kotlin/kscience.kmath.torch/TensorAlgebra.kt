package kscience.kmath.torch

import kscience.kmath.operations.Field
import kscience.kmath.operations.Ring


public interface TensorAlgebra<T, TorchTensorType : TensorStructure<T>> : Ring<TorchTensorType> {

    public operator fun T.plus(other: TorchTensorType): TorchTensorType
    public operator fun TorchTensorType.plus(value: T): TorchTensorType
    public operator fun TorchTensorType.plusAssign(value: T): Unit
    public operator fun TorchTensorType.plusAssign(b: TorchTensorType): Unit

    public operator fun T.minus(other: TorchTensorType): TorchTensorType
    public operator fun TorchTensorType.minus(value: T): TorchTensorType
    public operator fun TorchTensorType.minusAssign(value: T): Unit
    public operator fun TorchTensorType.minusAssign(b: TorchTensorType): Unit

    public operator fun T.times(other: TorchTensorType): TorchTensorType
    public operator fun TorchTensorType.times(value: T): TorchTensorType
    public operator fun TorchTensorType.timesAssign(value: T): Unit
    public operator fun TorchTensorType.timesAssign(b: TorchTensorType): Unit

    public infix fun TorchTensorType.dot(b: TorchTensorType): TorchTensorType

    public fun diagonalEmbedding(
        diagonalEntries: TorchTensorType,
        offset: Int = 0, dim1: Int = -2, dim2: Int = -1
    ): TorchTensorType

    public fun TorchTensorType.transpose(i: Int, j: Int): TorchTensorType
    public fun TorchTensorType.view(shape: IntArray): TorchTensorType

    public fun TorchTensorType.abs(): TorchTensorType
    public fun TorchTensorType.sum(): TorchTensorType

}

public interface TensorFieldAlgebra<T, TorchTensorType : TensorStructure<T>> :
    TensorAlgebra<T, TorchTensorType>, Field<TorchTensorType> {

    public operator fun TorchTensorType.divAssign(b: TorchTensorType)

    public fun TorchTensorType.exp(): TorchTensorType
    public fun TorchTensorType.log(): TorchTensorType

    public fun TorchTensorType.svd(): Triple<TorchTensorType, TorchTensorType, TorchTensorType>
    public fun TorchTensorType.symEig(eigenvectors: Boolean = true): Pair<TorchTensorType, TorchTensorType>

}