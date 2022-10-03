/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("LocalVariableName")

package space.kscience.kmath.functions

import space.kscience.kmath.functions.testUtils.IntModuloRing
import space.kscience.kmath.functions.testUtils.Rational
import space.kscience.kmath.functions.testUtils.RationalField
import space.kscience.kmath.functions.testUtils.m
import space.kscience.kmath.functions.testUtils.o
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.fail


class NumberedPolynomialTest {
    @Test
    fun test_Polynomial_Int_plus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-22, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(5, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) + -3,
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-3, 1),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) + -3,
                "test 2"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 1),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) + -3,
                "test 3"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ) + -3,
                "test 4"
            )
            val polynomial_5 = NumberedPolynomial(
                listOf<UInt>() to Rational(-22, 9),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_5,
                polynomial_5 + 0,
                "test 5"
            )
            val polynomial_6 = NumberedPolynomial(
                listOf<UInt>() to Rational(0, 9),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_6,
                polynomial_6 + 0,
                "test 6"
            )
            val polynomial_7 = NumberedPolynomial(
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_7,
                polynomial_7 + 0,
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Int_minus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-22, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(5, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) - 3,
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-3, 1),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) - 3,
                "test 2"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 1),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) - 3,
                "test 3"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ) - 3,
                "test 4"
            )
            val polynomial_5 = NumberedPolynomial(
                listOf<UInt>() to Rational(-22, 9),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_5,
                polynomial_5 - 0,
                "test 5"
            )
            val polynomial_6 = NumberedPolynomial(
                listOf<UInt>() to Rational(0, 9),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_6,
                polynomial_6 - 0,
                "test 6"
            )
            val polynomial_7 = NumberedPolynomial(
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_7,
                polynomial_7 - 0,
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Int_times() {
        IntModuloRing(35).numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to m(34),
                    listOf(3u) to m(2),
                    listOf(0u, 1u) to m(1),
                    listOf(1u) to m(20),
                    listOf(0u, 0u, 2u) to m(2),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to m(22),
                    listOf(3u) to m(26),
                    listOf(0u, 1u) to m(13),
                    listOf(1u) to m(15),
                    listOf(0u, 0u, 2u) to m(26),
                ) * 27,
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to m(0),
                    listOf(3u) to m(0),
                    listOf(0u, 1u) to m(0),
                    listOf(1u) to m(0),
                    listOf(0u, 0u, 2u) to m(0),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to m(7),
                    listOf(3u) to m(0),
                    listOf(0u, 1u) to m(49),
                    listOf(1u) to m(21),
                    listOf(0u, 0u, 2u) to m(14),
                ) * 15,
                "test 2"
            )
            val polynomial = NumberedPolynomial(
                listOf<UInt>() to m(22),
                listOf(3u) to m(26),
                listOf(0u, 1u) to m(13),
                listOf(1u) to m(15),
                listOf(0u, 0u, 2u) to m(26),
            )
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
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-22, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                -3 + NumberedPolynomial(
                    listOf<UInt>() to Rational(5, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-3, 1),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                -3 + NumberedPolynomial(
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 2"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                -3 + NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 3"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ),
                -3 + NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ),
                "test 4"
            )
            val polynomial_5 = NumberedPolynomial(
                listOf<UInt>() to Rational(-22, 9),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_5,
                0 + polynomial_5,
                "test 5"
            )
            val polynomial_6 = NumberedPolynomial(
                listOf<UInt>() to Rational(0, 9),
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_6,
                0 + polynomial_6,
                "test 6"
            )
            val polynomial_7 = NumberedPolynomial(
                listOf(3u) to Rational(-8, 9),
                listOf(0u, 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_7,
                0 + polynomial_7,
                "test 7"
            )
        }
    }
    @Test
    fun test_Int_Polynomial_minus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(22, 9),
                    listOf(3u) to Rational(8, 9),
                    listOf(0u, 4u) to Rational(8, 7),
                ),
                3 - NumberedPolynomial(
                    listOf<UInt>() to Rational(5, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(3, 1),
                    listOf(3u) to Rational(8, 9),
                    listOf(0u, 4u) to Rational(8, 7),
                ),
                3 - NumberedPolynomial(
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 2"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 1),
                    listOf(3u) to Rational(8, 9),
                    listOf(0u, 4u) to Rational(8, 7),
                ),
                3 - NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 3"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ),
                3 - NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ),
                "test 4"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(22, 9),
                    listOf(3u) to Rational(8, 9),
                    listOf(0u, 4u) to Rational(8, 7),
                ),
                0 - NumberedPolynomial(
                    listOf<UInt>() to Rational(-22, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 5"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 9),
                    listOf(3u) to Rational(8, 9),
                    listOf(0u, 4u) to Rational(8, 7),
                ),
                0 - NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 6"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf(3u) to Rational(8, 9),
                    listOf(0u, 4u) to Rational(8, 7),
                ),
                0 - NumberedPolynomial(
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 7"
            )
        }
    }
    @Test
    fun test_Int_Polynomial_times() {
        IntModuloRing(35).numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to m(34),
                    listOf(3u) to m(2),
                    listOf(0u, 1u) to m(1),
                    listOf(1u) to m(20),
                    listOf(0u, 0u, 2u) to m(2),
                ),
                27 * NumberedPolynomial(
                    listOf<UInt>() to m(22),
                    listOf(3u) to m(26),
                    listOf(0u, 1u) to m(13),
                    listOf(1u) to m(15),
                    listOf(0u, 0u, 2u) to m(26),
                ),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to m(0),
                    listOf(3u) to m(0),
                    listOf(0u, 1u) to m(0),
                    listOf(1u) to m(0),
                    listOf(0u, 0u, 2u) to m(0),
                ),
                15 * NumberedPolynomial(
                    listOf<UInt>() to m(7),
                    listOf(3u) to m(0),
                    listOf(0u, 1u) to m(49),
                    listOf(1u) to m(21),
                    listOf(0u, 0u, 2u) to m(14),
                ),
                "test 2"
            )
            val polynomial = NumberedPolynomial(
                listOf<UInt>() to m(22),
                listOf(3u) to m(26),
                listOf(0u, 1u) to m(13),
                listOf(1u) to m(15),
                listOf(0u, 0u, 2u) to m(26),
            )
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
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-22, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(5, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) + Rational(-3),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-3, 1),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) + Rational(-3),
                "test 2"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 1),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) + Rational(-3),
                "test 3"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ) + Rational(-3),
                "test 4"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-22, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-22, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) + Rational(0),
                "test 5"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) + Rational(0),
                "test 6"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) + Rational(0),
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Constant_minus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-22, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(5, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) - Rational(3),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-3, 1),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) - Rational(3),
                "test 2"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 1),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) - Rational(3),
                "test 3"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ) - Rational(3),
                "test 4"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-22, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-22, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) - Rational(0),
                "test 5"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) - Rational(0),
                "test 6"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                NumberedPolynomial(
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ) - Rational(0),
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Constant_times() {
        IntModuloRing(35).numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to m(34),
                    listOf(3u) to m(2),
                    listOf(0u, 1u) to m(1),
                    listOf(1u) to m(20),
                    listOf(0u, 0u, 2u) to m(2),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to m(22),
                    listOf(3u) to m(26),
                    listOf(0u, 1u) to m(13),
                    listOf(1u) to m(15),
                    listOf(0u, 0u, 2u) to m(26),
                ) * m(27),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to m(0),
                    listOf(3u) to m(0),
                    listOf(0u, 1u) to m(0),
                    listOf(1u) to m(0),
                    listOf(0u, 0u, 2u) to m(0),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to m(7),
                    listOf(3u) to m(0),
                    listOf(0u, 1u) to m(49),
                    listOf(1u) to m(21),
                    listOf(0u, 0u, 2u) to m(14),
                ) * m(15),
                "test 2"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to m(0),
                    listOf(3u) to m(0),
                    listOf(0u, 1u) to m(0),
                    listOf(1u) to m(0),
                    listOf(0u, 0u, 2u) to m(0),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to m(22),
                    listOf(3u) to m(26),
                    listOf(0u, 1u) to m(13),
                    listOf(1u) to m(15),
                    listOf(0u, 0u, 2u) to m(26),
                ) * m(0),
                "test 3"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to m(22),
                    listOf(3u) to m(26),
                    listOf(0u, 1u) to m(13),
                    listOf(1u) to m(15),
                    listOf(0u, 0u, 2u) to m(26),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to m(22),
                    listOf(3u) to m(26),
                    listOf(0u, 1u) to m(13),
                    listOf(1u) to m(15),
                    listOf(0u, 0u, 2u) to m(26),
                ) * m(1),
                "test 4"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_plus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-22, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                Rational(-3) + NumberedPolynomial(
                    listOf<UInt>() to Rational(5, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-3, 1),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                Rational(-3) + NumberedPolynomial(
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 2"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 1),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                Rational(-3) + NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 3"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ),
                Rational(-3) + NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ),
                "test 4"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-22, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                Rational(0) + NumberedPolynomial(
                    listOf<UInt>() to Rational(-22, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 5"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                Rational(0) + NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 6"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                Rational(0) + NumberedPolynomial(
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 7"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_minus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(22, 9),
                    listOf(3u) to Rational(8, 9),
                    listOf(0u, 4u) to Rational(8, 7),
                ),
                Rational(3) - NumberedPolynomial(
                    listOf<UInt>() to Rational(5, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(3, 1),
                    listOf(3u) to Rational(8, 9),
                    listOf(0u, 4u) to Rational(8, 7),
                ),
                Rational(3) - NumberedPolynomial(
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 2"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 1),
                    listOf(3u) to Rational(8, 9),
                    listOf(0u, 4u) to Rational(8, 7),
                ),
                Rational(3) - NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 3"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ),
                Rational(3) - NumberedPolynomial(
                    listOf<UInt>() to Rational(27, 9),
                    listOf(3u) to Rational(0),
                    listOf(0u, 4u) to Rational(0),
                ),
                "test 4"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(22, 9),
                    listOf(3u) to Rational(8, 9),
                    listOf(0u, 4u) to Rational(8, 7),
                ),
                Rational(0) - NumberedPolynomial(
                    listOf<UInt>() to Rational(-22, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 5"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 9),
                    listOf(3u) to Rational(8, 9),
                    listOf(0u, 4u) to Rational(8, 7),
                ),
                Rational(0) - NumberedPolynomial(
                    listOf<UInt>() to Rational(0, 9),
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 6"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(3u) to Rational(8, 9),
                    listOf(0u, 4u) to Rational(8, 7),
                ),
                Rational(0) - NumberedPolynomial(
                    listOf(3u) to Rational(-8, 9),
                    listOf(0u, 4u) to Rational(-8, 7),
                ),
                "test 7"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_times() {
        IntModuloRing(35).numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to m(34),
                    listOf(3u) to m(2),
                    listOf(0u, 1u) to m(1),
                    listOf(1u) to m(20),
                    listOf(0u, 0u, 2u) to m(2),
                ),
                m(27) * NumberedPolynomial(
                    listOf<UInt>() to m(22),
                    listOf(3u) to m(26),
                    listOf(0u, 1u) to m(13),
                    listOf(1u) to m(15),
                    listOf(0u, 0u, 2u) to m(26),
                ),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to m(0),
                    listOf(3u) to m(0),
                    listOf(0u, 1u) to m(0),
                    listOf(1u) to m(0),
                    listOf(0u, 0u, 2u) to m(0),
                ),
                m(15) * NumberedPolynomial(
                    listOf<UInt>() to m(7),
                    listOf(3u) to m(0),
                    listOf(0u, 1u) to m(49),
                    listOf(1u) to m(21),
                    listOf(0u, 0u, 2u) to m(14),
                ),
                "test 2"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to m(0),
                    listOf(3u) to m(0),
                    listOf(0u, 1u) to m(0),
                    listOf(1u) to m(0),
                    listOf(0u, 0u, 2u) to m(0),
                ),
                m(0) * NumberedPolynomial(
                    listOf<UInt>() to m(22),
                    listOf(3u) to m(26),
                    listOf(0u, 1u) to m(13),
                    listOf(1u) to m(15),
                    listOf(0u, 0u, 2u) to m(26),
                ),
                "test 3"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to m(22),
                    listOf(3u) to m(26),
                    listOf(0u, 1u) to m(13),
                    listOf(1u) to m(15),
                    listOf(0u, 0u, 2u) to m(26),
                ),
                m(1) * NumberedPolynomial(
                    listOf<UInt>() to m(22),
                    listOf(3u) to m(26),
                    listOf(0u, 1u) to m(13),
                    listOf(1u) to m(15),
                    listOf(0u, 0u, 2u) to m(26),
                ),
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_unaryMinus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf(5u) to Rational(-5, 9),
                    listOf<UInt>() to Rational(8, 9),
                    listOf(0u, 0u, 0u, 0u, 0u, 0u, 13u) to Rational(8, 7),
                ),
                -NumberedPolynomial(
                    listOf(5u) to Rational(5, 9),
                    listOf<UInt>() to Rational(-8, 9),
                    listOf(0u, 0u, 0u, 0u, 0u, 0u, 13u) to Rational(-8, 7),
                ),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf(5u) to Rational(-5, 9),
                    listOf<UInt>() to Rational(8, 9),
                    listOf(0u, 0u, 0u, 0u, 0u, 0u, 13u) to Rational(8, 7),
                    listOf(0u, 4u) to Rational(0),
                    listOf(5u) to Rational(0),
                ),
                -NumberedPolynomial(
                    listOf(5u) to Rational(5, 9),
                    listOf<UInt>() to Rational(-8, 9),
                    listOf(0u, 0u, 0u, 0u, 0u, 0u, 13u) to Rational(-8, 7),
                    listOf(0u, 4u) to Rational(0),
                    listOf(5u) to Rational(0),
                ),
                "test 2"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_plus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-17, 2),
                    listOf(1u) to Rational(-1, 3),
                    listOf(2u) to Rational(-25, 21),
                    listOf(0u, 1u) to Rational(146, 63),
                    listOf(1u, 1u) to Rational(-3, 5),
                    listOf(2u, 1u) to Rational(61, 15),
                    listOf(0u, 2u) to Rational(157, 63),
                    listOf(1u, 2u) to Rational(-55, 21),
                    listOf(2u, 2u) to Rational(11, 24),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(6, 4),
                    listOf(1u) to Rational(-2, 6),
                    listOf(2u) to Rational(10, 6),
                    listOf(0u, 1u) to Rational(17, 7),
                    listOf(1u, 1u) to Rational(-7, 7),
                    listOf(2u, 1u) to Rational(12, 5),
                    listOf(0u, 2u) to Rational(12, 7),
                    listOf(1u, 2u) to Rational(-10, 3),
                    listOf(2u, 2u) to Rational(9, 8),
                ) + NumberedPolynomial(
                    listOf<UInt>() to Rational(-20, 2),
                    listOf(1u) to Rational(0, 9),
                    listOf(2u) to Rational(-20, 7),
                    listOf(0u, 1u) to Rational(-1, 9),
                    listOf(1u, 1u) to Rational(2, 5),
                    listOf(2u, 1u) to Rational(10, 6),
                    listOf(0u, 2u) to Rational(7, 9),
                    listOf(1u, 2u) to Rational(5, 7),
                    listOf(2u, 2u) to Rational(-2, 3),
                ),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-17, 2),
                    listOf(1u) to Rational(-1, 3),
                    listOf(2u) to Rational(-25, 21),
                    listOf(0u, 1u) to Rational(-1, 9),
                    listOf(1u, 1u) to Rational(2, 5),
                    listOf(2u, 1u) to Rational(10, 6),
                    listOf(0u, 2u) to Rational(157, 63),
                    listOf(1u, 2u) to Rational(-55, 21),
                    listOf(2u, 2u) to Rational(11, 24),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(6, 4),
                    listOf(1u) to Rational(-2, 6),
                    listOf(2u) to Rational(10, 6),
                    listOf(0u, 2u) to Rational(12, 7),
                    listOf(1u, 2u) to Rational(-10, 3),
                    listOf(2u, 2u) to Rational(9, 8),
                ) + NumberedPolynomial(
                    listOf<UInt>() to Rational(-20, 2),
                    listOf(1u) to Rational(0, 9),
                    listOf(2u) to Rational(-20, 7),
                    listOf(0u, 1u) to Rational(-1, 9),
                    listOf(1u, 1u) to Rational(2, 5),
                    listOf(2u, 1u) to Rational(10, 6),
                    listOf(0u, 2u) to Rational(7, 9),
                    listOf(1u, 2u) to Rational(5, 7),
                    listOf(2u, 2u) to Rational(-2, 3),
                ),
                "test 2"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-17, 2),
                    listOf(1u) to Rational(-1, 3),
                    listOf(2u) to Rational(-25, 21),
                    listOf(0u, 1u) to Rational(-1, 9),
                    listOf(1u, 1u) to Rational(2, 5),
                    listOf(2u, 1u) to Rational(10, 6),
                    listOf(0u, 2u) to Rational(12, 7),
                    listOf(1u, 2u) to Rational(-10, 3),
                    listOf(2u, 2u) to Rational(9, 8),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(6, 4),
                    listOf(1u) to Rational(-2, 6),
                    listOf(2u) to Rational(10, 6),
                    listOf(0u, 2u) to Rational(12, 7),
                    listOf(1u, 2u) to Rational(-10, 3),
                    listOf(2u, 2u) to Rational(9, 8),
                ) + NumberedPolynomial(
                    listOf<UInt>() to Rational(-20, 2),
                    listOf(1u) to Rational(0, 9),
                    listOf(2u) to Rational(-20, 7),
                    listOf(0u, 1u) to Rational(-1, 9),
                    listOf(1u, 1u) to Rational(2, 5),
                    listOf(2u, 1u) to Rational(10, 6),
                    listOf(0u, 2u) to Rational(0),
                    listOf(1u, 2u) to Rational(0),
                    listOf(2u, 2u) to Rational(0),
                ),
                "test 3"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(1u) to Rational(0),
                    listOf(2u) to Rational(0),
                    listOf(0u, 1u) to Rational(0),
                    listOf(1u, 1u) to Rational(0),
                    listOf(2u, 1u) to Rational(0),
                    listOf(0u, 2u) to Rational(0),
                    listOf(1u, 2u) to Rational(0),
                    listOf(2u, 2u) to Rational(0),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(6, 4),
                    listOf(1u) to Rational(-2, 6),
                    listOf(2u) to Rational(10, 6),
                    listOf(0u, 1u) to Rational(17, 7),
                    listOf(1u, 1u) to Rational(-7, 7),
                    listOf(2u, 1u) to Rational(12, 5),
                    listOf(0u, 2u) to Rational(12, 7),
                    listOf(1u, 2u) to Rational(-10, 3),
                    listOf(2u, 2u) to Rational(9, 8),
                ) + NumberedPolynomial(
                    listOf<UInt>() to Rational(-6, 4),
                    listOf(1u) to Rational(2, 6),
                    listOf(2u) to Rational(-10, 6),
                    listOf(0u, 1u) to Rational(-17, 7),
                    listOf(1u, 1u) to Rational(7, 7),
                    listOf(2u, 1u) to Rational(-12, 5),
                    listOf(0u, 2u) to Rational(-12, 7),
                    listOf(1u, 2u) to Rational(10, 3),
                    listOf(2u, 2u) to Rational(-9, 8),
                ),
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_minus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-17, 2),
                    listOf(1u) to Rational(-1, 3),
                    listOf(2u) to Rational(-25, 21),
                    listOf(0u, 1u) to Rational(146, 63),
                    listOf(1u, 1u) to Rational(-3, 5),
                    listOf(2u, 1u) to Rational(61, 15),
                    listOf(0u, 2u) to Rational(157, 63),
                    listOf(1u, 2u) to Rational(-55, 21),
                    listOf(2u, 2u) to Rational(11, 24),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(6, 4),
                    listOf(1u) to Rational(-2, 6),
                    listOf(2u) to Rational(10, 6),
                    listOf(0u, 1u) to Rational(17, 7),
                    listOf(1u, 1u) to Rational(-7, 7),
                    listOf(2u, 1u) to Rational(12, 5),
                    listOf(0u, 2u) to Rational(12, 7),
                    listOf(1u, 2u) to Rational(-10, 3),
                    listOf(2u, 2u) to Rational(9, 8),
                ) - NumberedPolynomial(
                    listOf<UInt>() to Rational(20, 2),
                    listOf(1u) to Rational(0, 9),
                    listOf(2u) to Rational(20, 7),
                    listOf(0u, 1u) to Rational(1, 9),
                    listOf(1u, 1u) to Rational(-2, 5),
                    listOf(2u, 1u) to Rational(-10, 6),
                    listOf(0u, 2u) to Rational(-7, 9),
                    listOf(1u, 2u) to Rational(-5, 7),
                    listOf(2u, 2u) to Rational(2, 3),
                ),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-17, 2),
                    listOf(1u) to Rational(-1, 3),
                    listOf(2u) to Rational(-25, 21),
                    listOf(0u, 1u) to Rational(-1, 9),
                    listOf(1u, 1u) to Rational(2, 5),
                    listOf(2u, 1u) to Rational(10, 6),
                    listOf(0u, 2u) to Rational(157, 63),
                    listOf(1u, 2u) to Rational(-55, 21),
                    listOf(2u, 2u) to Rational(11, 24),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(6, 4),
                    listOf(1u) to Rational(-2, 6),
                    listOf(2u) to Rational(10, 6),
                    listOf(0u, 2u) to Rational(12, 7),
                    listOf(1u, 2u) to Rational(-10, 3),
                    listOf(2u, 2u) to Rational(9, 8),
                ) - NumberedPolynomial(
                    listOf<UInt>() to Rational(20, 2),
                    listOf(1u) to Rational(0, 9),
                    listOf(2u) to Rational(20, 7),
                    listOf(0u, 1u) to Rational(1, 9),
                    listOf(1u, 1u) to Rational(-2, 5),
                    listOf(2u, 1u) to Rational(-10, 6),
                    listOf(0u, 2u) to Rational(-7, 9),
                    listOf(1u, 2u) to Rational(-5, 7),
                    listOf(2u, 2u) to Rational(2, 3),
                ),
                "test 2"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(-17, 2),
                    listOf(1u) to Rational(-1, 3),
                    listOf(2u) to Rational(-25, 21),
                    listOf(0u, 1u) to Rational(-1, 9),
                    listOf(1u, 1u) to Rational(2, 5),
                    listOf(2u, 1u) to Rational(10, 6),
                    listOf(0u, 2u) to Rational(12, 7),
                    listOf(1u, 2u) to Rational(-10, 3),
                    listOf(2u, 2u) to Rational(9, 8),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(6, 4),
                    listOf(1u) to Rational(-2, 6),
                    listOf(2u) to Rational(10, 6),
                    listOf(0u, 2u) to Rational(12, 7),
                    listOf(1u, 2u) to Rational(-10, 3),
                    listOf(2u, 2u) to Rational(9, 8),
                ) - NumberedPolynomial(
                    listOf<UInt>() to Rational(20, 2),
                    listOf(1u) to Rational(0, 9),
                    listOf(2u) to Rational(20, 7),
                    listOf(0u, 1u) to Rational(1, 9),
                    listOf(1u, 1u) to Rational(-2, 5),
                    listOf(2u, 1u) to Rational(-10, 6),
                    listOf(0u, 2u) to Rational(0),
                    listOf(1u, 2u) to Rational(0),
                    listOf(2u, 2u) to Rational(0),
                ),
                "test 3"
            )
            assertEquals(
                NumberedPolynomial(
                    listOf<UInt>() to Rational(0),
                    listOf(1u) to Rational(0),
                    listOf(2u) to Rational(0),
                    listOf(0u, 1u) to Rational(0),
                    listOf(1u, 1u) to Rational(0),
                    listOf(2u, 1u) to Rational(0),
                    listOf(0u, 2u) to Rational(0),
                    listOf(1u, 2u) to Rational(0),
                    listOf(2u, 2u) to Rational(0),
                ),
                NumberedPolynomial(
                    listOf<UInt>() to Rational(6, 4),
                    listOf(1u) to Rational(-2, 6),
                    listOf(2u) to Rational(10, 6),
                    listOf(0u, 1u) to Rational(17, 7),
                    listOf(1u, 1u) to Rational(-7, 7),
                    listOf(2u, 1u) to Rational(12, 5),
                    listOf(0u, 2u) to Rational(12, 7),
                    listOf(1u, 2u) to Rational(-10, 3),
                    listOf(2u, 2u) to Rational(9, 8),
                ) - NumberedPolynomial(
                    listOf<UInt>() to Rational(6, 4),
                    listOf(1u) to Rational(-2, 6),
                    listOf(2u) to Rational(10, 6),
                    listOf(0u, 1u) to Rational(17, 7),
                    listOf(1u, 1u) to Rational(-7, 7),
                    listOf(2u, 1u) to Rational(12, 5),
                    listOf(0u, 2u) to Rational(12, 7),
                    listOf(1u, 2u) to Rational(-10, 3),
                    listOf(2u, 2u) to Rational(9, 8),
                ),
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_times() {
        IntModuloRing(35).numberedPolynomialSpace {
            // (p + q + r) * (p^2 + q^2 + r^2 - pq - pr - qr) = p^3 + q^3 + r^3 - 3pqr
            assertEquals(
                NumberedPolynomial(
                    listOf(3u) to m(1),
                    listOf(0u, 3u) to m(1),
                    listOf(0u, 0u, 3u) to m(1),
                    listOf(1u, 2u) to m(0),
                    listOf(0u, 1u, 2u) to m(0),
                    listOf(2u, 0u, 1u) to m(0),
                    listOf(1u, 0u, 2u) to m(0),
                    listOf(2u, 1u) to m(0),
                    listOf(0u, 2u, 1u) to m(0),
                    listOf(1u, 1u, 1u) to m(-3),
                ),
                NumberedPolynomial(
                    listOf(1u) to m(1),
                    listOf(0u, 1u) to m(1),
                    listOf(0u, 0u, 1u) to m(1),
                ) * NumberedPolynomial(
                    listOf(2u) to m(1),
                    listOf(0u, 2u) to m(1),
                    listOf(0u, 0u, 2u) to m(1),
                    listOf(1u, 1u) to m(-1),
                    listOf(0u, 1u, 1u) to m(-1),
                    listOf(1u, 0u, 1u) to m(-1),
                ),
                "test 1"
            )
            // Spoiler: 5 * 7 = 0
            assertEquals(
                NumberedPolynomial(
                    listOf(2u) to m(0),
                    listOf(0u, 2u) to m(0),
                    listOf(0u, 0u, 2u) to m(0),
                    listOf(1u, 1u) to m(0),
                    listOf(0u, 1u, 1u) to m(0),
                    listOf(1u, 0u, 1u) to m(0),
                ),
                NumberedPolynomial(
                    listOf(1u) to m(5),
                    listOf(0u, 1u) to m(-25),
                    listOf(0u, 0u, 1u) to m(10),
                ) * NumberedPolynomial(
                    listOf(1u) to m(21),
                    listOf(0u, 1u) to m(14),
                    listOf(0u, 0u, 1u) to m(-7),
                ),
                "test 2"
            )
        }
    }
    @Test
    fun test_lastVariable() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                -1,
                NumberedPolynomial().lastVariable,
                "test 1"
            )
            assertEquals(
                -1,
                NumberedPolynomial(
                    listOf<UInt>() to o
                ).lastVariable,
                "test 2"
            )
            assertEquals(
                2,
                NumberedPolynomial(
                    listOf(1u, 2u, 3u) to o
                ).lastVariable,
                "test 3"
            )
            assertEquals(
                3,
                NumberedPolynomial(
                    listOf(0u, 1u, 2u, 1u, 0u) to o
                ).also { println(it) }.lastVariable,
                "test 4"
            )
            assertEquals(
                2,
                NumberedPolynomial(
                    listOf<UInt>() to o,
                    listOf(0u, 1u) to o,
                    listOf(2u, 0u, 1u) to o,
                ).lastVariable,
                "test 5"
            )
        }
    }
    @Test
    fun test_degree() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                -1,
                NumberedPolynomial().degree,
                "test 1"
            )
            assertEquals(
                0,
                NumberedPolynomial(
                    listOf<UInt>() to o
                ).degree,
                "test 2"
            )
            assertEquals(
                6,
                NumberedPolynomial(
                    listOf(1u, 2u, 3u) to o
                ).degree,
                "test 3"
            )
            assertEquals(
                4,
                NumberedPolynomial(
                    listOf(0u, 1u, 2u, 1u, 0u) to o
                ).degree,
                "test 4"
            )
            assertEquals(
                3,
                NumberedPolynomial(
                    listOf<UInt>() to o,
                    listOf(0u, 1u) to o,
                    listOf(2u, 0u, 1u) to o,
                ).degree,
                "test 5"
            )
            assertEquals(
                4,
                NumberedPolynomial(
                    listOf<UInt>() to o,
                    listOf(0u, 1u) to o,
                    listOf(2u, 0u, 1u) to o,
                    listOf(0u, 0u, 0u, 4u) to o,
                ).degree,
                "test 6"
            )
        }
    }
    @Test
    fun test_degrees() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                listOf(),
                NumberedPolynomial().degrees,
                "test 1"
            )
            assertEquals(
                listOf(),
                NumberedPolynomial(
                    listOf<UInt>() to o
                ).degrees,
                "test 2"
            )
            assertEquals(
                listOf(1u, 2u, 3u),
                NumberedPolynomial(
                    listOf(1u, 2u, 3u) to o
                ).degrees,
                "test 3"
            )
            assertEquals(
                listOf(0u, 1u, 2u, 1u),
                NumberedPolynomial(
                    listOf(0u, 1u, 2u, 1u, 0u) to o
                ).degrees,
                "test 4"
            )
            assertEquals(
                listOf(2u, 1u, 1u),
                NumberedPolynomial(
                    listOf<UInt>() to o,
                    listOf(0u, 1u) to o,
                    listOf(2u, 0u, 1u) to o,
                ).degrees,
                "test 5"
            )
            assertEquals(
                listOf(2u, 2u, 2u, 4u),
                NumberedPolynomial(
                    listOf<UInt>() to o,
                    listOf(1u, 2u) to o,
                    listOf(0u, 1u, 2u) to o,
                    listOf(2u, 0u, 1u) to o,
                    listOf(0u, 0u, 0u, 4u) to o,
                ).degrees,
                "test 6"
            )
        }
    }
    @Test
    fun test_degreeBy() {
        RationalField.numberedPolynomialSpace {
            fun NumberedPolynomial<Rational>.collectDegrees(limit: Int = lastVariable + 2): List<UInt> = List(limit) { degreeBy(it) }
            assertEquals(
                listOf(0u),
                NumberedPolynomial().collectDegrees(),
                "test 1"
            )
            assertEquals(
                listOf(0u),
                NumberedPolynomial(
                    listOf<UInt>() to o
                ).collectDegrees(),
                "test 2"
            )
            assertEquals(
                listOf(1u, 2u, 3u, 0u),
                NumberedPolynomial(
                    listOf(1u, 2u, 3u) to o
                ).collectDegrees(),
                "test 3"
            )
            assertEquals(
                listOf(0u, 1u, 2u, 1u, 0u),
                NumberedPolynomial(
                    listOf(0u, 1u, 2u, 1u, 0u) to o
                ).collectDegrees(),
                "test 4"
            )
            assertEquals(
                listOf(2u, 1u, 1u, 0u),
                NumberedPolynomial(
                    listOf<UInt>() to o,
                    listOf(0u, 1u) to o,
                    listOf(2u, 0u, 1u) to o,
                ).collectDegrees(),
                "test 5"
            )
            assertEquals(
                listOf(2u, 2u, 2u, 4u, 0u),
                NumberedPolynomial(
                    listOf<UInt>() to o,
                    listOf(1u, 2u) to o,
                    listOf(0u, 1u, 2u) to o,
                    listOf(2u, 0u, 1u) to o,
                    listOf(0u, 0u, 0u, 4u) to o,
                ).collectDegrees(),
                "test 6"
            )
        }
    }
    @Test
    fun test_degreeBy_Collection() {
        RationalField.numberedPolynomialSpace {
            fun NumberedPolynomial<Rational>.checkDegreeBy(message: String? = null) {
                val lastVariable = lastVariable
                val indexCollectionSequence: Sequence<List<Int>> = sequence {
                    val appearances = MutableList(lastVariable + 2) { 0 }
                    while (true) {
                        yield(
                            buildList {
                                for ((variable, count) in appearances.withIndex()) repeat(count) { add(variable) }
                            }
                        )
                        val indexChange = appearances.indexOfFirst { it < 4 }
                        if (indexChange == -1) break
                        appearances[indexChange] += 1
                        for (index in 0 until indexChange) appearances[index] = 0
                    }
                }
                for (indexCollection in indexCollectionSequence) {
                    val expected = coefficients.keys.maxOfOrNull { degs -> degs.slice(indexCollection.distinct().filter { it in degs.indices }).sum() } ?: 0u
                    val actual = degreeBy(indexCollection)
                    if (actual != expected)
                        fail("${message ?: ""} Incorrect answer for variable collection $indexCollection: expected $expected, actual $actual")
                }
            }
            NumberedPolynomial().checkDegreeBy("test 1")
            NumberedPolynomial(
                listOf<UInt>() to o
            ).checkDegreeBy("test 2")
            NumberedPolynomial(
                listOf(1u, 2u, 3u) to o
            ).checkDegreeBy("test 3")
            NumberedPolynomial(
                listOf(0u, 1u, 2u, 1u, 0u) to o
            ).checkDegreeBy("test 4")
            NumberedPolynomial(
                listOf<UInt>() to o,
                listOf(0u, 1u) to o,
                listOf(2u, 0u, 1u) to o,
            ).checkDegreeBy("test 5")
            NumberedPolynomial(
                listOf<UInt>() to o,
                listOf(1u, 2u) to o,
                listOf(0u, 1u, 2u) to o,
                listOf(2u, 0u, 1u) to o,
                listOf(0u, 0u, 0u, 4u) to o,
            ).checkDegreeBy("test 6")
        }
    }
    @Test
    fun test_countOfVariables() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                0,
                NumberedPolynomial().countOfVariables,
                "test 1"
            )
            assertEquals(
                0,
                NumberedPolynomial(
                    listOf<UInt>() to o
                ).countOfVariables,
                "test 2"
            )
            assertEquals(
                3,
                NumberedPolynomial(
                    listOf(1u, 2u, 3u) to o
                ).countOfVariables,
                "test 3"
            )
            assertEquals(
                3,
                NumberedPolynomial(
                    listOf(0u, 1u, 2u, 1u, 0u) to o
                ).countOfVariables,
                "test 4"
            )
            assertEquals(
                3,
                NumberedPolynomial(
                    listOf<UInt>() to o,
                    listOf(0u, 1u) to o,
                    listOf(2u, 0u, 1u) to o,
                ).countOfVariables,
                "test 5"
            )
            assertEquals(
                4,
                NumberedPolynomial(
                    listOf<UInt>() to o,
                    listOf(1u, 2u) to o,
                    listOf(0u, 1u, 2u) to o,
                    listOf(2u, 0u, 1u) to o,
                    listOf(0u, 0u, 0u, 4u) to o,
                ).countOfVariables,
                "test 6"
            )
        }
    }
    @Test
    fun test_RF_countOfVariables() {
        RationalField.numberedRationalFunctionSpace {
            assertEquals(
                0,
                NumberedRationalFunction(
                    NumberedPolynomial()
                ).countOfVariables,
                "test 1"
            )
            assertEquals(
                0,
                NumberedRationalFunction(
                    NumberedPolynomial(),
                    NumberedPolynomial()
                ).countOfVariables,
                "test 2"
            )
            assertEquals(
                0,
                NumberedRationalFunction(
                    NumberedPolynomial(
                        listOf<UInt>() to o
                    )
                ).countOfVariables,
                "test 3"
            )
            assertEquals(
                3,
                NumberedRationalFunction(
                    NumberedPolynomial(
                        listOf(1u, 2u, 3u) to o
                    )
                ).countOfVariables,
                "test 4"
            )
            assertEquals(
                3,
                NumberedRationalFunction(
                    NumberedPolynomial(
                        listOf(0u, 1u, 0u, 1u) to o
                    ),
                    NumberedPolynomial(
                        listOf(0u, 0u, 2u) to o
                    )
                ).countOfVariables,
                "test 5"
            )
            assertEquals(
                3,
                NumberedRationalFunction(
                    NumberedPolynomial(
                        listOf<UInt>() to o,
                        listOf(0u, 1u) to o,
                        listOf(2u, 0u, 1u) to o,
                    )
                ).countOfVariables,
                "test 6"
            )
            assertEquals(
                4,
                NumberedRationalFunction(
                    NumberedPolynomial(
                        listOf<UInt>() to o,
                        listOf(1u, 2u) to o,
                        listOf(2u, 0u, 1u) to o,
                    ), NumberedPolynomial(
                        listOf(0u, 1u, 2u) to o,
                        listOf(0u, 0u, 0u, 4u) to o,
                    )
                ).countOfVariables,
                "test 7"
            )
        }
    }
}