package space.kscience.kmath.misc

import kotlin.test.Test
import kotlin.test.assertEquals

class CumulativeKtTest {
    @Test
    fun testCumulativeSum() {
        val initial = listOf(-1.0, 2.0, 1.0, 1.0)
        val cumulative = initial.cumulativeSum()
        assertEquals(listOf(-1.0, 1.0, 2.0, 3.0), cumulative)
    }
}
