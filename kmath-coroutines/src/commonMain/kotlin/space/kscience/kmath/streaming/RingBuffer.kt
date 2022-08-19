/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.streaming

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.VirtualBuffer

/**
 * Thread-safe ring buffer
 */
@Suppress("UNCHECKED_CAST")
public class RingBuffer<T>(
    private val buffer: MutableBuffer<T?>,
    private var startIndex: Int = 0,
    size: Int = 0,
) : Buffer<T> {
    private val mutex: Mutex = Mutex()

    override var size: Int = size
        private set

    override operator fun get(index: Int): T {
        require(index >= 0) { "Index must be positive" }
        require(index < size) { "Index $index is out of circular buffer size $size" }
        return buffer[startIndex.forward(index)] as T
    }

    public fun isFull(): Boolean = size == buffer.size

    /**
     * Iterator could provide wrong results if buffer is changed in initialization (iteration is safe)
     */
    override operator fun iterator(): Iterator<T> = object : AbstractIterator<T>() {
        private var count = size
        private var index = startIndex
        val copy = buffer.copy()

        override fun computeNext() {
            if (count == 0) done() else {
                setNext(copy[index] as T)
                index = index.forward(1)
                count--
            }
        }
    }

    /**
     * A safe snapshot operation
     */
    public suspend fun snapshot(): Buffer<T> = mutex.withLock {
        val copy = buffer.copy()
        VirtualBuffer(size) { i -> copy[startIndex.forward(i)] as T }
    }

    public suspend fun push(element: T) {
        mutex.withLock {
            buffer[startIndex.forward(size)] = element
            if (isFull()) startIndex++ else size++
        }
    }


    @Suppress("NOTHING_TO_INLINE")
    private inline fun Int.forward(n: Int): Int = (this + n) % (buffer.size)

    override fun toString(): String = Buffer.toString(this)

    public companion object {
        public inline fun <reified T : Any> build(size: Int, empty: T): RingBuffer<T> {
            val buffer = MutableBuffer.auto(size) { empty } as MutableBuffer<T?>
            return RingBuffer(buffer)
        }

        /**
         * Slow yet universal buffer
         */
        public fun <T> boxing(size: Int): RingBuffer<T> {
            val buffer: MutableBuffer<T?> = MutableBuffer.boxing(size) { null }
            return RingBuffer(buffer)
        }
    }
}
