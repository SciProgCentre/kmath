package space.kscience.kmath.stat

import space.kscience.kmath.operations.asIterable
import space.kscience.kmath.structures.Buffer

/**
 * Rank statistics
 */
public class Rank<T : Comparable<T>> : BlockingStatistic<T, IntArray> {
    override fun evaluateBlocking(data: Buffer<T>): IntArray = Companion.evaluate(data)

    public companion object {
        public fun <T : Comparable<T>> evaluate(data: Buffer<T>): IntArray {
            // https://www.geeksforgeeks.org/rank-elements-array/
            val permutations = ArrayList<Pair<T, Int>>(data.size)
            data.asIterable().mapIndexedTo(permutations) { i, v -> v to i }
            permutations.sortBy { it.first }
            var rank = 1
            var i = 0
            val r = IntArray(data.size)
            while (i < data.size) {
                var j = i
                while (j < data.size - 1 && permutations[j].first == permutations[j + 1]) ++j
                val n = j - i + 1
                (0 until n).map { k ->
                    val idx = permutations[i + k].second
                    r[idx] = rank + ((n - 1) * 0.5f).toInt()
                }
                rank += n
                i += n
            }
            return r
        }
    }
}