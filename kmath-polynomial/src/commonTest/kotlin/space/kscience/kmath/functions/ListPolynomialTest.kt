/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("LocalVariableName")

package space.kscience.kmath.functions

import space.kscience.kmath.functions.testUtils.IntModuloRing
import space.kscience.kmath.functions.testUtils.ListPolynomial
import space.kscience.kmath.functions.testUtils.Rational
import space.kscience.kmath.functions.testUtils.RationalField
import kotlin.test.*


class ListPolynomialTest {
    @Test
    fun test_Polynomial_Int_plus() {
        RationalField.listPolynomialSpace {
            assertEquals(
                ListPolynomial(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
                ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)) + -3,
                "test 1"
            )
            assertEquals(
                ListPolynomial(Rational(0), Rational(0), Rational(0), Rational(0)),
                ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)) + 2,
                "test 2"
            )
            assertEquals(
                ListPolynomial(Rational(0)),
                ListPolynomial(Rational(-2)) + 2,
                "test 3"
            )
            val polynomial_4 = ListPolynomial<Rational>()
            assertSame(
                polynomial_4,
                polynomial_4 + 0,
                "test 4"
            )
            val polynomial_5 = ListPolynomial(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7))
            assertSame(
                polynomial_5,
                polynomial_5 + 0,
                "test 5"
            )
            assertEquals(
                ListPolynomial(Rational(-1), Rational(0), Rational(0), Rational(0)),
                ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)) + 1,
                "test 6"
            )
            assertEquals(
                ListPolynomial(Rational(-1)),
                ListPolynomial(Rational(-2)) + 1,
                "test 7"
            )
            assertEquals(
                ListPolynomial(Rational(2)),
                ListPolynomial<Rational>() + 2,
                "test 8"
            )
        }
    }
    @Test
    fun test_Polynomial_Int_minus() {
        RationalField.listPolynomialSpace {
            assertEquals(
                ListPolynomial(Rational(32, 9), Rational(-8, 9), Rational(-8, 7)),
                ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)) - -3,
                "test 1"
            )
            assertEquals(
                ListPolynomial(Rational(0), Rational(0), Rational(0), Rational(0)),
                ListPolynomial(Rational(2), Rational(0), Rational(0), Rational(0)) - 2,
                "test 2"
            )
            assertEquals(
                ListPolynomial(Rational(0)),
                ListPolynomial(Rational(2)) - 2,
                "test 3"
            )
            val polynomial_4 = ListPolynomial<Rational>()
            assertSame(
                polynomial_4,
                polynomial_4 - 0,
                "test 4"
            )
            val polynomial_5 = ListPolynomial(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7))
            assertEquals(
                polynomial_5,
                polynomial_5 - 0,
                "test 5"
            )
            assertEquals(
                ListPolynomial(Rational(1), Rational(0), Rational(0), Rational(0)),
                ListPolynomial(Rational(2), Rational(0), Rational(0), Rational(0)) - 1,
                "test 6"
            )
            assertEquals(
                ListPolynomial(Rational(1)),
                ListPolynomial(Rational(2)) - 1,
                "test 7"
            )
            assertEquals(
                ListPolynomial(Rational(-2)),
                ListPolynomial<Rational>() - 2,
                "test 8"
            )
        }
    }
    @Test
    fun test_Polynomial_Int_times() {
        IntModuloRing(35).listPolynomialSpace {
            assertEquals(
                ListPolynomial(34, 2, 1, 20, 2),
                ListPolynomial(22, 26, 13, 15, 26) * 27,
                "test 1"
            )
            assertEquals(
                ListPolynomial(0, 0, 0, 0, 0),
                ListPolynomial(7, 0, 49, 21, 14) * 15,
                "test 2"
            )
            val polynomial = ListPolynomial(22, 26, 13, 15, 26)
            assertSame(
                zero,
                polynomial * 0,
                "test 3"
            )
            assertSame(
                polynomial,
                polynomial * 1,
                "test 4"
            )
        }
    }
    @Test
    fun test_Int_Polynomial_plus() {
        RationalField.listPolynomialSpace {
            assertEquals(
                ListPolynomial(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
                -3 + ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)),
                "test 1"
            )
            assertEquals(
                ListPolynomial(Rational(0), Rational(0), Rational(0), Rational(0)),
                2 + ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)),
                "test 2"
            )
            assertEquals(
                ListPolynomial(Rational(0)),
                2 + ListPolynomial(Rational(-2)),
                "test 3"
            )
            val polynomial_4 = ListPolynomial<Rational>()
            assertSame(
                polynomial_4,
                0 + polynomial_4,
                "test 4"
            )
            val polynomial_5 = ListPolynomial<Rational>(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7))
            assertSame(
                polynomial_5,
                0 + polynomial_5,
                "test 5"
            )
            assertEquals(
                ListPolynomial(Rational(-1), Rational(0), Rational(0), Rational(0)),
                1 + ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)),
                "test 6"
            )
            assertEquals(
                ListPolynomial(Rational(-1)),
                1 + ListPolynomial(Rational(-2)),
                "test 7"
            )
            assertEquals(
                ListPolynomial(Rational(2)),
                2 + ListPolynomial(),
                "test 8"
            )
        }
    }
    @Test
    fun test_Int_Polynomial_minus() {
        RationalField.listPolynomialSpace {
            assertEquals(
                ListPolynomial(Rational(32, 9), Rational(-8, 9), Rational(-8, 7)),
                3 - ListPolynomial(Rational(-5, 9), Rational(8, 9), Rational(8, 7)),
                "test 1"
            )
            assertEquals(
                ListPolynomial(Rational(0), Rational(0), Rational(0), Rational(0)),
                -2 - ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)),
                "test 2"
            )
            assertEquals(
                ListPolynomial(Rational(0)),
                -2 - ListPolynomial(Rational(-2)),
                "test 3"
            )
            assertEquals(
                ListPolynomial(Rational(-32, 9), Rational(-8, -9), Rational(8, 7)),
                0 - ListPolynomial(Rational(32, 9), Rational(-8, 9), Rational(-8, 7)),
                "test 4"
            )
            assertEquals(
                ListPolynomial(),
                0 - ListPolynomial(),
                "test 5"
            )
            assertEquals(
                ListPolynomial(Rational(1), Rational(0), Rational(0), Rational(0)),
                -1 - ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)),
                "test 6"
            )
            assertEquals(
                ListPolynomial(Rational(1)),
                -1 - ListPolynomial(Rational(-2)),
                "test 7"
            )
            assertEquals(
                ListPolynomial(Rational(-2)),
                -2 - ListPolynomial(),
                "test 8"
            )
        }
    }
    @Test
    fun test_Int_Polynomial_times() {
        IntModuloRing(35).listPolynomialSpace {
            assertEquals(
                ListPolynomial(34, 2, 1, 20, 2),
                27 * ListPolynomial(22, 26, 13, 15, 26),
                "test 1"
            )
            assertEquals(
                ListPolynomial(0, 0, 0, 0, 0),
                15 * ListPolynomial(7, 0, 49, 21, 14),
                "test 2"
            )
            val polynomial = ListPolynomial(22, 26, 13, 15, 26)
            assertSame(
                zero,
                0 * polynomial,
                "test 3"
            )
            assertSame(
                polynomial,
                1 * polynomial,
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_Constant_plus() {
        RationalField.listPolynomialSpace {
            assertEquals(
                ListPolynomial(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
                ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)) + Rational(-3),
                "test 1"
            )
            assertEquals(
                ListPolynomial(Rational(0), Rational(0), Rational(0), Rational(0)),
                ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)) + Rational(2),
                "test 2"
            )
            assertEquals(
                ListPolynomial(Rational(0)),
                ListPolynomial(Rational(-2)) + Rational(2),
                "test 3"
            )
            assertEquals(
                ListPolynomial(Rational(0)),
                ListPolynomial<Rational>() + Rational(0),
                "test 4"
            )
            assertEquals(
                ListPolynomial(Rational(-1), Rational(0), Rational(0), Rational(0)),
                ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)) + Rational(1),
                "test 5"
            )
            assertEquals(
                ListPolynomial(Rational(-1)),
                ListPolynomial(Rational(-2)) + Rational(1),
                "test 6"
            )
            assertEquals(
                ListPolynomial(Rational(2)),
                ListPolynomial<Rational>() + Rational(2),
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Constant_minus() {
        RationalField.listPolynomialSpace {
            assertEquals(
                ListPolynomial(Rational(32, 9), Rational(-8, 9), Rational(-8, 7)),
                ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)) - Rational(-3),
                "test 1"
            )
            assertEquals(
                ListPolynomial(Rational(0), Rational(0), Rational(0), Rational(0)),
                ListPolynomial(Rational(2), Rational(0), Rational(0), Rational(0)) - Rational(2),
                "test 2"
            )
            assertEquals(
                ListPolynomial(Rational(0)),
                ListPolynomial(Rational(2)) - Rational(2),
                "test 3"
            )
            assertEquals(
                ListPolynomial(Rational(0)),
                ListPolynomial<Rational>() - Rational(0),
                "test 4"
            )
            assertEquals(
                ListPolynomial(Rational(1), Rational(0), Rational(0), Rational(0)),
                ListPolynomial(Rational(2), Rational(0), Rational(0), Rational(0)) - Rational(1),
                "test 5"
            )
            assertEquals(
                ListPolynomial(Rational(1)),
                ListPolynomial(Rational(2)) - Rational(1),
                "test 6"
            )
            assertEquals(
                ListPolynomial(Rational(-2)),
                ListPolynomial<Rational>() - Rational(2),
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Constant_times() {
        IntModuloRing(35).listPolynomialSpace {
            assertEquals(
                ListPolynomial(34, 2, 1, 20, 2),
                ListPolynomial(22, 26, 13, 15, 26) * 27.asConstant(),
                "test 1"
            )
            assertEquals(
                ListPolynomial(0, 0, 0, 0, 0),
                ListPolynomial(7, 0, 49, 21, 14) * 15.asConstant(),
                "test 2"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_plus() {
        RationalField.listPolynomialSpace {
            assertEquals(
                ListPolynomial(Rational(-22, 9), Rational(-8, 9), Rational(-8, 7)),
                Rational(-3) + ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)),
                "test 1"
            )
            assertEquals(
                ListPolynomial(Rational(0), Rational(0), Rational(0), Rational(0)),
                Rational(2) + ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)),
                "test 2"
            )
            assertEquals(
                ListPolynomial(Rational(0)),
                Rational(2) + ListPolynomial(Rational(-2)),
                "test 3"
            )
            assertEquals(
                ListPolynomial(Rational(0)),
                Rational(0) + ListPolynomial(),
                "test 4"
            )
            assertEquals(
                ListPolynomial(Rational(-1), Rational(0), Rational(0), Rational(0)),
                Rational(1) + ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)),
                "test 5"
            )
            assertEquals(
                ListPolynomial(Rational(-1)),
                Rational(1) + ListPolynomial(Rational(-2)),
                "test 6"
            )
            assertEquals(
                ListPolynomial(Rational(2)),
                Rational(2) + ListPolynomial(),
                "test 7"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_minus() {
        RationalField.listPolynomialSpace {
            assertEquals(
                ListPolynomial(Rational(32, 9), Rational(-8, 9), Rational(-8, 7)),
                Rational(3) - ListPolynomial(Rational(-5, 9), Rational(8, 9), Rational(8, 7)),
                "test 1"
            )
            assertEquals(
                ListPolynomial(Rational(0), Rational(0), Rational(0), Rational(0)),
                Rational(-2) - ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)),
                "test 2"
            )
            assertEquals(
                ListPolynomial(Rational(0)),
                Rational(-2) - ListPolynomial(Rational(-2)),
                "test 3"
            )
            assertEquals(
                ListPolynomial(Rational(0)),
                Rational(0) - ListPolynomial(),
                "test 4"
            )
            assertEquals(
                ListPolynomial(Rational(1), Rational(0), Rational(0), Rational(0)),
                Rational(-1) - ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)),
                "test 5"
            )
            assertEquals(
                ListPolynomial(Rational(1)),
                Rational(-1) - ListPolynomial(Rational(-2)),
                "test 6"
            )
            assertEquals(
                ListPolynomial(Rational(-2)),
                Rational(-2) - ListPolynomial(),
                "test 7"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_times() {
        IntModuloRing(35).listPolynomialSpace {
            assertEquals(
                ListPolynomial(34, 2, 1, 20, 2),
                27 * ListPolynomial(22, 26, 13, 15, 26),
                "test 1"
            )
            assertEquals(
                ListPolynomial(0, 0, 0, 0, 0),
                15 * ListPolynomial(7, 0, 49, 21, 14),
                "test 2"
            )
        }
    }
    @Test
    fun test_Polynomial_unaryMinus() {
        RationalField.listPolynomialSpace {
            assertEquals(
                ListPolynomial(Rational(-5, 9), Rational(8, 9), Rational(8, 7)),
                -ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)),
                "test 1"
            )
            assertEquals(
                ListPolynomial(Rational(-5, 9), Rational(8, 9), Rational(8, 7), Rational(0), Rational(0)),
                -ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7), Rational(0), Rational(0)),
                "test 2"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_plus() {
        RationalField.listPolynomialSpace {
            // (5/9 - 8/9 x - 8/7 x^2) + (-5/7 + 5/1 x + 5/8 x^2) ?= -10/63 + 37/9 x - 29/56 x^2
            assertEquals(
                ListPolynomial(Rational(-10, 63), Rational(37, 9), Rational(-29, 56)),
                ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)) +
                        ListPolynomial(Rational(-5, 7), Rational(5, 1), Rational(5, 8)),
                "test 1"
            )
            // (-2/9 - 8/3 x) + (0 + 9/4 x + 2/4 x^2) ?= -2/9 - 5/12 x + 2/4 x^2
            assertEquals(
                ListPolynomial(Rational(-2, 9), Rational(-5, 12), Rational(2, 4)),
                ListPolynomial(Rational(-2, 9), Rational(-8, 3)) +
                        ListPolynomial(Rational(0), Rational(9, 4), Rational(2, 4)),
                "test 2"
            )
            // (-4/7 - 2/6 x + 0 x^2 + 0 x^3) + (-6/3 - 7/2 x + 2/3 x^2) ?= -18/7 - 23/6 x + 2/3 x^2
            assertEquals(
                ListPolynomial(Rational(-18, 7), Rational(-23, 6), Rational(2, 3), Rational(0)),
                ListPolynomial(Rational(-4, 7), Rational(-2, 6), Rational(0), Rational(0)) +
                        ListPolynomial(Rational(-6, 3), Rational(-7, 2), Rational(2, 3)),
                "test 3"
            )
            // (-2/4 - 6/9 x - 4/9 x^2) + (2/4 + 6/9 x + 4/9 x^2) ?= 0
            assertEquals(
                ListPolynomial(Rational(0), Rational(0), Rational(0)),
                ListPolynomial(Rational(-2, 4), Rational(-6, 9), Rational(-4, 9)) +
                        ListPolynomial(Rational(2, 4), Rational(6, 9), Rational(4, 9)),
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_minus() {
        RationalField.listPolynomialSpace {
            // (5/9 - 8/9 x - 8/7 x^2) - (-5/7 + 5/1 x + 5/8 x^2) ?= 80/63 - 53/9 x - 99/56 x^2
            assertEquals(
                ListPolynomial(Rational(80, 63), Rational(-53, 9), Rational(-99, 56)),
                ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)) -
                        ListPolynomial(Rational(-5, 7), Rational(5, 1), Rational(5, 8)),
                "test 1"
            )
            // (-2/9 - 8/3 x) - (0 + 9/4 x + 2/4 x^2) ?= -2/9 - 59/12 x - 2/4 x^2
            assertEquals(
                ListPolynomial(Rational(-2, 9), Rational(-59, 12), Rational(-2, 4)),
                ListPolynomial(Rational(-2, 9), Rational(-8, 3)) -
                        ListPolynomial(Rational(0), Rational(9, 4), Rational(2, 4)),
                "test 2"
            )
            // (-4/7 - 2/6 x + 0 x^2 + 0 x^3) - (-6/3 - 7/2 x + 2/3 x^2) ?= 10/7 + 19/6 x - 2/3 x^2
            assertEquals(
                ListPolynomial(Rational(10, 7), Rational(19, 6), Rational(-2, 3), Rational(0)),
                ListPolynomial(Rational(-4, 7), Rational(-2, 6), Rational(0), Rational(0)) -
                        ListPolynomial(Rational(-6, 3), Rational(-7, 2), Rational(2, 3)),
                "test 3"
            )
            // (-2/4 - 6/9 x - 4/9 x^2) - (-2/4 - 6/9 x - 4/9 x^2) ?= 0
            assertEquals(
                ListPolynomial(Rational(0), Rational(0), Rational(0)),
                ListPolynomial(Rational(-2, 4), Rational(-6, 9), Rational(-4, 9)) -
                        ListPolynomial(Rational(-2, 4), Rational(-6, 9), Rational(-4, 9)),
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_times() {
        IntModuloRing(35).listPolynomialSpace {
            // (1 + x + x^2) * (1 - x + x^2) ?= 1 + x^2 + x^4
            assertEquals(
                ListPolynomial(1, 0, 1, 0, 1),
                ListPolynomial(1, -1, 1) * ListPolynomial(1, 1, 1),
                "test 1"
            )
            // Spoiler: 5 * 7 = 0
            assertEquals(
                ListPolynomial(0, 0, 0, 0, 0),
                ListPolynomial(5, -25, 10) * ListPolynomial(21, 14, -7),
                "test 2"
            )
        }
    }
}