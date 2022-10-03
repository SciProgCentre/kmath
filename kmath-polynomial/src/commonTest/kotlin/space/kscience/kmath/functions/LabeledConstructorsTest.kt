/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.functions.testUtils.t
import space.kscience.kmath.functions.testUtils.x
import space.kscience.kmath.functions.testUtils.y
import space.kscience.kmath.functions.testUtils.z
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class LabeledConstructorsTest {
    @Test
    @UnstableKMathAPI
    fun testDSL1() {
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf(x to 2u, z to 3u) to 5,
                mapOf(y to 1u) to -6,
            ),
            Int.algebra.labeledPolynomialSpace {
                LabeledPolynomialDSL1 {
                    5 { x pow 2u; z pow 3u }
                    (-6) { y pow 1u }
                }
            },
            "test 1"
        )
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf<Symbol, UInt>() to -1,
            ),
            Int.algebra.labeledPolynomialSpace {
                LabeledPolynomialDSL1 {
                    5 { }
                    (-6) { }
                }
            },
            "test 2"
        )
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf(x to 2u) to -1,
            ),
            Int.algebra.labeledPolynomialSpace {
                LabeledPolynomialDSL1 {
                    5 { x pow 1u; x pow 1u }
                    (-6) { x pow 2u }
                }
            },
            "test 3"
        )
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf(x to 2u) to -1,
            ),
            Int.algebra.labeledPolynomialSpace {
                LabeledPolynomialDSL1 {
                    5 { x pow 1u; x pow 1u }
                    (-6) { x pow 2u; z pow 0u }
                }
            },
            "test 3"
        )
    }
    @Test
    @UnstableKMathAPI
    fun testFabric() {
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf(x to 2u, z to 3u) to 5,
                mapOf(y to 1u) to -6,
            ),
            Int.algebra {
                LabeledPolynomial(
                    mapOf(x to 2u, z to 3u) to 5,
                    mapOf(y to 1u) to -6,
                )
            },
            "test 1"
        )
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf(x to 2u, z to 3u) to 5,
                mapOf(y to 1u) to -6,
            ),
            Int.algebra {
                LabeledPolynomial(
                    mapOf(x to 2u, y to 0u, z to 3u, t to 0u) to 5,
                    mapOf(x to 0u, y to 1u, z to 0u, t to 0u) to -6,
                )
            },
            "test 2"
        )
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf<Symbol, UInt>() to -1,
            ),
            Int.algebra {
                LabeledPolynomial(
                    mapOf(x to 0u) to 5,
                    mapOf(y to 0u, z to 0u) to -6,
                )
            },
            "test 3"
        )
        assertEquals(
            LabeledPolynomialAsIs(
                mapOf<Symbol, UInt>() to 0,
            ),
            Int.algebra {
                LabeledPolynomial(
                    mapOf(x to 0u) to 5,
                    mapOf(z to 0u, t to 0u) to -5,
                )
            },
            "test 4"
        )
    }
}