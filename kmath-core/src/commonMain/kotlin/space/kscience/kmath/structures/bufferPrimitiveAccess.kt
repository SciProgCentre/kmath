/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.kmath.UnstableKMathAPI

/**
 * Non-boxing access to primitive [Double]
 */
@UnstableKMathAPI
public fun Buffer<Double>.getDouble(index: Int): Double = if (this is BufferView) {
    val originIndex = originIndex(index)
    if (originIndex >= 0) {
        origin.getDouble(originIndex)
    } else {
        get(index)
    }
} else if (this is Float64Buffer) {
    array[index]
} else {
    get(index)
}

/**
 * Non-boxing access to primitive [Int]
 */
@UnstableKMathAPI
public fun Buffer<Int>.getInt(index: Int): Int = if (this is BufferView) {
    val originIndex = originIndex(index)
    if (originIndex >= 0) {
        origin.getInt(originIndex)
    } else {
        get(index)
    }
} else if (this is Int32Buffer) {
    array[index]
} else {
    get(index)
}