/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd4j

import space.kscience.kmath.misc.toIntExact

internal fun LongArray.toIntArray(): IntArray = IntArray(size) { this[it].toIntExact() }

internal fun LongArray.linspace(start: Long, stop: Long) = Array(this.size) {
    start + it * ((stop - start) / (this.size - 1))
}

internal fun LongArray.zeros() = Array(this.size) { 0 }

internal fun LongArray.ones() = Array(this.size) { 1 }

internal fun repeat(number: Long, size: Int) = LongArray(size) { number }