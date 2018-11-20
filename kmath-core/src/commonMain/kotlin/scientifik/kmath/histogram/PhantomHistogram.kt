package scientifik.kmath.histogram

import scientifik.kmath.linear.RealVector
import scientifik.kmath.structures.NDStructure

class BinTemplate(val center: RealVector, val sizes: RealVector) {
    fun contains(vector: Point<out Double>): Boolean {
        if (vector.size != center.size) error("Dimension mismatch for input vector. Expected ${center.size}, but found ${vector.size}")
        return vector.asSequence().mapIndexed { i, value -> value in (center[i] - sizes[i] / 2)..(center[i] + sizes[i] / 2) }.all { it }
    }
}

class PhantomBin(val template: BinTemplate, override val value: Number) : Bin<Double> {

    override fun contains(vector: Point<out Double>): Boolean = template.contains(vector)

    override val dimension: Int
        get() = template.center.size

    override val center: Point<Double>
        get() = template.center

}

class PhantomHistogram(
        val bins: Map<BinTemplate, IntArray>,
        val data: NDStructure<Double>
) : Histogram<Double, PhantomBin> {

    override val dimension: Int
        get() = data.dimension

    override fun iterator(): Iterator<PhantomBin> {
        return bins.asSequence().map {entry-> PhantomBin(entry.key,data[entry.value]) }.iterator()
    }

    override fun get(point: Point<out Double>): PhantomBin? {
        val template = bins.keys.find { it.contains(point) }
        return template?.let { PhantomBin(it, data[bins[it]!!]) }
    }

}