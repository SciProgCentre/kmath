/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions


//operator fun <T: Field<T>> RationalFunction<T>.invoke(arg: T): T = numerator(arg) / denominator(arg)
//
//fun <T: Field<T>> RationalFunction<T>.reduced(): RationalFunction<T> =
//    polynomialGCD(numerator, denominator).let {
//        RationalFunction(
//            numerator / it,
//            denominator / it
//        )
//    }

///**
// * Returns result of applying formal derivative to the polynomial.
// *
// * @param T Field where we are working now.
// * @return Result of the operator.
// */
//fun <T: Ring<T>> RationalFunction<T>.derivative() =
//    RationalFunction(
//        numerator.derivative() * denominator - denominator.derivative() * numerator,
//        denominator * denominator
//    )