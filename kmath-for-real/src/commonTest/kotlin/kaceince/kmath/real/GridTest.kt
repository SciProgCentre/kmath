package kaceince.kmath.real

import space.kscience.kmath.real.step
import kotlin.test.Test
import kotlin.test.assertEquals

class GridTest {
    @Test
    fun testStepGrid() {
        val grid = 0.0..1.0 step 0.2
        assertEquals(6, grid.size)
    }
}