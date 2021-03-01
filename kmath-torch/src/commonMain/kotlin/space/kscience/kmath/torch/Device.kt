package space.kscience.kmath.torch


public sealed class Device {
    public object CPU: Device() {
        override fun toString(): String {
            return "CPU"
        }
    }
    public data class CUDA(val index: Int): Device()
    public fun toInt(): Int {
        when(this) {
            is CPU -> return 0
            is CUDA -> return this.index + 1
        }
    }
    public companion object {
        public fun fromInt(deviceInt: Int): Device {
            return if (deviceInt == 0) CPU else CUDA(
                deviceInt - 1
            )
        }
    }
}