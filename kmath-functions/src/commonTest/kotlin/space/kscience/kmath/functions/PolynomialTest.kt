/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.algebra
import kotlin.test.Test
import kotlin.test.assertEquals

class PolynomialTest {
    @Test
    fun simple_polynomial_test() {
        val polynomial : Polynomial<Double>
        Double.algebra.scalablePolynomial {
            val x = Polynomial(listOf(0.0, 1.0))
            polynomial = x * x - 2 * x + 1
        }
        assertEquals(0.0, polynomial.substitute(1.0), 0.001)
    }
    @Test
    fun testIntegration() {
        val polynomial = Polynomial(1.0, -2.0, 1.0)
        assertEquals(0.0, polynomial.substitute(1.0), 0.001)
    }
}