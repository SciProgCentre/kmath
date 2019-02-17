package scientifik.kmath.sequential

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.BufferFactory

/**
 * A processor that collects incoming elements into fixed size buffers
 */
class JoinProcessor<T>(
    scope: CoroutineScope,
    bufferSize: Int,
    bufferFactory: BufferFactory<T> = Buffer.Companion::boxing
) : AbstractProcessor<T, Buffer<T>>(scope) {

    private val input = Channel<T>(bufferSize)

    private val output = produce(coroutineContext) {
        val list = ArrayList<T>(bufferSize)
        while (isActive) {
            list.clear()
            repeat(bufferSize) {
                list.add(input.receive())
            }
            val buffer = bufferFactory(bufferSize) { list[it] }
            send(buffer)
        }
    }

    override suspend fun receive(): Buffer<T> = output.receive()

    override suspend fun send(value: T) {
        input.send(value)
    }
}

/**
 * A processor that splits incoming buffers into individual elements
 */
class SplitProcessor<T>(scope: CoroutineScope) : AbstractProcessor<Buffer<T>, T>(scope) {

    private val input = Channel<Buffer<T>>()

    private val mutex = Mutex()

    private var currentBuffer: Buffer<T>? = null

    private var pos = 0


    override suspend fun receive(): T {
        mutex.withLock {
            while (currentBuffer == null || pos == currentBuffer!!.size) {
                currentBuffer = input.receive()
                pos = 0
            }
            return currentBuffer!![pos].also { pos++ }
        }
    }

    override suspend fun send(value: Buffer<T>) {
        input.send(value)
    }
}

fun <T> Producer<T>.chunked(chunkSize: Int, bufferFactory: BufferFactory<T>) =
    JoinProcessor<T>(this, chunkSize, bufferFactory).also { connect(it) }

inline fun <reified T : Any> Producer<T>.chunked(chunkSize: Int) =
    JoinProcessor<T>(this, chunkSize, Buffer.Companion::auto).also { connect(it) }



