/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.test.misc

import space.kscience.kmath.operations.*
import space.kscience.kmath.operations.BigInt.Companion.ZERO as I0

// TODO: Move to corresponding module "kmath-number-theory"

/**
 * Computes [Greatest Common Divisor](https://en.wikipedia.org/wiki/Greatest_common_divisor) of [a] and [b].
 *
 * It's computed by [Euclidean algorithm](https://en.wikipedia.org/wiki/Greatest_common_divisor#Euclidean_algorithm).
 * Hence, its time complexity is $$O(\log(a+b))$$ (see [Wolfram MathWorld](https://mathworld.wolfram.com/EuclideanAlgorithm.html)).
 */
public tailrec fun gcd(a: BigInt, b: BigInt): BigInt = if (a == I0) abs(b) else gcd(b % a, a)

/**
 * Computes [Greatest Common Divisor](https://en.wikipedia.org/wiki/Greatest_common_divisor) of the [values].
 */
public fun gcd(vararg values: BigInt): BigInt = values.reduce(::gcd)
public fun gcd(values: Iterable<BigInt>): BigInt = values.reduce(::gcd)