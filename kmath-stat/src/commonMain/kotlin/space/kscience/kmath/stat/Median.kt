package space.kscience.kmath.stat

import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.asSequence

/**
 * Non-composable median
 */
public class Median<T>(private val comparator: Comparator<T>) : BlockingStatistic<T, T> {
    public override fun evaluateBlocking(data: Buffer<T>): T =
        data.asSequence().sortedWith(comparator).toList()[data.size / 2] //TODO check if this is correct

    public companion object {
        public val real: Median<Double> = Median { a: Double, b: Double -> a.compareTo(b) }
    }
}