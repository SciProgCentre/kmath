/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd4j

import org.nd4j.linalg.factory.Nd4j
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.asList
import space.kscience.kmath.nd.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.fail

internal class Nd4jArrayStructureTest {
    @OptIn(PerformancePitfall::class)
    @Test
    fun testElements() {
        val nd = Nd4j.create(doubleArrayOf(1.0, 2.0, 3.0))!!
        val struct = nd.asDoubleStructure()
        val res = struct.elements().map(Pair<IntArray, Double>::second).toList()
        assertEquals(listOf(1.0, 2.0, 3.0), res)
    }

    @Test
    fun testShape() {
        val nd = Nd4j.rand(10, 2, 3, 6) ?: fail()
        val struct = nd.asDoubleStructure()
        assertEquals(intArrayOf(10, 2, 3, 6).toList(), struct.shape.asList())
    }

    @Test
    fun testEquals() {
        val nd1 = Nd4j.create(doubleArrayOf(1.0, 2.0, 3.0)) ?: fail()
        val struct1 = nd1.asDoubleStructure()
        assertEquals(struct1, struct1)
        assertNotEquals(struct1 as Any?, null)
        val nd2 = Nd4j.create(doubleArrayOf(1.0, 2.0, 3.0)) ?: fail()
        val struct2 = nd2.asDoubleStructure()
        assertEquals(struct1, struct2)
        assertEquals(struct2, struct1)
        val nd3 = Nd4j.create(doubleArrayOf(1.0, 2.0, 3.0)) ?: fail()
        val struct3 = nd3.asDoubleStructure()
        assertEquals(struct2, struct3)
        assertEquals(struct1, struct3)
    }

    @Test
    fun testHashCode() {
        val nd1 = Nd4j.create(doubleArrayOf(1.0, 2.0, 3.0)) ?: fail()
        val struct1 = nd1.asDoubleStructure()
        val nd2 = Nd4j.create(doubleArrayOf(1.0, 2.0, 3.0)) ?: fail()
        val struct2 = nd2.asDoubleStructure()
        assertEquals(struct1.hashCode(), struct2.hashCode())
    }

    @Test
    fun testDimension() {
        val nd = Nd4j.rand(8, 16, 3, 7, 1)!!
        val struct = nd.asFloatStructure()
        assertEquals(5, struct.dimension)
    }

    @Test
    fun testGet() {
        val nd = Nd4j.rand(10, 2, 3, 6) ?: fail()
        val struct = nd.asIntStructure()
        assertEquals(nd.getInt(0, 0, 0, 0), struct[0, 0, 0, 0])
    }

    @Test
    fun testSet() {
        val nd = Nd4j.rand(17, 12, 4, 8)!!
        val struct = nd.asIntStructure()
        struct[intArrayOf(1, 2, 3, 4)] = 777
        assertEquals(777, struct[1, 2, 3, 4])
    }
}
