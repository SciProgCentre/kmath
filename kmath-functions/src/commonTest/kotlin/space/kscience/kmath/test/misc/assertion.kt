/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.test.misc

import kotlin.test.assertEquals


fun <T> assertContentEquals(expected: Map<T, Double>, actual: Map<T, Double>, absoluteTolerance: Double, message: String? = null) {
    assertEquals(expected.keys, actual.keys, message)
    for ((key, expectedValue) in expected) assertEquals(expectedValue, actual[key]!!, absoluteTolerance, message)
}