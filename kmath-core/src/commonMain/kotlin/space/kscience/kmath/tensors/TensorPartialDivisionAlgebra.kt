package space.kscience.kmath.tensors

// https://proofwiki.org/wiki/Definition:Division_Algebra
public interface TensorPartialDivisionAlgebra<T, TensorType : TensorStructure<T>> :
    TensorAlgebra<T, TensorType> {

    public operator fun TensorType.div(value: T): TensorType
    public operator fun TensorType.div(other: TensorType): TensorType
    public operator fun TensorType.divAssign(value: T)
    public operator fun TensorType.divAssign(other: TensorType)

    //https://pytorch.org/docs/stable/generated/torch.mean.html#torch.mean
    public fun TensorType.mean(dim: Int, keepDim: Boolean): TensorType

    //https://pytorch.org/docs/stable/generated/torch.quantile.html#torch.quantile
    public fun TensorType.quantile(q: T, dim: Int, keepDim: Boolean): TensorType

    //https://pytorch.org/docs/stable/generated/torch.std.html#torch.std
    public fun TensorType.std(dim: Int, unbiased: Boolean, keepDim: Boolean): TensorType

    //https://pytorch.org/docs/stable/generated/torch.var.html#torch.var
    public fun TensorType.variance(dim: Int, unbiased: Boolean, keepDim: Boolean): TensorType

    //https://pytorch.org/docs/stable/generated/torch.histc.html#torch.histc
    public fun TensorType.histc(bins: Int, min: T, max: T): TensorType

}
