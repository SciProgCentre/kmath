package space.kscience.kmath.tensors

// https://proofwiki.org/wiki/Definition:Algebra_over_Ring
public interface TensorAlgebra<T, TensorType : TensorStructure<T>> {

    public fun TensorType.value(): T

    public fun eye(n: Int): TensorType
    public fun zeros(shape: IntArray): TensorType
    public fun zeroesLike(other: TensorType): TensorType
    public fun ones(shape: IntArray): TensorType
    public fun onesLike(shape: IntArray): TensorType

    public fun TensorType.copy(): TensorType

    public operator fun T.plus(other: TensorType): TensorType
    public operator fun TensorType.plus(value: T): TensorType
    public operator fun TensorType.plus(other: TensorType): TensorType
    public operator fun TensorType.plusAssign(value: T): Unit
    public operator fun TensorType.plusAssign(other: TensorType): Unit

    public operator fun T.minus(other: TensorType): TensorType
    public operator fun TensorType.minus(value: T): TensorType
    public operator fun TensorType.minus(other: TensorType): TensorType
    public operator fun TensorType.minusAssign(value: T): Unit
    public operator fun TensorType.minusAssign(other: TensorType): Unit

    public operator fun T.times(other: TensorType): TensorType
    public operator fun TensorType.times(value: T): TensorType
    public operator fun TensorType.times(other: TensorType): TensorType
    public operator fun TensorType.timesAssign(value: T): Unit
    public operator fun TensorType.timesAssign(other: TensorType): Unit
    public operator fun TensorType.unaryMinus(): TensorType


    //https://pytorch.org/docs/stable/generated/torch.matmul.html
    public infix fun TensorType.dot(other: TensorType): TensorType

    //https://pytorch.org/docs/stable/generated/torch.diag_embed.html
    public fun diagonalEmbedding(
        diagonalEntries: TensorType,
        offset: Int = 0, dim1: Int = -2, dim2: Int = -1
    ): TensorType

    //https://pytorch.org/docs/stable/generated/torch.transpose.html
    public fun TensorType.transpose(i: Int, j: Int): TensorType

    //https://pytorch.org/docs/stable/tensor_view.html
    public fun TensorType.view(shape: IntArray): TensorType
    public fun TensorType.viewAs(other: TensorType): TensorType

    //https://pytorch.org/docs/stable/generated/torch.abs.html
    public fun TensorType.abs(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.sum.html
    public fun TensorType.sum(): TensorType
}

// https://proofwiki.org/wiki/Definition:Division_Algebra
public interface TensorPartialDivisionAlgebra<T, TensorType : TensorStructure<T>> :
    TensorAlgebra<T, TensorType> {

    public operator fun TensorType.div(value: T): TensorType
    public operator fun TensorType.div(other: TensorType): TensorType
    public operator fun TensorType.divAssign(value: T)
    public operator fun TensorType.divAssign(other: TensorType)

    //https://pytorch.org/docs/stable/generated/torch.exp.html
    public fun TensorType.exp(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.log.html
    public fun TensorType.log(): TensorType

    // todo change type of pivots
    //https://pytorch.org/docs/stable/generated/torch.lu.html
    public fun TensorType.lu(): Pair<TensorType, IntTensor>

    //https://pytorch.org/docs/stable/generated/torch.lu_unpack.html
    public fun luUnpack(A_LU: TensorType, pivots: IntTensor): Triple<TensorType, TensorType, TensorType>

    //https://pytorch.org/docs/stable/generated/torch.svd.html
    public fun TensorType.svd(): Triple<TensorType, TensorType, TensorType>

    //https://pytorch.org/docs/stable/generated/torch.symeig.html
    public fun TensorType.symEig(eigenvectors: Boolean = true): Pair<TensorType, TensorType>

}