/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.functions.testUtils.Rational
import space.kscience.kmath.functions.testUtils.RationalField
import kotlin.test.Test
import kotlin.test.assertEquals


@OptIn(UnstableKMathAPI::class)
class PolynomialUtilTest {
    @Test
    fun test_Polynomial_value_Double() {
        assertEquals(
            0.0,
            Polynomial(1.0, -2.0, 1.0).value(1.0),
            0.001,
            "test 1"
        )
        assertEquals(
            0.0,
            Polynomial(1.0, -2.0, 1.0).value(1.0),
            0.001,
            "test 1"
        )
        assertEquals(
            1.1931904761904761,
            Polynomial(0.625, 2.6666666666666665, 0.5714285714285714, 1.5).value(0.2),
            0.001,
            "test 2"
        )
        assertEquals(
            0.5681904761904762,
            Polynomial(0.0, 2.6666666666666665, 0.5714285714285714, 1.5).value(0.2),
            0.001,
            "test 3"
        )
        assertEquals(
            1.1811904761904761,
            Polynomial(0.625, 2.6666666666666665, 0.5714285714285714, 0.0).value(0.2),
            0.001,
            "test 4"
        )
        assertEquals(
            1.1703333333333332,
            Polynomial(0.625, 2.6666666666666665, 0.0, 1.5).value(0.2),
            0.001,
            "test 5"
        )
    }
    @Test
    fun test_Polynomial_value_Constant() {
        assertEquals(
            Rational(0),
            Polynomial(Rational(1), Rational(-2), Rational(1)).value(RationalField, Rational(1)),
            "test 1"
        )
        assertEquals(
            Rational(25057, 21000),
            Polynomial(Rational(5, 8), Rational(8, 3), Rational(4, 7), Rational(3, 2))
                .value(RationalField, Rational(1, 5)),
            "test 2"
        )
        assertEquals(
            Rational(2983, 5250),
            Polynomial(Rational(0), Rational(8, 3), Rational(4, 7), Rational(3, 2))
                .value(RationalField, Rational(1, 5)),
            "test 3"
        )
        assertEquals(
            Rational(4961, 4200),
            Polynomial(Rational(5, 8), Rational(8, 3), Rational(4, 7), Rational(0))
                .value(RationalField, Rational(1, 5)),
            "test 4"
        )
        assertEquals(
            Rational(3511, 3000),
            Polynomial(Rational(5, 8), Rational(8, 3), Rational(0), Rational(3, 2))
                .value(RationalField, Rational(1, 5)),
            "test 5"
        )
    }
    @Test
    fun test_Polynomial_differentiate() {
        assertEquals(
            Polynomial(Rational(-2), Rational(2)),
            Polynomial(Rational(1), Rational(-2), Rational(1)).differentiate(RationalField),
            "test 1"
        )
        assertEquals(
            Polynomial(Rational(-8, 3), Rational(8, 9), Rational(15, 7), Rational(-20, 9)),
            Polynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).differentiate(RationalField),
            "test 2"
        )
        assertEquals(
            Polynomial(Rational(0), Rational(8, 9), Rational(15, 7), Rational(-20, 9)),
            Polynomial(Rational(0), Rational(0), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).differentiate(RationalField),
            "test 3"
        )
        assertEquals(
            Polynomial(Rational(-8, 3), Rational(8, 9), Rational(15, 7), Rational(0)),
            Polynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(0)).differentiate(RationalField),
            "test 4"
        )
    }
    @Test
    fun test_Polynomial_integrate() {
        assertEquals(
            Polynomial(Rational(0), Rational(1), Rational(-1), Rational(1, 3)),
            Polynomial(Rational(1), Rational(-2), Rational(1)).integrate(RationalField),
            "test 1"
        )
        assertEquals(
            Polynomial(Rational(0), Rational(1, 5), Rational(-4, 3), Rational(4, 27), Rational(5, 28), Rational(-1, 9)),
            Polynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).integrate(RationalField),
            "test 2"
        )
        assertEquals(
            Polynomial(Rational(0), Rational(0), Rational(0), Rational(4, 27), Rational(5, 28), Rational(-1, 9)),
            Polynomial(Rational(0), Rational(0), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).integrate(RationalField),
            "test 3"
        )
        assertEquals(
            Polynomial(Rational(0), Rational(1, 5), Rational(-4, 3), Rational(4, 27), Rational(5, 28), Rational(0)),
            Polynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(0)).integrate(RationalField),
            "test 4"
        )
    }
}