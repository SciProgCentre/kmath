package scientifik.kmath.nd4j

import org.nd4j.linalg.factory.Nd4j
import scientifik.kmath.structures.get
import kotlin.test.Test
import kotlin.test.assertEquals

internal class INDArrayStructureTest {
    @Test
    fun testElements() {
        val nd = Nd4j.create(doubleArrayOf(1.0, 2.0, 3.0))!!
        val struct = INDArrayDoubleStructure(nd)
        val res = struct.elements().map(Pair<IntArray, Double>::second).toList()
        assertEquals(listOf(1.0, 2.0, 3.0), res)
    }

    @Test
    fun testShape() {
        val nd = Nd4j.rand(10, 2, 3, 6)!!
        val struct = INDArrayLongStructure(nd)
        assertEquals(intArrayOf(10, 2, 3, 6).toList(), struct.shape.toList())
    }

    @Test
    fun testEquals() {
        val nd1 = Nd4j.create(doubleArrayOf(1.0, 2.0, 3.0))!!
        val struct1 = INDArrayDoubleStructure(nd1)
        val nd2 = Nd4j.create(doubleArrayOf(1.0, 2.0, 3.0))!!
        val struct2 = INDArrayDoubleStructure(nd2)
        assertEquals(struct1, struct2)
    }

    @Test
    fun testDimension() {
        val nd = Nd4j.rand(8, 16, 3, 7, 1)!!
        val struct = INDArrayFloatStructure(nd)
        assertEquals(5, struct.dimension)
    }

    @Test
    fun testGet() {
        val nd = Nd4j.rand(10, 2, 3, 6)!!
        val struct = INDArrayIntStructure(nd)
        assertEquals(nd.getInt(0, 0, 0, 0), struct[0, 0, 0, 0])
    }

    @Test
    fun testSet() {
        val nd = Nd4j.rand(17, 12, 4, 8)!!
        val struct = INDArrayIntStructure(nd)
        struct[intArrayOf(1, 2, 3, 4)] = 777
        assertEquals(777, struct[1, 2, 3, 4])
    }
}
