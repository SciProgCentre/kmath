/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.samplers

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.random.chain
import space.kscience.kmath.stat.Sampler
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * Distribution sampler that uses the Alias method. It can be used to sample from n values each with an associated
 * probability. This implementation is based on the detailed explanation of the alias method by Keith Schartz and
 * implements Vose's algorithm.
 *
 * Vose, M.D., A linear algorithm for generating random numbers with a given distribution, IEEE Transactions on
 * Software Engineering, 17, 972-975, 1991. The algorithm will sample values in O(1) time after a pre-processing step
 * of O(n) time.
 *
 * The alias tables are constructed using fraction probabilities with an assumed denominator of 253. In the generic
 * case sampling uses UniformRandomProvider.nextInt(int) and the upper 53-bits from UniformRandomProvider.nextLong().
 *
 * Zero padding the input probabilities can be used to make more sampling more efficient. Any zero entry will always be
 * aliased removing the requirement to compute a long. Increased sampling speed comes at the cost of increased storage
 * space. The algorithm requires approximately 12 bytes of storage per input probability, that is n * 12 for size n.
 * Zero-padding only requires 4 bytes of storage per padded value as the probability is known to be zero.
 *
 * An optimisation is performed for small table sizes that are a power of 2. In this case the sampling uses 1 or 2
 * calls from UniformRandomProvider.nextInt() to generate up to 64-bits for creation of an 11-bit index and 53-bits
 * for the long. This optimisation requires a generator with a high cycle length for the lower order bits.
 *
 * Larger table sizes that are a power of 2 will benefit from fast algorithms for UniformRandomProvider.nextInt(int)
 * that exploit the power of 2.
 *
 * Based on Commons RNG implementation.
 * See [https://commons.apache.org/proper/commons-rng/commons-rng-sampling/apidocs/org/apache/commons/rng/sampling/distribution/AliasMethodDiscreteSampler.html].
 */
public open class AliasMethodDiscreteSampler private constructor(
    // Deliberate direct storage of input arrays
    protected val probability: LongArray,
    protected val alias: IntArray,
) : Sampler<Int> {

    private class SmallTableAliasMethodDiscreteSampler(
        probability: LongArray,
        alias: IntArray,
    ) : AliasMethodDiscreteSampler(probability, alias) {
        // Assume the table size is a power of 2 and create the mask
        private val mask: Int = alias.size - 1

        override fun sample(generator: RandomGenerator): Chain<Int> = generator.chain {
            val bits = generator.nextInt()
            // Isolate lower bits
            val j = bits and mask

            // Optimisation for zero-padded input tables
            if (j >= probability.size)
            // No probability must use the alias
                return@chain alias[j]

            // Create a uniform random deviate as a long.
            // This replicates functionality from the o.a.c.rng.core.utils.NumberFactory.makeLong
            val longBits = generator.nextInt().toLong() shl 32 or (bits.toLong() and hex_ffffffff)
            // Choose between the two. Use a 53-bit long for the probability.
            if (longBits ushr 11 < probability[j]) j else alias[j]
        }

        private companion object {
            private const val hex_ffffffff = 4294967295L
        }
    }

    override fun sample(generator: RandomGenerator): Chain<Int> = generator.chain {
        // This implements the algorithm in accordance with Vose (1991):
        // v = uniform()  in [0, 1)
        // j = uniform(n) in [0, n)
        // if v < prob[j] then
        //   return j
        // else
        //   return alias[j]
        val j = generator.nextInt(alias.size)

        // Optimisation for zero-padded input tables
        // No probability must use the alias
        if (j >= probability.size) return@chain alias[j]

        // Note: We could check the probability before computing a deviate.
        // p(j) == 0  => alias[j]
        // p(j) == 1  => j
        // However it is assumed these edge cases are rare:
        //
        // The probability table will be 1 for approximately 1/n samples i.e., only the
        // last unpaired probability. This is only worth checking for when the table size (n)
        // is small. But in that case the user should zero-pad the table for performance.
        //
        // The probability table will be 0 when an input probability was zero. We
        // will assume this is also rare if modelling a discrete distribution where
        // all samples are possible. The edge case for zero-padded tables is handled above.

        // Choose between the two. Use a 53-bit long for the probability.
        if (generator.nextLong() ushr 11 < probability[j]) j else alias[j]
    }

    override fun toString(): String = "Alias method"

    public companion object {
        private const val DEFAULT_ALPHA = 0
        private const val ZERO = 0.0
        private const val ONE_AS_NUMERATOR = 1L shl 53
        private const val CONVERT_TO_NUMERATOR: Double = ONE_AS_NUMERATOR.toDouble()
        private const val MAX_SMALL_POWER_2_SIZE = 1 shl 11

        private fun fillRemainingIndices(length: Int, indices: IntArray, small: Int): Int {
            var updatedSmall = small
            (length until indices.size).forEach { i -> indices[updatedSmall++] = i }
            return updatedSmall
        }

        private fun findLastNonZeroIndex(probabilities: DoubleArray): Int {
            // No bounds check is performed when decrementing as the array contains at least one
            // value above zero.
            var nonZeroIndex = probabilities.size - 1
            while (probabilities[nonZeroIndex] == ZERO) nonZeroIndex--
            return nonZeroIndex
        }

        private fun computeSize(length: Int, alpha: Int): Int {
            // If No padding
            if (alpha < 0) return length
            // Use the number of leading zeros function to find the next power of 2,
            // i.e. ceil(log2(x))
            var pow2 = 32 - numberOfLeadingZeros(length - 1)
            // Increase by the alpha. Clip this to limit to a positive integer (2^30)
            pow2 = min(30, pow2 + alpha)
            // Use max to handle a length above the highest possible power of 2
            return max(length, 1 shl pow2)
        }

        private fun fillTable(
            probability: LongArray,
            alias: IntArray,
            indices: IntArray,
            start: Int,
            end: Int,
        ) = (start until end).forEach { i ->
            val index = indices[i]
            probability[index] = ONE_AS_NUMERATOR
            alias[index] = index
        }

        private fun isSmallPowerOf2(n: Int): Boolean = n <= MAX_SMALL_POWER_2_SIZE && n and n - 1 == 0

        private fun numberOfLeadingZeros(i: Int): Int {
            var mutI = i
            if (mutI <= 0) return if (mutI == 0) 32 else 0
            var n = 31

            if (mutI >= 1 shl 16) {
                n -= 16
                mutI = mutI ushr 16
            }

            if (mutI >= 1 shl 8) {
                n -= 8
                mutI = mutI ushr 8
            }

            if (mutI >= 1 shl 4) {
                n -= 4
                mutI = mutI ushr 4
            }

            if (mutI >= 1 shl 2) {
                n -= 2
                mutI = mutI ushr 2
            }

            return n - (mutI ushr 1)
        }
    }

    @Suppress("FunctionName")
    public fun AliasMethodDiscreteSampler(
        probabilities: DoubleArray,
        alpha: Int = DEFAULT_ALPHA,
    ): Sampler<Int> {
        // The Alias method balances N categories with counts around the mean into N sections,
        // each allocated 'mean' observations.
        //
        // Consider 4 categories with counts 6,3,2,1. The histogram can be balanced into a
        // 2D array as 4 sections with a height of the mean:
        //
        // 6
        // 6
        // 6
        // 63   => 6366   --
        // 632     6326    |-- mean
        // 6321    6321   --
        //
        // section abcd
        //
        // Each section is divided as:
        // a: 6=1/1
        // b: 3=1/1
        // c: 2=2/3; 6=1/3   (6 is the alias)
        // d: 1=1/3; 6=2/3   (6 is the alias)
        //
        // The sample is obtained by randomly selecting a section, then choosing, which category
        // from the pair based on a uniform random deviate.
        val sumProb = InternalUtils.validateProbabilities(probabilities)
        // Allow zero-padding
        val n = computeSize(probabilities.size, alpha)
        // Partition into small and large by splitting on the average.
        val mean = sumProb / n
        // The cardinality of smallSize + largeSize = n.
        // So fill the same array from either end.
        val indices = IntArray(n)
        var large = n
        var small = 0

        probabilities.indices.forEach { i ->
            if (probabilities[i] >= mean) indices[--large] = i else indices[small++] = i
        }

        small = fillRemainingIndices(probabilities.size, indices, small)
        // This may be smaller than the input length if the probabilities were already padded.
        val nonZeroIndex = findLastNonZeroIndex(probabilities)
        // The probabilities are modified so use a copy.
        // Note: probabilities are required only up to last nonZeroIndex
        val remainingProbabilities = probabilities.copyOf(nonZeroIndex + 1)
        // Allocate the final tables.
        // Probability table may be truncated (when zero padded).
        // The alias table is full length.
        val probability = LongArray(remainingProbabilities.size)
        val alias = IntArray(n)

        // This loop uses each large in turn to fill the alias table for small probabilities that
        // do not reach the requirement to fill an entire section alone (i.e., p < mean).
        // Since the sum of the small should be less than the sum of the large it should use up
        // all the small first. However, floating point round-off can result in
        // misclassification of items as small or large. The Vose algorithm handles this using
        // a while loop conditioned on the size of both sets and a subsequent loop to use
        // unpaired items.
        while (large != n && small != 0) {
            // Index of the small and the large probabilities.
            val j = indices[--small]
            val k = indices[large++]

            // Optimisation for zero-padded input:
            // p(j) = 0 above the last nonZeroIndex
            if (j > nonZeroIndex)
            // The entire amount for the section is taken from the alias.
                remainingProbabilities[k] -= mean
            else {
                val pj = remainingProbabilities[j]
                // Item j is a small probability that is below the mean.
                // Compute the weight of the section for item j: pj / mean.
                // This is scaled by 2^53 and the ceiling function used to round-up
                // the probability to a numerator of a fraction in the range [1,2^53].
                // Ceiling ensures non-zero values.
                probability[j] = ceil(CONVERT_TO_NUMERATOR * (pj / mean)).toLong()
                // The remaining amount for the section is taken from the alias.
                // Effectively: probabilities[k] -= (mean - pj)
                remainingProbabilities[k] += pj - mean
            }

            // If not j then the alias is k
            alias[j] = k

            // Add the remaining probability from large to the appropriate list.
            if (remainingProbabilities[k] >= mean) indices[--large] = k else indices[small++] = k
        }

        // Final loop conditions to consume unpaired items.
        // Note: The large set should never be non-empty but this can occur due to round-off
        // error so consume from both.
        fillTable(probability, alias, indices, 0, small)
        fillTable(probability, alias, indices, large, n)

        // Change the algorithm for small power of 2 sized tables
        return if (isSmallPowerOf2(n)) {
            SmallTableAliasMethodDiscreteSampler(probability, alias)
        } else {
            AliasMethodDiscreteSampler(probability, alias)
        }
    }
}
