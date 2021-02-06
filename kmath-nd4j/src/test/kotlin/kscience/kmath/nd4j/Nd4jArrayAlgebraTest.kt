package kscience.kmath.nd4j

import kscience.kmath.operations.invoke
import org.nd4j.linalg.factory.Nd4j
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

internal class Nd4jArrayAlgebraTest {
    @Test
    fun testProduce() {
        val res = (RealNd4jArrayField(intArrayOf(2, 2))) { produce { it.sum().toDouble() } }
        val expected = (Nd4j.create(2, 2) ?: fail()).asRealStructure()
        expected[intArrayOf(0, 0)] = 0.0
        expected[intArrayOf(0, 1)] = 1.0
        expected[intArrayOf(1, 0)] = 1.0
        expected[intArrayOf(1, 1)] = 2.0
        assertEquals(expected, res)
    }

    @Test
    fun testMap() {
        val res = (IntNd4jArrayRing(intArrayOf(2, 2))) { one.map() { it + it * 2 } }
        val expected = (Nd4j.create(2, 2) ?: fail()).asIntStructure()
        expected[intArrayOf(0, 0)] = 3
        expected[intArrayOf(0, 1)] = 3
        expected[intArrayOf(1, 0)] = 3
        expected[intArrayOf(1, 1)] = 3
        assertEquals(expected, res)
    }

    @Test
    fun testAdd() {
        val res = (IntNd4jArrayRing(intArrayOf(2, 2))) { one + 25 }
        val expected = (Nd4j.create(2, 2) ?: fail()).asIntStructure()
        expected[intArrayOf(0, 0)] = 26
        expected[intArrayOf(0, 1)] = 26
        expected[intArrayOf(1, 0)] = 26
        expected[intArrayOf(1, 1)] = 26
        assertEquals(expected, res)
    }
}
