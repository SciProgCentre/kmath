/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals


class NumberedConstructorsTest {
    @Test
    @UnstableKMathAPI
    fun testDSL1() {
        assertEquals(
            NumberedPolynomialAsIs(
                listOf(2u, 0u, 3u) to 5,
                listOf(0u, 1u) to -6,
            ),
            Int.algebra.numberedPolynomialSpace {
                NumberedPolynomialDSL1 {
                    5 { 0 pow 2u; 2 pow 3u }
                    (-6) { 1 pow 1u }
                }
            },
            "test 1"
        )
        assertEquals(
            NumberedPolynomialAsIs(
                listOf<UInt>() to -1,
            ),
            Int.algebra.numberedPolynomialSpace {
                NumberedPolynomialDSL1 {
                    5 { }
                    (-6) { }
                }
            },
            "test 2"
        )
        assertEquals(
            NumberedPolynomialAsIs(
                listOf(2u) to -1,
            ),
            Int.algebra.numberedPolynomialSpace {
                NumberedPolynomialDSL1 {
                    5 { 0 pow 1u; 0 pow 1u }
                    (-6) { 0 pow 2u }
                }
            },
            "test 3"
        )
        assertEquals(
            NumberedPolynomialAsIs(
                listOf(2u) to -1,
            ),
            Int.algebra.numberedPolynomialSpace {
                NumberedPolynomialDSL1 {
                    5 { 0 pow 1u; 0 pow 1u }
                    (-6) { 0 pow 2u; 2 pow 0u }
                }
            },
            "test 3"
        )
    }
    @Test
    @UnstableKMathAPI
    fun testFabric() {
        assertEquals(
            NumberedPolynomialAsIs(
                listOf(2u, 0u, 3u) to 5,
                listOf(0u, 1u) to -6,
            ),
            Int.algebra {
                NumberedPolynomial(
                    listOf(2u, 0u, 3u) to 5,
                    listOf(0u, 1u) to -6,
                )
            },
            "test 1"
        )
        assertEquals(
            NumberedPolynomialAsIs(
                listOf(2u, 0u, 3u) to 5,
                listOf(0u, 1u) to -6,
            ),
            Int.algebra {
                NumberedPolynomial(
                    listOf(2u, 0u, 3u, 0u) to 5,
                    listOf(0u, 1u, 0u, 0u) to -6,
                )
            },
            "test 2"
        )
        assertEquals(
            NumberedPolynomialAsIs(
                listOf<UInt>() to -1,
            ),
            Int.algebra {
                NumberedPolynomial(
                    listOf(0u) to 5,
                    listOf(0u, 0u) to -6,
                )
            },
            "test 3"
        )
        assertEquals(
            NumberedPolynomialAsIs(
                listOf<UInt>() to 0,
            ),
            Int.algebra {
                NumberedPolynomial(
                    listOf(0u) to 5,
                    listOf(0u, 0u) to -5,
                )
            },
            "test 4"
        )
    }
}