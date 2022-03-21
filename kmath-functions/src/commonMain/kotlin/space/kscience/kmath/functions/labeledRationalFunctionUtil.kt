/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Ring
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * Creates a [LabeledRationalFunctionSpace] over a received ring.
 */
public fun <C, A : Ring<C>> A.labeledRationalFunction(): LabeledRationalFunctionSpace<C, A> =
    LabeledRationalFunctionSpace(this)

/**
 * Creates a [LabeledRationalFunctionSpace]'s scope over a received ring.
 */
public inline fun <C, A : Ring<C>, R> A.labeledRationalFunction(block: LabeledRationalFunctionSpace<C, A>.() -> R): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return LabeledRationalFunctionSpace(this).block()
}

//fun <T: Field<T>> LabeledRationalFunction<T>.reduced(): LabeledRationalFunction<T> {
//    val greatestCommonDivider = polynomialGCD(numerator, denominator)
//    return LabeledRationalFunction(
//        numerator / greatestCommonDivider,
//        denominator / greatestCommonDivider
//    )
//}