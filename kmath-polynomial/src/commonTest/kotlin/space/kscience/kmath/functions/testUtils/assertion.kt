/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions.testUtils

import space.kscience.kmath.functions.LabeledPolynomial
import space.kscience.kmath.functions.LabeledRationalFunction
import space.kscience.kmath.functions.NumberedPolynomial
import space.kscience.kmath.functions.NumberedRationalFunction
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


fun <T> assertContentEquals(
    expected: Map<T, Double>,
    actual: Map<T, Double>,
    absoluteTolerance: Double,
    message: String? = null
) {
    assertEquals(expected.keys, actual.keys, message)
    for ((key, expectedValue) in expected) assertEquals(expectedValue, actual[key]!!, absoluteTolerance, message)
}

fun assertEquals(
    expected: NumberedPolynomial<Double>,
    actual: NumberedPolynomial<Double>,
    absoluteTolerance: Double,
    message: String? = null
) {
    assertContentEquals(
        expected.coefficients,
        actual.coefficients,
        absoluteTolerance,
        message
    )
}

fun assertEquals(
    expected: LabeledPolynomial<Double>,
    actual: LabeledPolynomial<Double>,
    absoluteTolerance: Double,
    message: String? = null
) {
    assertContentEquals(
        expected.coefficients,
        actual.coefficients,
        absoluteTolerance,
        message
    )
}

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

fun assertEquals(
    expected: LabeledRationalFunction<Double>,
    actual: LabeledRationalFunction<Double>,
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

inline fun <reified T : Throwable> assertFailsWithTypeAndMessage(
    expectedMessage: String? = null,
    assertionMessage: String? = null,
    block: () -> Unit
) {
    assertEquals(
        expectedMessage,
        assertFailsWith(T::class, assertionMessage, block).message,
        assertionMessage
    )
}