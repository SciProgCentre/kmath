/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.test.misc.Rational
import space.kscience.kmath.test.misc.RationalField
import space.kscience.kmath.test.misc.assertContentEquals
import kotlin.test.Test
import kotlin.test.assertEquals


class NumberedPolynomialUtilTest {
    @Test
    fun test_substitute_Double_Map() {
        assertContentEquals(
            mapOf(emptyList<UInt>() to 0.0),
            NumberedPolynomialAsIs(
                listOf<UInt>() to 1.0,
                listOf(1u) to -2.0,
                listOf(2u) to 1.0,
            ).substitute(mapOf(
                0 to 1.0
            )).coefficients,
            0.001,
            "test 1"
        )
        assertContentEquals(
            mapOf(
                listOf<UInt>() to 0.8597048543814783,
                listOf(1u) to 0.22997637465889875,
                listOf(2u) to 0.32675302591924016,
                listOf(0u, 1u) to 0.4561746111587508,
                listOf(1u, 1u) to 0.5304946210170756,
                listOf(2u, 1u) to 0.6244313712888998,
                listOf(0u, 2u) to 0.2700930201481795,
                listOf(1u, 2u) to -0.06962351375204712,
                listOf(2u, 2u) to -0.015206988092131501,
            ),
            NumberedPolynomialAsIs(
                listOf<UInt>() to 0.8597048543814783,
                listOf(1u) to 0.22997637465889875,
                listOf(2u) to 0.32675302591924016,
                listOf(0u, 1u) to 0.4561746111587508,
                listOf(1u, 1u) to 0.5304946210170756,
                listOf(2u, 1u) to 0.6244313712888998,
                listOf(0u, 2u) to 0.2700930201481795,
                listOf(1u, 2u) to -0.06962351375204712,
                listOf(2u, 2u) to -0.015206988092131501,
            ).substitute(mapOf()).coefficients,
            0.001,
            "test 2"
        )
        assertContentEquals(
            mapOf(
                listOf<UInt>() to 0.8597048543814783,
                listOf(0u, 1u) to 0.4561746111587508,
                listOf(0u, 2u) to 0.2700930201481795,
            ),
            NumberedPolynomialAsIs(
                listOf<UInt>() to 0.8597048543814783,
                listOf(1u) to 0.22997637465889875,
                listOf(2u) to 0.32675302591924016,
                listOf(0u, 1u) to 0.4561746111587508,
                listOf(1u, 1u) to 0.5304946210170756,
                listOf(2u, 1u) to 0.6244313712888998,
                listOf(0u, 2u) to 0.2700930201481795,
                listOf(1u, 2u) to -0.06962351375204712,
                listOf(2u, 2u) to -0.015206988092131501,
            ).substitute(mapOf(
                0 to 0.0
            )).coefficients,
            0.001,
            "test 3"
        )
        assertContentEquals(
            mapOf(
                listOf<UInt>() to 1.433510890645169,
                listOf(1u) to 0.6264844682514724,
                listOf(2u) to 0.8405727903771333,
            ),
            NumberedPolynomialAsIs(
                listOf<UInt>() to 0.8597048543814783,
                listOf(1u) to 0.22997637465889875,
                listOf(2u) to 0.32675302591924016,
                listOf(0u, 1u) to 0.4561746111587508,
                listOf(1u, 1u) to 0.5304946210170756,
                listOf(2u, 1u) to 0.6244313712888998,
                listOf(0u, 2u) to 0.2700930201481795,
                listOf(1u, 2u) to -0.06962351375204712,
                listOf(2u, 2u) to -0.015206988092131501,
            ).substitute(mapOf(
                1 to 0.8400458576651112
            )).coefficients,
            0.001,
            "test 4"
        )
        assertContentEquals(
            mapOf(
                listOf<UInt>() to 1.934530767358133,
            ),
            NumberedPolynomialAsIs(
                listOf<UInt>() to 0.8597048543814783,
                listOf(1u) to 0.22997637465889875,
                listOf(2u) to 0.32675302591924016,
                listOf(0u, 1u) to 0.4561746111587508,
                listOf(1u, 1u) to 0.5304946210170756,
                listOf(2u, 1u) to 0.6244313712888998,
                listOf(0u, 2u) to 0.2700930201481795,
                listOf(1u, 2u) to -0.06962351375204712,
                listOf(2u, 2u) to -0.015206988092131501,
            ).substitute(mapOf(
                0 to 0.4846192734143442,
                1 to 0.8400458576651112,
            )).coefficients,
            0.001,
            "test 5"
        )
        assertContentEquals(
            mapOf(
                listOf<UInt>() to 0.8597048543814783,
                listOf(1u) to 0.22997637465889875,
                listOf(2u) to 0.32675302591924016,
                listOf(0u, 1u) to 0.4561746111587508,
                listOf(1u, 1u) to 0.5304946210170756,
                listOf(2u, 1u) to 0.6244313712888998,
                listOf(0u, 2u) to 0.2700930201481795,
                listOf(1u, 2u) to -0.06962351375204712,
                listOf(2u, 2u) to -0.015206988092131501,
            ),
            NumberedPolynomialAsIs(
                listOf<UInt>() to 0.8597048543814783,
                listOf(1u) to 0.22997637465889875,
                listOf(2u) to 0.32675302591924016,
                listOf(0u, 1u) to 0.4561746111587508,
                listOf(1u, 1u) to 0.5304946210170756,
                listOf(2u, 1u) to 0.6244313712888998,
                listOf(0u, 2u) to 0.2700930201481795,
                listOf(1u, 2u) to -0.06962351375204712,
                listOf(2u, 2u) to -0.015206988092131501,
            ).substitute(mapOf(
                5 to 0.9211194782050933
            )).coefficients,
            0.001,
            "test 6"
        )
    }
    @Test
    fun test_substitute_Constant() {
        assertEquals(
            NumberedPolynomialAsIs(
                listOf<UInt>() to Rational(0)
            ),
            NumberedPolynomialAsIs(
                listOf<UInt>() to Rational(1),
                listOf(1u) to Rational(-2),
                listOf(2u) to Rational(1)
            ).substitute(RationalField, mapOf(
                0 to Rational(1)
            )),
            "test 1"
        )
        // https://www.wolframalpha.com/input?i=%28-3%2F2+%2B+8%2F6+x+%2B+14%2F6+x%5E2%29+%2B+%28-3%2F1+%2B+-19%2F2+x+%2B+9%2F4+x%5E2%29+y+%2B+%285%2F5+%2B+18%2F9+x+%2B+5%2F2+x%5E2%29+y%5E2+where+x+%3D+-2%2F5%2C+y+%3D+12%2F9
        assertEquals(
            NumberedPolynomialAsIs(
                listOf<UInt>() to Rational(143, 150)
            ),
            NumberedPolynomialAsIs(
                listOf<UInt>() to Rational(-3, 2),
                listOf(1u) to Rational(8, 6),
                listOf(2u) to Rational(14, 6),
                listOf(0u, 1u) to Rational(-3, 1),
                listOf(1u, 1u) to Rational(-19, 2),
                listOf(2u, 1u) to Rational(9, 4),
                listOf(0u, 2u) to Rational(5, 5),
                listOf(1u, 2u) to Rational(18, 9),
                listOf(2u, 2u) to Rational(5, 2),
            ).substitute(RationalField, mapOf(
                0 to Rational(-2, 5),
                1 to Rational(12, 9),
            )),
            "test 2"
        )
        // https://www.wolframalpha.com/input?i=%28%28-3%2F2+%2B+8%2F6+x+%2B+14%2F6+x%5E2%29+%2B+%28-3%2F1+%2B+-19%2F2+x+%2B+9%2F4+x%5E2%29+y+%2B+%285%2F5+%2B+18%2F9+x+%2B+5%2F2+x%5E2%29+y%5E2%29+p%5E8+where+x+%3D+q%2Fp%2C+y+%3D+x%5E3%2C+p+%3D+-2%2F5%2C+q+%3D+12%2F9
        assertEquals(
            NumberedPolynomialAsIs(
            listOf<UInt>() to Rational(47639065216, 2562890625)
            ),
            NumberedPolynomialAsIs(
                listOf(8u) to Rational(-3, 2),
                listOf(7u, 1u) to Rational(8, 6),
                listOf(6u, 2u) to Rational(14, 6),
                listOf(5u, 3u) to Rational(-3, 1),
                listOf(4u, 4u) to Rational(-19, 2),
                listOf(3u, 5u) to Rational(9, 4),
                listOf(2u, 6u) to Rational(5, 5),
                listOf(1u, 7u) to Rational(18, 9),
                listOf(0u, 8u) to Rational(5, 2),
            ).substitute(RationalField, mapOf(
                0 to Rational(-2, 5),
                1 to Rational(12, 9),
            )),
            "test 3"
        )
        // https://www.wolframalpha.com/input?i=%28%28-3%2F2+%2B+8%2F6+x+%2B+14%2F6+x%5E2%29+%2B+%28-3%2F1+%2B+-19%2F2+x+%2B+9%2F4+x%5E2%29+y+%2B+%285%2F5+%2B+18%2F9+x+%2B+5%2F2+x%5E2%29+y%5E2%29+p%5E8+where+x+%3D+q%2Fp%2C+y+%3D+x%5E3%2C+p+%3D+-2%2F5%2C+q+%3D+12%2F9
        assertEquals(
            NumberedPolynomialAsIs(
                listOf(8u) to Rational(-3, 2),
                listOf(7u, 1u) to Rational(8, 6),
                listOf(6u, 2u) to Rational(14, 6),
                listOf(5u, 3u) to Rational(-3, 1),
                listOf(4u, 4u) to Rational(-19, 2),
                listOf(3u, 5u) to Rational(9, 4),
                listOf(2u, 6u) to Rational(5, 5),
                listOf(1u, 7u) to Rational(18, 9),
                listOf(0u, 8u) to Rational(5, 2),
            ),
            NumberedPolynomialAsIs(
                listOf(8u) to Rational(-3, 2),
                listOf(7u, 1u) to Rational(8, 6),
                listOf(6u, 2u) to Rational(14, 6),
                listOf(5u, 3u) to Rational(-3, 1),
                listOf(4u, 4u) to Rational(-19, 2),
                listOf(3u, 5u) to Rational(9, 4),
                listOf(2u, 6u) to Rational(5, 5),
                listOf(1u, 7u) to Rational(18, 9),
                listOf(0u, 8u) to Rational(5, 2),
            ).substitute(RationalField, mapOf<Int, Rational>()),
            "test 4"
        )
    }
    @Test
    fun test_substitute_Polynomial() {
        assertEquals(
            NumberedPolynomialAsIs(
                listOf<UInt>() to Rational(0)
            ),
            NumberedPolynomialAsIs(
                listOf<UInt>() to Rational(1),
                listOf(1u) to Rational(-2),
                listOf(2u) to Rational(1)
            ).substitute(RationalField, mapOf(
                0 to NumberedPolynomialAsIs(
                    listOf<UInt>() to Rational(1)
                )
            )),
            "test 1"
        )
        // https://www.wolframalpha.com/input?i=%28-3%2F2+%2B+8%2F6+x+%2B+14%2F6+x%5E2%29+%2B+%28-3%2F1+%2B+-19%2F2+x+%2B+9%2F4+x%5E2%29+y+%2B+%285%2F5+%2B+18%2F9+x+%2B+5%2F2+x%5E2%29+y%5E2+where+x+%3D+-2%2F5%2C+y+%3D+12%2F9
        assertEquals(
            NumberedPolynomialAsIs(
                listOf<UInt>() to Rational(-3, 2),
                listOf(0u, 1u) to Rational(-92, 21),
                listOf(0u, 2u) to Rational(-2627, 2352),
                listOf(0u, 3u) to Rational(4565, 3136),
                listOf(0u, 4u) to Rational(605, 1568),
                listOf(1u) to Rational(-20, 3),
                listOf(1u, 1u) to Rational(1445, 21),
                listOf(1u, 2u) to Rational(-13145, 392),
                listOf(1u, 3u) to Rational(-3025, 196),
                listOf(2u) to Rational(175, 3),
                listOf(2u, 1u) to Rational(2475, 28),
                listOf(2u, 2u) to Rational(15125, 98),
                listOf(3u) to Rational(0),
                listOf(3u, 1u) to Rational(0),
                listOf(4u) to Rational(0),
            ),
            NumberedPolynomialAsIs(
                listOf<UInt>() to Rational(-3, 2),
                listOf(1u) to Rational(8, 6),
                listOf(2u) to Rational(14, 6),
                listOf(0u, 1u) to Rational(-3, 1),
                listOf(1u, 1u) to Rational(-19, 2),
                listOf(2u, 1u) to Rational(9, 4),
                listOf(0u, 2u) to Rational(5, 5),
                listOf(1u, 2u) to Rational(18, 9),
                listOf(2u, 2u) to Rational(5, 2),
            ).substitute(RationalField, mapOf(
                0 to NumberedPolynomialAsIs(
                    listOf(1u) to Rational(-5, 1),
                    listOf(0u, 1u) to Rational(2, 8),
                ),
                1 to NumberedPolynomialAsIs(
                    listOf(1u) to Rational(0, 5),
                    listOf(0u, 1u) to Rational(11, 7),
                ),
            )),
            "test 2"
        )
    }
}