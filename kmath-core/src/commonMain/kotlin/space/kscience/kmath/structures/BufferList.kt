/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import kotlin.jvm.JvmInline

@JvmInline
private value class BufferList<T>(val buffer: Buffer<T>) : List<T> {
    override val size: Int get() = buffer.size

    override fun get(index: Int): T = buffer[index]

    override fun isEmpty(): Boolean = buffer.size == 0

    override fun iterator(): Iterator<T> = buffer.iterator()

    override fun listIterator(index: Int): ListIterator<T> = object : ListIterator<T> {
        var currentIndex = index

        override fun hasNext(): Boolean = currentIndex < buffer.size - 1

        override fun hasPrevious(): Boolean = currentIndex > 0

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            return get(currentIndex++)
        }

        override fun nextIndex(): Int = currentIndex

        override fun previous(): T {
            if (!hasPrevious()) throw NoSuchElementException()
            return get(--currentIndex)
        }

        override fun previousIndex(): Int = currentIndex - 1

    }

    override fun listIterator(): ListIterator<T> = listIterator(0)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> =
        buffer.slice(fromIndex..toIndex).asList()

    override fun lastIndexOf(element: T): Int {
        for (i in buffer.indices.reversed()) {
            if (buffer[i] == element) return i
        }
        return -1
    }

    override fun indexOf(element: T): Int {
        for (i in buffer.indices) {
            if (buffer[i] == element) return i
        }
        return -1
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        val remainingElements = HashSet(elements)
        for (e in buffer) {
            if (e in remainingElements) {
                remainingElements.remove(e)
            }
            if (remainingElements.isEmpty()) {
                return true
            }
        }
        return false
    }

    override fun contains(element: T): Boolean = indexOf(element) >= 0
}

/**
 * Returns a zero-copy list that reflects the content of the buffer.
 */
public fun <T> Buffer<T>.asList(): List<T> = BufferList(this)