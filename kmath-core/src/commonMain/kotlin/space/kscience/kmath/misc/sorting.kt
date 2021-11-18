/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.misc

import kotlin.comparisons.*
import space.kscience.kmath.structures.Buffer

/**
 * Return a new list filled with buffer indices. Indice order is defined by sorting associated buffer value.
 * This feature allows to sort buffer values without reordering its content.
 *
 * @return List of buffer indices, sorted by associated value.
 */
@PerformancePitfall
@UnstableKMathAPI
public fun <V: Comparable<V>> Buffer<V>.permSort() : IntArray = _permSortWith(compareBy<Int> { get(it) })

@PerformancePitfall
@UnstableKMathAPI
public fun <V: Comparable<V>> Buffer<V>.permSortDescending() : IntArray = _permSortWith(compareByDescending<Int> { get(it) })

@PerformancePitfall
@UnstableKMathAPI
public fun <V, C: Comparable<C>> Buffer<V>.permSortBy(selector: (V) -> C) : IntArray = _permSortWith(compareBy<Int> { selector(get(it)) })

@PerformancePitfall
@UnstableKMathAPI
public fun <V, C: Comparable<C>> Buffer<V>.permSortByDescending(selector: (V) -> C) : IntArray = _permSortWith(compareByDescending<Int> { selector(get(it)) })

@PerformancePitfall
@UnstableKMathAPI
public fun <V> Buffer<V>.permSortWith(comparator : Comparator<V>) : IntArray = _permSortWith { i1, i2 -> comparator.compare(get(i1), get(i2)) }

@PerformancePitfall
@UnstableKMathAPI
private fun <V> Buffer<V>._permSortWith(comparator : Comparator<Int>) : IntArray {
    if (size < 2) return IntArray(size)

    /* TODO: optimisation : keep a constant big array of indices (Ex: from 0 to 4096), then create indice
     * arrays more efficiently by copying subpart of cached one. For bigger needs, we could copy entire
     * cached array, then fill remaining indices manually. Not done for now, because:
     *  1. doing it right would require some statistics about common used buffer sizes.
     *  2. Some benchmark would be needed to ensure it would really provide better performance
     */
    val packedIndices = IntArray(size) { idx -> idx }

    /* TODO: find an efficient way to sort in-place instead, and return directly the IntArray.
     * Not done for now, because no standard utility is provided yet. An open issue exists for this.
     * See: https://youtrack.jetbrains.com/issue/KT-37860
     */
    return packedIndices.sortedWith(comparator).toIntArray()
}
