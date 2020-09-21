package kscience.kmath.histogram

public actual class LongCounter {
    private var sum: Long = 0L

    public actual fun decrement() {
        sum--
    }

    public actual fun increment() {
        sum++
    }

    public actual fun reset() {
        sum = 0
    }

    public actual fun sum(): Long = sum

    public actual fun add(l: Long) {
        sum += l
    }
}

public actual class DoubleCounter {
    private var sum: Double = 0.0

    public actual fun reset() {
        sum = 0.0
    }

    public actual fun sum(): Double = sum

    public actual fun add(d: Double) {
        sum += d
    }
}
