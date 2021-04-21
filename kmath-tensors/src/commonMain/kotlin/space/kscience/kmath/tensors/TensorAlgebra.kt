package space.kscience.kmath.tensors

// https://proofwiki.org/wiki/Definition:Algebra_over_Ring
public interface TensorAlgebra<T> {

    public fun TensorStructure<T>.value(): T

    public operator fun T.plus(other: TensorStructure<T>): TensorStructure<T>
    public operator fun TensorStructure<T>.plus(value: T): TensorStructure<T>
    public operator fun TensorStructure<T>.plus(other: TensorStructure<T>): TensorStructure<T>
    public operator fun TensorStructure<T>.plusAssign(value: T): Unit
    public operator fun TensorStructure<T>.plusAssign(other: TensorStructure<T>): Unit

    public operator fun T.minus(other: TensorStructure<T>): TensorStructure<T>
    public operator fun TensorStructure<T>.minus(value: T): TensorStructure<T>
    public operator fun TensorStructure<T>.minus(other: TensorStructure<T>): TensorStructure<T>
    public operator fun TensorStructure<T>.minusAssign(value: T): Unit
    public operator fun TensorStructure<T>.minusAssign(other: TensorStructure<T>): Unit

    public operator fun T.times(other: TensorStructure<T>): TensorStructure<T>
    public operator fun TensorStructure<T>.times(value: T): TensorStructure<T>
    public operator fun TensorStructure<T>.times(other: TensorStructure<T>): TensorStructure<T>
    public operator fun TensorStructure<T>.timesAssign(value: T): Unit
    public operator fun TensorStructure<T>.timesAssign(other: TensorStructure<T>): Unit
    public operator fun TensorStructure<T>.unaryMinus(): TensorStructure<T>

    //https://pytorch.org/cppdocs/notes/tensor_indexing.html
    public operator fun TensorStructure<T>.get(i: Int): TensorStructure<T>

    //https://pytorch.org/docs/stable/generated/torch.transpose.html
    public fun TensorStructure<T>.transpose(i: Int = -2, j: Int = -1): TensorStructure<T>

    //https://pytorch.org/docs/stable/tensor_view.html
    public fun TensorStructure<T>.view(shape: IntArray): TensorStructure<T>
    public fun TensorStructure<T>.viewAs(other: TensorStructure<T>): TensorStructure<T>

    //https://pytorch.org/docs/stable/generated/torch.matmul.html
    public infix fun TensorStructure<T>.dot(other: TensorStructure<T>): TensorStructure<T>

    //https://pytorch.org/docs/stable/generated/torch.diag_embed.html
    public fun diagonalEmbedding(
        diagonalEntries: TensorStructure<T>,
        offset: Int = 0, dim1: Int = -2, dim2: Int = -1
    ): TensorStructure<T>

}
