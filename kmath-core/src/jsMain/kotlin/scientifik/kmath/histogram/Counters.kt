package scientifik.kmath.histogram

actual class LongCounter{
    private var sum: Long = 0
    actual fun decrement() {sum--}
    actual fun increment() {sum++}
    actual fun reset() {sum = 0}
    actual fun sum(): Long = sum
    actual fun add(l: Long) {sum+=l}
}
actual class DoubleCounter{
    private var sum: Double = 0.0
    actual fun reset() {sum = 0.0}
    actual fun sum(): Double = sum
    actual fun add(d: Double) {sum+=d}
}