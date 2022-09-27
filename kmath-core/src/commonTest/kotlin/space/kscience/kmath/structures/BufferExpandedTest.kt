package space.kscience.kmath.structures

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

internal class BufferExpandedTest {
    private val buffer = (0..100).toList().asBuffer()

    @Test
    fun shrink(){
        val view = buffer.slice(20..30)
        assertEquals(20, view[0])
        assertEquals(30, view[10])
        assertFails { view[11] }
    }

    @Test
    fun expandNegative(){
        val view: BufferView<Int> = buffer.expand(-20..113,0)
        assertEquals(0,view[4])
        assertEquals(0,view[123])
        assertEquals(100, view[120])
        assertFails { view[-2] }
        assertFails { view[134] }
    }
}