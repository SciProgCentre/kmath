/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.histogram

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.RealField

/**
 * Common representation for atomic counters
 */
public interface Counter<T : Any> {
    public fun add(delta: T)
    public val value: T
    public companion object{
        public fun real(): ObjectCounter<Double> = ObjectCounter(RealField)
    }
}

public class IntCounter : Counter<Int> {
    private val innerValue = atomic(0)

    override fun add(delta: Int) {
        innerValue += delta
    }

    override val value: Int get() = innerValue.value
}

public class LongCounter : Counter<Long> {
    private val innerValue = atomic(0L)

    override fun add(delta: Long) {
        innerValue += delta
    }

    override val value: Long get() = innerValue.value
}

public class ObjectCounter<T : Any>(public val space: Group<T>) : Counter<T> {
    private val innerValue = atomic(space.zero)

    override fun add(delta: T) {
        innerValue.getAndUpdate { space.run { it + delta } }
    }

    override val value: T get() = innerValue.value
}


