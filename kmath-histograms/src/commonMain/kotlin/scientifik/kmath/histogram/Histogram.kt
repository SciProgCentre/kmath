package scientifik.kmath.histogram

import scientifik.kmath.domains.Domain
import scientifik.kmath.linear.Point
import scientifik.kmath.structures.ArrayBuffer
import scientifik.kmath.structures.RealBuffer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The bin in the histogram. The histogram is by definition always done in the real space
 */
interface Bin<T : Any> : Domain<T> {
    /**
     * The value of this bin
     */
    val value: Number
    val center: Point<T>
}

interface Histogram<T : Any, out B : Bin<T>> : Iterable<B> {

    /**
     * Find existing bin, corresponding to given coordinates
     */
    operator fun get(point: Point<out T>): B?

    /**
     * Dimension of the histogram
     */
    val dimension: Int

}

interface MutableHistogram<T : Any, out B : Bin<T>> : Histogram<T, B> {

    /**
     * Increment appropriate bin
     */
    fun putWithWeight(point: Point<out T>, weight: Double)

    fun put(point: Point<out T>): Unit = putWithWeight(point, 1.0)
}

fun <T : Any> MutableHistogram<T, *>.put(vararg point: T): Unit = put(ArrayBuffer(point))

fun MutableHistogram<Double, *>.put(vararg point: Number): Unit =
    put(RealBuffer(point.map { it.toDouble() }.toDoubleArray()))

fun MutableHistogram<Double, *>.put(vararg point: Double): Unit = put(RealBuffer(point))

fun <T : Any> MutableHistogram<T, *>.fill(sequence: Iterable<Point<T>>): Unit = sequence.forEach { put(it) }

/**
 * Pass a sequence builder into histogram
 */
fun <T : Any> MutableHistogram<T, *>.fill(block: suspend SequenceScope<Point<T>>.() -> Unit): Unit =
    fill(sequence(block).asIterable())
