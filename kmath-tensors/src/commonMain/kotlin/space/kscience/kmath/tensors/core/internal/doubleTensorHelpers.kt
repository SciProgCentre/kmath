/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core.internal

import space.kscience.kmath.nd.*
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.asBuffer
import space.kscience.kmath.structures.indices
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.eye
import space.kscience.kmath.tensors.core.BufferedTensor
import space.kscience.kmath.tensors.core.DoubleTensor
import space.kscience.kmath.tensors.core.OffsetDoubleBuffer
import space.kscience.kmath.tensors.core.copyToTensor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt


internal fun MutableStructure2D<Double>.jacobiHelper(
    maxIteration: Int,
    epsilon: Double,
): Pair<DoubleBuffer, Structure2D<Double>> {
    val n = rowNum
    val A_ = copyToTensor()
    val V = eye(n)
    val D = DoubleBuffer(n) { get(it, it) }
    val B = DoubleBuffer(n) { get(it, it) }
    val Z = DoubleBuffer(n) { 0.0 }

    // assume that buffered tensor is square matrix
    operator fun DoubleTensor.get(i: Int, j: Int): Double {
        return source[i * shape[0] + j]
    }

    operator fun BufferedTensor<Double>.set(i: Int, j: Int, value: Double) {
        source[i * shape[0] + j] = value
    }

    fun maxOffDiagonal(matrix: DoubleTensor): Double {
        var maxOffDiagonalElement = 0.0
        for (i in 0 until n - 1) {
            for (j in i + 1 until n) {
                maxOffDiagonalElement = max(maxOffDiagonalElement, abs(matrix[i, j]))
            }
        }
        return maxOffDiagonalElement
    }

    fun rotate(a: DoubleTensor, s: Double, tau: Double, i: Int, j: Int, k: Int, l: Int) {
        val g = a[i, j]
        val h = a[k, l]
        a[i, j] = g - s * (h + g * tau)
        a[k, l] = h + s * (g - h * tau)
    }

    fun jacobiIteration(
        a: DoubleTensor,
        v: DoubleTensor,
        d: DoubleBuffer,
        z: DoubleBuffer,
    ) {
        for (ip in 0 until n - 1) {
            for (iq in ip + 1 until n) {
                val g = 100.0 * abs(a[ip, iq])

                if (g <= epsilon * abs(d[ip]) && g <= epsilon * abs(d[iq])) {
                    a[ip, iq] = 0.0
                    continue
                }

                var h = d[iq] - d[ip]
                val t = when {
                    g <= epsilon * abs(h) -> (a[ip, iq]) / h
                    else -> {
                        val theta = 0.5 * h / (a[ip, iq])
                        val denominator = abs(theta) + sqrt(1.0 + theta * theta)
                        if (theta < 0.0) -1.0 / denominator else 1.0 / denominator
                    }
                }

                val c = 1.0 / sqrt(1 + t * t)
                val s = t * c
                val tau = s / (1.0 + c)
                h = t * a[ip, iq]
                z[ip] -= h
                z[iq] += h
                d[ip] -= h
                d[iq] += h
                a[ip, iq] = 0.0

                for (j in 0 until ip) {
                    rotate(a, s, tau, j, ip, j, iq)
                }
                for (j in (ip + 1) until iq) {
                    rotate(a, s, tau, ip, j, j, iq)
                }
                for (j in (iq + 1) until n) {
                    rotate(a, s, tau, ip, j, iq, j)
                }
                for (j in 0 until n) {
                    rotate(v, s, tau, j, ip, j, iq)
                }
            }
        }
    }

    fun updateDiagonal(
        d: DoubleBuffer,
        z: DoubleBuffer,
        b: DoubleBuffer,
    ) {
        for (ip in 0 until d.size) {
            b[ip] += z[ip]
            d[ip] = b[ip]
            z[ip] = 0.0
        }
    }

    var sm = maxOffDiagonal(A_)
    for (iteration in 0 until maxIteration) {
        if (sm < epsilon) {
            break
        }

        jacobiIteration(A_, V, D, Z)
        updateDiagonal(D, Z, B)
        sm = maxOffDiagonal(A_)
    }

    // TODO sort eigenvalues
    return D to V.as2D()
}

/**
 * Concatenate a list of arrays
 */
internal fun List<OffsetDoubleBuffer>.concat(): DoubleBuffer {
    val array = DoubleArray(sumOf { it.size })
    var pointer = 0
    while (pointer < array.size) {
        for (bufferIndex in indices) {
            val buffer = get(bufferIndex)
            for (innerIndex in buffer.indices) {
                array[pointer] = buffer[innerIndex]
                pointer++
            }
        }
    }
    return array.asBuffer()
}

internal val DoubleTensor.vectors: List<DoubleTensor>
    get() {
        val n = shape.size
        val vectorOffset = shape[n - 1]
        val vectorShape = ShapeND(shape.last())

        return List(linearSize / vectorOffset) { index ->
            val offset = index * vectorOffset
            DoubleTensor(vectorShape, source.view(offset, vectorShape.first()))
        }
    }


internal fun DoubleTensor.vectorSequence(): Sequence<DoubleTensor> = vectors.asSequence()


internal val DoubleTensor.matrices: List<DoubleTensor>
    get() {
        val n = shape.size
        check(n >= 2) { "Expected tensor with 2 or more dimensions, got size $n" }
        val matrixOffset = shape[n - 1] * shape[n - 2]
        val matrixShape = ShapeND(shape[n - 2], shape[n - 1])

        val size = matrixShape.linearSize

        return List(linearSize / matrixOffset) { index ->
            val offset = index * matrixOffset
            DoubleTensor(matrixShape, source.view(offset, size))
        }
    }

internal fun DoubleTensor.matrixSequence(): Sequence<DoubleTensor> = matrices.asSequence()