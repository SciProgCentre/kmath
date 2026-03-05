/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.LongRing

/*
 *
 * Binomial coefficient implementation based on [Common Numbers](https://github.com/apache/commons-numbers/blob/master/commons-numbers-combinatorics/src/main/java/org/apache/commons/numbers/combinatorics/BinomialCoefficient.java)
 *
 * Representation of the [
 * binomial coefficient](https://mathworld.wolfram.com/BinomialCoefficient.html).
 * It is "`n choose k`", the number of `k`-element subsets that
 * can be selected from an `n`-element set.
 */

/** The maximum m that can be computed without overflow of a long.
 * `C(68, 34) > 2^63`.  */
private const val MAX_M = 33

/** The maximum n that can be computed without intermediate overflow for any m.
 * `C(61, 30) * 30 < 2^63`.  */
private const val SMALL_N = 61

/** The maximum n that can be computed without overflow of a long for any m.
 * `C(66, 33) < 2^63`.  */
private const val LIMIT_N = 66


/**
 * Check binomial preconditions.
 *
 *
 * For convenience in implementations this returns the smaller of
 * `k` or `n - k` allowing symmetry to be exploited in
 * computing the binomial coefficient.
 *
 * @param n Size of the set.
 * @param k Size of the subsets to be counted.
 * @return min(k, n - k)
 * @throws IllegalArgumentException if `n < 0`.
 * @throws IllegalArgumentException if `k > n` or `k < 0`.
 */
private fun checkBinomial(
    n: Int,
    k: Int
): Int {
    // Combine all checks with a single branch:
    // 0 <= n; 0 <= k <= n
    // Note: If n >= 0 && k >= 0 && n - k < 0 then k > n.
    val m = n - k
    // Bitwise or will detect a negative sign bit in any of the numbers
    if ((n or k or m) < 0) {
        // Raise the correct exception
        if (n < 0) {
            throw IllegalArgumentException("n < 0")
        }
        throw IllegalArgumentException("k > n or k < 0")
    }
    return if (m < k) m else k
}

/**
 * Computes the binomial coefficient.
 *
 *
 * The largest value of `n` for which *all* coefficients can
 * fit into a `long` is 66. Larger `n` may result in an
 * [ArithmeticException] depending on the value of `k`.
 *
 *
 * Any `min(k, n - k) >= 34` cannot fit into a `long`
 * and will result in an [ArithmeticException].
 *
 * @param n Size of the set.
 * @param k Size of the subsets to be counted.
 * @return `n choose k`.
 * @throws IllegalArgumentException if `n < 0`, `k < 0` or `k > n`.
 * @throws ArithmeticException if the result is too large to be
 * represented by a `long`.
 */
public fun IntRing.binomialCoefficient(n: Int, k: Int): Long {
    val m = checkBinomial(n, k)

    if (m == 0) {
        return 1
    }
    if (m == 1) {
        return n.toLong()
    }

    // We use the formulae:
    // (n choose m) = n! / (n-m)! / m!
    // (n choose m) = ((n-m+1)*...*n) / (1*...*m)
    // which can be written
    // (n choose m) = (n-1 choose m-1) * n / m
    var result: Long = 1
    if (n <= SMALL_N) {
        // For n <= 61, the naive implementation cannot overflow.
        var i = n - m + 1
        for (j in 1..m) {
            result = result * i / j
            i++
        }
    } else if (n <= LIMIT_N) {
        // For n > 61 but n <= 66, the result cannot overflow,
        // but we must take care not to overflow intermediate values.
        var i = n - m + 1
        for (j in 1..m) {
            // We know that (result * i) is divisible by j,
            // but (result * i) may overflow, so we split j:
            // Filter out the gcd, d, so j/d and i/d are integer.
            // result is divisible by (j/d) because (j/d)
            // is relative prime to (i/d) and is a divisor of
            // result * (i/d).
            val d: Int = gcd(i, j)
            result = (result / (j / d)) * (i / d)
            ++i
        }
    } else {
        if (m > MAX_M) {
            error("$n choose $k")
        }

        // For n > 66, a result overflow might occur, so we check
        // the multiplication, taking care to not overflow
        // unnecessary.
        var i = n - m + 1
        for (j in 1..m) {
            val d = gcd(i, j)
            result = LongRing.multiplyExact(result / (j / d), (i / d).toLong())
            ++i
        }
    }

    return result
}