/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.*


// TODO: All of this should be moved to algebraic structures' place for utilities
// TODO: Move receiver to context receiver
/**
 * Returns product of [arg] and integer [multiplier].
 *
 * @param arg the multiplicand.
 * @param multiplier the integer multiplier.
 * @return product of the multiplicand [arg] and the multiplier [multiplier].
 * @author Gleb Minaev
 */
internal fun <C> Group<C>.multiplyBySquaring(arg: C, multiplier: Int): C =
    if (multiplier >= 0) multiplyBySquaring(arg, multiplier.toUInt())
    else multiplyBySquaring(-arg, (-multiplier).toUInt())

// TODO: Move receiver to context receiver
/**
 * Adds product of [arg] and [multiplier] to [base].
 *
 * @param base the augend.
 * @param arg the multiplicand.
 * @param multiplier the integer multiplier.
 * @return sum of the augend [base] and product of the multiplicand [arg] and the multiplier [multiplier].
 * @author Gleb Minaev
 */
internal fun <C> GroupOps<C>.addMultipliedBySquaring(base: C, arg: C, multiplier: Int): C =
    if (multiplier >= 0) addMultipliedBySquaring(base, arg, multiplier.toUInt())
    else addMultipliedBySquaring(base, -arg, (-multiplier).toUInt())

// TODO: Move receiver to context receiver
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
internal tailrec fun <C> Group<C>.multiplyBySquaring(arg: C, multiplier: UInt): C =
    when {
        multiplier == 0u -> zero
        multiplier == 1u -> arg
        multiplier and 1u == 0u -> multiplyBySquaring(arg + arg, multiplier shr 1)
        multiplier and 1u == 1u -> addMultipliedBySquaring(arg, arg + arg, multiplier shr 1)
        else -> error("Error in multiplication group instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
    }

// TODO: Move receiver to context receiver
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
internal tailrec fun <C> GroupOps<C>.addMultipliedBySquaring(base: C, arg: C, multiplier: UInt): C =
    when {
        multiplier == 0u -> base
        multiplier == 1u -> base + arg
        multiplier and 1u == 0u -> addMultipliedBySquaring(base, arg + arg, multiplier shr 1)
        multiplier and 1u == 1u -> addMultipliedBySquaring(base + arg, arg + arg, multiplier shr 1)
        else -> error("Error in multiplication group instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
    }

// TODO: Move receiver to context receiver
/**
 * Raises [arg] to the integer power [exponent].
 *
 * @param arg the base of the power.
 * @param exponent the exponent of the power.
 * @return [arg] raised to the power [exponent].
 * @author Gleb Minaev
 */
internal fun <C> Field<C>.exponentiationBySquaring(arg: C, exponent: Int): C =
    if (exponent >= 0) exponentiationBySquaring(arg, exponent.toUInt())
    else exponentiationBySquaring(one / arg, (-exponent).toUInt())

// TODO: Move receiver to context receiver
/**
 * Multiplies [base] and [arg] raised to the integer power [exponent].
 *
 * @param base the multiplicand.
 * @param arg the base of the power.
 * @param exponent the exponent of the power.
 * @return product of [base] and [arg] raised to the power [exponent].
 * @author Gleb Minaev
 */
internal fun <C> Field<C>.multiplyExponentiationBySquaring(base: C, arg: C, exponent: Int): C =
    if (exponent >= 0) multiplyExponentiationBySquaring(base, arg, exponent.toUInt())
    else multiplyExponentiationBySquaring(base, one / arg, (-exponent).toUInt())

// TODO: Move receiver to context receiver
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
internal tailrec fun <C> Ring<C>.exponentiationBySquaring(arg: C, exponent: UInt): C =
    when {
        exponent == 0u -> zero
        exponent == 1u -> arg
        exponent and 1u == 0u -> exponentiationBySquaring(arg * arg, exponent shr 1)
        exponent and 1u == 1u -> multiplyExponentiationBySquaring(arg, arg * arg, exponent shr 1)
        else -> error("Error in multiplication group instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
    }

// TODO: Move receiver to context receiver
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
internal tailrec fun <C> RingOps<C>.multiplyExponentiationBySquaring(base: C, arg: C, exponent: UInt): C =
    when {
        exponent == 0u -> base
        exponent == 1u -> base + arg
        exponent and 1u == 0u -> multiplyExponentiationBySquaring(base, arg * arg, exponent shr 1)
        exponent and 1u == 1u -> multiplyExponentiationBySquaring(base * arg, arg * arg, exponent shr 1)
        else -> error("Error in multiplication group instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
    }