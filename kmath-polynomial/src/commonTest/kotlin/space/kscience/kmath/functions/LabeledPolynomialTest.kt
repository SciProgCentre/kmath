/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("LocalVariableName")

package space.kscience.kmath.functions

import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.functions.testUtils.IntModuloRing
import space.kscience.kmath.functions.testUtils.Rational
import space.kscience.kmath.functions.testUtils.RationalField
import space.kscience.kmath.functions.testUtils.iota
import space.kscience.kmath.functions.testUtils.m
import space.kscience.kmath.functions.testUtils.o
import space.kscience.kmath.functions.testUtils.s
import space.kscience.kmath.functions.testUtils.t
import space.kscience.kmath.functions.testUtils.x
import space.kscience.kmath.functions.testUtils.y
import space.kscience.kmath.functions.testUtils.z
import kotlin.test.*


// TODO: Тесты на конвертацию.
class LabeledPolynomialTest {
    @Test
    fun test_Variable_Int_plus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(5),
                    mapOf(x to 1u) to Rational(1),
                ),
                x + 5,
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(1),
                ),
                x + 0,
                "test 2"
            )
        }
    }
    @Test
    fun test_Variable_Int_minus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-5),
                    mapOf(x to 1u) to Rational(1),
                ),
                x - 5,
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(1),
                ),
                x - 0,
                "test 2"
            )
        }
    }
    @Test
    fun test_Variable_Int_times() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(5),
                ),
                x * 5,
                "test 1"
            )
            assertSame(
                zero,
                x * 0,
                "test 2"
            )
        }
    }
    @Test
    fun test_Int_Variable_plus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(5),
                    mapOf(x to 1u) to Rational(1),
                ),
                5 + x,
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(1),
                ),
                0 + x,
                "test 2"
            )
        }
    }
    @Test
    fun test_Int_Variable_minus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(5),
                    mapOf(x to 1u) to Rational(-1),
                ),
                5 - x,
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(-1),
                ),
                0 - x,
                "test 2"
            )
        }
    }
    @Test
    fun test_Int_Variable_times() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(5),
                ),
                5 * x,
                "test 1"
            )
            assertSame(
                zero,
                0 * x,
                "test 2"
            )
        }
    }
    @Test
    fun test_Polynomial_Int_plus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-22, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(5, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) + -3,
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-3, 1),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) + -3,
                "test 2"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 1),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) + -3,
                "test 3"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ) + -3,
                "test 4"
            )
            val polynomial_5 = LabeledPolynomial(
                mapOf<Symbol, UInt>() to Rational(-22, 9),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(x to 0u, y to 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_5,
                polynomial_5 + 0,
                "test 5"
            )
            val polynomial_6 = LabeledPolynomial(
                mapOf<Symbol, UInt>() to Rational(0, 9),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(x to 0u, y to 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_6,
                polynomial_6 + 0,
                "test 6"
            )
            val polynomial_7 = LabeledPolynomial(
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(x to 0u, y to 4u) to Rational(-8, 7),
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
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-22, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(5, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) - 3,
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-3, 1),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) - 3,
                "test 2"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 1),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) - 3,
                "test 3"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ) - 3,
                "test 4"
            )
            val polynomial_5 = LabeledPolynomial(
                mapOf<Symbol, UInt>() to Rational(-22, 9),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(x to 0u, y to 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_5,
                polynomial_5 - 0,
                "test 5"
            )
            val polynomial_6 = LabeledPolynomial(
                mapOf<Symbol, UInt>() to Rational(0, 9),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(x to 0u, y to 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_6,
                polynomial_6 - 0,
                "test 6"
            )
            val polynomial_7 = LabeledPolynomial(
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(x to 0u, y to 4u) to Rational(-8, 7),
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
        IntModuloRing(35).labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(34),
                    mapOf(x to 3u) to m(2),
                    mapOf(x to 0u, y to 1u) to m(1),
                    mapOf(x to 1u) to m(20),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(2),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(22),
                    mapOf(x to 3u) to m(26),
                    mapOf(x to 0u, y to 1u) to m(13),
                    mapOf(x to 1u) to m(15),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(26),
                ) * 27,
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(0),
                    mapOf(x to 3u) to m(0),
                    mapOf(x to 0u, y to 1u) to m(0),
                    mapOf(x to 1u) to m(0),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(0),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(7),
                    mapOf(x to 3u) to m(0),
                    mapOf(x to 0u, y to 1u) to m(49),
                    mapOf(x to 1u) to m(21),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(14),
                ) * 15,
                "test 2"
            )
            val polynomial = LabeledPolynomial(
                mapOf<Symbol, UInt>() to m(22),
                mapOf(x to 3u) to m(26),
                mapOf(x to 0u, y to 1u) to m(13),
                mapOf(x to 1u) to m(15),
                mapOf(x to 0u, y to 0u, z to 2u) to m(26),
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
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-22, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                -3 + LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(5, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-3, 1),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                -3 + LabeledPolynomial(
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 2"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                -3 + LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 3"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ),
                -3 + LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ),
                "test 4"
            )
            val polynomial_5 = LabeledPolynomial(
                mapOf<Symbol, UInt>() to Rational(-22, 9),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(x to 0u, y to 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_5,
                0 + polynomial_5,
                "test 5"
            )
            val polynomial_6 = LabeledPolynomial(
                mapOf<Symbol, UInt>() to Rational(0, 9),
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(x to 0u, y to 4u) to Rational(-8, 7),
            )
            assertSame(
                polynomial_6,
                0 + polynomial_6,
                "test 6"
            )
            val polynomial_7 = LabeledPolynomial(
                mapOf(x to 3u) to Rational(-8, 9),
                mapOf(x to 0u, y to 4u) to Rational(-8, 7),
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
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(22, 9),
                    mapOf(x to 3u) to Rational(8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(8, 7),
                ),
                3 - LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(5, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(3, 1),
                    mapOf(x to 3u) to Rational(8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(8, 7),
                ),
                3 - LabeledPolynomial(
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 2"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 1),
                    mapOf(x to 3u) to Rational(8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(8, 7),
                ),
                3 - LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 3"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ),
                3 - LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ),
                "test 4"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(22, 9),
                    mapOf(x to 3u) to Rational(8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(8, 7),
                ),
                0 - LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-22, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 5"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 9),
                    mapOf(x to 3u) to Rational(8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(8, 7),
                ),
                0 - LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 6"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf(x to 3u) to Rational(8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(8, 7),
                ),
                0 - LabeledPolynomial(
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 7"
            )
        }
    }
    @Test
    fun test_Int_Polynomial_times() {
        IntModuloRing(35).labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(34),
                    mapOf(x to 3u) to m(2),
                    mapOf(x to 0u, y to 1u) to m(1),
                    mapOf(x to 1u) to m(20),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(2),
                ),
                27 * LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(22),
                    mapOf(x to 3u) to m(26),
                    mapOf(x to 0u, y to 1u) to m(13),
                    mapOf(x to 1u) to m(15),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(26),
                ),
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(0),
                    mapOf(x to 3u) to m(0),
                    mapOf(x to 0u, y to 1u) to m(0),
                    mapOf(x to 1u) to m(0),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(0),
                ),
                15 * LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(7),
                    mapOf(x to 3u) to m(0),
                    mapOf(x to 0u, y to 1u) to m(49),
                    mapOf(x to 1u) to m(21),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(14),
                ),
                "test 2"
            )
            val polynomial = LabeledPolynomial(
                mapOf<Symbol, UInt>() to m(22),
                mapOf(x to 3u) to m(26),
                mapOf(x to 0u, y to 1u) to m(13),
                mapOf(x to 1u) to m(15),
                mapOf(x to 0u, y to 0u, z to 2u) to m(26),
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
    fun test_Variable_Constant_plus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(5),
                    mapOf(x to 1u) to Rational(1),
                ),
                x + Rational(5),
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 1u) to Rational(1),
                ),
                x + Rational(0),
                "test 2"
            )
        }
    }
    @Test
    fun test_Variable_Constant_minus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-5),
                    mapOf(x to 1u) to Rational(1),
                ),
                x - Rational(5),
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 1u) to Rational(1),
                ),
                x - Rational(0),
                "test 2"
            )
        }
    }
    @Test
    fun test_Variable_Constant_times() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(5),
                ),
                x * Rational(5),
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(0),
                ),
                x * Rational(0),
                "test 2"
            )
        }
    }
    @Test
    fun test_Constant_Variable_plus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(5),
                    mapOf(x to 1u) to Rational(1),
                ),
                Rational(5) + x,
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 1u) to Rational(1),
                ),
                Rational(0) + x,
                "test 2"
            )
        }
    }
    @Test
    fun test_Constant_Variable_minus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(5),
                    mapOf(x to 1u) to Rational(-1),
                ),
                Rational(5) - x,
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 1u) to Rational(-1),
                ),
                Rational(0) - x,
                "test 2"
            )
        }
    }
    @Test
    fun test_Constant_Variable_times() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(5),
                ),
                Rational(5) * x,
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(0),
                ),
                Rational(0) * x,
                "test 2"
            )
        }
    }
    @Test
    fun test_Polynomial_Constant_plus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-22, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(5, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) + Rational(-3),
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-3, 1),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) + Rational(-3),
                "test 2"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 1),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) + Rational(-3),
                "test 3"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ) + Rational(-3),
                "test 4"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-22, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-22, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) + Rational(0),
                "test 5"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) + Rational(0),
                "test 6"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) + Rational(0),
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Constant_minus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-22, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(5, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) - Rational(3),
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-3, 1),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) - Rational(3),
                "test 2"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 1),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) - Rational(3),
                "test 3"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ) - Rational(3),
                "test 4"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-22, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-22, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) - Rational(0),
                "test 5"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) - Rational(0),
                "test 6"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                LabeledPolynomial(
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ) - Rational(0),
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Constant_times() {
        IntModuloRing(35).labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(34),
                    mapOf(x to 3u) to m(2),
                    mapOf(x to 0u, y to 1u) to m(1),
                    mapOf(x to 1u) to m(20),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(2),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(22),
                    mapOf(x to 3u) to m(26),
                    mapOf(x to 0u, y to 1u) to m(13),
                    mapOf(x to 1u) to m(15),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(26),
                ) * m(27),
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(0),
                    mapOf(x to 3u) to m(0),
                    mapOf(x to 0u, y to 1u) to m(0),
                    mapOf(x to 1u) to m(0),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(0),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(7),
                    mapOf(x to 3u) to m(0),
                    mapOf(x to 0u, y to 1u) to m(49),
                    mapOf(x to 1u) to m(21),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(14),
                ) * m(15),
                "test 2"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(0),
                    mapOf(x to 3u) to m(0),
                    mapOf(x to 0u, y to 1u) to m(0),
                    mapOf(x to 1u) to m(0),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(0),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(22),
                    mapOf(x to 3u) to m(26),
                    mapOf(x to 0u, y to 1u) to m(13),
                    mapOf(x to 1u) to m(15),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(26),
                ) * m(0),
                "test 3"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(22),
                    mapOf(x to 3u) to m(26),
                    mapOf(x to 0u, y to 1u) to m(13),
                    mapOf(x to 1u) to m(15),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(26),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(22),
                    mapOf(x to 3u) to m(26),
                    mapOf(x to 0u, y to 1u) to m(13),
                    mapOf(x to 1u) to m(15),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(26),
                ) * m(1),
                "test 4"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_plus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-22, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                Rational(-3) + LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(5, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-3, 1),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                Rational(-3) + LabeledPolynomial(
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 2"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 1),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                Rational(-3) + LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 3"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ),
                Rational(-3) + LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ),
                "test 4"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-22, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                Rational(0) + LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-22, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 5"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                Rational(0) + LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 6"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                Rational(0) + LabeledPolynomial(
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 7"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_minus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(22, 9),
                    mapOf(x to 3u) to Rational(8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(8, 7),
                ),
                Rational(3) - LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(5, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(3, 1),
                    mapOf(x to 3u) to Rational(8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(8, 7),
                ),
                Rational(3) - LabeledPolynomial(
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 2"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 1),
                    mapOf(x to 3u) to Rational(8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(8, 7),
                ),
                Rational(3) - LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 3"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ),
                Rational(3) - LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(27, 9),
                    mapOf(x to 3u) to Rational(0),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                ),
                "test 4"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(22, 9),
                    mapOf(x to 3u) to Rational(8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(8, 7),
                ),
                Rational(0) - LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-22, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 5"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 9),
                    mapOf(x to 3u) to Rational(8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(8, 7),
                ),
                Rational(0) - LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0, 9),
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 6"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 3u) to Rational(8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(8, 7),
                ),
                Rational(0) - LabeledPolynomial(
                    mapOf(x to 3u) to Rational(-8, 9),
                    mapOf(x to 0u, y to 4u) to Rational(-8, 7),
                ),
                "test 7"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_times() {
        IntModuloRing(35).labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(34),
                    mapOf(x to 3u) to m(2),
                    mapOf(x to 0u, y to 1u) to m(1),
                    mapOf(x to 1u) to m(20),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(2),
                ),
                m(27) * LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(22),
                    mapOf(x to 3u) to m(26),
                    mapOf(x to 0u, y to 1u) to m(13),
                    mapOf(x to 1u) to m(15),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(26),
                ),
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(0),
                    mapOf(x to 3u) to m(0),
                    mapOf(x to 0u, y to 1u) to m(0),
                    mapOf(x to 1u) to m(0),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(0),
                ),
                m(15) * LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(7),
                    mapOf(x to 3u) to m(0),
                    mapOf(x to 0u, y to 1u) to m(49),
                    mapOf(x to 1u) to m(21),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(14),
                ),
                "test 2"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(0),
                    mapOf(x to 3u) to m(0),
                    mapOf(x to 0u, y to 1u) to m(0),
                    mapOf(x to 1u) to m(0),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(0),
                ),
                m(0) * LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(22),
                    mapOf(x to 3u) to m(26),
                    mapOf(x to 0u, y to 1u) to m(13),
                    mapOf(x to 1u) to m(15),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(26),
                ),
                "test 3"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(22),
                    mapOf(x to 3u) to m(26),
                    mapOf(x to 0u, y to 1u) to m(13),
                    mapOf(x to 1u) to m(15),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(26),
                ),
                m(1) * LabeledPolynomial(
                    mapOf<Symbol, UInt>() to m(22),
                    mapOf(x to 3u) to m(26),
                    mapOf(x to 0u, y to 1u) to m(13),
                    mapOf(x to 1u) to m(15),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(26),
                ),
                "test 4"
            )
        }
    }
    @Test
    fun test_Variable_unaryPlus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(1),
                ),
                +x
            )
        }
    }
    @Test
    fun test_Variable_unaryMinus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(-1),
                ),
                -x
            )
        }
    }
    @Test
    fun test_Variable_Variable_plus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(1),
                    mapOf(y to 1u) to Rational(1),
                ),
                x + y,
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(2),
                ),
                x + x,
                "test 2"
            )
        }
    }
    @Test
    fun test_Variable_Variable_minus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(1),
                    mapOf(y to 1u) to Rational(-1),
                ),
                x - y,
                "test 1"
            )
            assertSame(
                zero,
                x - x,
                "test 2"
            )
        }
    }
    @Test
    fun test_Variable_Variable_times() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u, y to 1u) to Rational(1),
                ),
                x * y,
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 2u) to Rational(1),
                ),
                x * x,
                "test 2"
            )
        }
    }
    @Test
    fun test_Variable_Polynomial_plus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(7, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                x + LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(6, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                y + LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                "test 2"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                    mapOf(iota to 1u) to Rational(1),
                ),
                iota + LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                "test 3"
            )
        }
    }
    @Test
    fun test_Variable_Polynomial_minus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(16, 4),
                    mapOf(x to 1u) to Rational(-1, 3),
                    mapOf(x to 2u) to Rational(-3, 8),
                    mapOf(y to 1u) to Rational(1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(-6, 5),
                    mapOf(y to 2u) to Rational(13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(-13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(-11, 8),
                ),
                x - LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(16, 4),
                    mapOf(x to 1u) to Rational(-4, 3),
                    mapOf(x to 2u) to Rational(-3, 8),
                    mapOf(y to 1u) to Rational(8, 7),
                    mapOf(x to 1u, y to 1u) to Rational(15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(-6, 5),
                    mapOf(y to 2u) to Rational(13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(-13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(-11, 8),
                ),
                y - LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                "test 2"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(16, 4),
                    mapOf(x to 1u) to Rational(-4, 3),
                    mapOf(x to 2u) to Rational(-3, 8),
                    mapOf(y to 1u) to Rational(1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(-6, 5),
                    mapOf(y to 2u) to Rational(13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(-13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(-11, 8),
                    mapOf(iota to 1u) to Rational(1),
                ),
                iota - LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                "test 3"
            )
        }
    }
    @Test
    fun test_Variable_Polynomial_times() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(-16, 4),
                    mapOf(x to 2u) to Rational(4, 3),
                    mapOf(x to 3u) to Rational(3, 8),
                    mapOf(x to 1u, y to 1u) to Rational(-1, 7),
                    mapOf(x to 2u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 3u, y to 1u) to Rational(6, 5),
                    mapOf(x to 1u, y to 2u) to Rational(-13, 3),
                    mapOf(x to 2u, y to 2u) to Rational(13, 4),
                    mapOf(x to 3u, y to 2u) to Rational(11, 8),
                ),
                x * LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(y to 1u) to Rational(-16, 4),
                    mapOf(x to 1u, y to 1u) to Rational(4, 3),
                    mapOf(x to 2u, y to 1u) to Rational(3, 8),
                    mapOf(y to 2u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 2u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 2u) to Rational(6, 5),
                    mapOf(y to 3u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 3u) to Rational(13, 4),
                    mapOf(x to 2u, y to 3u) to Rational(11, 8),
                ),
                y * LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                "test 2"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(iota to 1u) to Rational(-16, 4),
                    mapOf(x to 1u, iota to 1u) to Rational(4, 3),
                    mapOf(x to 2u, iota to 1u) to Rational(3, 8),
                    mapOf(y to 1u, iota to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u, iota to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u, iota to 1u) to Rational(6, 5),
                    mapOf(y to 2u, iota to 1u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u, iota to 1u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u, iota to 1u) to Rational(11, 8),
                ),
                iota * LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                "test 3"
            )
        }
    }
    @Test
    fun test_Polynomial_Variable_plus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(7, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ) + x,
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(6, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ) + y,
                "test 2"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                    mapOf(iota to 1u) to Rational(1),
                ),
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ) + iota,
                "test 3"
            )
        }
    }
    @Test
    fun test_Polynomial_Variable_minus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(1, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ) - x,
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-8, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ),
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ) - y,
                "test 2"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                    mapOf(iota to 1u) to Rational(-1),
                ),
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ) - iota,
                "test 3"
            )
        }
    }
    @Test
    fun test_Polynomial_Variable_times() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(x to 1u) to Rational(-16, 4),
                    mapOf(x to 2u) to Rational(4, 3),
                    mapOf(x to 3u) to Rational(3, 8),
                    mapOf(x to 1u, y to 1u) to Rational(-1, 7),
                    mapOf(x to 2u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 3u, y to 1u) to Rational(6, 5),
                    mapOf(x to 1u, y to 2u) to Rational(-13, 3),
                    mapOf(x to 2u, y to 2u) to Rational(13, 4),
                    mapOf(x to 3u, y to 2u) to Rational(11, 8),
                ),
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ) * x,
                "test 1"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(y to 1u) to Rational(-16, 4),
                    mapOf(x to 1u, y to 1u) to Rational(4, 3),
                    mapOf(x to 2u, y to 1u) to Rational(3, 8),
                    mapOf(y to 2u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 2u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 2u) to Rational(6, 5),
                    mapOf(y to 3u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 3u) to Rational(13, 4),
                    mapOf(x to 2u, y to 3u) to Rational(11, 8),
                ),
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ) * y,
                "test 2"
            )
            assertEquals(
                LabeledPolynomialAsIs(
                    mapOf(iota to 1u) to Rational(-16, 4),
                    mapOf(x to 1u, iota to 1u) to Rational(4, 3),
                    mapOf(x to 2u, iota to 1u) to Rational(3, 8),
                    mapOf(y to 1u, iota to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u, iota to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u, iota to 1u) to Rational(6, 5),
                    mapOf(y to 2u, iota to 1u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u, iota to 1u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u, iota to 1u) to Rational(11, 8),
                ),
                LabeledPolynomialAsIs(
                    mapOf<Symbol, UInt>() to Rational(-16, 4),
                    mapOf(x to 1u) to Rational(4, 3),
                    mapOf(x to 2u) to Rational(3, 8),
                    mapOf(y to 1u) to Rational(-1, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-15, 3),
                    mapOf(x to 2u, y to 1u) to Rational(6, 5),
                    mapOf(y to 2u) to Rational(-13, 3),
                    mapOf(x to 1u, y to 2u) to Rational(13, 4),
                    mapOf(x to 2u, y to 2u) to Rational(11, 8),
                ) * iota,
                "test 3"
            )
        }
    }
    @Test
    fun test_Polynomial_unaryMinus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf(x to 5u) to Rational(-5, 9),
                    mapOf<Symbol, UInt>() to Rational(8, 9),
                    mapOf(iota to 13u) to Rational(8, 7),
                ),
                -LabeledPolynomial(
                    mapOf(x to 5u) to Rational(5, 9),
                    mapOf<Symbol, UInt>() to Rational(-8, 9),
                    mapOf(iota to 13u) to Rational(-8, 7),
                ),
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf(x to 5u) to Rational(-5, 9),
                    mapOf<Symbol, UInt>() to Rational(8, 9),
                    mapOf(iota to 13u) to Rational(8, 7),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                    mapOf(x to 5u) to Rational(0),
                ),
                -LabeledPolynomial(
                    mapOf(x to 5u) to Rational(5, 9),
                    mapOf<Symbol, UInt>() to Rational(-8, 9),
                    mapOf(iota to 13u) to Rational(-8, 7),
                    mapOf(x to 0u, y to 4u) to Rational(0),
                    mapOf(x to 5u) to Rational(0),
                ),
                "test 2"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_plus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-17, 2),
                    mapOf(x to 1u) to Rational(-1, 3),
                    mapOf(x to 2u) to Rational(-25, 21),
                    mapOf(x to 0u, y to 1u) to Rational(146, 63),
                    mapOf(x to 1u, y to 1u) to Rational(-3, 5),
                    mapOf(x to 2u, y to 1u) to Rational(61, 15),
                    mapOf(x to 0u, y to 2u) to Rational(157, 63),
                    mapOf(x to 1u, y to 2u) to Rational(-55, 21),
                    mapOf(x to 2u, y to 2u) to Rational(11, 24),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(6, 4),
                    mapOf(x to 1u) to Rational(-2, 6),
                    mapOf(x to 2u) to Rational(10, 6),
                    mapOf(x to 0u, y to 1u) to Rational(17, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-7, 7),
                    mapOf(x to 2u, y to 1u) to Rational(12, 5),
                    mapOf(x to 0u, y to 2u) to Rational(12, 7),
                    mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                    mapOf(x to 2u, y to 2u) to Rational(9, 8),
                ) + LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-20, 2),
                    mapOf(x to 1u) to Rational(0, 9),
                    mapOf(x to 2u) to Rational(-20, 7),
                    mapOf(x to 0u, y to 1u) to Rational(-1, 9),
                    mapOf(x to 1u, y to 1u) to Rational(2, 5),
                    mapOf(x to 2u, y to 1u) to Rational(10, 6),
                    mapOf(x to 0u, y to 2u) to Rational(7, 9),
                    mapOf(x to 1u, y to 2u) to Rational(5, 7),
                    mapOf(x to 2u, y to 2u) to Rational(-2, 3),
                ),
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-17, 2),
                    mapOf(x to 1u) to Rational(-1, 3),
                    mapOf(x to 2u) to Rational(-25, 21),
                    mapOf(x to 0u, y to 1u) to Rational(-1, 9),
                    mapOf(x to 1u, y to 1u) to Rational(2, 5),
                    mapOf(x to 2u, y to 1u) to Rational(10, 6),
                    mapOf(x to 0u, y to 2u) to Rational(157, 63),
                    mapOf(x to 1u, y to 2u) to Rational(-55, 21),
                    mapOf(x to 2u, y to 2u) to Rational(11, 24),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(6, 4),
                    mapOf(x to 1u) to Rational(-2, 6),
                    mapOf(x to 2u) to Rational(10, 6),
                    mapOf(x to 0u, y to 2u) to Rational(12, 7),
                    mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                    mapOf(x to 2u, y to 2u) to Rational(9, 8),
                ) + LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-20, 2),
                    mapOf(x to 1u) to Rational(0, 9),
                    mapOf(x to 2u) to Rational(-20, 7),
                    mapOf(x to 0u, y to 1u) to Rational(-1, 9),
                    mapOf(x to 1u, y to 1u) to Rational(2, 5),
                    mapOf(x to 2u, y to 1u) to Rational(10, 6),
                    mapOf(x to 0u, y to 2u) to Rational(7, 9),
                    mapOf(x to 1u, y to 2u) to Rational(5, 7),
                    mapOf(x to 2u, y to 2u) to Rational(-2, 3),
                ),
                "test 2"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-17, 2),
                    mapOf(x to 1u) to Rational(-1, 3),
                    mapOf(x to 2u) to Rational(-25, 21),
                    mapOf(x to 0u, y to 1u) to Rational(-1, 9),
                    mapOf(x to 1u, y to 1u) to Rational(2, 5),
                    mapOf(x to 2u, y to 1u) to Rational(10, 6),
                    mapOf(x to 0u, y to 2u) to Rational(12, 7),
                    mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                    mapOf(x to 2u, y to 2u) to Rational(9, 8),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(6, 4),
                    mapOf(x to 1u) to Rational(-2, 6),
                    mapOf(x to 2u) to Rational(10, 6),
                    mapOf(x to 0u, y to 2u) to Rational(12, 7),
                    mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                    mapOf(x to 2u, y to 2u) to Rational(9, 8),
                ) + LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-20, 2),
                    mapOf(x to 1u) to Rational(0, 9),
                    mapOf(x to 2u) to Rational(-20, 7),
                    mapOf(x to 0u, y to 1u) to Rational(-1, 9),
                    mapOf(x to 1u, y to 1u) to Rational(2, 5),
                    mapOf(x to 2u, y to 1u) to Rational(10, 6),
                    mapOf(x to 0u, y to 2u) to Rational(0),
                    mapOf(x to 1u, y to 2u) to Rational(0),
                    mapOf(x to 2u, y to 2u) to Rational(0),
                ),
                "test 3"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 1u) to Rational(0),
                    mapOf(x to 2u) to Rational(0),
                    mapOf(x to 0u, y to 1u) to Rational(0),
                    mapOf(x to 1u, y to 1u) to Rational(0),
                    mapOf(x to 2u, y to 1u) to Rational(0),
                    mapOf(x to 0u, y to 2u) to Rational(0),
                    mapOf(x to 1u, y to 2u) to Rational(0),
                    mapOf(x to 2u, y to 2u) to Rational(0),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(6, 4),
                    mapOf(x to 1u) to Rational(-2, 6),
                    mapOf(x to 2u) to Rational(10, 6),
                    mapOf(x to 0u, y to 1u) to Rational(17, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-7, 7),
                    mapOf(x to 2u, y to 1u) to Rational(12, 5),
                    mapOf(x to 0u, y to 2u) to Rational(12, 7),
                    mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                    mapOf(x to 2u, y to 2u) to Rational(9, 8),
                ) + LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-6, 4),
                    mapOf(x to 1u) to Rational(2, 6),
                    mapOf(x to 2u) to Rational(-10, 6),
                    mapOf(x to 0u, y to 1u) to Rational(-17, 7),
                    mapOf(x to 1u, y to 1u) to Rational(7, 7),
                    mapOf(x to 2u, y to 1u) to Rational(-12, 5),
                    mapOf(x to 0u, y to 2u) to Rational(-12, 7),
                    mapOf(x to 1u, y to 2u) to Rational(10, 3),
                    mapOf(x to 2u, y to 2u) to Rational(-9, 8),
                ),
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_minus() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-17, 2),
                    mapOf(x to 1u) to Rational(-1, 3),
                    mapOf(x to 2u) to Rational(-25, 21),
                    mapOf(x to 0u, y to 1u) to Rational(146, 63),
                    mapOf(x to 1u, y to 1u) to Rational(-3, 5),
                    mapOf(x to 2u, y to 1u) to Rational(61, 15),
                    mapOf(x to 0u, y to 2u) to Rational(157, 63),
                    mapOf(x to 1u, y to 2u) to Rational(-55, 21),
                    mapOf(x to 2u, y to 2u) to Rational(11, 24),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(6, 4),
                    mapOf(x to 1u) to Rational(-2, 6),
                    mapOf(x to 2u) to Rational(10, 6),
                    mapOf(x to 0u, y to 1u) to Rational(17, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-7, 7),
                    mapOf(x to 2u, y to 1u) to Rational(12, 5),
                    mapOf(x to 0u, y to 2u) to Rational(12, 7),
                    mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                    mapOf(x to 2u, y to 2u) to Rational(9, 8),
                ) - LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(20, 2),
                    mapOf(x to 1u) to Rational(0, 9),
                    mapOf(x to 2u) to Rational(20, 7),
                    mapOf(x to 0u, y to 1u) to Rational(1, 9),
                    mapOf(x to 1u, y to 1u) to Rational(-2, 5),
                    mapOf(x to 2u, y to 1u) to Rational(-10, 6),
                    mapOf(x to 0u, y to 2u) to Rational(-7, 9),
                    mapOf(x to 1u, y to 2u) to Rational(-5, 7),
                    mapOf(x to 2u, y to 2u) to Rational(2, 3),
                ),
                "test 1"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-17, 2),
                    mapOf(x to 1u) to Rational(-1, 3),
                    mapOf(x to 2u) to Rational(-25, 21),
                    mapOf(x to 0u, y to 1u) to Rational(-1, 9),
                    mapOf(x to 1u, y to 1u) to Rational(2, 5),
                    mapOf(x to 2u, y to 1u) to Rational(10, 6),
                    mapOf(x to 0u, y to 2u) to Rational(157, 63),
                    mapOf(x to 1u, y to 2u) to Rational(-55, 21),
                    mapOf(x to 2u, y to 2u) to Rational(11, 24),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(6, 4),
                    mapOf(x to 1u) to Rational(-2, 6),
                    mapOf(x to 2u) to Rational(10, 6),
                    mapOf(x to 0u, y to 2u) to Rational(12, 7),
                    mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                    mapOf(x to 2u, y to 2u) to Rational(9, 8),
                ) - LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(20, 2),
                    mapOf(x to 1u) to Rational(0, 9),
                    mapOf(x to 2u) to Rational(20, 7),
                    mapOf(x to 0u, y to 1u) to Rational(1, 9),
                    mapOf(x to 1u, y to 1u) to Rational(-2, 5),
                    mapOf(x to 2u, y to 1u) to Rational(-10, 6),
                    mapOf(x to 0u, y to 2u) to Rational(-7, 9),
                    mapOf(x to 1u, y to 2u) to Rational(-5, 7),
                    mapOf(x to 2u, y to 2u) to Rational(2, 3),
                ),
                "test 2"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(-17, 2),
                    mapOf(x to 1u) to Rational(-1, 3),
                    mapOf(x to 2u) to Rational(-25, 21),
                    mapOf(x to 0u, y to 1u) to Rational(-1, 9),
                    mapOf(x to 1u, y to 1u) to Rational(2, 5),
                    mapOf(x to 2u, y to 1u) to Rational(10, 6),
                    mapOf(x to 0u, y to 2u) to Rational(12, 7),
                    mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                    mapOf(x to 2u, y to 2u) to Rational(9, 8),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(6, 4),
                    mapOf(x to 1u) to Rational(-2, 6),
                    mapOf(x to 2u) to Rational(10, 6),
                    mapOf(x to 0u, y to 2u) to Rational(12, 7),
                    mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                    mapOf(x to 2u, y to 2u) to Rational(9, 8),
                ) - LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(20, 2),
                    mapOf(x to 1u) to Rational(0, 9),
                    mapOf(x to 2u) to Rational(20, 7),
                    mapOf(x to 0u, y to 1u) to Rational(1, 9),
                    mapOf(x to 1u, y to 1u) to Rational(-2, 5),
                    mapOf(x to 2u, y to 1u) to Rational(-10, 6),
                    mapOf(x to 0u, y to 2u) to Rational(0),
                    mapOf(x to 1u, y to 2u) to Rational(0),
                    mapOf(x to 2u, y to 2u) to Rational(0),
                ),
                "test 3"
            )
            assertEquals(
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(0),
                    mapOf(x to 1u) to Rational(0),
                    mapOf(x to 2u) to Rational(0),
                    mapOf(x to 0u, y to 1u) to Rational(0),
                    mapOf(x to 1u, y to 1u) to Rational(0),
                    mapOf(x to 2u, y to 1u) to Rational(0),
                    mapOf(x to 0u, y to 2u) to Rational(0),
                    mapOf(x to 1u, y to 2u) to Rational(0),
                    mapOf(x to 2u, y to 2u) to Rational(0),
                ),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(6, 4),
                    mapOf(x to 1u) to Rational(-2, 6),
                    mapOf(x to 2u) to Rational(10, 6),
                    mapOf(x to 0u, y to 1u) to Rational(17, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-7, 7),
                    mapOf(x to 2u, y to 1u) to Rational(12, 5),
                    mapOf(x to 0u, y to 2u) to Rational(12, 7),
                    mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                    mapOf(x to 2u, y to 2u) to Rational(9, 8),
                ) - LabeledPolynomial(
                    mapOf<Symbol, UInt>() to Rational(6, 4),
                    mapOf(x to 1u) to Rational(-2, 6),
                    mapOf(x to 2u) to Rational(10, 6),
                    mapOf(x to 0u, y to 1u) to Rational(17, 7),
                    mapOf(x to 1u, y to 1u) to Rational(-7, 7),
                    mapOf(x to 2u, y to 1u) to Rational(12, 5),
                    mapOf(x to 0u, y to 2u) to Rational(12, 7),
                    mapOf(x to 1u, y to 2u) to Rational(-10, 3),
                    mapOf(x to 2u, y to 2u) to Rational(9, 8),
                ),
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_times() {
        IntModuloRing(35).labeledPolynomialSpace {
            // (p + q + r) * (p^2 + q^2 + r^2 - pq - pr - qr) = p^3 + q^3 + r^3 - 3pqr
            assertEquals(
                LabeledPolynomial(
                    mapOf(x to 3u) to m(1),
                    mapOf(x to 0u, y to 3u) to m(1),
                    mapOf(x to 0u, y to 0u, z to 3u) to m(1),
                    mapOf(x to 1u, y to 2u) to m(0),
                    mapOf(x to 0u, y to 1u, z to 2u) to m(0),
                    mapOf(x to 2u, y to 0u, z to 1u) to m(0),
                    mapOf(x to 1u, y to 0u, z to 2u) to m(0),
                    mapOf(x to 2u, y to 1u) to m(0),
                    mapOf(x to 0u, y to 2u, z to 1u) to m(0),
                    mapOf(x to 1u, y to 1u, z to 1u) to m(-3),
                ),
                LabeledPolynomial(
                    mapOf(x to 1u) to m(1),
                    mapOf(x to 0u, y to 1u) to m(1),
                    mapOf(x to 0u, y to 0u, z to 1u) to m(1),
                ) * LabeledPolynomial(
                    mapOf(x to 2u) to m(1),
                    mapOf(x to 0u, y to 2u) to m(1),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(1),
                    mapOf(x to 1u, y to 1u) to m(-1),
                    mapOf(x to 0u, y to 1u, z to 1u) to m(-1),
                    mapOf(x to 1u, y to 0u, z to 1u) to m(-1),
                ),
                "test 1"
            )
            // Spoiler: 5 * 7 = 0
            assertEquals(
                LabeledPolynomial(
                    mapOf(x to 2u) to m(0),
                    mapOf(x to 0u, y to 2u) to m(0),
                    mapOf(x to 0u, y to 0u, z to 2u) to m(0),
                    mapOf(x to 1u, y to 1u) to m(0),
                    mapOf(x to 0u, y to 1u, z to 1u) to m(0),
                    mapOf(x to 1u, y to 0u, z to 1u) to m(0),
                ),
                LabeledPolynomial(
                    mapOf(x to 1u) to m(5),
                    mapOf(x to 0u, y to 1u) to m(-25),
                    mapOf(x to 0u, y to 0u, z to 1u) to m(10),
                ) * LabeledPolynomial(
                    mapOf(x to 1u) to m(21),
                    mapOf(x to 0u, y to 1u) to m(14),
                    mapOf(x to 0u, y to 0u, z to 1u) to m(-7),
                ),
                "test 2"
            )
        }
    }
    @Test
    fun test_degree() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                -1,
                LabeledPolynomial().degree,
                "test 1"
            )
            assertEquals(
                0,
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o
                ).degree,
                "test 2"
            )
            assertEquals(
                6,
                LabeledPolynomial(
                    mapOf(x to 1u, y to 2u, z to 3u) to o
                ).degree,
                "test 3"
            )
            assertEquals(
                4,
                LabeledPolynomial(
                    mapOf(x to 0u, y to 1u, z to 2u, t to 1u, s to 0u) to o
                ).degree,
                "test 4"
            )
            assertEquals(
                3,
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o,
                    mapOf(x to 0u, y to 1u) to o,
                    mapOf(x to 2u, y to 0u, z to 1u) to o,
                ).degree,
                "test 5"
            )
            assertEquals(
                4,
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o,
                    mapOf(x to 0u, y to 1u) to o,
                    mapOf(x to 2u, y to 0u, z to 1u) to o,
                    mapOf(x to 0u, y to 0u, z to 0u, t to 4u) to o,
                ).degree,
                "test 6"
            )
        }
    }
    @Test
    fun test_degrees() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                mapOf(),
                LabeledPolynomial().degrees,
                "test 1"
            )
            assertEquals(
                mapOf(),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o
                ).degrees,
                "test 2"
            )
            assertEquals(
                mapOf(x to 1u, y to 2u, z to 3u),
                LabeledPolynomial(
                    mapOf(x to 1u, y to 2u, z to 3u) to o
                ).degrees,
                "test 3"
            )
            assertEquals(
                mapOf(y to 1u, z to 2u, t to 1u),
                LabeledPolynomial(
                    mapOf(x to 0u, y to 1u, z to 2u, t to 1u, s to 0u) to o
                ).degrees,
                "test 4"
            )
            assertEquals(
                mapOf(x to 2u, y to 1u, z to 1u),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o,
                    mapOf(x to 0u, y to 1u) to o,
                    mapOf(x to 2u, y to 0u, z to 1u) to o,
                ).degrees,
                "test 5"
            )
            assertEquals(
                mapOf(x to 2u, y to 2u, z to 2u, t to 4u),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o,
                    mapOf(x to 1u, y to 2u) to o,
                    mapOf(x to 0u, y to 1u, z to 2u) to o,
                    mapOf(x to 2u, y to 0u, z to 1u) to o,
                    mapOf(x to 0u, y to 0u, z to 0u, t to 4u) to o,
                ).degrees,
                "test 6"
            )
        }
    }
    @Test
    fun test_degreeBy() {
        RationalField.labeledPolynomialSpace {
            fun LabeledPolynomial<Rational>.collectDegrees(variables: Set<Symbol> = this.variables + iota): Map<Symbol, UInt> = variables.associateWith { degreeBy(it) }
            assertEquals(
                mapOf(iota to 0u),
                LabeledPolynomial().collectDegrees(),
                "test 1"
            )
            assertEquals(
                mapOf(iota to 0u),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o
                ).collectDegrees(),
                "test 2"
            )
            assertEquals(
                mapOf(x to 1u, y to 2u, z to 3u, iota to 0u),
                LabeledPolynomial(
                    mapOf(x to 1u, y to 2u, z to 3u) to o
                ).collectDegrees(),
                "test 3"
            )
            assertEquals(
                mapOf(y to 1u, z to 2u, t to 1u, iota to 0u),
                LabeledPolynomial(
                    mapOf(x to 0u, y to 1u, z to 2u, t to 1u, s to 0u) to o
                ).collectDegrees(),
                "test 4"
            )
            assertEquals(
                mapOf(x to 2u, y to 1u, z to 1u, iota to 0u),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o,
                    mapOf(x to 0u, y to 1u) to o,
                    mapOf(x to 2u, y to 0u, z to 1u) to o,
                ).collectDegrees(),
                "test 5"
            )
            assertEquals(
                mapOf(x to 2u, y to 2u, z to 2u, t to 4u, iota to 0u),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o,
                    mapOf(x to 1u, y to 2u) to o,
                    mapOf(x to 0u, y to 1u, z to 2u) to o,
                    mapOf(x to 2u, y to 0u, z to 1u) to o,
                    mapOf(x to 0u, y to 0u, z to 0u, t to 4u) to o,
                ).collectDegrees(),
                "test 6"
            )
        }
    }
    @Test
    fun test_degreeBy_Collection() {
        RationalField.labeledPolynomialSpace {
            fun LabeledPolynomial<Rational>.checkDegreeBy(message: String? = null) {
                val variables = variables.toList() + iota
                val variablesCollectionSequence: Sequence<List<Symbol>> = sequence {
                    val appearances = MutableList(variables.size) { 0 }
                    while (true) {
                        yield(
                            buildList {
                                for ((variableIndex, count) in appearances.withIndex()) repeat(count) { add(variables[variableIndex]) }
                            }
                        )
                        val indexChange = appearances.indexOfFirst { it < 4 }
                        if (indexChange == -1) break
                        appearances[indexChange] += 1
                        for (index in 0 until indexChange) appearances[index] = 0
                    }
                }
                for (variablesCollection in variablesCollectionSequence) {
                    val expected = coefficients.keys.maxOfOrNull { degs -> degs.filterKeys { it in variablesCollection }.values.sum() } ?: 0u
                    val actual = degreeBy(variablesCollection)
                    if (actual != expected)
                        fail("${message ?: ""} Incorrect answer for variable collection $variablesCollection: expected $expected, actual $actual")
                }
            }
            LabeledPolynomial().checkDegreeBy("test 1")
            LabeledPolynomial(
                mapOf<Symbol, UInt>() to o
            ).checkDegreeBy("test 2")
            LabeledPolynomial(
                mapOf(x to 1u, y to 2u, z to 3u) to o
            ).checkDegreeBy("test 3")
            LabeledPolynomial(
                mapOf(x to 0u, y to 1u, z to 2u, t to 1u, s to 0u) to o
            ).checkDegreeBy("test 4")
            LabeledPolynomial(
                mapOf<Symbol, UInt>() to o,
                mapOf(x to 0u, y to 1u) to o,
                mapOf(x to 2u, y to 0u, z to 1u) to o,
            ).checkDegreeBy("test 5")
            LabeledPolynomial(
                mapOf<Symbol, UInt>() to o,
                mapOf(x to 1u, y to 2u) to o,
                mapOf(x to 0u, y to 1u, z to 2u) to o,
                mapOf(x to 2u, y to 0u, z to 1u) to o,
                mapOf(x to 0u, y to 0u, z to 0u, t to 4u) to o,
            ).checkDegreeBy("test 6")
        }
    }
    @Test
    fun test_variables() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                setOf(),
                LabeledPolynomial().variables,
                "test 1"
            )
            assertEquals(
                setOf(),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o
                ).variables,
                "test 2"
            )
            assertEquals(
                setOf(x, y, z),
                LabeledPolynomial(
                    mapOf(x to 1u, y to 2u, z to 3u) to o
                ).variables,
                "test 3"
            )
            assertEquals(
                setOf(y, z, t),
                LabeledPolynomial(
                    mapOf(x to 0u, y to 1u, z to 2u, t to 1u, s to 0u) to o
                ).variables,
                "test 4"
            )
            assertEquals(
                setOf(x, y, z),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o,
                    mapOf(x to 0u, y to 1u) to o,
                    mapOf(x to 2u, y to 0u, z to 1u) to o,
                ).variables,
                "test 5"
            )
            assertEquals(
                setOf(x, y, z, t),
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o,
                    mapOf(x to 1u, y to 2u) to o,
                    mapOf(x to 0u, y to 1u, z to 2u) to o,
                    mapOf(x to 2u, y to 0u, z to 1u) to o,
                    mapOf(x to 0u, y to 0u, z to 0u, t to 4u) to o,
                ).variables,
                "test 6"
            )
        }
    }
    @Test
    fun test_countOfVariables() {
        RationalField.labeledPolynomialSpace {
            assertEquals(
                0,
                LabeledPolynomial().countOfVariables,
                "test 1"
            )
            assertEquals(
                0,
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o
                ).countOfVariables,
                "test 2"
            )
            assertEquals(
                3,
                LabeledPolynomial(
                    mapOf(x to 1u, y to 2u, z to 3u) to o
                ).countOfVariables,
                "test 3"
            )
            assertEquals(
                3,
                LabeledPolynomial(
                    mapOf(x to 0u, y to 1u, z to 2u, t to 1u, s to 0u) to o
                ).countOfVariables,
                "test 4"
            )
            assertEquals(
                3,
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o,
                    mapOf(x to 0u, y to 1u) to o,
                    mapOf(x to 2u, y to 0u, z to 1u) to o,
                ).countOfVariables,
                "test 5"
            )
            assertEquals(
                4,
                LabeledPolynomial(
                    mapOf<Symbol, UInt>() to o,
                    mapOf(x to 1u, y to 2u) to o,
                    mapOf(x to 0u, y to 1u, z to 2u) to o,
                    mapOf(x to 2u, y to 0u, z to 1u) to o,
                    mapOf(x to 0u, y to 0u, z to 0u, t to 4u) to o,
                ).countOfVariables,
                "test 6"
            )
        }
    }
    @Test
    fun test_RF_countOfVariables() {
        RationalField.labeledRationalFunctionSpace {
            assertEquals(
                0,
                LabeledRationalFunction(
                    LabeledPolynomial()
                ).countOfVariables,
                "test 1"
            )
            assertEquals(
                0,
                LabeledRationalFunction(
                    LabeledPolynomial(),
                    LabeledPolynomial()
                ).countOfVariables,
                "test 2"
            )
            assertEquals(
                0,
                LabeledRationalFunction(
                    LabeledPolynomial(
                        mapOf<Symbol, UInt>() to o
                    )
                ).countOfVariables,
                "test 3"
            )
            assertEquals(
                3,
                LabeledRationalFunction(
                    LabeledPolynomial(
                        mapOf(x to 1u, y to 2u, z to 3u) to o
                    )
                ).countOfVariables,
                "test 4"
            )
            assertEquals(
                3,
                LabeledRationalFunction(
                    LabeledPolynomial(
                        mapOf(x to 0u, y to 1u, z to 0u, t to 1u) to o
                    ),
                    LabeledPolynomial(
                        mapOf(x to 0u, y to 0u, z to 2u) to o
                    )
                ).countOfVariables,
                "test 5"
            )
            assertEquals(
                3,
                LabeledRationalFunction(
                    LabeledPolynomial(
                        mapOf<Symbol, UInt>() to o,
                        mapOf(x to 0u, y to 1u) to o,
                        mapOf(x to 2u, y to 0u, z to 1u) to o,
                    )
                ).countOfVariables,
                "test 6"
            )
            assertEquals(
                4,
                LabeledRationalFunction(
                    LabeledPolynomial(
                        mapOf<Symbol, UInt>() to o,
                        mapOf(x to 1u, y to 2u) to o,
                        mapOf(x to 2u, y to 0u, z to 1u) to o,
                    ), LabeledPolynomial(
                        mapOf(x to 0u, y to 1u, z to 2u) to o,
                        mapOf(x to 0u, y to 0u, z to 0u, t to 4u) to o,
                    )
                ).countOfVariables,
                "test 7"
            )
        }
    }
}