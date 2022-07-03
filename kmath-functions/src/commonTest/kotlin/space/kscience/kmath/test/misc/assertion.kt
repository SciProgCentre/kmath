/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.test.misc

import space.kscience.kmath.functions.NumberedPolynomial
import space.kscience.kmath.functions.NumberedRationalFunction
import kotlin.test.assertEquals


fun <T> assertContentEquals(expected: Map<T, Double>, actual: Map<T, Double>, absoluteTolerance: Double, message: String? = null) {
    assertEquals(expected.keys, actual.keys, message)
    for ((key, expectedValue) in expected) assertEquals(expectedValue, actual[key]!!, absoluteTolerance, message)
}

fun assertEquals(
    expected: NumberedPolynomial<Double>,
    actual: NumberedPolynomial<Double>,
    absoluteTolerance: Double,
    message: String? = null
) = assertContentEquals(
    expected.coefficients,
    actual.coefficients,
    absoluteTolerance,
    message
)

fun assertEquals(
    expected: NumberedRationalFunction<Double>,
    actual: NumberedRationalFunction<Double>,
    absoluteTolerance: Double,
    message: String? = null
) {
    assertEquals(
        expected.numerator,
        actual.numerator,
        absoluteTolerance,
        message
    )
    assertEquals(
        expected.denominator,
        actual.denominator,
        absoluteTolerance,
        message
    )
}