/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.functions

import kotlin.test.Test
import kotlin.test.assertEquals

class PolynomialTest {
    @Test
    fun testIntegration() {
        val polynomial = Polynomial(1.0, -2.0, 1.0)
        assertEquals(0.0, polynomial.value(1.0), 0.001)
    }
}