/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */


package space.kscience.kmath.misc

import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.VirtualBuffer

/**
 * Return a new array filled with buffer indices. Indices order is defined by sorting associated buffer value.
 * This feature allows sorting buffer values without reordering its content.
 *
 * @return Buffer indices, sorted by associated value.
 */
@UnstableKMathAPI
public fun <V : Comparable<V>> Buffer<V>.indicesSorted(): IntArray = permSortIndicesWith(compareBy { get(it) })

/**
 * Create a zero-copy virtual buffer that contains the same elements but in ascending order
 */
@OptIn(UnstableKMathAPI::class)
public fun <V : Comparable<V>> Buffer<V>.sorted(): Buffer<V> {
    val permutations = indicesSorted()
    return VirtualBuffer(size) { this[permutations[it]] }
}

@UnstableKMathAPI
public fun <V : Comparable<V>> Buffer<V>.indicesSortedDescending(): IntArray =
    permSortIndicesWith(compareByDescending { get(it) })

/**
 * Create a zero-copy virtual buffer that contains the same elements but in descending order
 */
@OptIn(UnstableKMathAPI::class)
public fun <V : Comparable<V>> Buffer<V>.sortedDescending(): Buffer<V> {
    val permutations = indicesSortedDescending()
    return VirtualBuffer(size) { this[permutations[it]] }
}

@UnstableKMathAPI
public fun <V, C : Comparable<C>> Buffer<V>.indicesSortedBy(selector: (V) -> C): IntArray =
    permSortIndicesWith(compareBy { selector(get(it)) })

@OptIn(UnstableKMathAPI::class)
public fun <V, C : Comparable<C>> Buffer<V>.sortedBy(selector: (V) -> C): Buffer<V> {
    val permutations = indicesSortedBy(selector)
    return VirtualBuffer(size) { this[permutations[it]] }
}

@UnstableKMathAPI
public fun <V, C : Comparable<C>> Buffer<V>.indicesSortedByDescending(selector: (V) -> C): IntArray =
    permSortIndicesWith(compareByDescending { selector(get(it)) })

@OptIn(UnstableKMathAPI::class)
public fun <V, C : Comparable<C>> Buffer<V>.sortedByDescending(selector: (V) -> C): Buffer<V> {
    val permutations = indicesSortedByDescending(selector)
    return VirtualBuffer(size) { this[permutations[it]] }
}

@UnstableKMathAPI
public fun <V> Buffer<V>.indicesSortedWith(comparator: Comparator<V>): IntArray =
    permSortIndicesWith { i1, i2 -> comparator.compare(get(i1), get(i2)) }

private fun <V> Buffer<V>.permSortIndicesWith(comparator: Comparator<Int>): IntArray {
    if (size < 2) return IntArray(size) { 0 }

    /* TODO: optimisation : keep a constant big array of indices (Ex: from 0 to 4096), then create indices
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

/**
 * Checks that the [Buffer] is sorted (ascending) and throws [IllegalArgumentException] if it is not.
 */
public fun <T : Comparable<T>> Buffer<T>.requireSorted() {
    for (i in 0..(size - 2)) {
        require(get(i + 1) >= get(i)) { "The buffer is not sorted at index $i" }
    }
}
