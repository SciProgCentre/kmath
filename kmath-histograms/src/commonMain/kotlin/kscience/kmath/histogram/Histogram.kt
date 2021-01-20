package kscience.kmath.histogram

import kscience.kmath.domains.Domain
import kscience.kmath.linear.Point
import kscience.kmath.structures.ArrayBuffer
import kscience.kmath.structures.RealBuffer

/**
 * The bin in the histogram. The histogram is by definition always done in the real space
 */
public interface Bin<T : Any> : Domain<T> {
    /**
     * The value of this bin.
     */
    public val value: Number

    public val center: Point<T>
}

public interface Histogram<T : Any, out B : Bin<T>> : Iterable<B> {
    /**
     * Find existing bin, corresponding to given coordinates
     */
    public operator fun get(point: Point<out T>): B?

    /**
     * Dimension of the histogram
     */
    public val dimension: Int
}

public interface MutableHistogram<T : Any, out B : Bin<T>> : Histogram<T, B> {

    /**
     * Increment appropriate bin
     */
    public fun putWithWeight(point: Point<out T>, weight: Double)

    public fun put(point: Point<out T>): Unit = putWithWeight(point, 1.0)
}

public fun <T : Any> MutableHistogram<T, *>.put(vararg point: T): Unit = put(ArrayBuffer(point))

public fun MutableHistogram<Double, *>.put(vararg point: Number): Unit =
    put(RealBuffer(point.map { it.toDouble() }.toDoubleArray()))

public fun MutableHistogram<Double, *>.put(vararg point: Double): Unit = put(RealBuffer(point))
public fun <T : Any> MutableHistogram<T, *>.fill(sequence: Iterable<Point<T>>): Unit = sequence.forEach { put(it) }

/**
 * Pass a sequence builder into histogram
 */
public fun <T : Any> MutableHistogram<T, *>.fill(block: suspend SequenceScope<Point<T>>.() -> Unit): Unit =
    fill(sequence(block).asIterable())
