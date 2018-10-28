package scientifik.kmath.histogram

/*
 * Common representation for atomic counters
 */


expect class LongCounter(){
    fun	decrement()
    fun	increment()
    fun	reset()
    fun	sum(): Long
    fun add(l:Long)
}

expect class DoubleCounter(){
    fun	reset()
    fun	sum(): Double
    fun add(d: Double)
}