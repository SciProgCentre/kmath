package space.kscience.kmath.tensors

// https://proofwiki.org/wiki/Definition:Algebra_over_Ring
public interface TensorAlgebra<T, TensorType : TensorStructure<T>> {

    public fun TensorType.value(): T

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

    //https://pytorch.org/cppdocs/notes/tensor_indexing.html
    public operator fun TensorType.get(i: Int): TensorType

    //https://pytorch.org/docs/stable/generated/torch.transpose.html
    public fun TensorType.transpose(i: Int, j: Int): TensorType

    //https://pytorch.org/docs/stable/tensor_view.html
    public fun TensorType.view(shape: IntArray): TensorType
    public fun TensorType.viewAs(other: TensorType): TensorType

    //https://pytorch.org/docs/stable/generated/torch.matmul.html
    public infix fun TensorType.dot(other: TensorType): TensorType

    //https://pytorch.org/docs/stable/generated/torch.diag_embed.html
    public fun diagonalEmbedding(
        diagonalEntries: TensorType,
        offset: Int = 0, dim1: Int = 0, dim2: Int = 1
    ): TensorType

}
