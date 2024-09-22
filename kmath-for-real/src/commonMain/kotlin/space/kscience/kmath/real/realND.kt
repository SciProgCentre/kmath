/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.real

import space.kscience.kmath.nd.BufferND
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.structures.Float64Buffer

/**
 * Map one [BufferND] using function without indices.
 */
public inline fun BufferND<Float64>.mapInline(crossinline transform: Float64Field.(Double) -> Double): BufferND<Float64> {
    val array = DoubleArray(indices.linearSize) { offset -> Float64Field.transform(buffer[offset]) }
    return BufferND(indices, Float64Buffer(array))
}

/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy.
 */
public operator fun Function1<Double, Double>.invoke(elementND: BufferND<Float64>): BufferND<Float64> =
    elementND.mapInline { this@invoke(it) }

/* plus and minus */

/**
 * Summation operation for [BufferND] and single element
 */
public operator fun BufferND<Float64>.plus(arg: Double): BufferND<Float64> = mapInline { it + arg }

/**
 * Subtraction operation between [BufferND] and single element
 */
public operator fun BufferND<Float64>.minus(arg: Double): BufferND<Float64> = mapInline { it - arg }