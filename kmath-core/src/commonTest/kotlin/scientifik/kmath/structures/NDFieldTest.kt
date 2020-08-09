package scientifik.kmath.structures

import kotlin.test.Test
import kotlin.test.assertEquals


class NDFieldTest {
    @Test
    fun testStrides() {
        val ndArray = NDElement.real(intArrayOf(10, 10)) { (it[0] + it[1]).toDouble() }
        assertEquals(ndArray[5, 5], 10.0)
    }
}
