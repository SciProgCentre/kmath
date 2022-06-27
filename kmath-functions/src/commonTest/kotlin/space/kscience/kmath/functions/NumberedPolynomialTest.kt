/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("LocalVariableName")

package space.kscience.kmath.functions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.test.misc.*
import kotlin.test.*


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
    @Test
    fun test_lastVariable() {
        val o = Rational(0)
        RationalField.numberedPolynomialSpace {
            assertEquals(
                -1,
                NumberedPolynomial {}.lastVariable,
                "test 1"
            )
            assertEquals(
                -1,
                NumberedPolynomial {
                    o {}
                }.lastVariable,
                "test 2"
            )
            assertEquals(
                2,
                NumberedPolynomial {
                    o { 1 pow 1u; 2 pow 2u; 3 pow 3u }
                }.lastVariable,
                "test 3"
            )
            assertEquals(
                3,
                NumberedPolynomial {
                    o { 1 pow 0u; 2 pow 1u; 3 pow 2u; 4 pow 1u; 5 pow 0u }
                }.also { println(it) }.lastVariable,
                "test 4"
            )
            assertEquals(
                2,
                NumberedPolynomial {
                    o {}
                    o { 2 pow 1u }
                    o { 1 pow 2u; 3 pow 1u }
                }.lastVariable,
                "test 5"
            )
        }
    }
    @Test
    fun test_degree() {
        val o = Rational(0)
        RationalField.numberedPolynomialSpace {
            assertEquals(
                -1,
                NumberedPolynomial {}.degree,
                "test 1"
            )
            assertEquals(
                0,
                NumberedPolynomial {
                    o {}
                }.degree,
                "test 2"
            )
            assertEquals(
                6,
                NumberedPolynomial {
                    o { 1 pow 1u; 2 pow 2u; 3 pow 3u }
                }.degree,
                "test 3"
            )
            assertEquals(
                4,
                NumberedPolynomial {
                    o { 1 pow 0u; 2 pow 1u; 3 pow 2u; 4 pow 1u; 5 pow 0u }
                }.degree,
                "test 4"
            )
            assertEquals(
                3,
                NumberedPolynomial {
                    o {}
                    o { 2 pow 1u }
                    o { 1 pow 2u; 3 pow 1u }
                }.degree,
                "test 5"
            )
            assertEquals(
                4,
                NumberedPolynomial {
                    o {}
                    o { 2 pow 1u }
                    o { 1 pow 2u; 3 pow 1u }
                    o { 4 pow 4u }
                }.degree,
                "test 6"
            )
        }
    }
    @Test
    fun test_countOfVariables() {
        val o = Rational(0)
        RationalField.numberedPolynomialSpace {
            assertEquals(
                listOf(),
                NumberedPolynomial {}.degrees,
                "test 1"
            )
            assertEquals(
                listOf(),
                NumberedPolynomial {
                    o {}
                }.degrees,
                "test 2"
            )
            assertEquals(
                listOf(1u, 2u, 3u),
                NumberedPolynomial {
                    o { 1 pow 1u; 2 pow 2u; 3 pow 3u }
                }.degrees,
                "test 3"
            )
            assertEquals(
                listOf(0u, 1u, 2u, 1u),
                NumberedPolynomial {
                    o { 1 pow 0u; 2 pow 1u; 3 pow 2u; 4 pow 1u; 5 pow 0u }
                }.degrees,
                "test 4"
            )
            assertEquals(
                listOf(2u, 1u, 1u),
                NumberedPolynomial {
                    o {}
                    o { 2 pow 1u }
                    o { 1 pow 2u; 3 pow 1u }
                }.degrees,
                "test 5"
            )
            assertEquals(
                listOf(2u, 2u, 2u, 4u),
                NumberedPolynomial {
                    o {}
                    o { 1 pow 1u; 2 pow 2u }
                    o { 2 pow 1u; 3 pow 2u }
                    o { 1 pow 2u; 3 pow 1u }
                    o { 4 pow 4u }
                }.degrees,
                "test 6"
            )
        }
    }
    @Test
    fun test_degreeBy() {
        val o = Rational(0)
        RationalField.numberedPolynomialSpace {
            fun NumberedPolynomial<Rational>.collectDegrees(limit: Int = lastVariable + 2): List<UInt> = List(limit) { degreeBy(it) }
            assertEquals(
                listOf(0u),
                NumberedPolynomial {}.collectDegrees(),
                "test 1"
            )
            assertEquals(
                listOf(0u),
                NumberedPolynomial {
                    o {}
                }.collectDegrees(),
                "test 2"
            )
            assertEquals(
                listOf(1u, 2u, 3u, 0u),
                NumberedPolynomial {
                    o { 1 pow 1u; 2 pow 2u; 3 pow 3u }
                }.collectDegrees(),
                "test 3"
            )
            assertEquals(
                listOf(0u, 1u, 2u, 1u, 0u),
                NumberedPolynomial {
                    o { 1 pow 0u; 2 pow 1u; 3 pow 2u; 4 pow 1u; 5 pow 0u }
                }.collectDegrees(),
                "test 4"
            )
            assertEquals(
                listOf(2u, 1u, 1u, 0u),
                NumberedPolynomial {
                    o {}
                    o { 2 pow 1u }
                    o { 1 pow 2u; 3 pow 1u }
                }.collectDegrees(),
                "test 5"
            )
            assertEquals(
                listOf(2u, 2u, 2u, 4u, 0u),
                NumberedPolynomial {
                    o {}
                    o { 1 pow 1u; 2 pow 2u }
                    o { 2 pow 1u; 3 pow 2u }
                    o { 1 pow 2u; 3 pow 1u }
                    o { 4 pow 4u }
                }.collectDegrees(),
                "test 6"
            )
        }
    }
    @Test
    fun test_degreeBy_Collection() {
        val o = Rational(0)
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
            NumberedPolynomial {}.checkDegreeBy("test 1")
            NumberedPolynomial {
                o {}
            }.checkDegreeBy("test 2")
            NumberedPolynomial {
                o { 1 pow 1u; 2 pow 2u; 3 pow 3u }
            }.checkDegreeBy("test 3")
            NumberedPolynomial {
                o { 1 pow 0u; 2 pow 1u; 3 pow 2u; 4 pow 1u; 5 pow 0u }
            }.checkDegreeBy("test 4")
            NumberedPolynomial {
                o {}
                o { 2 pow 1u }
                o { 1 pow 2u; 3 pow 1u }
            }.checkDegreeBy("test 5")
            NumberedPolynomial {
                o {}
                o { 1 pow 1u; 2 pow 2u }
                o { 2 pow 1u; 3 pow 2u }
                o { 1 pow 2u; 3 pow 1u }
                o { 4 pow 4u }
            }.checkDegreeBy("test 6")
        }
    }
    @Test
    fun test_degrees() {
        val o = Rational(0)
        RationalField.numberedPolynomialSpace {
            assertEquals(
                0,
                NumberedPolynomial {}.countOfVariables,
                "test 1"
            )
            assertEquals(
                0,
                NumberedPolynomial {
                    o {}
                }.countOfVariables,
                "test 2"
            )
            assertEquals(
                3,
                NumberedPolynomial {
                    o { 1 pow 1u; 2 pow 2u; 3 pow 3u }
                }.countOfVariables,
                "test 3"
            )
            assertEquals(
                3,
                NumberedPolynomial {
                    o { 1 pow 0u; 2 pow 1u; 3 pow 2u; 4 pow 1u; 5 pow 0u }
                }.countOfVariables,
                "test 4"
            )
            assertEquals(
                3,
                NumberedPolynomial {
                    o {}
                    o { 2 pow 1u }
                    o { 1 pow 2u; 3 pow 1u }
                }.countOfVariables,
                "test 5"
            )
            assertEquals(
                4,
                NumberedPolynomial {
                    o {}
                    o { 1 pow 1u; 2 pow 2u }
                    o { 2 pow 1u; 3 pow 2u }
                    o { 1 pow 2u; 3 pow 1u }
                    o { 4 pow 4u }
                }.countOfVariables,
                "test 6"
            )
        }
    }
}