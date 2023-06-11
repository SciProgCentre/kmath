/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

/**
 * The same as [zipWithNext], but includes link between last and first element
 */
public inline fun <T, R> List<T>.zipWithNextCircular(transform: (a: T, b: T) -> R): List<R> {
    if (size < 2) return emptyList()
    return indices.map { i ->
        if (i == size - 1) {
            transform(last(), first())
        } else {
            transform(get(i), get(i + 1))
        }
    }
}

public fun <T> List<T>.zipWithNextCircular(): List<Pair<T, T>> = zipWithNextCircular { l, r -> l to r }