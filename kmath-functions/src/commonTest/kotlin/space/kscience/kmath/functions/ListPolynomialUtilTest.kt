/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.test.misc.Rational
import space.kscience.kmath.test.misc.RationalField
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class ListPolynomialUtilTest {
    @Test
    fun test_substitute_Double() {
        assertEquals(
            0.0,
            ListPolynomial(1.0, -2.0, 1.0).substitute(1.0),
            0.001,
            "test 1"
        )
        assertEquals(
            1.1931904761904761,
            ListPolynomial(0.625, 2.6666666666666665, 0.5714285714285714, 1.5).substitute(0.2),
            0.001,
            "test 2"
        )
        assertEquals(
            0.5681904761904762,
            ListPolynomial(0.0, 2.6666666666666665, 0.5714285714285714, 1.5).substitute(0.2),
            0.001,
            "test 3"
        )
        assertEquals(
            1.1811904761904761,
            ListPolynomial(0.625, 2.6666666666666665, 0.5714285714285714, 0.0).substitute(0.2),
            0.001,
            "test 4"
        )
        assertEquals(
            1.1703333333333332,
            ListPolynomial(0.625, 2.6666666666666665, 0.0, 1.5).substitute(0.2),
            0.001,
            "test 5"
        )
    }
    @Test
    fun test_substitute_Constant() {
        assertEquals(
            Rational(0),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).substitute(RationalField, Rational(1)),
            "test 1"
        )
        assertEquals(
            Rational(25057, 21000),
            ListPolynomial(Rational(5,8), Rational(8, 3), Rational(4, 7), Rational(3, 2))
                .substitute(RationalField, Rational(1, 5)),
            "test 2"
        )
        assertEquals(
            Rational(2983, 5250),
            ListPolynomial(Rational(0), Rational(8, 3), Rational(4, 7), Rational(3, 2))
                .substitute(RationalField, Rational(1, 5)),
            "test 3"
        )
        assertEquals(
            Rational(4961, 4200),
            ListPolynomial(Rational(5,8), Rational(8, 3), Rational(4, 7), Rational(0))
                .substitute(RationalField, Rational(1, 5)),
            "test 4"
        )
        assertEquals(
            Rational(3511, 3000),
            ListPolynomial(Rational(5,8), Rational(8, 3), Rational(0), Rational(3, 2))
                .substitute(RationalField, Rational(1, 5)),
            "test 5"
        )
    }
    @Test
    fun test_substitute_Polynomial() {
        assertEquals(
            ListPolynomial(Rational(0)),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).substitute(RationalField, ListPolynomial(Rational(1))),
            "test 1"
        )
        assertEquals(
            ListPolynomial(Rational(709, 378), Rational(155, 252), Rational(19, 525), Rational(2, 875)),
            ListPolynomial(Rational(1, 7), Rational(9, 4), Rational(1, 3), Rational(2, 7))
                .substitute(RationalField, ListPolynomial(Rational(6, 9), Rational(1, 5))),
            "test 2"
        )
        assertEquals(
            ListPolynomial(Rational(655, 378), Rational(155, 252), Rational(19, 525), Rational(2, 875)),
            ListPolynomial(Rational(0), Rational(9, 4), Rational(1, 3), Rational(2, 7))
                .substitute(RationalField, ListPolynomial(Rational(6, 9), Rational(1, 5))),
            "test 3"
        )
        assertEquals(
            ListPolynomial(Rational(677, 378), Rational(97, 180), Rational(1, 75), Rational(0)),
            ListPolynomial(Rational(1, 7), Rational(9, 4), Rational(1, 3), Rational(0))
                .substitute(RationalField, ListPolynomial(Rational(6, 9), Rational(1, 5))),
            "test 4"
        )
        assertEquals(
            ListPolynomial(Rational(653, 378), Rational(221, 420), Rational(4, 175), Rational(2, 875)),
            ListPolynomial(Rational(1, 7), Rational(9, 4), Rational(0), Rational(2, 7))
                .substitute(RationalField, ListPolynomial(Rational(6, 9), Rational(1, 5))),
            "test 5"
        )
        assertEquals(
            ListPolynomial(Rational(89, 54), Rational(0), Rational(0), Rational(0)),
            ListPolynomial(Rational(0), Rational(9, 4), Rational(1, 3), Rational(0))
                .substitute(RationalField, ListPolynomial(Rational(6, 9), Rational(0))),
            "test 6"
        )
    }
    @Test
    fun test_derivative() {
        assertEquals(
            ListPolynomial(Rational(-2), Rational(2)),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).derivative(RationalField),
            "test 1"
        )
        assertEquals(
            ListPolynomial(Rational(-8, 3), Rational(8, 9), Rational(15, 7), Rational(-20, 9)),
            ListPolynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).derivative(RationalField),
            "test 2"
        )
        assertEquals(
            ListPolynomial(Rational(0), Rational(8, 9), Rational(15, 7), Rational(-20, 9)),
            ListPolynomial(Rational(0), Rational(0), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).derivative(RationalField),
            "test 3"
        )
        assertEquals(
            ListPolynomial(Rational(-8, 3), Rational(8, 9), Rational(15, 7), Rational(0)),
            ListPolynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(0)).derivative(RationalField),
            "test 4"
        )
    }
    @Test
    fun test_nthDerivative() {
        assertEquals(
            ListPolynomial(Rational(-2), Rational(2)),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).nthDerivative(RationalField, 1),
            "test 1"
        )
        assertFailsWith<IllegalArgumentException>("test2") {
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).nthDerivative(RationalField, -1)
        }
        assertEquals(
            ListPolynomial(Rational(1), Rational(-2), Rational(1)),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).nthDerivative(RationalField, 0),
            "test 3"
        )
        assertEquals(
            ListPolynomial(Rational(2)),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).nthDerivative(RationalField, 2),
            "test 4"
        )
        assertEquals(
            ListPolynomial(),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).nthDerivative(RationalField, 3),
            "test 5"
        )
        assertEquals(
            ListPolynomial(),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).nthDerivative(RationalField, 4),
            "test 6"
        )
        assertEquals(
            ListPolynomial(Rational(8, 9), Rational(30, 7), Rational(-20, 3)),
            ListPolynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).nthDerivative(RationalField, 2),
            "test 7"
        )
        assertEquals(
            ListPolynomial(Rational(8, 9), Rational(30, 7), Rational(-20, 3)),
            ListPolynomial(Rational(0), Rational(0), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).nthDerivative(RationalField, 2),
            "test 8"
        )
        assertEquals(
            ListPolynomial(Rational(8, 9), Rational(30, 7), Rational(0)),
            ListPolynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(0)).nthDerivative(RationalField, 2),
            "test 9"
        )
    }
    @Test
    fun test_antiderivative() {
        assertEquals(
            ListPolynomial(Rational(0), Rational(1), Rational(-1), Rational(1, 3)),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).antiderivative(RationalField),
            "test 1"
        )
        assertEquals(
            ListPolynomial(Rational(0), Rational(1, 5), Rational(-4, 3), Rational(4, 27), Rational(5, 28), Rational(-1, 9)),
            ListPolynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).antiderivative(RationalField),
            "test 2"
        )
        assertEquals(
            ListPolynomial(Rational(0), Rational(0), Rational(0), Rational(4, 27), Rational(5, 28), Rational(-1, 9)),
            ListPolynomial(Rational(0), Rational(0), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).antiderivative(RationalField),
            "test 3"
        )
        assertEquals(
            ListPolynomial(Rational(0), Rational(1, 5), Rational(-4, 3), Rational(4, 27), Rational(5, 28), Rational(0)),
            ListPolynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(0)).antiderivative(RationalField),
            "test 4"
        )
    }
    @Test
    fun test_nthAntiderivative() {
        assertEquals(
            ListPolynomial(Rational(0), Rational(1), Rational(-1), Rational(1, 3)),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).nthAntiderivative(RationalField, 1),
            "test 1"
        )
        assertFailsWith<IllegalArgumentException>("test2") {
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).nthAntiderivative(RationalField, -1)
        }
        assertEquals(
            ListPolynomial(Rational(1), Rational(-2), Rational(1)),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).nthAntiderivative(RationalField, 0),
            "test 3"
        )
        assertEquals(
            ListPolynomial(Rational(0), Rational(0), Rational(1, 2), Rational(-1, 3), Rational(1, 12)),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).nthAntiderivative(RationalField, 2),
            "test 4"
        )
        assertEquals(
            ListPolynomial(Rational(0), Rational(0), Rational(0), Rational(1, 6), Rational(-1, 12), Rational(1, 60)),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).nthAntiderivative(RationalField, 3),
            "test 5"
        )
        assertEquals(
            ListPolynomial(Rational(0), Rational(0), Rational(0), Rational(0), Rational(1, 24), Rational(-1, 60), Rational(1, 360)),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).nthAntiderivative(RationalField, 4),
            "test 6"
        )
        assertEquals(
            ListPolynomial(Rational(0), Rational(0), Rational(1, 10), Rational(-4, 9), Rational(1, 27), Rational(1, 28), Rational(-1, 54)),
            ListPolynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).nthAntiderivative(RationalField, 2),
            "test 7"
        )
        assertEquals(
            ListPolynomial(Rational(0), Rational(0), Rational(0), Rational(0), Rational(1, 27), Rational(1, 28), Rational(-1, 54)),
            ListPolynomial(Rational(0), Rational(0), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).nthAntiderivative(RationalField, 2),
            "test 8"
        )
        assertEquals(
            ListPolynomial(Rational(0), Rational(0), Rational(1, 10), Rational(-4, 9), Rational(1, 27), Rational(1, 28), Rational(0)),
            ListPolynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(0)).nthAntiderivative(RationalField, 2),
            "test 9"
        )
    }
}