package space.kscience.kmath.stat

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.misc.sorted
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBufferFactory

public interface StatisticalAlgebra<T, out A : Algebra<T>, out BA : BufferAlgebra<T, A>> : Algebra<Buffer<T>> {
    public val bufferAlgebra: BA
    public val elementAlgebra: A get() = bufferAlgebra.elementAlgebra
    override val bufferFactory: MutableBufferFactory<Buffer<T>> get() = bufferAlgebra.bufferFactory
}

/**
 * Compute [empirical CDF function](https://en.wikipedia.org/wiki/Empirical_distribution_function)
 */
public fun <T : Comparable<T>> StatisticalAlgebra<T, *, *>.ecdf(buffer: Buffer<T>): (T) -> Double = { arg ->
    buffer.asIterable().count { it < arg }.toDouble() / buffer.size
}

/**
 * Resulting value of kolmogorov-smirnov two-sample statistic
 */
@UnstableKMathAPI
public data class KMComparisonResult<T : Comparable<T>>(val n: Int, val m: Int, val value: T)

/**
 * Kolmogorov-Smirnov sample comparison test
 * Implementation copied from https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/index.html?org/apache/commons/math3/stat/inference/KolmogorovSmirnovTest.html
 */
@UnstableKMathAPI
public fun <T : Comparable<T>, A, BA : BufferAlgebra<T, A>> StatisticalAlgebra<T, A, BA>.ksComparisonStatistic(
    x: Buffer<T>,
    y: Buffer<T>,
): KMComparisonResult<T> where A : Group<T>, A : NumericAlgebra<T> = with(elementAlgebra) {
    // Copy and sort the sample arrays
    val sx = x.sorted()
    val sy = y.sorted()
    val n = sx.size
    val m = sy.size

    var rankX: Int = 0
    var rankY: Int = 0
    var curD: T = zero

    // Find the max difference between cdf_x and cdf_y
    var supD: T = zero
    do {
        val z = if (sx[rankX] <= sy[rankY]) sx[rankX] else sy[rankY]
        while (rankX < n && sx[rankX].compareTo(z) == 0) {
            rankX += 1
            curD += number(m)
        }

        while (rankY < m && sy[rankY].compareTo(z) == 0) {
            rankY += 1
            curD -= number(n)
        }

        when {
            curD > supD -> supD = curD
            -curD > supD -> supD = -curD
        }
    } while (rankX < n && rankY < m)
    return KMComparisonResult(n, m, supD)
}