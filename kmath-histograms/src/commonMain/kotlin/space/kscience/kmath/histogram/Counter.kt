/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Group

/**
 * Common representation for atomic counters
 */
public interface Counter<T : Any> {
    public fun add(delta: T)
    public val value: T

    public companion object {
        public fun ofDouble(): ObjectCounter<Double> = ObjectCounter(DoubleField)
        public fun <T: Any> of(group: Group<T>): ObjectCounter<T> = ObjectCounter(group)
    }
}

public class IntCounter : Counter<Int> {
    private val innerValue = atomic(0)

    override fun add(delta: Int) {
        innerValue += delta
    }

    override val value: Int get() = innerValue.value
}

public operator fun IntCounter.inc(): IntCounter {
    add(1)
    return this
}

public operator fun IntCounter.dec(): IntCounter {
    add(-1)
    return this
}

public class LongCounter : Counter<Long> {
    private val innerValue = atomic(0L)

    override fun add(delta: Long) {
        innerValue += delta
    }

    override val value: Long get() = innerValue.value
}

public operator fun LongCounter.inc(): LongCounter {
    add(1L)
    return this
}

public operator fun LongCounter.dec(): LongCounter {
    add(-1L)
    return this
}

public class ObjectCounter<T : Any>(private val group: Group<T>) : Counter<T> {
    private val innerValue = atomic(group.zero)

    override fun add(delta: T) {
        innerValue.getAndUpdate { group.run { it + delta } }
    }

    override val value: T get() = innerValue.value
}


