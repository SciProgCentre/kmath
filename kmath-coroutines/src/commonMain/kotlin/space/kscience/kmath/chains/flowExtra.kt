/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.chains

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.scan
import space.kscience.kmath.operations.GroupOps
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.invoke

/**
 * Return a [Flow] of a cumulative sum of elements in the flow. The operation is _intermediate_ and _stateful_.
 */
public fun <T> Flow<T>.cumulativeSum(group: GroupOps<T>): Flow<T> = with(group) {
    runningReduce { sum, element -> sum + element }
}

/**
 * Return a [Flow] of mean values of elements in the flow. The operation is _intermediate_ and _stateful_.
 */
public fun <T, S> Flow<T>.mean(space: S): Flow<T> where S : Ring<T>, S : ScaleOperations<T> = space {
    data class Accumulator(var sum: T, var num: Int)

    scan(Accumulator(zero, 0)) { sum, element ->
        sum.apply {
            this.sum += element
            this.num += 1
        }
    }.map { it.sum / it.num }
}