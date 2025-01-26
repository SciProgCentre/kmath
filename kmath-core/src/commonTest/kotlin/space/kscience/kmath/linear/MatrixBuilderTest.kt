/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.structures.Float64
import kotlin.test.Test
import kotlin.test.assertEquals

@UnstableKMathAPI
class MatrixBuilderTest {

    @Test
    fun buildCompositeMatrix() = with(Float64.algebra.linearSpace) {

        val matrix = vstack(
            sparse(1, 5) { set(0, 4, 1.0) },
            hstack(
                sparse(4, 4).fill(
                    1.0, 1.0, 0.0, 0.0,
                    0.0, 1.0, 1.0, 0.0,
                    0.0, 0.0, 1.0, 1.0,
                    0.0, 0.0, 0.0, 1.0
                ),
                sparse(4, 1)
            )
        )

        println(StructureND.toString(matrix))

        assertEquals(1.0, matrix[0, 4])
    }
}