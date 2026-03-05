/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

import space.kscience.kmath.operations.IntRing
import kotlin.math.max
import kotlin.math.min

/**
 * GCD algorithm based on [Commons Numbers](https://github.com/apache/commons-numbers/blob/0111af00b00c8cedf658adcc631ddbb0b400c03a/commons-numbers-core/src/main/java/org/apache/commons/numbers/core/ArithmeticUtils.java#L67)
 *
 * Computes the greatest common divisor of the absolute value of two
 * numbers, using a modified version of the "binary gcd" method.
 * See Knuth 4.5.2 algorithm B.
 * The algorithm is due to Josef Stein (1961).
 * <br></br>
 * Special cases:
 *
 *  * The invocations
 * `gcd(Integer.MIN_VALUE, Integer.MIN_VALUE)`,
 * `gcd(Integer.MIN_VALUE, 0)` and
 * `gcd(0, Integer.MIN_VALUE)` throw an
 * `ArithmeticException`, because the result would be 2^31, which
 * is too large for an int value.
 *  * The result of `gcd(x, x)`, `gcd(0, x)` and
 * `gcd(x, 0)` is the absolute value of `x`, except
 * for the special cases above.
 *  * The invocation `gcd(0, 0)` is the only one which returns
 * `0`.
 *
 *
 *
 * Two numbers are relatively prime, or coprime, if their gcd is 1.
 *
 * @param p Number.
 * @param q Number.
 * @return the greatest common divisor (never negative).
 * @throws ArithmeticException if the result cannot be represented as
 * a non-negative `int` value.
 */
public fun IntRing.gcd(p: Int, q: Int): Int {
    // Perform the gcd algorithm on negative numbers, so that -2^31 does not
    // need to be handled separately
    var a = if (p > 0) -p else p
    var b = if (q > 0) -q else q

    val negatedGcd: Int
    if (a == 0) {
        negatedGcd = b
    } else if (b == 0) {
        negatedGcd = a
    } else {
        // Make "a" and "b" odd, keeping track of common power of 2.
        val aTwos: Int = a.countTrailingZeroBits()
        val bTwos: Int = b.countTrailingZeroBits()
        a = a shr aTwos
        b = b shr bTwos
        val shift = min(aTwos, bTwos)

        // "a" and "b" are negative and odd.
        // If a < b then "gdc(a, b)" is equal to "gcd(a - b, b)".
        // If a > b then "gcd(a, b)" is equal to "gcd(b - a, a)".
        // Hence, in the successive iterations:
        //  "a" becomes the negative absolute difference of the current values,
        //  "b" becomes that value of the two that is closer to zero.
        while (a != b) {
            val delta = a - b
            b = max(a, b)
            a = if (delta > 0) -delta else delta

            // Remove any power of 2 in "a" ("b" is guaranteed to be odd).
            a = a shr a.countTrailingZeroBits()
        }

        // Recover the common power of 2.
        negatedGcd = a shl shift
    }
    if (negatedGcd == Int.Companion.MIN_VALUE) {
        error("overflow: gcd($p, $q) is 2^31")
    }
    return -negatedGcd
}