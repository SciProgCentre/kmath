package space.kscience.kmath.tensors

// https://proofwiki.org/wiki/Definition:Algebra_over_Ring
public interface TensorAlgebra<T, TensorType : TensorStructure<T>>{

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
    public infix fun TensorType.dotAssign(other: TensorType): Unit
    public infix fun TensorType.dotRightAssign(other: TensorType): Unit

    //https://pytorch.org/docs/stable/generated/torch.diag_embed.html
    public fun diagonalEmbedding(
        diagonalEntries: TensorType,
        offset: Int = 0, dim1: Int = -2, dim2: Int = -1
    ): TensorType

    //https://pytorch.org/docs/stable/generated/torch.transpose.html
    public fun TensorType.transpose(i: Int, j: Int): TensorType
    public fun TensorType.transposeAssign(i: Int, j: Int): Unit

    //https://pytorch.org/docs/stable/tensor_view.html
    public fun TensorType.view(shape: IntArray): TensorType

    //https://pytorch.org/docs/stable/generated/torch.abs.html
    public fun TensorType.abs(): TensorType
    public fun TensorType.absAssign(): Unit

    //https://pytorch.org/docs/stable/generated/torch.sum.html
    public fun TensorType.sum(): TensorType
    public fun TensorType.sumAssign(): Unit
}

// https://proofwiki.org/wiki/Definition:Division_Algebra
public interface TensorPartialDivisionAlgebra<T, TensorType : TensorStructure<T>> :
    TensorAlgebra<T, TensorType> {

    public operator fun TensorType.div(other: TensorType): TensorType
    public operator fun TensorType.divAssign(other: TensorType)

    //https://pytorch.org/docs/stable/generated/torch.exp.html
    public fun TensorType.exp(): TensorType
    public fun TensorType.expAssign(): Unit

    //https://pytorch.org/docs/stable/generated/torch.log.html
    public fun TensorType.log(): TensorType
    public fun TensorType.logAssign(): Unit

    //https://pytorch.org/docs/stable/generated/torch.svd.html
    public fun TensorType.svd(): Triple<TensorType, TensorType, TensorType>

    //https://pytorch.org/docs/stable/generated/torch.symeig.html
    public fun TensorType.symEig(eigenvectors: Boolean = true): Pair<TensorType, TensorType>

}

public inline fun <T, TensorType : TensorStructure<T>,
        TorchTensorAlgebraType : TensorAlgebra<T, TensorType>>
        TorchTensorAlgebraType.checkShapeCompatible(
    a: TensorType, b: TensorType
): Unit =
    check(a.shape contentEquals b.shape) {
        "Tensors must be of identical shape"
    }

public inline fun <T, TensorType : TensorStructure<T>,
        TorchTensorAlgebraType : TensorAlgebra<T, TensorType>>
        TorchTensorAlgebraType.checkDot(a: TensorType, b: TensorType): Unit {
    val sa = a.shape
    val sb = b.shape
    val na = sa.size
    val nb = sb.size
    var status: Boolean
    if (nb == 1) {
        status = sa.last() == sb[0]
    } else {
        status = sa.last() == sb[nb - 2]
        if ((na > 2) and (nb > 2)) {
            status = status and
                    (sa.take(nb - 2).toIntArray() contentEquals sb.take(nb - 2).toIntArray())
        }
    }
    check(status) { "Incompatible shapes $sa and $sb for dot product" }
}

public inline fun <T, TensorType : TensorStructure<T>,
        TorchTensorAlgebraType : TensorAlgebra<T, TensorType>>
        TorchTensorAlgebraType.checkTranspose(dim: Int, i: Int, j: Int): Unit =
    check((i < dim) and (j < dim)) {
        "Cannot transpose $i to $j for a tensor of dim $dim"
    }

public inline fun <T, TensorType : TensorStructure<T>,
        TorchTensorAlgebraType : TensorAlgebra<T, TensorType>>
        TorchTensorAlgebraType.checkView(a: TensorType, shape: IntArray): Unit =
    check(a.shape.reduce(Int::times) == shape.reduce(Int::times))