package scientifik.kmath.histogram

import scientifik.kmath.linear.Point
import scientifik.kmath.linear.Vector
import scientifik.kmath.operations.Space
import scientifik.kmath.structures.NDStructure
import scientifik.kmath.structures.asSequence

data class BinTemplate<T : Comparable<T>>(val center: Vector<T, *>, val sizes: Point<T>) {
    fun contains(vector: Point<out T>): Boolean {
        if (vector.size != center.size) error("Dimension mismatch for input vector. Expected ${center.size}, but found ${vector.size}")
        val upper = center.context.run { center + sizes / 2.0 }
        val lower = center.context.run { center - sizes / 2.0 }
        return vector.asSequence().mapIndexed { i, value ->
            value in lower[i]..upper[i]
        }.all { it }
    }
}

/**
 * A space to perform arithmetic operations on histograms
 */
interface HistogramSpace<T : Any, B : Bin<T>, H : Histogram<T, B>> : Space<H> {
    /**
     * Rules for performing operations on bins
     */
    val binSpace: Space<Bin<T>>
}

class PhantomBin<T : Comparable<T>>(val template: BinTemplate<T>, override val value: Number) : Bin<T> {

    override fun contains(vector: Point<out T>): Boolean = template.contains(vector)

    override val dimension: Int
        get() = template.center.size

    override val center: Point<T>
        get() = template.center

}

/**
 * Immutable histogram with explicit structure for content and additional external bin description.
 * Bin search is slow, but full histogram algebra is supported.
 * @param bins transform a template into structure index
 */
class PhantomHistogram<T : Comparable<T>>(
    val bins: Map<BinTemplate<T>, IntArray>,
    val data: NDStructure<Number>
) : Histogram<T, PhantomBin<T>> {

    override val dimension: Int
        get() = data.dimension

    override fun iterator(): Iterator<PhantomBin<T>> =
            bins.asSequence().map { entry -> PhantomBin(entry.key, data[entry.value]) }.iterator()

    override fun get(point: Point<out T>): PhantomBin<T>? {
        val template = bins.keys.find { it.contains(point) }
        return template?.let { PhantomBin(it, data[bins[it]!!]) }
    }

}