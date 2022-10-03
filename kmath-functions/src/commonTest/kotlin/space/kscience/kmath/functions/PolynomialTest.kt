/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("LocalVariableName")

package space.kscience.kmath.functions

import space.kscience.kmath.functions.testUtils.*
import kotlin.test.*


class PolynomialTest {
    @Test
    fun test_Polynomial_Constant_plus() {
        RationalField.polynomialSpace {
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
                Polynomial(Rational(0)),
                Polynomial(Rational(-2)) + Rational(2),
                "test 3"
            )
            assertEquals(
                Polynomial(Rational(0)),
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
        RationalField.polynomialSpace {
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
                Polynomial(Rational(0)),
                Polynomial(Rational(2)) - Rational(2),
                "test 3"
            )
            assertEquals(
                Polynomial(Rational(0)),
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
    fun test_Polynomial_Constant_times() {
        IntModuloRing(35).polynomialSpace {
            assertEquals(
                Polynomial(34, 2, 1, 20, 2),
                Polynomial(22, 26, 13, 15, 26) * m(27),
                "test 1"
            )
            assertEquals(
                Polynomial(0, 0, 0, 0, 0),
                Polynomial(7, 0, 49, 21, 14) * m(15),
                "test 2"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_plus() {
        RationalField.polynomialSpace {
            assertEquals(
                Polynomial(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
                Rational(-3) + Polynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)),
                "test 1"
            )
            assertEquals(
                Polynomial(Rational(0), Rational(0), Rational(0), Rational(0)),
                Rational(2) + Polynomial(Rational(-2), Rational(0), Rational(0), Rational(0)),
                "test 2"
            )
            assertEquals(
                Polynomial(Rational(0)),
                Rational(2) + Polynomial(Rational(-2)),
                "test 3"
            )
            assertEquals(
                Polynomial(Rational(0)),
                Rational(0) + Polynomial(),
                "test 4"
            )
            assertEquals(
                Polynomial(Rational(-1), Rational(0), Rational(0), Rational(0)),
                Rational(1) + Polynomial(Rational(-2), Rational(0), Rational(0), Rational(0)),
                "test 5"
            )
            assertEquals(
                Polynomial(Rational(-1)),
                Rational(1) + Polynomial(Rational(-2)),
                "test 6"
            )
            assertEquals(
                Polynomial(Rational(2)),
                Rational(2) + Polynomial(),
                "test 7"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_minus() {
        RationalField.polynomialSpace {
            assertEquals(
                Polynomial(Rational(32, 9), Rational(-8, 9), Rational(-8, 7)),
                Rational(3) - Polynomial(Rational(-5, 9), Rational(8, 9), Rational(8, 7)),
                "test 1"
            )
            assertEquals(
                Polynomial(Rational(0), Rational(0), Rational(0), Rational(0)),
                Rational(-2) - Polynomial(Rational(-2), Rational(0), Rational(0), Rational(0)),
                "test 2"
            )
            assertEquals(
                Polynomial(Rational(0)),
                Rational(-2) - Polynomial(Rational(-2)),
                "test 3"
            )
            assertEquals(
                Polynomial(Rational(0)),
                Rational(0) - Polynomial(),
                "test 4"
            )
            assertEquals(
                Polynomial(Rational(1), Rational(0), Rational(0), Rational(0)),
                Rational(-1) - Polynomial(Rational(-2), Rational(0), Rational(0), Rational(0)),
                "test 5"
            )
            assertEquals(
                Polynomial(Rational(1)),
                Rational(-1) - Polynomial(Rational(-2)),
                "test 6"
            )
            assertEquals(
                Polynomial(Rational(-2)),
                Rational(-2) - Polynomial(),
                "test 7"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_times() {
        IntModuloRing(35).polynomialSpace {
            assertEquals(
                Polynomial(34, 2, 1, 20, 2),
                m(27) * Polynomial(22, 26, 13, 15, 26),
                "test 1"
            )
            assertEquals(
                Polynomial(0, 0, 0, 0, 0),
                m(15) * Polynomial(7, 0, 49, 21, 14),
                "test 2"
            )
        }
    }
    @Test
    fun test_Polynomial_unaryMinus() {
        RationalField.polynomialSpace {
            assertEquals(
                Polynomial(Rational(-5, 9), Rational(8, 9), Rational(8, 7)),
                -Polynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)),
                "test 1"
            )
            assertEquals(
                Polynomial(Rational(-5, 9), Rational(8, 9), Rational(8, 7), Rational(0), Rational(0)),
                -Polynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7), Rational(0), Rational(0)),
                "test 2"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_plus() {
        RationalField.polynomialSpace {
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
                Polynomial(Rational(-18, 7), Rational(-23, 6), Rational(2, 3), Rational(0)),
                Polynomial(Rational(-4, 7), Rational(-2, 6), Rational(0), Rational(0)) +
                        Polynomial(Rational(-6, 3), Rational(-7, 2), Rational(2, 3)),
                "test 3"
            )
            // (-2/4 - 6/9 x - 4/9 x^2) + (2/4 + 6/9 x + 4/9 x^2) ?= 0
            assertEquals(
                Polynomial(Rational(0), Rational(0), Rational(0)),
                Polynomial(Rational(-2, 4), Rational(-6, 9), Rational(-4, 9)) +
                        Polynomial(Rational(2, 4), Rational(6, 9), Rational(4, 9)),
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_minus() {
        RationalField.polynomialSpace {
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
                Polynomial(Rational(10, 7), Rational(19, 6), Rational(-2, 3), Rational(0)),
                Polynomial(Rational(-4, 7), Rational(-2, 6), Rational(0), Rational(0)) -
                        Polynomial(Rational(-6, 3), Rational(-7, 2), Rational(2, 3)),
                "test 3"
            )
            // (-2/4 - 6/9 x - 4/9 x^2) - (-2/4 - 6/9 x - 4/9 x^2) ?= 0
            assertEquals(
                Polynomial(Rational(0), Rational(0), Rational(0)),
                Polynomial(Rational(-2, 4), Rational(-6, 9), Rational(-4, 9)) -
                        Polynomial(Rational(-2, 4), Rational(-6, 9), Rational(-4, 9)),
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_times() {
        IntModuloRing(35).polynomialSpace {
            // (1 + x + x^2) * (1 - x + x^2) ?= 1 + x^2 + x^4
            assertEquals(
                Polynomial(1, 0, 1, 0, 1),
                Polynomial(1, -1, 1) * Polynomial(1, 1, 1),
                "test 1"
            )
            // Spoiler: 5 * 7 = 0
            assertEquals(
                Polynomial(0, 0, 0, 0, 0),
                Polynomial(5, -25, 10) * Polynomial(21, 14, -7),
                "test 2"
            )
        }
    }
}