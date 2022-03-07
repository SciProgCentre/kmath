/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.real

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.DoubleL2Norm
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBuffer.Companion.double
import space.kscience.kmath.structures.asBuffer
import space.kscience.kmath.structures.indices
import kotlin.math.pow

public typealias DoubleVector = Point<Double>

@Suppress("FunctionName")
public fun DoubleVector(vararg doubles: Double): DoubleVector = doubles.asBuffer()

/**
 * Fill the vector with given [size] with given [value]
 */
@UnstableKMathAPI
public fun Buffer.Companion.same(size: Int, value: Number): DoubleVector = double(size) { value.toDouble() }

// Transformation methods

public inline fun DoubleVector.map(transform: (Double) -> Double): DoubleVector =
    double(size) { transform(get(it)) }

public inline fun DoubleVector.mapIndexed(transform: (index: Int, value: Double) -> Double): DoubleVector =
    double(size) { transform(it, get(it)) }

public operator fun DoubleVector.plus(other: DoubleVector): DoubleVector {
    require(size == other.size) { "Vector size $size expected but ${other.size} found" }
    return mapIndexed { index, value -> value + other[index] }
}

public operator fun DoubleVector.plus(number: Number): DoubleVector = map { it + number.toDouble() }

public operator fun Number.plus(vector: DoubleVector): DoubleVector = vector + this

public operator fun DoubleVector.unaryMinus(): Buffer<Double> = map { -it }

public operator fun DoubleVector.minus(other: DoubleVector): DoubleVector {
    require(size == other.size) { "Vector size $size expected but ${other.size} found" }
    return mapIndexed { index, value -> value - other[index] }
}

public operator fun DoubleVector.minus(number: Number): DoubleVector = map { it - number.toDouble() }

public operator fun Number.minus(vector: DoubleVector): DoubleVector = vector.map { toDouble() - it }

public operator fun DoubleVector.times(other: DoubleVector): DoubleVector {
    require(size == other.size) { "Vector size $size expected but ${other.size} found" }
    return mapIndexed { index, value -> value * other[index] }
}

public operator fun DoubleVector.times(number: Number): DoubleVector = map { it * number.toDouble() }

public operator fun Number.times(vector: DoubleVector): DoubleVector = vector * this

public operator fun DoubleVector.div(other: DoubleVector): DoubleVector {
    require(size == other.size) { "Vector size $size expected but ${other.size} found" }
    return mapIndexed { index, value -> value / other[index] }
}

public operator fun DoubleVector.div(number: Number): DoubleVector = map { it / number.toDouble() }

public operator fun Number.div(vector: DoubleVector): DoubleVector = vector.map { toDouble() / it }

//extended operations

public fun DoubleVector.pow(p: Double): DoubleVector = map { it.pow(p) }

public fun DoubleVector.pow(p: Int): DoubleVector = map { it.pow(p) }

public fun exp(vector: DoubleVector): DoubleVector = vector.map { kotlin.math.exp(it) }

public fun sqrt(vector: DoubleVector): DoubleVector = vector.map { kotlin.math.sqrt(it) }

public fun DoubleVector.square(): DoubleVector = map { it.pow(2) }

public fun sin(vector: DoubleVector): DoubleVector = vector.map { kotlin.math.sin(it) }

public fun cos(vector: DoubleVector): DoubleVector = vector.map { kotlin.math.cos(it) }

public fun tan(vector: DoubleVector): DoubleVector = vector.map { kotlin.math.tan(it) }

public fun ln(vector: DoubleVector): DoubleVector = vector.map { kotlin.math.ln(it) }

public fun log10(vector: DoubleVector): DoubleVector = vector.map { kotlin.math.log10(it) }

// reductions methods

public fun DoubleVector.sum(): Double {
    var res = 0.0
    for (i in indices) {
        res += get(i)
    }
    return res
}

public val DoubleVector.norm: Double get() = DoubleL2Norm.norm(this)