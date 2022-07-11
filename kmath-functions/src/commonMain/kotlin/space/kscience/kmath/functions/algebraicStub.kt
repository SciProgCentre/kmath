/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.*


// TODO: All of this should be moved to algebraic structures' place for utilities
// FIXME: Move receiver to context receiver
/**
 * Raises [arg] to the integer power [exponent].
 *
 * This is implementation of variation of [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring)
 *
 * @param arg the base of the power.
 * @param exponent the exponent of the power.
 * @return [arg] raised to the power [exponent].
 * @author Gleb Minaev
 */
internal tailrec fun <C> Ring<C>.exponentiateBySquaring(arg: C, exponent: UInt): C =
    when {
        exponent == 0u -> zero
        exponent == 1u -> arg
        exponent and 1u == 0u -> exponentiateBySquaring(arg * arg, exponent shr 1)
        exponent and 1u == 1u -> multiplyExponentiatedBySquaring(arg, arg * arg, exponent shr 1)
        else -> error("Error in multiplication group instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
    }

// FIXME: Move receiver to context receiver
/**
 * Multiplies [base] and [arg] raised to the integer power [exponent].
 *
 * This is implementation of variation of [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring)
 *
 * @param base the multiplicand.
 * @param arg the base of the power.
 * @param exponent the exponent of the power.
 * @return product of [base] and [arg] raised to the power [exponent].
 * @author Gleb Minaev
 */
internal tailrec fun <C> RingOps<C>.multiplyExponentiatedBySquaring(base: C, arg: C, exponent: UInt): C =
    when {
        exponent == 0u -> base
        exponent == 1u -> base * arg
        exponent and 1u == 0u -> multiplyExponentiatedBySquaring(base, arg * arg, exponent shr 1)
        exponent and 1u == 1u -> multiplyExponentiatedBySquaring(base * arg, arg * arg, exponent shr 1)
        else -> error("Error in multiplication group instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
    }