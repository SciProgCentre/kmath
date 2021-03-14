package space.kscience.kmath.tensors


public interface LinearOpsTensorAlgebra<T, TensorType : TensorStructure<T>> :
    TensorPartialDivisionAlgebra<T, TensorType> {

    //https://pytorch.org/docs/stable/generated/torch.eye.html
    public fun eye(n: Int): TensorType

    //https://pytorch.org/docs/stable/generated/torch.matmul.html
    public infix fun TensorType.dot(other: TensorType): TensorType

    //https://pytorch.org/docs/stable/generated/torch.diag_embed.html
    public fun diagonalEmbedding(
        diagonalEntries: TensorType,
        offset: Int = 0, dim1: Int = -2, dim2: Int = -1
    ): TensorType

    //https://pytorch.org/docs/stable/linalg.html#torch.linalg.det
    public fun TensorType.det(): TensorType

    //https://pytorch.org/docs/stable/linalg.html#torch.linalg.inv
    public fun TensorType.inv(): TensorType

    //https://pytorch.org/docs/stable/linalg.html#torch.linalg.cholesky
    public fun TensorType.cholesky(): TensorType

    //https://pytorch.org/docs/stable/linalg.html#torch.linalg.qr
    public fun TensorType.qr(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.lu.html
    public fun TensorType.lu(): Pair<TensorType, IntTensor>

    //https://pytorch.org/docs/stable/generated/torch.lu_unpack.html
    public fun luPivot(aLU: TensorType, pivots: IntTensor): Triple<TensorType, TensorType, TensorType>

    //https://pytorch.org/docs/stable/linalg.html#torch.linalg.svd
    public fun TensorType.svd(): Triple<TensorType, TensorType, TensorType>

    //https://pytorch.org/docs/stable/generated/torch.symeig.html
    public fun TensorType.symEig(eigenvectors: Boolean = true): Pair<TensorType, TensorType>

}