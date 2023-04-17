/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.PerformancePitfall

@OptIn(PerformancePitfall::class)
public fun <T> StructureND<T>.roll(axis: Int, step: Int = 1): StructureND<T> {
    require(axis in shape.indices) { "Axis $axis is outside of shape dimensions: [0, ${shape.size})" }
    return VirtualStructureND(shape) { index ->
        val newIndex: IntArray = IntArray(index.size) { indexAxis ->
            if (indexAxis == axis) {
                (index[indexAxis] + step).mod(shape[indexAxis])
            } else {
                index[indexAxis]
            }
        }
        get(newIndex)
    }
}

@OptIn(PerformancePitfall::class)
public fun <T> StructureND<T>.roll(pair: Pair<Int, Int>, vararg others: Pair<Int, Int>): StructureND<T> {
    val axisMap: Map<Int, Int> = mapOf(pair, *others)
    require(axisMap.keys.all { it in shape.indices }) { "Some of axes ${axisMap.keys} is outside of shape dimensions: [0, ${shape.size})" }
    return VirtualStructureND(shape) { index ->
        val newIndex: IntArray = IntArray(index.size) { indexAxis ->
            val offset = axisMap[indexAxis] ?: 0
            (index[indexAxis] + offset).mod(shape[indexAxis])
        }
        get(newIndex)
    }
}