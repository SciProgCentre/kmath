package space.kscience.kmath.histogram

import space.kscience.kmath.domains.Domain
import space.kscience.kmath.linear.Point
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.asBuffer

/**
 * The binned data element. Could be a histogram bin with a number of counts or an artificial construct
 */
public interface Bin<T : Any> : Domain<T> {
    /**
     * The value of this bin.
     */
    public val value: Number
}

public interface Histogram<T : Any, out B : Bin<T>> {
    /**
     * Find existing bin, corresponding to given coordinates
     */
    public operator fun get(point: Point<out T>): B?

    /**
     * Dimension of the histogram
     */
    public val dimension: Int

    public val bins: Iterable<B>
}

public fun interface HistogramBuilder<T : Any> {

    /**
     * Increment appropriate bin
     */
    public fun putValue(point: Point<out T>, value: Number)

}

public fun <T : Any, B : Bin<T>> HistogramBuilder<T>.put(point: Point<out T>): Unit = putValue(point, 1.0)

public fun <T : Any> HistogramBuilder<T>.put(vararg point: T): Unit = put(point.asBuffer())

public fun HistogramBuilder<Double>.put(vararg point: Number): Unit =
    put(DoubleBuffer(point.map { it.toDouble() }.toDoubleArray()))

public fun HistogramBuilder<Double>.put(vararg point: Double): Unit = put(DoubleBuffer(point))
public fun <T : Any> HistogramBuilder<T>.fill(sequence: Iterable<Point<T>>): Unit = sequence.forEach { put(it) }

/**
 * Pass a sequence builder into histogram
 */
public fun <T : Any> HistogramBuilder<T>.fill(block: suspend SequenceScope<Point<T>>.() -> Unit): Unit =
    fill(sequence(block).asIterable())
