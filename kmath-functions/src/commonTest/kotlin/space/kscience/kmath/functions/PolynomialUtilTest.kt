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


class PolynomialUtilTest {
    @Test
    fun test_substitute_Double() {
        val polynomial = Polynomial(1.0, -2.0, 1.0)
        assertEquals(0.0, polynomial.substitute(1.0), 0.001)
    }
    @Test
    fun test_substitute_Constant() {
        assertEquals(
            Rational(0),
            Polynomial(Rational(1), Rational(-2), Rational(1)).substitute(RationalField, Rational(1)),
            "test 1"
        )
        assertEquals(
            Rational(25057, 21000),
            Polynomial(Rational(5,8), Rational(8, 3), Rational(4, 7), Rational(3, 2))
                .substitute(RationalField, Rational(1, 5)),
            "test 2"
        )
        assertEquals(
            Rational(2983, 5250),
            Polynomial(Rational(0), Rational(8, 3), Rational(4, 7), Rational(3, 2))
                .substitute(RationalField, Rational(1, 5)),
            "test 3"
        )
        assertEquals(
            Rational(4961, 4200),
            Polynomial(Rational(5,8), Rational(8, 3), Rational(4, 7), Rational(0))
                .substitute(RationalField, Rational(1, 5)),
            "test 4"
        )
        assertEquals(
            Rational(3511, 3000),
            Polynomial(Rational(5,8), Rational(8, 3), Rational(0), Rational(3, 2))
                .substitute(RationalField, Rational(1, 5)),
            "test 5"
        )
    }
    @Test
    fun test_substitute_Polynomial() {
        assertEquals(
            Polynomial(),
            Polynomial(Rational(1), Rational(-2), Rational(1)).substitute(RationalField, Polynomial(Rational(1))),
            "test 1"
        )
        assertEquals(
            Polynomial(Rational(709, 378), Rational(155, 252), Rational(19, 525), Rational(2, 875)),
            Polynomial(Rational(1, 7), Rational(9, 4), Rational(1, 3), Rational(2, 7))
                .substitute(RationalField, Polynomial(Rational(6, 9), Rational(1, 5))),
            "test 2"
        )
        assertEquals(
            Polynomial(Rational(655, 378), Rational(155, 252), Rational(19, 525), Rational(2, 875)),
            Polynomial(Rational(0), Rational(9, 4), Rational(1, 3), Rational(2, 7))
                .substitute(RationalField, Polynomial(Rational(6, 9), Rational(1, 5))),
            "test 3"
        )
        assertEquals(
            Polynomial(Rational(677, 378), Rational(97, 180), Rational(1, 75)),
            Polynomial(Rational(1, 7), Rational(9, 4), Rational(1, 3), Rational(0))
                .substitute(RationalField, Polynomial(Rational(6, 9), Rational(1, 5))),
            "test 4"
        )
        assertEquals(
            Polynomial(Rational(653, 378), Rational(221, 420), Rational(4, 175), Rational(2, 875)),
            Polynomial(Rational(1, 7), Rational(9, 4), Rational(0), Rational(2, 7))
                .substitute(RationalField, Polynomial(Rational(6, 9), Rational(1, 5))),
            "test 5"
        )
        assertEquals(
            Polynomial(Rational(89, 54)),
            Polynomial(Rational(0), Rational(9, 4), Rational(1, 3), Rational(0))
                .substitute(RationalField, Polynomial(Rational(6, 9), Rational(0))),
            "test 6"
        )
    }
    @Test
    fun test_derivative() {
        assertEquals(
            Polynomial(Rational(-2), Rational(2)),
            Polynomial(Rational(1), Rational(-2), Rational(1)).derivative(RationalField),
            "test 1"
        )
        assertEquals(
            Polynomial(Rational(-8, 3), Rational(8, 9), Rational(15, 7), Rational(-20, 9)),
            Polynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).derivative(RationalField),
            "test 2"
        )
        assertEquals(
            Polynomial(Rational(0), Rational(8, 9), Rational(15, 7), Rational(-20, 9)),
            Polynomial(Rational(0), Rational(0), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).derivative(RationalField),
            "test 3"
        )
        assertEquals(
            Polynomial(Rational(-8, 3), Rational(8, 9), Rational(15, 7)),
            Polynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(0)).derivative(RationalField),
            "test 4"
        )
    }
    @Test
    fun test_nthDerivative() {
        assertEquals(
            Polynomial(Rational(-2), Rational(2)),
            Polynomial(Rational(1), Rational(-2), Rational(1)).nthDerivative(RationalField, 1),
            "test 1"
        )
        assertFailsWith<IllegalArgumentException>("test2") {
            Polynomial(Rational(1), Rational(-2), Rational(1)).nthDerivative(RationalField, -1)
        }
        assertEquals(
            Polynomial(Rational(1), Rational(-2), Rational(1)),
            Polynomial(Rational(1), Rational(-2), Rational(1)).nthDerivative(RationalField, 0),
            "test 3"
        )
        assertEquals(
            Polynomial(Rational(2)),
            Polynomial(Rational(1), Rational(-2), Rational(1)).nthDerivative(RationalField, 2),
            "test 4"
        )
        assertEquals(
            Polynomial(),
            Polynomial(Rational(1), Rational(-2), Rational(1)).nthDerivative(RationalField, 3),
            "test 5"
        )
        assertEquals(
            Polynomial(),
            Polynomial(Rational(1), Rational(-2), Rational(1)).nthDerivative(RationalField, 4),
            "test 6"
        )
        assertEquals(
            Polynomial(Rational(8, 9), Rational(30, 7), Rational(-20, 3)),
            Polynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).nthDerivative(RationalField, 2),
            "test 7"
        )
        assertEquals(
            Polynomial(Rational(8, 9), Rational(30, 7), Rational(-20, 3)),
            Polynomial(Rational(0), Rational(0), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).nthDerivative(RationalField, 2),
            "test 8"
        )
        assertEquals(
            Polynomial(Rational(8, 9), Rational(30, 7)),
            Polynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(0)).nthDerivative(RationalField, 2),
            "test 9"
        )
    }
    @Test
    fun test_antiderivative() {
        assertEquals(
            Polynomial(Rational(0), Rational(1), Rational(-1), Rational(1, 3)),
            Polynomial(Rational(1), Rational(-2), Rational(1)).antiderivative(RationalField),
            "test 1"
        )
        assertEquals(
            Polynomial(Rational(0), Rational(1, 5), Rational(-4, 3), Rational(4, 27), Rational(5, 28), Rational(-1, 9)),
            Polynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).antiderivative(RationalField),
            "test 2"
        )
        assertEquals(
            Polynomial(Rational(0), Rational(0), Rational(0), Rational(4, 27), Rational(5, 28), Rational(-1, 9)),
            Polynomial(Rational(0), Rational(0), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).antiderivative(RationalField),
            "test 3"
        )
        assertEquals(
            Polynomial(Rational(0), Rational(1, 5), Rational(-4, 3), Rational(4, 27), Rational(5, 28)),
            Polynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(0)).antiderivative(RationalField),
            "test 4"
        )
    }
    @Test
    fun test_nthAntiderivative() {
        assertEquals(
            Polynomial(Rational(0), Rational(1), Rational(-1), Rational(1, 3)),
            Polynomial(Rational(1), Rational(-2), Rational(1)).nthAntiderivative(RationalField, 1),
            "test 1"
        )
        assertFailsWith<IllegalArgumentException>("test2") {
            Polynomial(Rational(1), Rational(-2), Rational(1)).nthAntiderivative(RationalField, -1)
        }
        assertEquals(
            Polynomial(Rational(1), Rational(-2), Rational(1)),
            Polynomial(Rational(1), Rational(-2), Rational(1)).nthAntiderivative(RationalField, 0),
            "test 3"
        )
        assertEquals(
            Polynomial(Rational(0), Rational(0), Rational(1, 2), Rational(-1, 3), Rational(1, 12)),
            Polynomial(Rational(1), Rational(-2), Rational(1)).nthAntiderivative(RationalField, 2),
            "test 4"
        )
        assertEquals(
            Polynomial(Rational(0), Rational(0), Rational(0), Rational(1, 6), Rational(-1, 12), Rational(1, 60)),
            Polynomial(Rational(1), Rational(-2), Rational(1)).nthAntiderivative(RationalField, 3),
            "test 5"
        )
        assertEquals(
            Polynomial(Rational(0), Rational(0), Rational(0), Rational(0), Rational(1, 24), Rational(-1, 60), Rational(1, 360)),
            Polynomial(Rational(1), Rational(-2), Rational(1)).nthAntiderivative(RationalField, 4),
            "test 6"
        )
        assertEquals(
            Polynomial(Rational(0), Rational(0), Rational(1, 10), Rational(-4, 9), Rational(1, 27), Rational(1, 28), Rational(-1, 54)),
            Polynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).nthAntiderivative(RationalField, 2),
            "test 7"
        )
        assertEquals(
            Polynomial(Rational(0), Rational(0), Rational(0), Rational(0), Rational(1, 27), Rational(1, 28), Rational(-1, 54)),
            Polynomial(Rational(0), Rational(0), Rational(4, 9), Rational(5, 7), Rational(-5, 9)).nthAntiderivative(RationalField, 2),
            "test 8"
        )
        assertEquals(
            Polynomial(Rational(0), Rational(0), Rational(1, 10), Rational(-4, 9), Rational(1, 27), Rational(1, 28)),
            Polynomial(Rational(1, 5), Rational(-8, 3), Rational(4, 9), Rational(5, 7), Rational(0)).nthAntiderivative(RationalField, 2),
            "test 9"
        )
    }
}