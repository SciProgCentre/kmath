/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.*


// TODO: All of this should be moved to algebraic structures' place for utilities
// FIXME: Move receiver to context receiver
/**
 * Returns product of [arg] and integer [multiplier].
 *
 * @param arg the multiplicand.
 * @param multiplier the integer multiplier.
 * @return product of the multiplicand [arg] and the multiplier [multiplier].
 * @author Gleb Minaev
 */
internal fun <C> Group<C>.multiplyByDoubling(arg: C, multiplier: Int): C =
    if (multiplier >= 0) multiplyByDoubling(arg, multiplier.toUInt())
    else multiplyByDoubling(-arg, (-multiplier).toUInt())

// FIXME: Move receiver to context receiver
/**
 * Adds product of [arg] and [multiplier] to [base].
 *
 * @param base the augend.
 * @param arg the multiplicand.
 * @param multiplier the integer multiplier.
 * @return sum of the augend [base] and product of the multiplicand [arg] and the multiplier [multiplier].
 * @author Gleb Minaev
 */
internal fun <C> GroupOps<C>.addMultipliedByDoubling(base: C, arg: C, multiplier: Int): C =
    if (multiplier >= 0) addMultipliedByDoubling(base, arg, multiplier.toUInt())
    else addMultipliedByDoubling(base, -arg, (-multiplier).toUInt())

// FIXME: Move receiver to context receiver
/**
 * Returns product of [arg] and integer [multiplier].
 *
 * This is implementation of variation of [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring)
 *
 * @param arg the multiplicand.
 * @param multiplier the integer multiplier.
 * @return product of the multiplicand [arg] and the multiplier [multiplier].
 * @author Gleb Minaev
 */
internal tailrec fun <C> Group<C>.multiplyByDoubling(arg: C, multiplier: UInt): C =
    when {
        multiplier == 0u -> zero
        multiplier == 1u -> arg
        multiplier and 1u == 0u -> multiplyByDoubling(arg + arg, multiplier shr 1)
        multiplier and 1u == 1u -> addMultipliedByDoubling(arg, arg + arg, multiplier shr 1)
        else -> error("Error in multiplication group instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
    }

// FIXME: Move receiver to context receiver
/**
 * Adds product of [arg] and [multiplier] to [base].
 *
 * This is implementation of variation of [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring)
 *
 * @param base the augend.
 * @param arg the multiplicand.
 * @param multiplier the integer multiplier.
 * @return sum of the augend [base] and product of the multiplicand [arg] and the multiplier [multiplier].
 * @author Gleb Minaev
 */
internal tailrec fun <C> GroupOps<C>.addMultipliedByDoubling(base: C, arg: C, multiplier: UInt): C =
    when {
        multiplier == 0u -> base
        multiplier == 1u -> base + arg
        multiplier and 1u == 0u -> addMultipliedByDoubling(base, arg + arg, multiplier shr 1)
        multiplier and 1u == 1u -> addMultipliedByDoubling(base + arg, arg + arg, multiplier shr 1)
        else -> error("Error in multiplication group instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
    }

// FIXME: Move receiver to context receiver
/**
 * Raises [arg] to the integer power [exponent].
 *
 * @param arg the base of the power.
 * @param exponent the exponent of the power.
 * @return [arg] raised to the power [exponent].
 * @author Gleb Minaev
 */
internal fun <C> Field<C>.exponentiateBySquaring(arg: C, exponent: Int): C =
    if (exponent >= 0) exponentiateBySquaring(arg, exponent.toUInt())
    else exponentiateBySquaring(one / arg, (-exponent).toUInt())

// FIXME: Move receiver to context receiver
/**
 * Multiplies [base] and [arg] raised to the integer power [exponent].
 *
 * @param base the multiplicand.
 * @param arg the base of the power.
 * @param exponent the exponent of the power.
 * @return product of [base] and [arg] raised to the power [exponent].
 * @author Gleb Minaev
 */
internal fun <C> Field<C>.multiplyExponentiatedBySquaring(base: C, arg: C, exponent: Int): C =
    if (exponent >= 0) multiplyExponentiatedBySquaring(base, arg, exponent.toUInt())
    else multiplyExponentiatedBySquaring(base, one / arg, (-exponent).toUInt())

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