/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.LongRing

/**
 * Multiplies two integers exactly, throwing an exception if an overflow occurs.
 *
 * @param a the first integer operand.
 * @param b the second integer operand.
 * @return the product of the two integers if it can be represented within the range of Int.
 * @throws ArithmeticException if the result overflows the range of Int.
 */
public fun IntRing.multiplyExact(a: Int, b: Int): Int {
    val result = a.toLong() * b // Perform multiplication using a larger type (long)
    if (result < Int.MIN_VALUE || result > Int.MAX_VALUE) {
        throw ArithmeticException("Integer overflow")
    }
    return result.toInt()
}

/**
 * Multiplies two `Long` values and returns the exact result, checking for overflow.
 * Throws an `ArithmeticException` if the multiplication overflows.
 *
 * @param a the first `Long` value to multiply.
 * @param b the second `Long` value to multiply.
 * @return the exact result of multiplying `a` and `b`.
 * @throws ArithmeticException if the multiplication results in an overflow.
 */
public fun LongRing.multiplyExact(a: Long, b: Long): Long {
    // Check if the result of a * b would overflow a long
    if (a != 0L && b != 0L) {
        val result = a * b
        // Check for overflow by using the inverse operation (division)
        if (result / b != a) {
            throw ArithmeticException("Long overflow")
        }
    } else if (a == Long.Companion.MIN_VALUE || b == Long.Companion.MIN_VALUE) {
        // Special case check for Long.MIN_VALUE as it has no positive counterpart
        if ((a == Long.Companion.MIN_VALUE && b != 0L && b != 1L) || (b == Long.Companion.MIN_VALUE && a != 0L && a != 1L)) {
            throw ArithmeticException("Long overflow")
        }
    }
    return a * b
}