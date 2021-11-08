package space.kscience.kmath.stat

import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.asIterable
import space.kscience.kmath.structures.sorted

public interface StatisticalAlgebra<T, out A : Algebra<T>, out BA : BufferAlgebra<T, A>> : Algebra<Buffer<T>> {
    public val bufferAlgebra: BA
    public val elementAlgebra: A get() = bufferAlgebra.elementAlgebra
    public val bufferFactory: BufferFactory<T> get() = bufferAlgebra.bufferFactory
}

/**
 * Compute [empirical CDF function](https://en.wikipedia.org/wiki/Empirical_distribution_function)
 */
public fun <T : Comparable<T>> StatisticalAlgebra<T, *, *>.ecdf(buffer: Buffer<T>): (T) -> Double = { arg ->
    buffer.asIterable().count { it < arg }.toDouble() / buffer.size
}

/**
 * Implementation copied from https://commons.apache.org/proper/commons-math/javadocs/api-3.6.1/index.html?org/apache/commons/math3/stat/inference/KolmogorovSmirnovTest.html
 */
public fun <T : Comparable<T>, A, BA : BufferAlgebra<T, A>> StatisticalAlgebra<T, A, BA>.kolmogorovSmirnovTest(
    x: Buffer<T>,
    y: Buffer<T>,
): T where A : Group<T>, A : NumericAlgebra<T> = elementAlgebra.invoke {
    // Copy and sort the sample arrays
    val sx = x.sorted()
    val sy = y.sorted()
    val n = sx.size
    val m = sy.size

    var rankX = 0
    var rankY = 0
    var curD: T = zero

    // Find the max difference between cdf_x and cdf_y
    var supD: T = zero
    do {
        val z = if (sx[rankX] <= sy[rankY]) sx[rankX] else sy[rankY]
        while (rankX < n && sx[rankX].compareTo(z) == 0) {
            rankX += 1;
            curD += number(m);
        }

        while (rankY < m && sy[rankY].compareTo(z) == 0) {
            rankY += 1;
            curD -= number(n);
        }

        when {
            curD > supD -> supD = curD
            -curD > supD -> supD = -curD
        }
    } while (rankX < n && rankY < m);
    return supD;
}