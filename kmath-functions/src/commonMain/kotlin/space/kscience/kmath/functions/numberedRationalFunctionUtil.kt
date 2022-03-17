/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Ring
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * Creates a [NumberedRationalFunctionSpace] over a received ring.
 */
public fun <C, A : Ring<C>> A.numberedRationalFunction(): NumberedRationalFunctionSpace<C, A> =
    NumberedRationalFunctionSpace(this)

/**
 * Creates a [RationalFunctionSpace]'s scope over a received ring.
 */
public inline fun <C, A : Ring<C>, R> A.numberedRationalFunction(block: NumberedRationalFunctionSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return NumberedRationalFunctionSpace(this).block()
}

//fun <T: Field<T>> NumberedRationalFunction<T>.reduced(): NumberedRationalFunction<T> {
//    val greatestCommonDivider = polynomialGCD(numerator, denominator)
//    return NumberedRationalFunction(
//        numerator / greatestCommonDivider,
//        denominator / greatestCommonDivider
//    )
//}