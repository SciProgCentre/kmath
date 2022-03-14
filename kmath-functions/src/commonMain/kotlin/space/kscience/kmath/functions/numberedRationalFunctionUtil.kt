/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions


//// region Operator extensions
//
//// region Field case
//
//fun <T: Field<T>> NumberedRationalFunction<T>.reduced(): NumberedRationalFunction<T> {
//    val greatestCommonDivider = polynomialGCD(numerator, denominator)
//    return NumberedRationalFunction(
//        numerator / greatestCommonDivider,
//        denominator / greatestCommonDivider
//    )
//}
//
//// endregion
//
//// endregion