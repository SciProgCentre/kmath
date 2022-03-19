/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.algebra
import space.kscience.kmath.test.misc.Rational
import space.kscience.kmath.test.misc.RationalField
import kotlin.test.Test
import kotlin.test.assertEquals

class PolynomialTest {
    @Test
    fun test_Polynomial_Int_plus() {
        RationalField.polynomial {
            assertEquals(
                Polynomial(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
                Polynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)) + -3,
                "test 1"
            )
            assertEquals(
                Polynomial(Rational(0), Rational(0), Rational(0), Rational(0)),
                Polynomial(Rational(-2), Rational(0), Rational(0), Rational(0)) + 2,
                "test 2"
            )
            assertEquals(
                Polynomial(),
                Polynomial(Rational(-2)) + 2,
                "test 3"
            )
            assertEquals(
                Polynomial(),
                Polynomial<Rational>() + 0,
                "test 4"
            )
            assertEquals(
                Polynomial(Rational(-1), Rational(0), Rational(0), Rational(0)),
                Polynomial(Rational(-2), Rational(0), Rational(0), Rational(0)) + 1,
                "test 5"
            )
            assertEquals(
                Polynomial(Rational(-1)),
                Polynomial(Rational(-2)) + 1,
                "test 6"
            )
            assertEquals(
                Polynomial(Rational(2)),
                Polynomial<Rational>() + 2,
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Int_minus() {
        RationalField.polynomial {
            assertEquals(
                Polynomial(Rational(32, 9), Rational(-8, 9), Rational(-8, 7)),
                Polynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)) - -3,
                "test 1"
            )
            assertEquals(
                Polynomial(Rational(0), Rational(0), Rational(0), Rational(0)),
                Polynomial(Rational(2), Rational(0), Rational(0), Rational(0)) - 2,
                "test 2"
            )
            assertEquals(
                Polynomial(),
                Polynomial(Rational(2)) - 2,
                "test 3"
            )
            assertEquals(
                Polynomial(),
                Polynomial<Rational>() - 0,
                "test 4"
            )
            assertEquals(
                Polynomial(Rational(1), Rational(0), Rational(0), Rational(0)),
                Polynomial(Rational(2), Rational(0), Rational(0), Rational(0)) - 1,
                "test 5"
            )
            assertEquals(
                Polynomial(Rational(1)),
                Polynomial(Rational(2)) - 1,
                "test 6"
            )
            assertEquals(
                Polynomial(Rational(-2)),
                Polynomial<Rational>() - 2,
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Constant_plus() {
        RationalField.polynomial {
            assertEquals(
                Polynomial(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
                Polynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)) + Rational(-3),
                "test 1"
            )
            assertEquals(
                Polynomial(Rational(0), Rational(0), Rational(0), Rational(0)),
                Polynomial(Rational(-2), Rational(0), Rational(0), Rational(0)) + Rational(2),
                "test 2"
            )
            assertEquals(
                Polynomial(),
                Polynomial(Rational(-2)) + Rational(2),
                "test 3"
            )
            assertEquals(
                Polynomial(),
                Polynomial<Rational>() + Rational(0),
                "test 4"
            )
            assertEquals(
                Polynomial(Rational(-1), Rational(0), Rational(0), Rational(0)),
                Polynomial(Rational(-2), Rational(0), Rational(0), Rational(0)) + Rational(1),
                "test 5"
            )
            assertEquals(
                Polynomial(Rational(-1)),
                Polynomial(Rational(-2)) + Rational(1),
                "test 6"
            )
            assertEquals(
                Polynomial(Rational(2)),
                Polynomial<Rational>() + Rational(2),
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Constant_minus() {
        RationalField.polynomial {
            assertEquals(
                Polynomial(Rational(32, 9), Rational(-8, 9), Rational(-8, 7)),
                Polynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)) - Rational(-3),
                "test 1"
            )
            assertEquals(
                Polynomial(Rational(0), Rational(0), Rational(0), Rational(0)),
                Polynomial(Rational(2), Rational(0), Rational(0), Rational(0)) - Rational(2),
                "test 2"
            )
            assertEquals(
                Polynomial(),
                Polynomial(Rational(2)) - Rational(2),
                "test 3"
            )
            assertEquals(
                Polynomial(),
                Polynomial<Rational>() - Rational(0),
                "test 4"
            )
            assertEquals(
                Polynomial(Rational(1), Rational(0), Rational(0), Rational(0)),
                Polynomial(Rational(2), Rational(0), Rational(0), Rational(0)) - Rational(1),
                "test 5"
            )
            assertEquals(
                Polynomial(Rational(1)),
                Polynomial(Rational(2)) - Rational(1),
                "test 6"
            )
            assertEquals(
                Polynomial(Rational(-2)),
                Polynomial<Rational>() - Rational(2),
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_plus() {
        RationalField.polynomial {
            // (5/9 - 8/9 x - 8/7 x^2) + (-5/7 + 5/1 x + 5/8 x^2) ?= -10/63 + 37/9 x - 29/56 x^2
            assertEquals(
                Polynomial(Rational(-10, 63), Rational(37, 9), Rational(-29, 56)),
                Polynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)) +
                        Polynomial(Rational(-5, 7), Rational(5, 1), Rational(5, 8)),
                "test 1"
            )
            // (-2/9 - 8/3 x) + (0 + 9/4 x + 2/4 x^2) ?= -2/9 - 5/12 x + 2/4 x^2
            assertEquals(
                Polynomial(Rational(-2, 9), Rational(-5, 12), Rational(2, 4)),
                Polynomial(Rational(-2, 9), Rational(-8, 3)) +
                        Polynomial(Rational(0), Rational(9, 4), Rational(2, 4)),
                "test 2"
            )
            // (-4/7 - 2/6 x + 0 x^2 + 0 x^3) + (-6/3 - 7/2 x + 2/3 x^2) ?= -18/7 - 23/6 x + 2/3 x^2
            assertEquals(
                Polynomial(Rational(-18, 7), Rational(-23, 6), Rational(2, 3)),
                Polynomial(Rational(-4, 7), Rational(-2, 6), Rational(0), Rational(0)) +
                        Polynomial(Rational(-6, 3), Rational(-7, 2), Rational(2, 3)),
                "test 3"
            )
            // (-2/4 - 6/9 x - 4/9 x^2) + (2/4 + 6/9 x + 4/9 x^2) ?= 0
            assertEquals(
                Polynomial(),
                Polynomial(Rational(-2, 4), Rational(-6, 9), Rational(-4, 9)) +
                        Polynomial(Rational(2, 4), Rational(6, 9), Rational(4, 9)),
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_minus() {
        RationalField.polynomial {
            // (5/9 - 8/9 x - 8/7 x^2) - (-5/7 + 5/1 x + 5/8 x^2) ?= 80/63 - 53/9 x - 99/56 x^2
            assertEquals(
                Polynomial(Rational(80, 63), Rational(-53, 9), Rational(-99, 56)),
                Polynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)) -
                        Polynomial(Rational(-5, 7), Rational(5, 1), Rational(5, 8)),
                "test 1"
            )
            // (-2/9 - 8/3 x) - (0 + 9/4 x + 2/4 x^2) ?= -2/9 - 59/12 x - 2/4 x^2
            assertEquals(
                Polynomial(Rational(-2, 9), Rational(-59, 12), Rational(-2, 4)),
                Polynomial(Rational(-2, 9), Rational(-8, 3)) -
                        Polynomial(Rational(0), Rational(9, 4), Rational(2, 4)),
                "test 2"
            )
            // (-4/7 - 2/6 x + 0 x^2 + 0 x^3) - (-6/3 - 7/2 x + 2/3 x^2) ?= 10/7 + 19/6 x - 2/3 x^2
            assertEquals(
                Polynomial(Rational(10, 7), Rational(19, 6), Rational(-2, 3)),
                Polynomial(Rational(-4, 7), Rational(-2, 6), Rational(0), Rational(0)) -
                        Polynomial(Rational(-6, 3), Rational(-7, 2), Rational(2, 3)),
                "test 3"
            )
            // (-2/4 - 6/9 x - 4/9 x^2) - (-2/4 - 6/9 x - 4/9 x^2) ?= 0
            assertEquals(
                Polynomial(),
                Polynomial(Rational(-2, 4), Rational(-6, 9), Rational(-4, 9)) -
                        Polynomial(Rational(-2, 4), Rational(-6, 9), Rational(-4, 9)),
                "test 4"
            )
        }
    }
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