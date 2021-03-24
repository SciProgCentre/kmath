package space.kscience.kmath.tensors


public interface LinearOpsTensorAlgebra<T, TensorType : TensorStructure<T>, IndexTensorType: TensorStructure<Int>> :
    TensorPartialDivisionAlgebra<T, TensorType> {

    //https://pytorch.org/docs/stable/linalg.html#torch.linalg.inv
    public fun TensorType.inv(): TensorType

    //https://pytorch.org/docs/stable/linalg.html#torch.linalg.cholesky
    public fun TensorType.cholesky(): TensorType

    //https://pytorch.org/docs/stable/linalg.html#torch.linalg.qr
    public fun TensorType.qr(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.lu.html
    public fun TensorType.lu(): Pair<TensorType, IndexTensorType>

    //https://pytorch.org/docs/stable/generated/torch.lu_unpack.html
    public fun luPivot(luTensor: TensorType, pivotsTensor: IndexTensorType): Triple<TensorType, TensorType, TensorType>

    //https://pytorch.org/docs/stable/linalg.html#torch.linalg.svd
    public fun TensorType.svd(): Triple<TensorType, TensorType, TensorType>

    //https://pytorch.org/docs/stable/generated/torch.symeig.html
    public fun TensorType.symEig(eigenvectors: Boolean = true): Pair<TensorType, TensorType>

}