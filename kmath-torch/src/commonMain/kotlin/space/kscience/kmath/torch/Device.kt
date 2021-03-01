package space.kscience.kmath.torch


public sealed class Device {
    public object CPU: space.kscience.kmath.torch.Device() {
        override fun toString(): String {
            return "CPU"
        }
    }
    public data class CUDA(val index: Int): space.kscience.kmath.torch.Device()
    public fun toInt(): Int {
        when(this) {
            is space.kscience.kmath.torch.Device.CPU -> return 0
            is space.kscience.kmath.torch.Device.CUDA -> return this.index + 1
        }
    }
    public companion object {
        public fun fromInt(deviceInt: Int): space.kscience.kmath.torch.Device {
            return if (deviceInt == 0) space.kscience.kmath.torch.Device.CPU else space.kscience.kmath.torch.Device.CUDA(
                deviceInt - 1
            )
        }
    }
}