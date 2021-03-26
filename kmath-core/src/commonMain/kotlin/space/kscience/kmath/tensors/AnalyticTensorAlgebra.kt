package space.kscience.kmath.tensors


public interface AnalyticTensorAlgebra<T, TensorType : TensorStructure<T>> :
    TensorPartialDivisionAlgebra<T, TensorType> {

    //https://pytorch.org/docs/stable/generated/torch.exp.html
    public fun TensorType.exp(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.log.html
    public fun TensorType.log(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.sqrt.html
    public fun TensorType.sqrt(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.acos.html#torch.cos
    public fun TensorType.cos(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.acos.html#torch.acos
    public fun TensorType.acos(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.acosh.html#torch.cosh
    public fun TensorType.cosh(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.acosh.html#torch.acosh
    public fun TensorType.acosh(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.asin.html#torch.sin
    public fun TensorType.sin(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.asin.html#torch.asin
    public fun TensorType.asin(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.asin.html#torch.sinh
    public fun TensorType.sinh(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.asin.html#torch.asinh
    public fun TensorType.asinh(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.atan.html#torch.tan
    public fun TensorType.tan(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.atan.html#torch.atan
    public fun TensorType.atan(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.atanh.html#torch.tanh
    public fun TensorType.tanh(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.atanh.html#torch.atanh
    public fun TensorType.atanh(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.ceil.html#torch.ceil
    public fun TensorType.ceil(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.floor.html#torch.floor
    public fun TensorType.floor(): TensorType

}