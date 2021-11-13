/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.misc

import kotlin.comparisons.*
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.indices

/**
 * Return a new list filled with buffer indices. Indice order is defined by sorting associated buffer value.
 * This feature allows to sort buffer values without reordering its content.
 * 
 * @param descending True to revert sort order from highest to lowest values. Default to ascending order.
 * @return List of buffer indices, sorted by associated value.
 */
@PerformancePitfall
@UnstableKMathAPI
public fun <V: Comparable<V>> Buffer<V>.permSort(descending : Boolean = false) : IntArray {
    if (size < 2) return IntArray(size)

    val comparator = if (descending) compareByDescending<Int> { get(it) } else compareBy<Int> { get(it) }

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
