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
 */
internal tailrec fun <C> Group<C>.optimizedMultiply(arg: C, other: Int): C =
    when {
        other == 0 -> zero
        other == 1 -> arg
        other == -1 -> -arg
        other % 2 == 0 -> optimizedMultiply(arg + arg, other / 2)
        other % 2 == 1 -> optimizedAddMultiplied(arg, arg + arg, other / 2)
        other % 2 == -1 -> optimizedAddMultiplied(-arg, arg + arg, other / 2)
        else -> error("Error in multiplication group instant by integer: got reminder by division by 2 different from 0, 1 and -1")
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
internal tailrec fun <C> Group<C>.optimizedAddMultiplied(base: C, arg: C, multiplier: Int): C =
    when {
        multiplier == 0 -> base
        multiplier == 1 -> base + arg
        multiplier == -1 -> base - arg
        multiplier % 2 == 0 -> optimizedAddMultiplied(base, arg + arg, multiplier / 2)
        multiplier % 2 == 1 -> optimizedAddMultiplied(base + arg, arg + arg, multiplier / 2)
        multiplier % 2 == -1 -> optimizedAddMultiplied(base + arg, arg + arg, multiplier / 2)
        else -> error("Error in multiplication group instant by integer: got reminder by division by 2 different from 0, 1 and -1")
    }