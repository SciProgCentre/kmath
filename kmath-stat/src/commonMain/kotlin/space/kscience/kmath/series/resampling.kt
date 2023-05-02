/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.operations.BufferAlgebra
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.sumWithGroupOf

public fun <T, A : Ring<T>, BA : BufferAlgebra<T, A>, L : Comparable<L>> MonotonicSeriesAlgebra<T, A, BA, L>.import(
    data: List<Pair<L, T>>,
): Series<T> {
    val groupedData: Map<Int, List<Pair<L, T>>> = data.groupBy { floorOffset(it.first) }
    val minIndex = groupedData.minOf { it.key }
    val maxIndex = groupedData.maxOf { it.key }
    return elementAlgebra.bufferFactory(maxIndex - minIndex) { relativeIndex ->
        val index = relativeIndex + minIndex
        groupedData[index]?.sumWithGroupOf(elementAlgebra) { it.second } ?: elementAlgebra.zero
    }.moveTo(minIndex)
}