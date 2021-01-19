package kscience.kmath.tensors

import kscience.kmath.operations.Ring
import kscience.kmath.structures.MutableNDStructure

public typealias Tensor<T> = MutableNDStructure<T>

public val <T : Any> Tensor<T>.value: T
    get() {
        require(shape.contentEquals(intArrayOf(1))) { "Value available only for a tensor with no dimensions" }
        return get(intArrayOf(0))
    }

// https://proofwiki.org/wiki/Definition:Algebra_over_Ring
/**
 * To be moved to a separate project
 */
public interface TensorAlgebra<T, TensorType : Tensor<T>> : Ring<TensorType> {

    public operator fun T.plus(other: TensorType): TensorType
    public operator fun TensorType.plus(value: T): TensorType
    public operator fun TensorType.plusAssign(value: T): Unit
    public operator fun TensorType.plusAssign(other: TensorType): Unit

    public operator fun T.minus(other: TensorType): TensorType
    public operator fun TensorType.minus(value: T): TensorType
    public operator fun TensorType.minusAssign(value: T): Unit
    public operator fun TensorType.minusAssign(other: TensorType): Unit

    public operator fun T.times(other: TensorType): TensorType
    public operator fun TensorType.times(value: T): TensorType
    public operator fun TensorType.timesAssign(value: T): Unit
    public operator fun TensorType.timesAssign(other: TensorType): Unit


    public infix fun TensorType.dot(other: TensorType): TensorType
    public infix fun TensorType.dotAssign(other: TensorType): Unit
    public infix fun TensorType.dotRightAssign(other: TensorType): Unit

    public fun diagonalEmbedding(
        diagonalEntries: TensorType,
        offset: Int = 0, dim1: Int = -2, dim2: Int = -1,
    ): TensorType

    public fun TensorType.transpose(i: Int, j: Int): TensorType
    public fun TensorType.transposeAssign(i: Int, j: Int): Unit

    public fun TensorType.view(shape: IntArray): TensorType

    public fun TensorType.abs(): TensorType
    public fun TensorType.absAssign(): Unit
    public fun TensorType.sum(): TensorType
    public fun TensorType.sumAssign(): Unit
}

// https://proofwiki.org/wiki/Definition:Division_Algebra

public interface TensorPartialDivisionAlgebra<T, TensorType : Tensor<T>> : TensorAlgebra<T, TensorType> {

    public operator fun TensorType.div(other: TensorType): TensorType
    public operator fun TensorType.divAssign(other: TensorType)

    public fun TensorType.exp(): TensorType
    public fun TensorType.expAssign(): Unit
    public fun TensorType.log(): TensorType
    public fun TensorType.logAssign(): Unit

    public fun TensorType.svd(): Triple<TensorType, TensorType, TensorType>
    public fun TensorType.symEig(eigenvectors: Boolean = true): Pair<TensorType, TensorType>

}