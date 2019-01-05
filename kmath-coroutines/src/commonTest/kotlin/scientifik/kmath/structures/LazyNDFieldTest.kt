package scientifik.kmath.structures

import scientifik.kmath.operations.IntField
import kotlin.test.Test
import kotlin.test.assertEquals


class LazyNDFieldTest {
    @Test
    fun testLazyStructure() {
        var counter = 0
        val regularStructure = NDField.auto(intArrayOf(2, 2, 2), IntField).produce { it[0] + it[1] - it[2] }
        val result = (regularStructure.lazy(IntField) + 2).map {
            counter++
            it * it
        }
        assertEquals(4, result[0, 0, 0])
        assertEquals(1, counter)
    }
}