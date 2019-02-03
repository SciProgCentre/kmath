package scientifik.kmath.sequential

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import scientifik.kmath.operations.Space
import scientifik.kmath.structures.runBlocking
import java.util.*

/**
 * A moving average with fixed window
 */
class MovingAverage<T : Any>(val window: Int, val context: Space<T>) : Accumulator<T> {
    private val outputChannel = Channel<T>()
    private val queue = ArrayDeque<T>(window)
    private val mutex = Mutex()

    override suspend fun send(value: T) {
        mutex.withLock {
            queue.add(value)
            if (queue.size == window) {
                val sum = queue.fold(context.zero) { a, b -> context.run { a + b } }
                outputChannel.send(context.run { sum / window })
                queue.pop()
            }
        }
    }

    override fun push(value: T) = runBlocking { send(value) }

    val output: ReceiveChannel<T> = outputChannel
}