/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.operations.Float64Field
import kotlin.test.Test
import kotlin.test.assertEquals

class NdOperationsTest {
    @Test
    fun roll() {
        val structure = Float64Field.ndAlgebra.structureND(5, 5) { index ->
            index.sumOf { it.toDouble() }
        }

        println(StructureND.toString(structure))

        val rolled = structure.roll(0, -1)

        println(StructureND.toString(rolled))

        assertEquals(4.0, rolled[0, 0])
    }

}