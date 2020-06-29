package scientifik.kmath.nd4j

import org.nd4j.linalg.factory.Nd4j
import scientifik.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

internal class INDArrayAlgebraTest {
    @Test
    fun testProduce() {
        val res = (RealINDArrayField(intArrayOf(2, 2))) { produce { it.sum().toDouble() } }
        val expected = Nd4j.create(2, 2)!!.asRealStructure()
        expected[intArrayOf(0, 0)] = 0.0
        expected[intArrayOf(0, 1)] = 1.0
        expected[intArrayOf(1, 0)] = 1.0
        expected[intArrayOf(1, 1)] = 2.0
        assertEquals(expected, res)
    }

    @Test
    fun testMap() {
        val res = (IntINDArrayRing(intArrayOf(2, 2))) { map(one) { it + it * 2 } }
        val expected = Nd4j.create(2, 2)!!.asIntStructure()
        expected[intArrayOf(0, 0)] = 3
        expected[intArrayOf(0, 1)] = 3
        expected[intArrayOf(1, 0)] = 3
        expected[intArrayOf(1, 1)] = 3
        assertEquals(expected, res)
    }

    @Test
    fun testAdd() {
        val res = (IntINDArrayRing(intArrayOf(2, 2))) { one + 25 }
        val expected = Nd4j.create(2, 2)!!.asIntStructure()
        expected[intArrayOf(0, 0)] = 26
        expected[intArrayOf(0, 1)] = 26
        expected[intArrayOf(1, 0)] = 26
        expected[intArrayOf(1, 1)] = 26
        assertEquals(expected, res)
    }
}
