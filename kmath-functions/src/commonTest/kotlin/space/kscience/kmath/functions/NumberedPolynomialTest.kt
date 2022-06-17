/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("LocalVariableName")

package space.kscience.kmath.functions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.test.misc.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame


@UnstableKMathAPI
class NumberedPolynomialTest {
    @Test
    fun test_Polynomial_Int_plus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial {
                    Rational(-22, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(5, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } + -3,
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(-3, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } + -3,
                "test 2"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } + -3,
                "test 3"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } + -3,
                "test 4"
            )
            val polynomial_5 = NumberedPolynomial {
                Rational(-22, 9) with {}
                Rational(-8, 9) with { 1 pow 3u }
                Rational(-8, 7) with { 2 pow 4u }
            }
            assertSame(
                polynomial_5,
                polynomial_5 + 0,
                "test 5"
            )
            val polynomial_6 = NumberedPolynomial {
                Rational(0, 9) with {}
                Rational(-8, 9) with { 1 pow 3u }
                Rational(-8, 7) with { 2 pow 4u }
            }
            assertSame(
                polynomial_6,
                polynomial_6 + 0,
                "test 6"
            )
            val polynomial_7 = NumberedPolynomial {
                Rational(-8, 9) with { 1 pow 3u }
                Rational(-8, 7) with { 2 pow 4u }
            }
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
                NumberedPolynomial {
                    Rational(-22, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(5, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } - 3,
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(-3, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } - 3,
                "test 2"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } - 3,
                "test 3"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } - 3,
                "test 4"
            )
            val polynomial_5 = NumberedPolynomial {
                Rational(-22, 9) with {}
                Rational(-8, 9) with { 1 pow 3u }
                Rational(-8, 7) with { 2 pow 4u }
            }
            assertSame(
                polynomial_5,
                polynomial_5 - 0,
                "test 5"
            )
            val polynomial_6 = NumberedPolynomial {
                Rational(0, 9) with {}
                Rational(-8, 9) with { 1 pow 3u }
                Rational(-8, 7) with { 2 pow 4u }
            }
            assertSame(
                polynomial_6,
                polynomial_6 - 0,
                "test 6"
            )
            val polynomial_7 = NumberedPolynomial {
                Rational(-8, 9) with { 1 pow 3u }
                Rational(-8, 7) with { 2 pow 4u }
            }
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
                NumberedPolynomial {
                    m(34) with {}
                    m(2) with { 1 pow 3u }
                    m(1) with { 2 pow 1u }
                    m(20) with { 1 pow 1u }
                    m(2) with { 3 pow 2u }
               },
                NumberedPolynomial {
                    m(22) with {}
                    m(26) with { 1 pow 3u }
                    m(13) with { 2 pow 1u }
                    m(15) with { 1 pow 1u }
                    m(26) with { 3 pow 2u }
                } * 27,
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    m(0) with {}
                    m(0) with { 1 pow 3u }
                    m(0) with { 2 pow 1u }
                    m(0) with { 1 pow 1u }
                    m(0) with { 3 pow 2u }
                },
                NumberedPolynomial {
                    m(7) with {}
                    m(0) with { 1 pow 3u }
                    m(49) with { 2 pow 1u }
                    m(21) with { 1 pow 1u }
                    m(14) with { 3 pow 2u }
                } * 15,
                "test 2"
            )
            val polynomial = NumberedPolynomial {
                m(22) with {}
                m(26) with { 1 pow 3u }
                m(13) with { 2 pow 1u }
                m(15) with { 1 pow 1u }
                m(26) with { 3 pow 2u }
            }
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
                NumberedPolynomial {
                    Rational(-22, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                -3 + NumberedPolynomial {
                    Rational(5, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(-3, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                -3 + NumberedPolynomial {
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 2"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                -3 + NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 3"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                -3 + NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 4"
            )
            val polynomial_5 = NumberedPolynomial {
                Rational(-22, 9) with {}
                Rational(-8, 9) with { 1 pow 3u }
                Rational(-8, 7) with { 2 pow 4u }
            }
            assertSame(
                polynomial_5,
                0 + polynomial_5,
                "test 5"
            )
            val polynomial_6 = NumberedPolynomial {
                Rational(0, 9) with {}
                Rational(-8, 9) with { 1 pow 3u }
                Rational(-8, 7) with { 2 pow 4u }
            }
            assertSame(
                polynomial_6,
                0 + polynomial_6,
                "test 6"
            )
            val polynomial_7 = NumberedPolynomial {
                Rational(-8, 9) with { 1 pow 3u }
                Rational(-8, 7) with { 2 pow 4u }
            }
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
                NumberedPolynomial {
                    Rational(22, 9) with {}
                    Rational(8, 9) with { 1 pow 3u }
                    Rational(8, 7) with { 2 pow 4u }
                },
                3 - NumberedPolynomial {
                    Rational(5, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(3, 1) with {}
                    Rational(8, 9) with { 1 pow 3u }
                    Rational(8, 7) with { 2 pow 4u }
                },
                3 - NumberedPolynomial {
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 2"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(8, 9) with { 1 pow 3u }
                    Rational(8, 7) with { 2 pow 4u }
                },
                3 - NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 3"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(8, 9) with { 1 pow 3u }
                    Rational(8, 7) with { 2 pow 4u }
                },
                3 - NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 4"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(22, 9) with {}
                    Rational(8, 9) with { 1 pow 3u }
                    Rational(8, 7) with { 2 pow 4u }
                },
                0 - NumberedPolynomial {
                    Rational(-22, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 5"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 9) with {}
                    Rational(8, 9) with { 1 pow 3u }
                    Rational(8, 7) with { 2 pow 4u }
                },
                0 - NumberedPolynomial {
                    Rational(0, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 6"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(8, 9) with { 1 pow 3u }
                    Rational(8, 7) with { 2 pow 4u }
                },
                0 - NumberedPolynomial {
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 7"
            )
        }
    }
    @Test
    fun test_Int_Polynomial_times() {
        IntModuloRing(35).numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial {
                    m(34) with {}
                    m(2) with { 1 pow 3u }
                    m(1) with { 2 pow 1u }
                    m(20) with { 1 pow 1u }
                    m(2) with { 3 pow 2u }
                },
                27 * NumberedPolynomial {
                    m(22) with {}
                    m(26) with { 1 pow 3u }
                    m(13) with { 2 pow 1u }
                    m(15) with { 1 pow 1u }
                    m(26) with { 3 pow 2u }
                },
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    m(0) with {}
                    m(0) with { 1 pow 3u }
                    m(0) with { 2 pow 1u }
                    m(0) with { 1 pow 1u }
                    m(0) with { 3 pow 2u }
                },
                15 * NumberedPolynomial {
                    m(7) with {}
                    m(0) with { 1 pow 3u }
                    m(49) with { 2 pow 1u }
                    m(21) with { 1 pow 1u }
                    m(14) with { 3 pow 2u }
                },
                "test 2"
            )
            val polynomial = NumberedPolynomial {
                m(22) with {}
                m(26) with { 1 pow 3u }
                m(13) with { 2 pow 1u }
                m(15) with { 1 pow 1u }
                m(26) with { 3 pow 2u }
            }
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
                NumberedPolynomial {
                    Rational(-22, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(5, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } + Rational(-3),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(-3, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } + Rational(-3),
                "test 2"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } + Rational(-3),
                "test 3"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } + Rational(-3),
                "test 4"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(-22, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(-22, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } + Rational(0),
                "test 5"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(0, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } + Rational(0),
                "test 6"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } + Rational(0),
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Constant_minus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial {
                    Rational(-22, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(5, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } - Rational(3),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(-3, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } - Rational(3),
                "test 2"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } - Rational(3),
                "test 3"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } - Rational(3),
                "test 4"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(-22, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(-22, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } - Rational(0),
                "test 5"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(0, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } - Rational(0),
                "test 6"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                NumberedPolynomial {
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                } - Rational(0),
                "test 7"
            )
        }
    }
    @Test
    fun test_Polynomial_Constant_times() {
        IntModuloRing(35).numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial {
                    m(34) with {}
                    m(2) with { 1 pow 3u }
                    m(1) with { 2 pow 1u }
                    m(20) with { 1 pow 1u }
                    m(2) with { 3 pow 2u }
                },
                NumberedPolynomial {
                    m(22) with {}
                    m(26) with { 1 pow 3u }
                    m(13) with { 2 pow 1u }
                    m(15) with { 1 pow 1u }
                    m(26) with { 3 pow 2u }
                } * m(27),
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    m(0) with {}
                    m(0) with { 1 pow 3u }
                    m(0) with { 2 pow 1u }
                    m(0) with { 1 pow 1u }
                    m(0) with { 3 pow 2u }
                },
                NumberedPolynomial {
                    m(7) with {}
                    m(0) with { 1 pow 3u }
                    m(49) with { 2 pow 1u }
                    m(21) with { 1 pow 1u }
                    m(14) with { 3 pow 2u }
                } * m(15),
                "test 2"
            )
            assertEquals(
                NumberedPolynomial {
                    m(0) with {}
                    m(0) with { 1 pow 3u }
                    m(0) with { 2 pow 1u }
                    m(0) with { 1 pow 1u }
                    m(0) with { 3 pow 2u }
                },
                NumberedPolynomial {
                    m(22) with {}
                    m(26) with { 1 pow 3u }
                    m(13) with { 2 pow 1u }
                    m(15) with { 1 pow 1u }
                    m(26) with { 3 pow 2u }
                } * m(0),
                "test 3"
            )
            assertEquals(
                NumberedPolynomial {
                    m(22) with {}
                    m(26) with { 1 pow 3u }
                    m(13) with { 2 pow 1u }
                    m(15) with { 1 pow 1u }
                    m(26) with { 3 pow 2u }
                },
                NumberedPolynomial {
                    m(22) with {}
                    m(26) with { 1 pow 3u }
                    m(13) with { 2 pow 1u }
                    m(15) with { 1 pow 1u }
                    m(26) with { 3 pow 2u }
                } * m(1),
                "test 4"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_plus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial {
                    Rational(-22, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                Rational(-3) + NumberedPolynomial {
                    Rational(5, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(-3, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                Rational(-3) + NumberedPolynomial {
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 2"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                Rational(-3) + NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 3"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                Rational(-3) + NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 4"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(-22, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                Rational(0) + NumberedPolynomial {
                    Rational(-22, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 5"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                Rational(0) + NumberedPolynomial {
                    Rational(0, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 6"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                Rational(0) + NumberedPolynomial {
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 7"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_minus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial {
                    Rational(22, 9) with {}
                    Rational(8, 9) with { 1 pow 3u }
                    Rational(8, 7) with { 2 pow 4u }
                },
                Rational(3) - NumberedPolynomial {
                    Rational(5, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(3, 1) with {}
                    Rational(8, 9) with { 1 pow 3u }
                    Rational(8, 7) with { 2 pow 4u }
                },
                Rational(3) - NumberedPolynomial {
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 2"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(8, 9) with { 1 pow 3u }
                    Rational(8, 7) with { 2 pow 4u }
                },
                Rational(3) - NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 3"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 1) with {}
                    Rational(8, 9) with { 1 pow 3u }
                    Rational(8, 7) with { 2 pow 4u }
                },
                Rational(3) - NumberedPolynomial {
                    Rational(27, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 4"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(22, 9) with {}
                    Rational(8, 9) with { 1 pow 3u }
                    Rational(8, 7) with { 2 pow 4u }
                },
                Rational(0) - NumberedPolynomial {
                    Rational(-22, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 5"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0, 9) with {}
                    Rational(8, 9) with { 1 pow 3u }
                    Rational(8, 7) with { 2 pow 4u }
                },
                Rational(0) - NumberedPolynomial {
                    Rational(0, 9) with {}
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 6"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0) with {}
                    Rational(8, 9) with { 1 pow 3u }
                    Rational(8, 7) with { 2 pow 4u }
                },
                Rational(0) - NumberedPolynomial {
                    Rational(-8, 9) with { 1 pow 3u }
                    Rational(-8, 7) with { 2 pow 4u }
                },
                "test 7"
            )
        }
    }
    @Test
    fun test_Constant_Polynomial_times() {
        IntModuloRing(35).numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial {
                    m(34) with {}
                    m(2) with { 1 pow 3u }
                    m(1) with { 2 pow 1u }
                    m(20) with { 1 pow 1u }
                    m(2) with { 3 pow 2u }
                },
                m(27) * NumberedPolynomial {
                    m(22) with {}
                    m(26) with { 1 pow 3u }
                    m(13) with { 2 pow 1u }
                    m(15) with { 1 pow 1u }
                    m(26) with { 3 pow 2u }
                },
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    m(0) with {}
                    m(0) with { 1 pow 3u }
                    m(0) with { 2 pow 1u }
                    m(0) with { 1 pow 1u }
                    m(0) with { 3 pow 2u }
                },
                m(15) * NumberedPolynomial {
                    m(7) with {}
                    m(0) with { 1 pow 3u }
                    m(49) with { 2 pow 1u }
                    m(21) with { 1 pow 1u }
                    m(14) with { 3 pow 2u }
                },
                "test 2"
            )
            assertEquals(
                NumberedPolynomial {
                    m(0) with {}
                    m(0) with { 1 pow 3u }
                    m(0) with { 2 pow 1u }
                    m(0) with { 1 pow 1u }
                    m(0) with { 3 pow 2u }
                },
                m(0) * NumberedPolynomial {
                    m(22) with {}
                    m(26) with { 1 pow 3u }
                    m(13) with { 2 pow 1u }
                    m(15) with { 1 pow 1u }
                    m(26) with { 3 pow 2u }
                },
                "test 3"
            )
            assertEquals(
                NumberedPolynomial {
                    m(22) with {}
                    m(26) with { 1 pow 3u }
                    m(13) with { 2 pow 1u }
                    m(15) with { 1 pow 1u }
                    m(26) with { 3 pow 2u }
                },
                m(1) * NumberedPolynomial {
                    m(22) with {}
                    m(26) with { 1 pow 3u }
                    m(13) with { 2 pow 1u }
                    m(15) with { 1 pow 1u }
                    m(26) with { 3 pow 2u }
                },
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_unaryMinus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial {
                    Rational(-5, 9) with { 1 pow 5u }
                    Rational(8, 9) with {}
                    Rational(8, 7) with { 7 pow 13u }
                },
                -NumberedPolynomial {
                    Rational(5, 9) with { 1 pow 5u }
                    Rational(-8, 9) with {}
                    Rational(-8, 7) with { 7 pow 13u }
                },
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(-5, 9) with { 3 pow 7u }
                    Rational(8, 9) with {}
                    Rational(8, 7) with { 1 pow 3u }
                    Rational(0) with { 2 pow 4u }
                    Rational(0) with { 1 pow 5u }
                },
                -NumberedPolynomial {
                    Rational(5, 9) with { 3 pow 7u }
                    Rational(-8, 9) with {}
                    Rational(-8, 7) with { 1 pow 3u }
                    Rational(0) with { 2 pow 4u }
                    Rational(0) with { 1 pow 5u }
                },
                "test 2"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_plus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial {
                    Rational(-17, 2) with {}
                    Rational(-1, 3) with { 1 pow 1u }
                    Rational(-25, 21) with { 1 pow 2u }
                    Rational(146, 63) with { 2 pow 1u }
                    Rational(-3, 5) with { 1 pow 1u; 2 pow 1u }
                    Rational(61, 15) with { 1 pow 2u; 2 pow 1u }
                    Rational(157, 63) with { 2 pow 2u }
                    Rational(-55, 21) with { 1 pow 1u; 2 pow 2u }
                    Rational(11, 24) with { 1 pow 2u; 2 pow 2u }
                },
                NumberedPolynomial {
                    Rational(6, 4) with {}
                    Rational(-2, 6) with { 1 pow 1u }
                    Rational(10, 6) with { 1 pow 2u }
                    Rational(17, 7) with { 2 pow 1u }
                    Rational(-7, 7) with { 1 pow 1u; 2 pow 1u }
                    Rational(12, 5) with { 1 pow 2u; 2 pow 1u }
                    Rational(12, 7) with { 2 pow 2u }
                    Rational(-10, 3) with { 1 pow 1u; 2 pow 2u }
                    Rational(9, 8) with { 1 pow 2u; 2 pow 2u }
                } + NumberedPolynomial {
                    Rational(-20, 2) with {}
                    Rational(0, 9) with { 1 pow 1u }
                    Rational(-20, 7) with { 1 pow 2u }
                    Rational(-1, 9) with { 2 pow 1u }
                    Rational(2, 5) with { 1 pow 1u; 2 pow 1u }
                    Rational(10, 6) with { 1 pow 2u; 2 pow 1u }
                    Rational(7, 9) with { 2 pow 2u }
                    Rational(5, 7) with { 1 pow 1u; 2 pow 2u }
                    Rational(-2, 3) with { 1 pow 2u; 2 pow 2u }
                },
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(-17, 2) with {}
                    Rational(-1, 3) with { 1 pow 1u }
                    Rational(-25, 21) with { 1 pow 2u }
                    Rational(-1, 9) with { 2 pow 1u }
                    Rational(2, 5) with { 1 pow 1u; 2 pow 1u }
                    Rational(10, 6) with { 1 pow 2u; 2 pow 1u }
                    Rational(157, 63) with { 2 pow 2u }
                    Rational(-55, 21) with { 1 pow 1u; 2 pow 2u }
                    Rational(11, 24) with { 1 pow 2u; 2 pow 2u }
                },
                NumberedPolynomial {
                    Rational(6, 4) with {}
                    Rational(-2, 6) with { 1 pow 1u }
                    Rational(10, 6) with { 1 pow 2u }
                    Rational(12, 7) with { 2 pow 2u }
                    Rational(-10, 3) with { 1 pow 1u; 2 pow 2u }
                    Rational(9, 8) with { 1 pow 2u; 2 pow 2u }
                } + NumberedPolynomial {
                    Rational(-20, 2) with {}
                    Rational(0, 9) with { 1 pow 1u }
                    Rational(-20, 7) with { 1 pow 2u }
                    Rational(-1, 9) with { 2 pow 1u }
                    Rational(2, 5) with { 1 pow 1u; 2 pow 1u }
                    Rational(10, 6) with { 1 pow 2u; 2 pow 1u }
                    Rational(7, 9) with { 2 pow 2u }
                    Rational(5, 7) with { 1 pow 1u; 2 pow 2u }
                    Rational(-2, 3) with { 1 pow 2u; 2 pow 2u }
                },
                "test 2"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(-17, 2) with {}
                    Rational(-1, 3) with { 1 pow 1u }
                    Rational(-25, 21) with { 1 pow 2u }
                    Rational(-1, 9) with { 2 pow 1u }
                    Rational(2, 5) with { 1 pow 1u; 2 pow 1u }
                    Rational(10, 6) with { 1 pow 2u; 2 pow 1u }
                    Rational(12, 7) with { 2 pow 2u }
                    Rational(-10, 3) with { 1 pow 1u; 2 pow 2u }
                    Rational(9, 8) with { 1 pow 2u; 2 pow 2u }
                },
                NumberedPolynomial {
                    Rational(6, 4) with {}
                    Rational(-2, 6) with { 1 pow 1u }
                    Rational(10, 6) with { 1 pow 2u }
                    Rational(12, 7) with { 2 pow 2u }
                    Rational(-10, 3) with { 1 pow 1u; 2 pow 2u }
                    Rational(9, 8) with { 1 pow 2u; 2 pow 2u }
                } + NumberedPolynomial {
                    Rational(-20, 2) with {}
                    Rational(0, 9) with { 1 pow 1u }
                    Rational(-20, 7) with { 1 pow 2u }
                    Rational(-1, 9) with { 2 pow 1u }
                    Rational(2, 5) with { 1 pow 1u; 2 pow 1u }
                    Rational(10, 6) with { 1 pow 2u; 2 pow 1u }
                    Rational(0) with { 2 pow 2u }
                    Rational(0) with { 1 pow 1u; 2 pow 2u }
                    Rational(0) with { 1 pow 2u; 2 pow 2u }
                },
                "test 3"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0) with {}
                    Rational(0) with { 1 pow 1u }
                    Rational(0) with { 1 pow 2u }
                    Rational(0) with { 2 pow 1u }
                    Rational(0) with { 1 pow 1u; 2 pow 1u }
                    Rational(0) with { 1 pow 2u; 2 pow 1u }
                    Rational(0) with { 2 pow 2u }
                    Rational(0) with { 1 pow 1u; 2 pow 2u }
                    Rational(0) with { 1 pow 2u; 2 pow 2u }
                },
                NumberedPolynomial {
                    Rational(6, 4) with {}
                    Rational(-2, 6) with { 1 pow 1u }
                    Rational(10, 6) with { 1 pow 2u }
                    Rational(17, 7) with { 2 pow 1u }
                    Rational(-7, 7) with { 1 pow 1u; 2 pow 1u }
                    Rational(12, 5) with { 1 pow 2u; 2 pow 1u }
                    Rational(12, 7) with { 2 pow 2u }
                    Rational(-10, 3) with { 1 pow 1u; 2 pow 2u }
                    Rational(9, 8) with { 1 pow 2u; 2 pow 2u }
                } + NumberedPolynomial {
                    Rational(-6, 4) with {}
                    Rational(2, 6) with { 1 pow 1u }
                    Rational(-10, 6) with { 1 pow 2u }
                    Rational(-17, 7) with { 2 pow 1u }
                    Rational(7, 7) with { 1 pow 1u; 2 pow 1u }
                    Rational(-12, 5) with { 1 pow 2u; 2 pow 1u }
                    Rational(-12, 7) with { 2 pow 2u }
                    Rational(10, 3) with { 1 pow 1u; 2 pow 2u }
                    Rational(-9, 8) with { 1 pow 2u; 2 pow 2u }
                },
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_minus() {
        RationalField.numberedPolynomialSpace {
            assertEquals(
                NumberedPolynomial {
                    Rational(-17, 2) with {}
                    Rational(-1, 3) with { 1 pow 1u }
                    Rational(-25, 21) with { 1 pow 2u }
                    Rational(146, 63) with { 2 pow 1u }
                    Rational(-3, 5) with { 1 pow 1u; 2 pow 1u }
                    Rational(61, 15) with { 1 pow 2u; 2 pow 1u }
                    Rational(157, 63) with { 2 pow 2u }
                    Rational(-55, 21) with { 1 pow 1u; 2 pow 2u }
                    Rational(11, 24) with { 1 pow 2u; 2 pow 2u }
                },
                NumberedPolynomial {
                    Rational(6, 4) with {}
                    Rational(-2, 6) with { 1 pow 1u }
                    Rational(10, 6) with { 1 pow 2u }
                    Rational(17, 7) with { 2 pow 1u }
                    Rational(-7, 7) with { 1 pow 1u; 2 pow 1u }
                    Rational(12, 5) with { 1 pow 2u; 2 pow 1u }
                    Rational(12, 7) with { 2 pow 2u }
                    Rational(-10, 3) with { 1 pow 1u; 2 pow 2u }
                    Rational(9, 8) with { 1 pow 2u; 2 pow 2u }
                } - NumberedPolynomial {
                    Rational(20, 2) with {}
                    Rational(0, 9) with { 1 pow 1u }
                    Rational(20, 7) with { 1 pow 2u }
                    Rational(1, 9) with { 2 pow 1u }
                    Rational(-2, 5) with { 1 pow 1u; 2 pow 1u }
                    Rational(-10, 6) with { 1 pow 2u; 2 pow 1u }
                    Rational(-7, 9) with { 2 pow 2u }
                    Rational(-5, 7) with { 1 pow 1u; 2 pow 2u }
                    Rational(2, 3) with { 1 pow 2u; 2 pow 2u }
                },
                "test 1"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(-17, 2) with {}
                    Rational(-1, 3) with { 1 pow 1u }
                    Rational(-25, 21) with { 1 pow 2u }
                    Rational(-1, 9) with { 2 pow 1u }
                    Rational(2, 5) with { 1 pow 1u; 2 pow 1u }
                    Rational(10, 6) with { 1 pow 2u; 2 pow 1u }
                    Rational(157, 63) with { 2 pow 2u }
                    Rational(-55, 21) with { 1 pow 1u; 2 pow 2u }
                    Rational(11, 24) with { 1 pow 2u; 2 pow 2u }
                },
                NumberedPolynomial {
                    Rational(6, 4) with {}
                    Rational(-2, 6) with { 1 pow 1u }
                    Rational(10, 6) with { 1 pow 2u }
                    Rational(12, 7) with { 2 pow 2u }
                    Rational(-10, 3) with { 1 pow 1u; 2 pow 2u }
                    Rational(9, 8) with { 1 pow 2u; 2 pow 2u }
                } - NumberedPolynomial {
                    Rational(20, 2) with {}
                    Rational(0, 9) with { 1 pow 1u }
                    Rational(20, 7) with { 1 pow 2u }
                    Rational(1, 9) with { 2 pow 1u }
                    Rational(-2, 5) with { 1 pow 1u; 2 pow 1u }
                    Rational(-10, 6) with { 1 pow 2u; 2 pow 1u }
                    Rational(-7, 9) with { 2 pow 2u }
                    Rational(-5, 7) with { 1 pow 1u; 2 pow 2u }
                    Rational(2, 3) with { 1 pow 2u; 2 pow 2u }
                },
                "test 2"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(-17, 2) with {}
                    Rational(-1, 3) with { 1 pow 1u }
                    Rational(-25, 21) with { 1 pow 2u }
                    Rational(-1, 9) with { 2 pow 1u }
                    Rational(2, 5) with { 1 pow 1u; 2 pow 1u }
                    Rational(10, 6) with { 1 pow 2u; 2 pow 1u }
                    Rational(12, 7) with { 2 pow 2u }
                    Rational(-10, 3) with { 1 pow 1u; 2 pow 2u }
                    Rational(9, 8) with { 1 pow 2u; 2 pow 2u }
                },
                NumberedPolynomial {
                    Rational(6, 4) with {}
                    Rational(-2, 6) with { 1 pow 1u }
                    Rational(10, 6) with { 1 pow 2u }
                    Rational(12, 7) with { 2 pow 2u }
                    Rational(-10, 3) with { 1 pow 1u; 2 pow 2u }
                    Rational(9, 8) with { 1 pow 2u; 2 pow 2u }
                } - NumberedPolynomial {
                    Rational(20, 2) with {}
                    Rational(0, 9) with { 1 pow 1u }
                    Rational(20, 7) with { 1 pow 2u }
                    Rational(1, 9) with { 2 pow 1u }
                    Rational(-2, 5) with { 1 pow 1u; 2 pow 1u }
                    Rational(-10, 6) with { 1 pow 2u; 2 pow 1u }
                    Rational(0) with { 2 pow 2u }
                    Rational(0) with { 1 pow 1u; 2 pow 2u }
                    Rational(0) with { 1 pow 2u; 2 pow 2u }
                },
                "test 3"
            )
            assertEquals(
                NumberedPolynomial {
                    Rational(0) with {}
                    Rational(0) with { 1 pow 1u }
                    Rational(0) with { 1 pow 2u }
                    Rational(0) with { 2 pow 1u }
                    Rational(0) with { 1 pow 1u; 2 pow 1u }
                    Rational(0) with { 1 pow 2u; 2 pow 1u }
                    Rational(0) with { 2 pow 2u }
                    Rational(0) with { 1 pow 1u; 2 pow 2u }
                    Rational(0) with { 1 pow 2u; 2 pow 2u }
                },
                NumberedPolynomial {
                    Rational(6, 4) with {}
                    Rational(-2, 6) with { 1 pow 1u }
                    Rational(10, 6) with { 1 pow 2u }
                    Rational(17, 7) with { 2 pow 1u }
                    Rational(-7, 7) with { 1 pow 1u; 2 pow 1u }
                    Rational(12, 5) with { 1 pow 2u; 2 pow 1u }
                    Rational(12, 7) with { 2 pow 2u }
                    Rational(-10, 3) with { 1 pow 1u; 2 pow 2u }
                    Rational(9, 8) with { 1 pow 2u; 2 pow 2u }
                } - NumberedPolynomial {
                    Rational(6, 4) with {}
                    Rational(-2, 6) with { 1 pow 1u }
                    Rational(10, 6) with { 1 pow 2u }
                    Rational(17, 7) with { 2 pow 1u }
                    Rational(-7, 7) with { 1 pow 1u; 2 pow 1u }
                    Rational(12, 5) with { 1 pow 2u; 2 pow 1u }
                    Rational(12, 7) with { 2 pow 2u }
                    Rational(-10, 3) with { 1 pow 1u; 2 pow 2u }
                    Rational(9, 8) with { 1 pow 2u; 2 pow 2u }
                },
                "test 4"
            )
        }
    }
    @Test
    fun test_Polynomial_Polynomial_times() {
        IntModuloRing(35).numberedPolynomialSpace {
            // (p + q + r) * (p^2 + q^2 + r^2 - pq - pr - qr) = p^3 + q^3 + r^3 - 3pqr
            assertEquals(
                NumberedPolynomial {
                    m(1) with { 1 pow 3u }
                    m(1) with { 2 pow 3u }
                    m(1) with { 3 pow 3u }
                    m(0) with { 1 pow 1u; 2 pow 2u }
                    m(0) with { 2 pow 1u; 3 pow 2u }
                    m(0) with { 3 pow 1u; 1 pow 2u }
                    m(0) with { 1 pow 1u; 3 pow 2u }
                    m(0) with { 2 pow 1u; 1 pow 2u }
                    m(0) with { 3 pow 1u; 2 pow 2u }
                    m(-3) with { 1 pow 1u; 2 pow 1u; 3 pow 1u }
                },
                NumberedPolynomial {
                    m(1) with { 1 pow 1u }
                    m(1) with { 2 pow 1u }
                    m(1) with { 3 pow 1u }
                } * NumberedPolynomial {
                    m(1) with { 1 pow 2u }
                    m(1) with { 2 pow 2u }
                    m(1) with { 3 pow 2u }
                    m(-1) with { 1 pow 1u; 2 pow 1u }
                    m(-1) with { 2 pow 1u; 3 pow 1u }
                    m(-1) with { 3 pow 1u; 1 pow 1u }
                },
                "test 1"
            )
            // Spoiler: 5 * 7 = 0
            assertEquals(
                NumberedPolynomial {
                    m(0) with { 1 pow 2u }
                    m(0) with { 2 pow 2u }
                    m(0) with { 3 pow 2u }
                    m(0) with { 1 pow 1u; 2 pow 1u }
                    m(0) with { 2 pow 1u; 3 pow 1u }
                    m(0) with { 3 pow 1u; 1 pow 1u }
                },
                NumberedPolynomial {
                    m(5) with { 1 pow 1u }
                    m(-25) with { 2 pow 1u }
                    m(10) with { 3 pow 1u }
                } * NumberedPolynomial {
                    m(21) with { 1 pow 1u }
                    m(14) with { 2 pow 1u }
                    m(-7) with { 3 pow 1u }
                },
                "test 2"
            )
        }
    }
}