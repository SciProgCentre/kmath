package kscience.kmath.torch


public sealed class TorchDevice {
    public object TorchCPU: TorchDevice()
    public data class TorchCUDA(val index: Int): TorchDevice()
    public fun toInt(): Int {
        when(this) {
            is TorchCPU -> return 0
            is TorchCUDA -> return this.index + 1
        }
    }
    public companion object {
        public fun fromInt(deviceInt: Int): TorchDevice {
            return if (deviceInt == 0) TorchCPU else TorchCUDA(deviceInt-1)
        }
    }
}