package space.kscience.kmath.tensors


public interface AnalyticTensorAlgebra<T, TensorType : TensorStructure<T>> :
    TensorPartialDivisionAlgebra<T, TensorType> {

    //https://pytorch.org/docs/stable/generated/torch.exp.html
    public fun TensorType.exp(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.log.html
    public fun TensorType.log(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.sqrt.html
    public fun TensorType.sqrt(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.square.html
    public fun TensorType.square(): TensorType

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

    //https://pytorch.org/docs/stable/generated/torch.clamp.html#torch.clamp
    public fun TensorType.clamp(min: T, max: T): TensorType

    //https://pytorch.org/docs/stable/generated/torch.erf.html#torch.erf
    public fun TensorType.erf(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.erfinv.html#torch.erfinv
    public fun TensorType.erfinv(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.erfc.html#torch.erfc
    public fun TensorType.erfc(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.lerp.html#torch.lerp
    public fun TensorType.lerp(end: TensorType, weight: TensorType): TensorType

    //https://pytorch.org/docs/stable/generated/torch.lgamma.html#torch.lgamma
    public fun TensorType.lgamma(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.logit.html#torch.logit
    public fun TensorType.logit(eps: T): TensorType

    //https://pytorch.org/docs/stable/generated/torch.igamma.html#torch.igamma
    public fun TensorType.igamma(other: TensorType): TensorType

    //https://pytorch.org/docs/stable/generated/torch.igammac.html#torch.igammac
    public fun TensorType.igammac(other: TensorType): TensorType

    //https://pytorch.org/docs/stable/generated/torch.mvlgamma.html#torch.mvlgamma
    public fun TensorType.mvlgamma(dimensions: Int): TensorType

    //https://pytorch.org/docs/stable/generated/torch.polygamma.html#torch.polygamma
    public fun TensorType.polygamma(order: Int): TensorType

    //https://pytorch.org/docs/stable/generated/torch.pow.html#torch.pow
    public fun TensorType.pow(exponent: T): TensorType

    //https://pytorch.org/docs/stable/generated/torch.round.html#torch.round
    public fun TensorType.round(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.sigmoid.html#torch.sigmoid
    public fun TensorType.sigmoid(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.sinc.html#torch.sinc
    public fun TensorType.sinc(): TensorType

    //https://pytorch.org/docs/stable/generated/torch.heaviside.html#torch.heaviside
    public fun TensorType.heaviside(values: TensorType): TensorType

    //https://pytorch.org/docs/stable/generated/torch.trapz.html#torch.trapz
    public fun TensorType.trapz(xValues: TensorType, dim: Int): TensorType

}