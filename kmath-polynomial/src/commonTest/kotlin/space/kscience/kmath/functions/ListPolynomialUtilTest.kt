/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.functions.testUtils.Rational
import space.kscience.kmath.functions.testUtils.RationalField
import space.kscience.kmath.functions.testUtils.assertFailsWithTypeAndMessage
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals


@OptIn(UnstableKMathAPI::class)
class ListPolynomialUtilTest {
    @Test
    fun test_Polynomial_substitute_Double() {
        assertEquals(
            0.0,
            ListPolynomial(1.0, -2.0, 1.0).substitute(1.0),
            0.001,
            "test 1"
        )
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
    fun test_Polynomial_substitute_Constant() {
        assertEquals(
            Rational(0),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).substitute(RationalField, Rational(1)),
            "test 1"
        )
        assertEquals(
            Rational(25057, 21000),
            ListPolynomial(Rational(5, 8), Rational(8, 3), Rational(4, 7), Rational(3, 2))
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
            ListPolynomial(Rational(5, 8), Rational(8, 3), Rational(4, 7), Rational(0))
                .substitute(RationalField, Rational(1, 5)),
            "test 4"
        )
        assertEquals(
            Rational(3511, 3000),
            ListPolynomial(Rational(5, 8), Rational(8, 3), Rational(0), Rational(3, 2))
                .substitute(RationalField, Rational(1, 5)),
            "test 5"
        )
    }
    @Test
    fun test_Polynomial_substitute_Polynomial() {
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
    @Ignore // FIXME: This tests work only for sane realisations of the substitutions. Currently, it is not.
            //  Sane algorithm for substitution p(q/r) (p, q, and r are polynomials) should return denominator r^deg(p),
            //  not r^(deg(p)(deg(p)+1)/2) as it is now.
    fun test_Polynomial_substitute_RationalFunction() {
        assertEquals(
            ListRationalFunction(ListPolynomial(Rational(0)), ListPolynomial(Rational(1))),
            ListPolynomial(Rational(1), Rational(-2), Rational(1))
                .substitute(RationalField, ListRationalFunction(ListPolynomial(Rational(1)), ListPolynomial(Rational(1)))),
            "test 1"
        )
        assertEquals(
            ListRationalFunction(
                ListPolynomial(
                    Rational(66349, 243),
                    Rational(-17873, 405),
                    Rational(173533, 3780),
                    Rational(-91141, 567),
                    Rational(5773909, 105840),
                    Rational(-23243, 630),
                    Rational(1573, 27)
                ),
                ListPolynomial(
                    Rational(169, 81),
                    Rational(-130, 27),
                    Rational(115, 18),
                    Rational(-797, 54),
                    Rational(1985, 144),
                    Rational(-55, 6),
                    Rational(121, 9)
                )
            ),
            ListPolynomial(
                Rational(13, 3),
                Rational(-9, 5),
                Rational(5, 5)
            ).substitute(RationalField,
                ListRationalFunction(
                    ListPolynomial(
                        Rational(15, 1),
                        Rational(6, 9),
                        Rational(-3, 7)
                    ),
                    ListPolynomial(
                        Rational(-13, 9),
                        Rational(10, 6),
                        Rational(-10, 8),
                        Rational(11, 3)
                    )
                )
            ),
            "test 2"
        )
        assertEquals(
            ListRationalFunction(
                ListPolynomial(
                    Rational(0, 1),
                    Rational(0, 1),
                    Rational(-14, 9),
                    Rational(31, 14),
                    Rational(-5077, 980),
                    Rational(99, 35)
                ),
                ListPolynomial(
                    Rational(0, 1),
                    Rational(0, 1),
                    Rational(25, 9),
                    Rational(-25, 6),
                    Rational(1985, 144),
                    Rational(-55, 6),
                    Rational(121, 9)
                )
            ),
            ListPolynomial(
                Rational(0),
                Rational(-9, 5),
                Rational(5, 5)
            ).substitute(RationalField,
                ListRationalFunction(
                    ListPolynomial(
                        Rational(0),
                        Rational(6, 9),
                        Rational(-3, 7)
                    ),
                    ListPolynomial(
                        Rational(0),
                        Rational(10, 6),
                        Rational(-10, 8),
                        Rational(11, 3)
                    )
                )
            ),
            "test 3"
        )
        assertEquals(
            ListRationalFunction(
                ListPolynomial(
                    Rational(-898, 27),
                    Rational(271, 45),
                    Rational(-65, 12) ,
                    Rational(0),
                    Rational(0),
                    Rational(0),
                    Rational(0)
                ),
                ListPolynomial(
                    Rational(-13, 9),
                    Rational(5, 3),
                    Rational(-5, 4),
                    Rational(0),
                    Rational(0),
                    Rational(0),
                    Rational(0)
                )
            ),
            ListPolynomial(
                Rational(13, 3),
                Rational(-9, 5),
                Rational(0)
            ).substitute(RationalField,
                ListRationalFunction(
                    ListPolynomial(
                        Rational(15, 1),
                        Rational(6, 9),
                        Rational(0)
                    ),
                    ListPolynomial(
                        Rational(-13, 9),
                        Rational(10, 6),
                        Rational(-10, 8),
                        Rational(0)
                    )
                )
            ),
            "test 4"
        )
        assertEquals(
            ListRationalFunction(
                ListPolynomial(
                    Rational(56872, 243),
                    Rational(0, 1),
                    Rational(-90, 7),
                    Rational(-3718, 81),
                    Rational(9, 49),
                    Rational(0, 1),
                    Rational(1573, 27)
                ),
                ListPolynomial(
                    Rational(169, 81),
                    Rational(0, 1),
                    Rational(0, 1),
                    Rational(-286, 27),
                    Rational(0, 1),
                    Rational(0, 1),
                    Rational(121, 9)
                )
            ),
            ListPolynomial(
                Rational(13, 3),
                Rational(0),
                Rational(5, 5)
            ).substitute(RationalField,
                ListRationalFunction(
                    ListPolynomial(
                        Rational(15, 1),
                        Rational(0),
                        Rational(-3, 7)
                    ),
                    ListPolynomial(
                        Rational(-13, 9),
                        Rational(0),
                        Rational(0),
                        Rational(11, 3)
                    )
                )
            ),
            "test 5"
        )
    }
    @Test
    fun test_RationalFunction_substitute_Double() {
        assertEquals(
            0.0,
            ListRationalFunction(
                ListPolynomial(1.0, -2.0, 1.0),
                ListPolynomial(-6.302012278484357, 5.831971885376948, -9.271604788393432, 5.494387848015814, -3.7187384450880785)
            ).substitute(1.0),
            0.001,
            "test 1"
        )
        assertEquals(
            2.693702616649797,
            ListRationalFunction(
                ListPolynomial(-5.848840571263625, -1.660411278951134, -3.793740946372443, -9.624569269490076),
                ListPolynomial(-2.9680680215311073, -1.862973627119981, 4.776550592888336, -2.7320154512368466)
            ).substitute(-7.53452770353279),
            0.001,
            "test 2"
        )
        assertEquals(
            2.692226268901378,
            ListRationalFunction(
                ListPolynomial(0.0, -1.660411278951134, -3.793740946372443, -9.624569269490076),
                ListPolynomial(0.0, -1.862973627119981, 4.776550592888336, -2.7320154512368466)
            ).substitute(-7.53452770353279),
            0.001,
            "test 3"
        )
        assertEquals(
            -0.7394904842099175,
            ListRationalFunction(
                ListPolynomial(-5.848840571263625, -1.660411278951134, -3.793740946372443, 0.0),
                ListPolynomial(-2.9680680215311073, -1.862973627119981, 4.776550592888336, 0.0)
            ).substitute(-7.53452770353279),
            0.001,
            "test 4"
        )
        assertEquals(
            3.526835209398159,
            ListRationalFunction(
                ListPolynomial(-5.848840571263625, 0.0, 0.0, -9.624569269490076),
                ListPolynomial(-2.9680680215311073, 0.0, 0.0, -2.7320154512368466)
            ).substitute(-7.53452770353279),
            0.001,
            "test 5"
        )
    }
    @Test
    fun test_RationalFunction_substitute_Constant() {
        assertEquals(
            Rational(0),
            ListRationalFunction(
                ListPolynomial(Rational(1), Rational(-2), Rational(1)),
                ListPolynomial(Rational(1)),
            ).substitute(RationalField, Rational(1)),
            "test 1"
        )
        assertEquals(
            Rational(1149615, 61306),
            ListRationalFunction(
                ListPolynomial(Rational(17, 7), Rational(18, 3), Rational(18, 8), Rational(9, 1)),
                ListPolynomial(Rational(11, 9), Rational(-6, 5), Rational(-12, 7), Rational(2, 1)),
            ).substitute(RationalField, Rational(-7, 8)),
            "test 2"
        )
        assertEquals(
            Rational(3495, 586),
            ListRationalFunction(
                ListPolynomial(Rational(0), Rational(18, 3), Rational(18, 8), Rational(9, 1)),
                ListPolynomial(Rational(0), Rational(-6, 5), Rational(-12, 7), Rational(2, 1)),
            ).substitute(RationalField, Rational(-7, 8)),
            "test 3"
        )
        assertEquals(
            Rational(-88605, 77392),
            ListRationalFunction(
                ListPolynomial(Rational(17, 7), Rational(18, 3), Rational(18, 8), Rational(0)),
                ListPolynomial(Rational(11, 9), Rational(-6, 5), Rational(-12, 7), Rational(0)),
            ).substitute(RationalField, Rational(-7, 8)),
            "test 4"
        )
        assertEquals(
            Rational(116145, 3794),
            ListRationalFunction(
                ListPolynomial(Rational(17, 7), Rational(0), Rational(0), Rational(9, 1)),
                ListPolynomial(Rational(11, 9), Rational(0), Rational(0), Rational(2, 1)),
            ).substitute(RationalField, Rational(-7, 8)),
            "test 5"
        )
    }
    @Test
    fun test_RationalFunction_substitute_Polynomial() {
        assertEquals(
            ListRationalFunction(
                ListPolynomial(Rational(0)),
                ListPolynomial(Rational(1))
            ),
            ListRationalFunction(
                ListPolynomial(Rational(1), Rational(-2), Rational(1)),
                ListPolynomial(Rational(1)),
            ).substitute(RationalField, ListPolynomial(Rational(1))),
            "test 1"
        )
        assertEquals(
            ListRationalFunction(
                ListPolynomial(
                    Rational(-283303, 36),
                    Rational(-23593, 24),
                    Rational(368713, 192),
                    Rational(1455, 8),
                    Rational(-272171, 1536),
                    Rational(-2149, 192),
                    Rational(469, 64),
                    Rational(11, 48),
                    Rational(-11, 96)
                ),
                ListPolynomial(
                    Rational(5797, 12),
                    Rational(595, 16),
                    Rational(-5285, 72),
                    Rational(-745, 192),
                    Rational(1105, 288),
                    Rational(5, 48),
                    Rational(-5, 72)
                )
            ),
            ListRationalFunction(
                ListPolynomial(
                    Rational(2, 9),
                    Rational(11, 3),
                    Rational(-9, 4),
                    Rational(-6, 1),
                    Rational(-11, 6)
                ),
                ListPolynomial(
                    Rational(-2, 3),
                    Rational(-15, 4),
                    Rational(5, 9),
                    Rational(-5, 9)
                )
            ).substitute(RationalField,
                ListPolynomial(
                    Rational(-9, 1),
                    Rational(-1, 4),
                    Rational(2, 4)
                )
            ),
            "test 2"
        )
        assertEquals(
            ListRationalFunction(
                ListPolynomial(
                    Rational(0, 1),
                    Rational(-11, 12),
                    Rational(325, 192),
                    Rational(21, 32),
                    Rational(-1739, 1536),
                    Rational(227, 192),
                    Rational(-59, 64),
                    Rational(11, 48),
                    Rational(-11, 96)
                ),
                ListPolynomial(
                    Rational(0, 1),
                    Rational(15, 16),
                    Rational(-265, 144),
                    Rational(-25, 192),
                    Rational(25, 288),
                    Rational(5, 48),
                    Rational(-5, 72)
                )
            ),
            ListRationalFunction(
                ListPolynomial(
                    Rational(0, 9),
                    Rational(11, 3),
                    Rational(-9, 4),
                    Rational(-6, 1),
                    Rational(-11, 6)
                ),
                ListPolynomial(
                    Rational(0, 3),
                    Rational(-15, 4),
                    Rational(5, 9),
                    Rational(-5, 9)
                )
            ).substitute(RationalField,
                ListPolynomial(
                    Rational(0, 1),
                    Rational(-1, 4),
                    Rational(2, 4)
                )
            ),
            "test 3"
        )
        assertEquals(
            ListRationalFunction(
                ListPolynomial(
                    Rational(149723, 36),
                    Rational(8483, 24),
                    Rational(639, 64),
                    Rational(3, 32),
                    Rational(0),
                    Rational(0),
                    Rational(0),
                    Rational(0),
                    Rational(0)
                ),
                ListPolynomial(
                    Rational(937, 12),
                    Rational(55, 16),
                    Rational(5, 144),
                    Rational(0),
                    Rational(0),
                    Rational(0),
                    Rational(0)
                )
            ),
            ListRationalFunction(
                ListPolynomial(
                    Rational(2, 9),
                    Rational(11, 3),
                    Rational(-9, 4),
                    Rational(-6, 1),
                    Rational(0)
                ),
                ListPolynomial(
                    Rational(-2, 3),
                    Rational(-15, 4),
                    Rational(5, 9),
                    Rational(0)
                )
            ).substitute(RationalField,
                ListPolynomial(
                    Rational(-9, 1),
                    Rational(-1, 4),
                    Rational(0)
                )
            ),
            "test 4"
        )
        assertEquals(
            ListRationalFunction(
                ListPolynomial(
                    Rational(-216509, 18),
                    Rational(0, 1),
                    Rational(2673, 1),
                    Rational(0, 1),
                    Rational(-891, 4),
                    Rational(0, 1),
                    Rational(33, 4),
                    Rational(0, 1),
                    Rational(-11, 96)
                ),
                ListPolynomial(
                    Rational(1213, 3),
                    Rational(0, 1),
                    Rational(-135, 2),
                    Rational(0, 1),
                    Rational(15, 4),
                    Rational(0, 1),
                    Rational(-5, 72)
                )
            ),
            ListRationalFunction(
                ListPolynomial(
                    Rational(2, 9),
                    Rational(0),
                    Rational(0),
                    Rational(0),
                    Rational(-11, 6)
                ),
                ListPolynomial(
                    Rational(-2, 3),
                    Rational(0),
                    Rational(0),
                    Rational(-5, 9)
                )
            ).substitute(RationalField,
                ListPolynomial(
                    Rational(-9, 1),
                    Rational(0),
                    Rational(2, 4)
                )
            ),
            "test 5"
        )
    }
    @Test
    @Ignore // FIXME: This tests work only for sane realisations of the substitutions. Currently, it is not.
            //  Sane algorithm for substitution p(q/r) (p, q, and r are polynomials) should return denominator r^deg(p),
            //  not r^(deg(p)(deg(p)+1)/2) as it is now.
    fun test_RationalFunction_substitute_RationalFunction() {
        assertEquals(
            ListRationalFunction(
                ListPolynomial(Rational(0)),
                ListPolynomial(Rational(1))
            ),
            ListRationalFunction(
                ListPolynomial(Rational(1), Rational(-2), Rational(1)),
                ListPolynomial(Rational(1))
            ).substitute(RationalField,
                ListRationalFunction(
                    ListPolynomial(Rational(1)),
                    ListPolynomial(Rational(1))
                )
            ),
            "test 1"
        )
        assertEquals(
            ListRationalFunction(
                ListPolynomial(
                    Rational(130087, 3888),
                    Rational(-2866333, 65610),
                    Rational(-5076229, 97200),
                    Rational(222136997, 3280500),
                    Rational(754719329, 20995200),
                    Rational(-12010283, 324000),
                    Rational(-2011967, 172800),
                    Rational(18607, 2880),
                    Rational(4705, 4096)
                ),
                ListPolynomial(
                    Rational(-143820355, 3779136),
                    Rational(73886869, 1574640),
                    Rational(1440175193, 15746400),
                    Rational(-5308968857, 52488000),
                    Rational(-186910083731, 2099520000),
                    Rational(125043463, 1555200),
                    Rational(5299123, 388800),
                    Rational(-213757, 15360),
                    Rational(1380785, 147456)
                )
            ),
            ListRationalFunction(
                ListPolynomial(
                    Rational(1, 1),
                    Rational(-10, 5),
                    Rational(18, 8),
                    Rational(-8, 8)
                ),
                ListPolynomial(
                    Rational(-14, 8),
                    Rational(-14, 8),
                    Rational(-19, 6),
                    Rational(14, 3),
                    Rational(8, 9)
                )
            ).substitute(RationalField,
                ListRationalFunction(
                    ListPolynomial(
                        Rational(14, 9),
                        Rational(-2, 5),
                        Rational(-14, 7)
                    ),
                    ListPolynomial(
                        Rational(-6, 4),
                        Rational(5, 9),
                        Rational(1, 8)
                    )
                )
            ),
            "test 2"
        )
        assertEquals(
            ListRationalFunction(
                ListPolynomial(
                    Rational(0, 1),
                    Rational(0, 1),
                    Rational(0, 1),
                    Rational(0, 1),
                    Rational(5173, 18225),
                    Rational(904291, 364500),
                    Rational(283127, 43200),
                    Rational(37189, 5760),
                    Rational(147, 128)
                ),
                ListPolynomial(
                    Rational(0, 1),
                    Rational(0, 1),
                    Rational(0, 1),
                    Rational(0, 1),
                    Rational(-163589, 911250),
                    Rational(-881831, 291600),
                    Rational(-10722229, 777600),
                    Rational(-640921, 46080),
                    Rational(86303, 9216)
                )
            ),
            ListRationalFunction(
                ListPolynomial(
                    Rational(0),
                    Rational(-10, 5),
                    Rational(18, 8),
                    Rational(-8, 8)
                ),
                ListPolynomial(
                    Rational(0),
                    Rational(-14, 8),
                    Rational(-19, 6),
                    Rational(14, 3),
                    Rational(8, 9)
                )
            ).substitute(RationalField,
                ListRationalFunction(
                    ListPolynomial(
                        Rational(0),
                        Rational(-2, 5),
                        Rational(-14, 7)
                    ),
                    ListPolynomial(
                        Rational(0),
                        Rational(5, 9),
                        Rational(1, 8)
                    )
                )
            ),
            "test 3"
        )
        assertEquals(
            ListRationalFunction(
                ListPolynomial(
                    Rational(445, 16),
                    Rational(-2011, 54),
                    Rational(1359199, 72900),
                    Rational(-135733, 32805),
                    Rational(2254, 6561),
                    Rational(0, 1),
                    Rational(0, 1),
                    Rational(0, 1),
                    Rational(0, 1)
                ),
                ListPolynomial(
                    Rational(-2018387, 46656),
                    Rational(82316437, 1574640),
                    Rational(-9335047, 393660),
                    Rational(15765889, 3280500),
                    Rational(-242089, 656100),
                    Rational(0, 1),
                    Rational(0, 1),
                    Rational(0, 1),
                    Rational(0, 1)
                )
            ),
            ListRationalFunction(
                ListPolynomial(
                    Rational(1, 1),
                    Rational(-10, 5),
                    Rational(18, 8),
                    Rational(0)
                ),
                ListPolynomial(
                    Rational(-14, 8),
                    Rational(-14, 8),
                    Rational(-19, 6),
                    Rational(14, 3),
                    Rational(0)
                )
            ).substitute(RationalField,
                ListRationalFunction(
                    ListPolynomial(
                        Rational(14, 9),
                        Rational(-2, 5),
                        Rational(0)
                    ),
                    ListPolynomial(
                        Rational(-6, 4),
                        Rational(5, 9),
                        Rational(0)
                    )
                )
            ),
            "test 4"
        )
        assertEquals(
            ListRationalFunction(
                ListPolynomial(
                    Rational(41635, 3888),
                    Rational(0, 1),
                    Rational(-279187, 11664),
                    Rational(0, 1),
                    Rational(103769, 3456),
                    Rational(0, 1),
                    Rational(-11017, 768),
                    Rational(0, 1),
                    Rational(4097, 4096)
                ),
                ListPolynomial(
                    Rational(-13811791, 3779136),
                    Rational(0, 1),
                    Rational(-9999395, 419904),
                    Rational(0, 1),
                    Rational(6376601, 124416),
                    Rational(0, 1),
                    Rational(-3668315, 82944),
                    Rational(0, 1),
                    Rational(2097089, 147456)
                )
            ),
            ListRationalFunction(
                ListPolynomial(
                    Rational(1, 1),
                    Rational(0),
                    Rational(0),
                    Rational(-8, 8)
                ),
                ListPolynomial(
                    Rational(-14, 8),
                    Rational(0),
                    Rational(0),
                    Rational(0),
                    Rational(8, 9)
                )
            ).substitute(RationalField,
                ListRationalFunction(
                    ListPolynomial(
                        Rational(14, 9),
                        Rational(0),
                        Rational(-14, 7)
                    ),
                    ListPolynomial(
                        Rational(-6, 4),
                        Rational(0),
                        Rational(1, 8)
                    )
                )
            ),
            "test 5"
        )
    }
    @Test
    fun test_Polynomial_derivative() {
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
    fun test_Polynomial_nthDerivative() {
        assertEquals(
            ListPolynomial(Rational(-2), Rational(2)),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).nthDerivative(RationalField, 1),
            "test 1"
        )
        assertFailsWithTypeAndMessage<IllegalArgumentException>(
            "Order of derivative must be non-negative",
            "test2"
        ) {
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
    fun test_Polynomial_antiderivative() {
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
    fun test_Polynomial_nthAntiderivative() {
        assertEquals(
            ListPolynomial(Rational(0), Rational(1), Rational(-1), Rational(1, 3)),
            ListPolynomial(Rational(1), Rational(-2), Rational(1)).nthAntiderivative(RationalField, 1),
            "test 1"
        )
        assertFailsWithTypeAndMessage<IllegalArgumentException>(
            "Order of antiderivative must be non-negative",
            "test2"
        ) {
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