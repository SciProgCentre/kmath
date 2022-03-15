/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Group


// TODO: All of this should be moved to algebraic structures' place for utilities
// TODO: Move receiver to context receiver
/**
 * Multiplication of element and integer.
 *
 * @receiver the multiplicand.
 * @param other the multiplier.
 * @return the difference.
 * @author Gleb Minaev
 */
internal fun <C> Group<C>.optimizedMultiply(arg: C, other: Int): C =
    if (other >= 0) optimizedMultiply(arg, other.toUInt())
    else optimizedMultiply(arg, (-other).toUInt())

// TODO: Move receiver to context receiver
/**
 * Adds product of [arg] and [multiplier] to [base].
 *
 * @receiver the algebra to provide multiplication.
 * @param base the augend.
 * @param arg the multiplicand.
 * @param multiplier the multiplier.
 * @return sum of the augend [base] and product of the multiplicand [arg] and the multiplier [multiplier].
 * @author Gleb Minaev
 */
internal tailrec fun <C> Group<C>.optimizedAddMultiplied(base: C, arg: C, multiplier: Int): C =
    if (multiplier >= 0) optimizedAddMultiplied(base, arg, multiplier.toUInt())
    else optimizedAddMultiplied(base, arg, (-multiplier).toUInt())

// TODO: Move receiver to context receiver
/**
 * Multiplication of element and integer.
 *
 * @receiver the multiplicand.
 * @param other the multiplier.
 * @return the difference.
 * @author Gleb Minaev
 */
internal tailrec fun <C> Group<C>.optimizedMultiply(arg: C, other: UInt): C =
    when {
        other == 0u -> zero
        other == 1u -> arg
        other % 2u == 0u -> optimizedMultiply(arg + arg, other / 2u)
        other % 2u == 1u -> optimizedAddMultiplied(arg, arg + arg, other / 2u)
        else -> error("Error in multiplication group instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
    }

// TODO: Move receiver to context receiver
/**
 * Adds product of [arg] and [multiplier] to [base].
 *
 * @receiver the algebra to provide multiplication.
 * @param base the augend.
 * @param arg the multiplicand.
 * @param multiplier the multiplier.
 * @return sum of the augend [base] and product of the multiplicand [arg] and the multiplier [multiplier].
 * @author Gleb Minaev
 */
internal tailrec fun <C> Group<C>.optimizedAddMultiplied(base: C, arg: C, multiplier: UInt): C =
    when {
        multiplier == 0u -> base
        multiplier == 1u -> base + arg
        multiplier % 2u == 0u -> optimizedAddMultiplied(base, arg + arg, multiplier / 2u)
        multiplier % 2u == 1u -> optimizedAddMultiplied(base + arg, arg + arg, multiplier / 2u)
        else -> error("Error in multiplication group instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
    }