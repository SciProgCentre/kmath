/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.tensors.core.internal

import space.kscience.kmath.nd.as1D
import space.kscience.kmath.operations.toMutableList
import space.kscience.kmath.samplers.GaussianSampler
import space.kscience.kmath.stat.RandomGenerator
import space.kscience.kmath.structures.*
import space.kscience.kmath.tensors.core.BufferedTensor
import space.kscience.kmath.tensors.core.DoubleTensor
import kotlin.math.*

/**
 * Returns a reference to [IntArray] containing all the elements of this [Buffer] or copy the data.
 */
internal fun Buffer<Int>.array(): IntArray = when (this) {
    is IntBuffer -> array
    else -> this.toIntArray()
}

/**
 * Returns a reference to [DoubleArray] containing all the elements of this [Buffer] or copy the data.
 */
@PublishedApi
internal fun Buffer<Double>.array(): DoubleArray = when (this) {
    is DoubleBuffer -> array
    else -> this.toDoubleArray()
}

internal fun getRandomNormals(n: Int, seed: Long): DoubleArray {
    val distribution = GaussianSampler(0.0, 1.0)
    val generator = RandomGenerator.default(seed)
    return distribution.sample(generator).nextBufferBlocking(n).toDoubleArray()
}

internal fun getRandomUnitVector(n: Int, seed: Long): DoubleArray {
    val unnorm = getRandomNormals(n, seed)
    val norm = sqrt(unnorm.sumOf { it * it })
    return unnorm.map { it / norm }.toDoubleArray()
}

internal fun minusIndexFrom(n: Int, i: Int): Int = if (i >= 0) i else {
    val ii = n + i
    check(ii >= 0) {
        "Out of bound index $i for tensor of dim $n"
    }
    ii
}

internal fun <T> BufferedTensor<T>.minusIndex(i: Int): Int = minusIndexFrom(this.dimension, i)

internal fun format(value: Double, digits: Int = 4): String = buildString {
    val res = buildString {
        val ten = 10.0
        val approxOrder = if (value == 0.0) 0 else ceil(log10(abs(value))).toInt()
        val order = if (
            ((value % ten) == 0.0) ||
            (value == 1.0) ||
            ((1 / value) % ten == 0.0)
        ) approxOrder else approxOrder - 1
        val lead = value / ten.pow(order)
        if (value >= 0.0) append(' ')
        append(round(lead * ten.pow(digits)) / ten.pow(digits))
        when {
            order == 0 -> Unit
            order > 0 -> {
                append("e+")
                append(order)
            }
            else -> {
                append('e')
                append(order)
            }
        }
    }
    val fLength = digits + 6
    append(res)
    repeat(fLength - res.length) { append(' ') }
}

internal fun DoubleTensor.toPrettyString(): String = buildString {
    var offset = 0
    val shape = this@toPrettyString.shape
    val linearStructure = this@toPrettyString.linearStructure
    val vectorSize = shape.last()
    append("DoubleTensor(\n")
    var charOffset = 3
    for (vector in vectorSequence()) {
        repeat(charOffset) { append(' ') }
        val index = linearStructure.index(offset)
        for (ind in index.reversed()) {
            if (ind != 0) {
                break
            }
            append('[')
            charOffset += 1
        }

        val values = vector.as1D().toMutableList().map(::format)

        values.joinTo(this, separator = ", ")

        append(']')
        charOffset -= 1

        index.reversed().zip(shape.reversed()).drop(1).forEach { (ind, maxInd) ->
            if (ind != maxInd - 1) {
                return@forEach
            }
            append(']')
            charOffset -= 1
        }

        offset += vectorSize
        if (this@toPrettyString.numElements == offset) {
            break
        }

        append(",\n")
    }
    append("\n)")
}
