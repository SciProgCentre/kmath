package space.kscience.kmath.tensors

public interface ComplexTensorAlgebra<T,
        RealTensorType : TensorStructure<T>,
        ComplexTensorType : ComplexTensorStructure<T, RealTensorType>>
    : TensorPartialDivisionAlgebra<T, ComplexTensorType>{

    //https://pytorch.org/docs/stable/generated/torch.view_as_complex.html
    public fun RealTensorType.viewAsComplex(): ComplexTensorType

    //https://pytorch.org/docs/stable/generated/torch.angle.html
    public fun ComplexTensorType.angle(): RealTensorType

    //https://pytorch.org/docs/stable/generated/torch.stft.html#torch.stft
    public fun ComplexTensorType.shortTimeFourierTransform(
        nFFT: Int,
        hopLength: Int,
        winLength: Int,
        window: RealTensorType,
        normalised: Boolean,
        oneSided: Boolean
    )

    //https://pytorch.org/docs/stable/generated/torch.istft.html#torch.istft
    public fun ComplexTensorType.inverseShortTimeFourierTransform(
        nFFT: Int,
        hopLength: Int,
        winLength: Int,
        window: RealTensorType,
        center: Boolean,
        normalised: Boolean,
        oneSided: Boolean,
        length: Int
    )

    //https://pytorch.org/docs/stable/generated/torch.bartlett_window.html#torch.bartlett_window
    public fun bartlettWindow(windowLength: Int, periodic: Boolean): RealTensorType

    //https://pytorch.org/docs/stable/generated/torch.blackman_window.html#torch.blackman_window
    public fun blackmanWindow(windowLength: Int, periodic: Boolean): RealTensorType

    //https://pytorch.org/docs/stable/generated/torch.hamming_window.html#torch.hamming_window
    public fun hammingWindow(windowLength: Int, periodic: Boolean, alpha: T, beta: T): RealTensorType

    //https://pytorch.org/docs/stable/generated/torch.kaiser_window.html#torch.kaiser_window
    public fun kaiserWindow(windowLength: Int, periodic: Boolean, beta: T): RealTensorType
}