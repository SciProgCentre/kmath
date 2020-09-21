package kscience.kmath.streaming

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kscience.kmath.structures.asSequence
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RingBufferTest {
    @Test
    fun push() {
        val buffer = RingBuffer.build(20, Double.NaN)
        runBlocking {
            for (i in 1..30) {
                buffer.push(i.toDouble())
            }
            assertEquals(410.0, buffer.asSequence().sum())
        }
    }

    @Test
    fun windowed() {
        val flow = flow {
            var i = 0
            while (true) emit(i++)
        }

        val windowed = flow.windowed(10)

        runBlocking {
            val first = windowed.take(1).single()
            val res = windowed.take(15).map { it.asSequence().average() }.toList()
            assertEquals(0.0, res[0])
            assertEquals(4.5, res[9])
            assertEquals(9.5, res[14])
        }
    }
}