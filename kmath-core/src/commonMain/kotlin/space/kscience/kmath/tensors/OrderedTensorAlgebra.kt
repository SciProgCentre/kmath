package space.kscience.kmath.tensors

public interface OrderedTensorAlgebra<T, TensorType : TensorStructure<T>> :
    TensorAlgebra<T, TensorType> {

    //https://pytorch.org/docs/stable/generated/torch.max.html#torch.max
    public fun TensorType.max(dim: Int, keepDim: Boolean): TensorType

    //https://pytorch.org/docs/stable/generated/torch.cummax.html#torch.cummax
    public fun TensorType.cummax(dim: Int): TensorType

    //https://pytorch.org/docs/stable/generated/torch.min.html#torch.min
    public fun TensorType.min(dim: Int, keepDim: Boolean): TensorType

    //https://pytorch.org/docs/stable/generated/torch.cummin.html#torch.cummin
    public fun TensorType.cummin(dim: Int): TensorType

    //https://pytorch.org/docs/stable/generated/torch.median.html#torch.median
    public fun TensorType.median(dim: Int, keepDim: Boolean): TensorType

    //https://pytorch.org/docs/stable/generated/torch.maximum.html#torch.maximum
    public fun maximum(lhs: TensorType, rhs: TensorType)

    //https://pytorch.org/docs/stable/generated/torch.minimum.html#torch.minimum
    public fun minimum(lhs: TensorType, rhs: TensorType)

    //https://pytorch.org/docs/stable/generated/torch.sort.html#torch.sort
    public fun TensorType.sort(dim: Int, keepDim: Boolean, descending: Boolean): TensorType
}