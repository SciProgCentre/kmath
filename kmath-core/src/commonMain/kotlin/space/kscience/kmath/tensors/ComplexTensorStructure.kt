package space.kscience.kmath.tensors

public interface ComplexTensorStructure<T, RealTensorType: TensorStructure<T>> : TensorStructure<T> {

    //https://pytorch.org/docs/master/generated/torch.view_as_real.html
    public fun viewAsReal(): RealTensorType

    //https://pytorch.org/docs/stable/generated/torch.real.html
    public fun realPart(): RealTensorType

    //https://pytorch.org/docs/stable/generated/torch.imag.html
    public fun imaginaryPart(): RealTensorType

}