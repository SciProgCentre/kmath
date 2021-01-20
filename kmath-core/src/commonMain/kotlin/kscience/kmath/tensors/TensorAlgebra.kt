package kscience.kmath.tensors

import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.operations.Field
import kscience.kmath.operations.Ring
import kscience.kmath.structures.MutableNDStructure

public typealias Tensor<T> = MutableNDStructure<T>

@UnstableKMathAPI
public val <T : Any> Tensor<T>.value: T
    get() {
        require(shape.contentEquals(intArrayOf(1))) { "Value available only for a tensor with no dimensions" }
        return get(intArrayOf(0))
    }

// https://proofwiki.org/wiki/Definition:Algebra_over_Ring
/**
 * TODO To be moved to a separate project
 */
@UnstableKMathAPI
public interface TensorAlgebra<T, TT : Tensor<T>> : Ring<TT> {

    public operator fun T.plus(other: TT): TT
    public operator fun TT.plus(value: T): TT
    public operator fun TT.plusAssign(value: T): Unit
    public operator fun TT.plusAssign(other: TT): Unit

    public operator fun T.minus(other: TT): TT
    public operator fun TT.minus(value: T): TT
    public operator fun TT.minusAssign(value: T): Unit
    public operator fun TT.minusAssign(other: TT): Unit

    public operator fun T.times(other: TT): TT
    public operator fun TT.times(value: T): TT
    public operator fun TT.timesAssign(value: T): Unit
    public operator fun TT.timesAssign(other: TT): Unit


    public infix fun TT.dot(other: TT): TT
    public infix fun TT.dotAssign(other: TT): Unit
    public infix fun TT.dotRightAssign(other: TT): Unit

    public fun diagonalEmbedding(
        diagonalEntries: TT,
        offset: Int = 0, dim1: Int = -2, dim2: Int = -1,
    ): TT

    public fun TT.transpose(i: Int, j: Int): TT
    public fun TT.transposeAssign(i: Int, j: Int): Unit

    public fun TT.view(shape: IntArray): TT

    public fun abs(tensor: TT): TT
    public fun TT.absAssign(): Unit
    public fun TT.sum(): TT
    public fun TT.sumAssign(): Unit
}

// https://proofwiki.org/wiki/Definition:Division_Algebra

public interface TensorPartialDivisionAlgebra<T, TT : Tensor<T>> :
    TensorAlgebra<T, TT>, Field<TT> {

    public operator fun TT.divAssign(other: TT)

    public fun exp(tensor: TT): TT
    public fun TT.expAssign(): Unit
    public fun log(tensor: TT): TT
    public fun TT.logAssign(): Unit

    public fun svd(tensor: TT): Triple<TT, TT, TT>
    public fun symEig(tensor: TT, eigenvectors: Boolean = true): Pair<TT, TT>

}