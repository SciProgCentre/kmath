package scientifik.kmath.streaming

import kotlinx.coroutines.runBlocking
import scientifik.kmath.structures.asSequence
import kotlin.test.Test
import kotlin.test.assertEquals

class RingBufferTest {
    @Test
    fun testPush() {
        val buffer = RingBuffer.build(20, Double.NaN)
        runBlocking {
            for (i in 1..30) {
                buffer.push(i.toDouble())
            }
            assertEquals(410.0, buffer.asSequence().sum())
        }
    }
}