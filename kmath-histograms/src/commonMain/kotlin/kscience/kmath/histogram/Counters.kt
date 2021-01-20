package kscience.kmath.histogram

/*
 * Common representation for atomic counters
 * TODO replace with atomics
 */

public expect class LongCounter() {
    public fun decrement()
    public fun increment()
    public fun reset()
    public fun sum(): Long
    public fun add(l: Long)
}

public expect class DoubleCounter() {
    public fun reset()
    public fun sum(): Double
    public fun add(d: Double)
}
