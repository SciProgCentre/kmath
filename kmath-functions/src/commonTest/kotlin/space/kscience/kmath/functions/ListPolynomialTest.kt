/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.test.misc.*
import kotlin.test.*


class ListPolynomialTest {
    @Test
    fun test_Polynomial_Int_plus() {
        RationalField.listPolynomial {
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
                ListPolynomial(),
                ListPolynomial(Rational(-2)) + 2,
                "test 3"
            )
            assertEquals(
                ListPolynomial(),
                ListPolynomial<Rational>() + 0,
                "test 4"
            )
            assertEquals(
                ListPolynomial(Rational(-1), Rational(0), Rational(0), Rational(0)),
                ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)) + 1,
                "test 5"
            )
            assertEquals(
                ListPolynomial(Rational(-1)),
                ListPolynomial(Rational(-2)) + 1,
                "test 6"
            )
            assertEquals(
                ListPolynomial(Rational(2)),
                ListPolynomial<Rational>() + 2,
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Int_minus() {
        RationalField.listPolynomial {
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
                ListPolynomial(),
                ListPolynomial(Rational(2)) - 2,
                "test 3"
            )
            assertEquals(
                ListPolynomial(),
                ListPolynomial<Rational>() - 0,
                "test 4"
            )
            assertEquals(
                ListPolynomial(Rational(1), Rational(0), Rational(0), Rational(0)),
                ListPolynomial(Rational(2), Rational(0), Rational(0), Rational(0)) - 1,
                "test 5"
            )
            assertEquals(
                ListPolynomial(Rational(1)),
                ListPolynomial(Rational(2)) - 1,
                "test 6"
            )
            assertEquals(
                ListPolynomial(Rational(-2)),
                ListPolynomial<Rational>() - 2,
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Int_times() {
        IntModuloRing(35).listPolynomial {
            assertEquals(
                ListPolynomial(34, 2, 1, 20, 2),
                ListPolynomial(22, 26, 13, 15, 26) * 27,
                "test 1"
            )
            assertEquals(
                ListPolynomial(),
                ListPolynomial(7, 0, 49, 21, 14) * 15,
                "test 2"
            )
        }
    }
    @Test
    fun test_Int_Polynomial_plus() {
        RationalField.listPolynomial {
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
                ListPolynomial(),
                2 + ListPolynomial(Rational(-2)),
                "test 3"
            )
            assertEquals(
                ListPolynomial(),
                0 + ListPolynomial(),
                "test 4"
            )
            assertEquals(
                ListPolynomial(Rational(-1), Rational(0), Rational(0), Rational(0)),
                1 + ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)),
                "test 5"
            )
            assertEquals(
                ListPolynomial(Rational(-1)),
                1 + ListPolynomial(Rational(-2)),
                "test 6"
            )
            assertEquals(
                ListPolynomial(Rational(2)),
                2 + ListPolynomial(),
                "test 7"
            )
        }
    }
    @Test
    fun test_Int_Polynomial_minus() {
        RationalField.listPolynomial {
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
                ListPolynomial(),
                -2 - ListPolynomial(Rational(-2)),
                "test 3"
            )
            assertEquals(
                ListPolynomial(),
                0 - ListPolynomial(),
                "test 4"
            )
            assertEquals(
                ListPolynomial(Rational(1), Rational(0), Rational(0), Rational(0)),
                -1 - ListPolynomial(Rational(-2), Rational(0), Rational(0), Rational(0)),
                "test 5"
            )
            assertEquals(
                ListPolynomial(Rational(1)),
                -1 - ListPolynomial(Rational(-2)),
                "test 6"
            )
            assertEquals(
                ListPolynomial(Rational(-2)),
                -2 - ListPolynomial(),
                "test 7"
            )
        }
    }
    @Test
    fun test_Int_Polynomial_times() {
        IntModuloRing(35).listPolynomial {
            assertEquals(
                ListPolynomial(34, 2, 1, 20, 2),
                27 * ListPolynomial(22, 26, 13, 15, 26),
                "test 1"
            )
            assertEquals(
                ListPolynomial(),
                15 * ListPolynomial(7, 0, 49, 21, 14),
                "test 2"
            )
        }
    }
    @Test
    fun test_Polynomial_Constant_plus() {
        RationalField.listPolynomial {
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
                ListPolynomial(),
                ListPolynomial(Rational(-2)) + Rational(2),
                "test 3"
            )
            assertEquals(
                ListPolynomial(),
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
        RationalField.listPolynomial {
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
                ListPolynomial(),
                ListPolynomial(Rational(2)) - Rational(2),
                "test 3"
            )
            assertEquals(
                ListPolynomial(),
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
        IntModuloRing(35).listPolynomial {
            assertEquals(
                ListPolynomial(34, 2, 1, 20, 2),
                ListPolynomial(22, 26, 13, 15, 26) * number(27),
                "test 1"
            )
            assertEquals(
                ListPolynomial(),
                ListPolynomial(7, 0, 49, 21, 14) * number(15),
                "test 2"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_plus() {
        RationalField.listPolynomial {
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
                ListPolynomial(),
                Rational(2) + ListPolynomial(Rational(-2)),
                "test 3"
            )
            assertEquals(
                ListPolynomial(),
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
        RationalField.listPolynomial {
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
                ListPolynomial(),
                Rational(-2) - ListPolynomial(Rational(-2)),
                "test 3"
            )
            assertEquals(
                ListPolynomial(),
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
        IntModuloRing(35).listPolynomial {
            assertEquals(
                ListPolynomial(34, 2, 1, 20, 2),
                27 * ListPolynomial(22, 26, 13, 15, 26),
                "test 1"
            )
            assertEquals(
                ListPolynomial(),
                15 * ListPolynomial(7, 0, 49, 21, 14),
                "test 2"
            )
        }
    }
    @Test
    fun test_Polynomial_unaryMinus() {
        RationalField.listPolynomial {
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
        RationalField.listPolynomial {
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
                ListPolynomial(Rational(-18, 7), Rational(-23, 6), Rational(2, 3)),
                ListPolynomial(Rational(-4, 7), Rational(-2, 6), Rational(0), Rational(0)) +
                        ListPolynomial(Rational(-6, 3), Rational(-7, 2), Rational(2, 3)),
                "test 3"
            )
            // (-2/4 - 6/9 x - 4/9 x^2) + (2/4 + 6/9 x + 4/9 x^2) ?= 0
            assertEquals(
                ListPolynomial(),
                ListPolynomial(Rational(-2, 4), Rational(-6, 9), Rational(-4, 9)) +
                        ListPolynomial(Rational(2, 4), Rational(6, 9), Rational(4, 9)),
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_minus() {
        RationalField.listPolynomial {
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
                ListPolynomial(Rational(10, 7), Rational(19, 6), Rational(-2, 3)),
                ListPolynomial(Rational(-4, 7), Rational(-2, 6), Rational(0), Rational(0)) -
                        ListPolynomial(Rational(-6, 3), Rational(-7, 2), Rational(2, 3)),
                "test 3"
            )
            // (-2/4 - 6/9 x - 4/9 x^2) - (-2/4 - 6/9 x - 4/9 x^2) ?= 0
            assertEquals(
                ListPolynomial(),
                ListPolynomial(Rational(-2, 4), Rational(-6, 9), Rational(-4, 9)) -
                        ListPolynomial(Rational(-2, 4), Rational(-6, 9), Rational(-4, 9)),
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_times() {
        IntModuloRing(35).listPolynomial {
            // (1 + x + x^2) * (1 - x + x^2) ?= 1 + x^2 + x^4
            assertEquals(
                ListPolynomial(1, 0, 1, 0, 1),
                ListPolynomial(1, -1, 1) * ListPolynomial(1, 1, 1),
                "test 1"
            )
            // Spoiler: 5 * 7 = 0
            assertEquals(
                ListPolynomial(),
                ListPolynomial(5, -25, 10) * ListPolynomial(21, 14, -7),
                "test 2"
            )
        }
    }
    @Test
    fun test_Polynomial_isZero() {
        RationalField.listPolynomial {
            assertTrue("test 1") { ListPolynomial<Rational>().isZero() }
            assertTrue("test 2") { ListPolynomial<Rational>(Rational(0)).isZero() }
            assertTrue("test 3") { ListPolynomial<Rational>(Rational(0), Rational(0)).isZero() }
            assertTrue("test 4") { ListPolynomial<Rational>(Rational(0), Rational(0), Rational(0))
                .isZero() }
            assertFalse("test 5") { ListPolynomial<Rational>(Rational(3, 5)).isZero() }
            assertFalse("test 6") { ListPolynomial<Rational>(Rational(3, 5), Rational(0))
                .isZero() }
            assertFalse("test 7") { ListPolynomial<Rational>(Rational(0), Rational(3, 5), Rational(0))
                .isZero() }
        }
    }
    @Test
    fun test_Polynomial_isOne() {
        RationalField.listPolynomial {
            assertFalse("test 1") { ListPolynomial<Rational>().isOne() }
            assertFalse("test 2") { ListPolynomial(Rational(0)).isOne() }
            assertFalse("test 3") { ListPolynomial(Rational(0), Rational(0)).isOne() }
            assertFalse("test 4") { ListPolynomial(Rational(0), Rational(0), Rational(0))
                .isOne() }
            assertFalse("test 5") { ListPolynomial(Rational(3, 5)).isOne() }
            assertTrue("test 6") { ListPolynomial(Rational(5, 5)).isOne() }
            assertFalse("test 7") { ListPolynomial(Rational(3, 5), Rational(0)).isOne() }
            assertTrue("test 8") { ListPolynomial(Rational(3, 3), Rational(0)).isOne() }
            assertFalse("test 9") { ListPolynomial(Rational(0), Rational(3, 5), Rational(0))
                .isOne() }
            assertFalse("test 10") { ListPolynomial(Rational(0), Rational(5, 5), Rational(0))
                .isOne() }
            assertFalse("test 11") { ListPolynomial(Rational(1), Rational(3, 5), Rational(0))
                .isOne() }
            assertFalse("test 12") { ListPolynomial(Rational(1), Rational(5, 5), Rational(0))
                .isOne() }
        }
    }
    @Test
    fun test_Polynomial_isMinusOne() {
        RationalField.listPolynomial {
            assertFalse("test 1") { ListPolynomial<Rational>().isMinusOne() }
            assertFalse("test 2") { ListPolynomial(Rational(0)).isMinusOne() }
            assertFalse("test 3") { ListPolynomial(Rational(0), Rational(0)).isMinusOne() }
            assertFalse("test 4") { ListPolynomial(Rational(0), Rational(0), Rational(0))
                .isMinusOne() }
            assertFalse("test 5") { ListPolynomial(Rational(3, 5)).isMinusOne() }
            assertTrue("test 6") { ListPolynomial(Rational(-5, 5)).isMinusOne() }
            assertFalse("test 7") { ListPolynomial(Rational(3, 5), Rational(0)).isMinusOne() }
            assertTrue("test 8") { ListPolynomial(Rational(-3, 3), Rational(0)).isMinusOne() }
            assertFalse("test 9") { ListPolynomial(Rational(0), Rational(3, 5), Rational(0))
                .isMinusOne() }
            assertFalse("test 10") { ListPolynomial(Rational(0), Rational(5, -5), Rational(0))
                .isMinusOne() }
            assertFalse("test 11") { ListPolynomial(Rational(-1), Rational(3, 5), Rational(0))
                .isMinusOne() }
            assertFalse("test 12") { ListPolynomial(Rational(-1), Rational(5, -5), Rational(0))
                .isMinusOne() }
        }
    }
    @Test
    fun test_Polynomial_Polynomial_equalsTo() {
        RationalField.listPolynomial {
            assertTrue("test 1") {
                ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)) equalsTo
                        ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7))
            }
            assertTrue("test 2") {
                ListPolynomial(Rational(5, 9), Rational(0), Rational(-8, 7)) equalsTo
                        ListPolynomial(Rational(5, 9), Rational(0), Rational(-8, 7))
            }
            assertTrue("test 3") {
                ListPolynomial(Rational(0), Rational(0), Rational(-8, 7), Rational(0), Rational(0)) equalsTo
                        ListPolynomial(Rational(0), Rational(0), Rational(-8, 7))
            }
            assertFalse("test 4") {
                ListPolynomial(Rational(5, 9), Rational(5, 7), Rational(-8, 7)) equalsTo
                        ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7))
            }
            assertFalse("test 5") {
                ListPolynomial(Rational(8, 3), Rational(0), Rational(-8, 7)) equalsTo
                        ListPolynomial(Rational(5, 9), Rational(0), Rational(-8, 7))
            }
            assertFalse("test 6") {
                ListPolynomial(Rational(0), Rational(0), Rational(-8, 7), Rational(0), Rational(0)) equalsTo
                        ListPolynomial(Rational(0), Rational(0), Rational(8, 7))
            }
        }
    }
    @Test
    fun test_Polynomial_degree() {
        RationalField.listPolynomial {
            assertEquals(
                2,
                ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7)).degree,
                "test 1"
            )
            assertEquals(
                -1,
                ListPolynomial<Rational>().degree,
                "test 2"
            )
            assertEquals(
                -1,
                ListPolynomial(Rational(0)).degree,
                "test 3"
            )
            assertEquals(
                -1,
                ListPolynomial(Rational(0), Rational(0)).degree,
                "test 4"
            )
            assertEquals(
                -1,
                ListPolynomial(Rational(0), Rational(0), Rational(0)).degree,
                "test 5"
            )
            assertEquals(
                0,
                ListPolynomial(Rational(5, 9)).degree,
                "test 6"
            )
            assertEquals(
                0,
                ListPolynomial(Rational(5, 9), Rational(0)).degree,
                "test 7"
            )
            assertEquals(
                0,
                ListPolynomial(Rational(5, 9), Rational(0), Rational(0)).degree,
                "test 8"
            )
            assertEquals(
                2,
                ListPolynomial(Rational(0), Rational(0), Rational(-8, 7)).degree,
                "test 9"
            )
            assertEquals(
                2,
                ListPolynomial(Rational(5, 9), Rational(-8, 9), Rational(-8, 7), Rational(0), Rational(0)).degree,
                "test 10"
            )
            assertEquals(
                2,
                ListPolynomial(Rational(0), Rational(0), Rational(-8, 7), Rational(0), Rational(0)).degree,
                "test 11"
            )
        }
    }
    @Test
    fun test_Polynomial_asConstantOrNull() {
        RationalField.listPolynomial {
            assertEquals(
                Rational(0),
                ListPolynomial<Rational>().asConstantOrNull(),
                "test 1"
            )
            assertEquals(
                Rational(0),
                ListPolynomial(Rational(0)).asConstantOrNull(),
                "test 2"
            )
            assertEquals(
                Rational(0),
                ListPolynomial(Rational(0), Rational(0)).asConstantOrNull(),
                "test 3"
            )
            assertEquals(
                Rational(0),
                ListPolynomial(Rational(0), Rational(0), Rational(0)).asConstantOrNull(),
                "test 4"
            )
            assertEquals(
                Rational(-7, 9),
                ListPolynomial(Rational(-7, 9)).asConstantOrNull(),
                "test 5"
            )
            assertEquals(
                Rational(-7, 9),
                ListPolynomial(Rational(-7, 9), Rational(0)).asConstantOrNull(),
                "test 6"
            )
            assertEquals(
                Rational(-7, 9),
                ListPolynomial(Rational(-7, 9), Rational(0), Rational(0)).asConstantOrNull(),
                "test 7"
            )
            assertEquals(
                null,
                ListPolynomial(Rational(0), Rational(-7, 9)).asConstantOrNull(),
                "test 8"
            )
            assertEquals(
                null,
                ListPolynomial(Rational(0), Rational(-7, 9), Rational(0)).asConstantOrNull(),
                "test 9"
            )
            assertEquals(
                null,
                ListPolynomial(Rational(0), Rational(0), Rational(-7, 9)).asConstantOrNull(),
                "test 10"
            )
            assertEquals(
                null,
                ListPolynomial(Rational(4, 15), Rational(0), Rational(-7, 9)).asConstantOrNull(),
                "test 11"
            )
            assertEquals(
                null,
                ListPolynomial(Rational(4, 15), Rational(0), Rational(-7, 9), Rational(0))
                    .asConstantOrNull(),
                "test 12"
            )
        }
    }
}