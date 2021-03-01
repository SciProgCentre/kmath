package space.kscience.kmath.histogram

import space.kscience.kmath.domains.Domain
import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.NDSpace
import space.kscience.kmath.nd.NDStructure
import space.kscience.kmath.nd.Strides
import space.kscience.kmath.operations.Space
import space.kscience.kmath.operations.SpaceElement
import space.kscience.kmath.operations.invoke

/**
 * A simple histogram bin based on domain
 */
public data class DomainBin<T : Comparable<T>>(
    public val domain: Domain<T>,
    public override val value: Number,
) : Bin<T>, Domain<T> by domain

@OptIn(UnstableKMathAPI::class)
public class IndexedHistogram<T : Comparable<T>, V : Any>(
    override val context: IndexedHistogramSpace<T, V>,
    public val values: NDStructure<V>,
) : Histogram<T, Bin<T>>, SpaceElement<IndexedHistogram<T, V>, IndexedHistogramSpace<T, V>> {

    override fun get(point: Point<T>): Bin<T>? {
        val index = context.getIndex(point) ?: return null
        return context.produceBin(index, values[index])
    }

    override val dimension: Int get() = context.strides.shape.size

    override val bins: Iterable<Bin<T>>
        get() = context.strides.indices().map {
            context.produceBin(it, values[it])
        }.asIterable()

}

/**
 * A space for producing histograms with values in a NDStructure
 */
public interface IndexedHistogramSpace<T : Comparable<T>, V : Any> : Space<IndexedHistogram<T, V>> {
    //public val valueSpace: Space<V>
    public val strides: Strides
    public val histogramValueSpace: NDSpace<V, *> //= NDAlgebra.space(valueSpace, Buffer.Companion::boxing, *shape),

    /**
     * Resolve index of the bin including given [point]
     */
    public fun getIndex(point: Point<T>): IntArray?

    /**
     * Get a bin domain represented by given index
     */
    public fun getDomain(index: IntArray): Domain<T>?

    public fun produceBin(index: IntArray, value: V): Bin<T>

    public fun produce(builder: HistogramBuilder<T>.() -> Unit): IndexedHistogram<T, V>

    override fun add(a: IndexedHistogram<T, V>, b: IndexedHistogram<T, V>): IndexedHistogram<T, V> {
        require(a.context == this) { "Can't operate on a histogram produced by external space" }
        require(b.context == this) { "Can't operate on a histogram produced by external space" }
        return IndexedHistogram(this, histogramValueSpace.invoke { a.values + b.values })
    }

    override fun multiply(a: IndexedHistogram<T, V>, k: Number): IndexedHistogram<T, V> {
        require(a.context == this) { "Can't operate on a histogram produced by external space" }
        return IndexedHistogram(this, histogramValueSpace.invoke { a.values * k })
    }

    override val zero: IndexedHistogram<T, V> get() = produce { }
}

