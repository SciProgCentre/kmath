/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd4j

import org.nd4j.linalg.factory.Nd4j
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.one
import space.kscience.kmath.nd.structureND
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Int32Ring
import space.kscience.kmath.operations.invoke
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@OptIn(PerformancePitfall::class)
internal class Nd4jArrayAlgebraTest {
    @Test
    fun testProduce() {
        val res = Float64Field.nd4j.structureND(2, 2) { it.sum().toDouble() }
        val expected = (Nd4j.create(2, 2) ?: fail()).asDoubleStructure()
        expected[intArrayOf(0, 0)] = 0.0
        expected[intArrayOf(0, 1)] = 1.0
        expected[intArrayOf(1, 0)] = 1.0
        expected[intArrayOf(1, 1)] = 2.0
        assertEquals(expected, res)
    }

    @Test
    fun testMap() {
        val res = Int32Ring.nd4j {
            one(2, 2).map { it + it * 2 }
        }
        val expected = (Nd4j.create(2, 2) ?: fail()).asIntStructure()
        expected[intArrayOf(0, 0)] = 3
        expected[intArrayOf(0, 1)] = 3
        expected[intArrayOf(1, 0)] = 3
        expected[intArrayOf(1, 1)] = 3
        assertEquals(expected, res)
    }

    @Test
    fun testAdd() {
        val res = Int32Ring.nd4j { one(2, 2) + 25 }
        val expected = (Nd4j.create(2, 2) ?: fail()).asIntStructure()
        expected[intArrayOf(0, 0)] = 26
        expected[intArrayOf(0, 1)] = 26
        expected[intArrayOf(1, 0)] = 26
        expected[intArrayOf(1, 1)] = 26
        assertEquals(expected, res)
    }

    @Test
    fun testSin() = Float64Field.nd4j {
        val initial = structureND(2, 2) { (i, j) -> if (i == j) PI / 2 else 0.0 }
        val transformed = sin(initial)
        val expected = structureND(2, 2) { (i, j) -> if (i == j) 1.0 else 0.0 }

        println(transformed)
        assertTrue { StructureND.contentEquals(transformed, expected) }
    }
}
