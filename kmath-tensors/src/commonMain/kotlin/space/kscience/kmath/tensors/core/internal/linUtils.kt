/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core.internal

import space.kscience.kmath.nd.MutableStructure1D
import space.kscience.kmath.nd.MutableStructure2D
import space.kscience.kmath.nd.as1D
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.operations.asSequence
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.VirtualBuffer
import space.kscience.kmath.tensors.core.BufferedTensor
import space.kscience.kmath.tensors.core.DoubleTensor
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra
import space.kscience.kmath.tensors.core.IntTensor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

internal val <T> BufferedTensor<T>.vectors: VirtualBuffer<BufferedTensor<T>>
    get() {
        val n = shape.size
        val vectorOffset = shape[n - 1]
        val vectorShape = intArrayOf(shape.last())

        return VirtualBuffer(numElements / vectorOffset) { index ->
            val offset = index * vectorOffset
            BufferedTensor(vectorShape, mutableBuffer, bufferStart + offset)
        }
    }


internal fun <T> BufferedTensor<T>.vectorSequence(): Sequence<BufferedTensor<T>> = vectors.asSequence()

/**
 * A random access alternative to [matrixSequence]
 */
internal val <T> BufferedTensor<T>.matrices: VirtualBuffer<BufferedTensor<T>>
    get() {
        val n = shape.size
        check(n >= 2) { "Expected tensor with 2 or more dimensions, got size $n" }
        val matrixOffset = shape[n - 1] * shape[n - 2]
        val matrixShape = intArrayOf(shape[n - 2], shape[n - 1])

        return VirtualBuffer(numElements / matrixOffset) { index ->
            val offset = index * matrixOffset
            BufferedTensor(matrixShape, mutableBuffer, bufferStart + offset)
        }
    }

internal fun <T> BufferedTensor<T>.matrixSequence(): Sequence<BufferedTensor<T>> = matrices.asSequence()

internal fun dotTo(
    a: BufferedTensor<Double>,
    b: BufferedTensor<Double>,
    res: BufferedTensor<Double>,
    l: Int, m: Int, n: Int,
) {
    val aStart = a.bufferStart
    val bStart = b.bufferStart
    val resStart = res.bufferStart

    val aBuffer = a.mutableBuffer
    val bBuffer = b.mutableBuffer
    val resBuffer = res.mutableBuffer

    for (i in 0 until l) {
        for (j in 0 until n) {
            var curr = 0.0
            for (k in 0 until m) {
                curr += aBuffer[aStart + i * m + k] * bBuffer[bStart + k * n + j]
            }
            resBuffer[resStart + i * n + j] = curr
        }
    }
}

internal fun luHelper(
    lu: MutableStructure2D<Double>,
    pivots: MutableStructure1D<Int>,
    epsilon: Double,
): Boolean {

    val m = lu.rowNum

    for (row in 0..m) pivots[row] = row

    for (i in 0 until m) {
        var maxVal = 0.0
        var maxInd = i

        for (k in i until m) {
            val absA = abs(lu[k, i])
            if (absA > maxVal) {
                maxVal = absA
                maxInd = k
            }
        }

        if (abs(maxVal) < epsilon)
            return true // matrix is singular

        if (maxInd != i) {

            val j = pivots[i]
            pivots[i] = pivots[maxInd]
            pivots[maxInd] = j

            for (k in 0 until m) {
                val tmp = lu[i, k]
                lu[i, k] = lu[maxInd, k]
                lu[maxInd, k] = tmp
            }

            pivots[m] += 1

        }

        for (j in i + 1 until m) {
            lu[j, i] /= lu[i, i]
            for (k in i + 1 until m) {
                lu[j, k] -= lu[j, i] * lu[i, k]
            }
        }
    }
    return false
}

internal fun <T> BufferedTensor<T>.setUpPivots(): IntTensor {
    val n = this.shape.size
    val m = this.shape.last()
    val pivotsShape = IntArray(n - 1) { i -> this.shape[i] }
    pivotsShape[n - 2] = m + 1

    return IntTensor(
        pivotsShape,
        IntArray(pivotsShape.reduce(Int::times)) { 0 }
    )
}

internal fun DoubleTensorAlgebra.computeLU(
    tensor: DoubleTensor,
    epsilon: Double,
): Pair<DoubleTensor, IntTensor>? {

    checkSquareMatrix(tensor.shape)
    val luTensor = tensor.copy()
    val pivotsTensor = tensor.setUpPivots()

    for ((lu, pivots) in luTensor.matrixSequence().zip(pivotsTensor.vectorSequence()))
        if (luHelper(lu.as2D(), pivots.as1D(), epsilon))
            return null

    return Pair(luTensor, pivotsTensor)
}

internal fun pivInit(
    p: MutableStructure2D<Double>,
    pivot: MutableStructure1D<Int>,
    n: Int,
) {
    for (i in 0 until n) {
        p[i, pivot[i]] = 1.0
    }
}

internal fun luPivotHelper(
    l: MutableStructure2D<Double>,
    u: MutableStructure2D<Double>,
    lu: MutableStructure2D<Double>,
    n: Int,
) {
    for (i in 0 until n) {
        for (j in 0 until n) {
            if (i == j) {
                l[i, j] = 1.0
            }
            if (j < i) {
                l[i, j] = lu[i, j]
            }
            if (j >= i) {
                u[i, j] = lu[i, j]
            }
        }
    }
}

internal fun choleskyHelper(
    a: MutableStructure2D<Double>,
    l: MutableStructure2D<Double>,
    n: Int,
) {
    for (i in 0 until n) {
        for (j in 0 until i) {
            var h = a[i, j]
            for (k in 0 until j) {
                h -= l[i, k] * l[j, k]
            }
            l[i, j] = h / l[j, j]
        }
        var h = a[i, i]
        for (j in 0 until i) {
            h -= l[i, j] * l[i, j]
        }
        l[i, i] = sqrt(h)
    }
}

internal fun luMatrixDet(lu: MutableStructure2D<Double>, pivots: MutableStructure1D<Int>): Double {
    if (lu[0, 0] == 0.0) {
        return 0.0
    }
    val m = lu.shape[0]
    val sign = if ((pivots[m] - m) % 2 == 0) 1.0 else -1.0
    return (0 until m).asSequence().map { lu[it, it] }.fold(sign) { left, right -> left * right }
}

internal fun luMatrixInv(
    lu: MutableStructure2D<Double>,
    pivots: MutableStructure1D<Int>,
    invMatrix: MutableStructure2D<Double>,
) {
    val m = lu.shape[0]

    for (j in 0 until m) {
        for (i in 0 until m) {
            if (pivots[i] == j) {
                invMatrix[i, j] = 1.0
            }

            for (k in 0 until i) {
                invMatrix[i, j] -= lu[i, k] * invMatrix[k, j]
            }
        }

        for (i in m - 1 downTo 0) {
            for (k in i + 1 until m) {
                invMatrix[i, j] -= lu[i, k] * invMatrix[k, j]
            }
            invMatrix[i, j] /= lu[i, i]
        }
    }
}

internal fun DoubleTensorAlgebra.qrHelper(
    matrix: DoubleTensor,
    q: DoubleTensor,
    r: MutableStructure2D<Double>,
) {
    checkSquareMatrix(matrix.shape)
    val n = matrix.shape[0]
    val qM = q.as2D()
    val matrixT = matrix.transpose(0, 1)
    val qT = q.transpose(0, 1)

    for (j in 0 until n) {
        val v = matrixT[j]
        val vv = v.as1D()
        if (j > 0) {
            for (i in 0 until j) {
                r[i, j] = (qT[i] dot matrixT[j]).value()
                for (k in 0 until n) {
                    val qTi = qT[i].as1D()
                    vv[k] = vv[k] - r[i, j] * qTi[k]
                }
            }
        }
        r[j, j] = DoubleTensorAlgebra { (v dot v).sqrt().value() }
        for (i in 0 until n) {
            qM[i, j] = vv[i] / r[j, j]
        }
    }
}

internal fun DoubleTensorAlgebra.svd1d(a: DoubleTensor, epsilon: Double = 1e-10): DoubleTensor {
    val (n, m) = a.shape
    var v: DoubleTensor
    val b: DoubleTensor
    if (n > m) {
        b = a.transpose(0, 1).dot(a)
        v = DoubleTensor(intArrayOf(m), getRandomUnitVector(m, 0))
    } else {
        b = a.dot(a.transpose(0, 1))
        v = DoubleTensor(intArrayOf(n), getRandomUnitVector(n, 0))
    }

    var lastV: DoubleTensor
    while (true) {
        lastV = v
        v = b.dot(lastV)
        val norm = DoubleTensorAlgebra { (v dot v).sqrt().value() }
        v = v.times(1.0 / norm)
        if (abs(v.dot(lastV).value()) > 1 - epsilon) {
            return v
        }
    }
}

internal fun DoubleTensorAlgebra.svdPowerMethodHelper(
    matrix: DoubleTensor,
    USV: Triple<BufferedTensor<Double>, BufferedTensor<Double>, BufferedTensor<Double>>,
    m: Int, n: Int, epsilon: Double,
) {
    val res = ArrayList<Triple<Double, DoubleTensor, DoubleTensor>>(0)
    val (matrixU, matrixS, matrixV) = USV

    for (k in 0 until min(n, m)) {
        var a = matrix.copy()
        for ((singularValue, u, v) in res.slice(0 until k)) {
            val outerProduct = DoubleArray(u.shape[0] * v.shape[0])
            for (i in 0 until u.shape[0]) {
                for (j in 0 until v.shape[0]) {
                    outerProduct[i * v.shape[0] + j] = u[i].value() * v[j].value()
                }
            }
            a = a - singularValue.times(DoubleTensor(intArrayOf(u.shape[0], v.shape[0]), outerProduct))
        }
        var v: DoubleTensor
        var u: DoubleTensor
        var norm: Double
        if (n > m) {
            v = svd1d(a, epsilon)
            u = matrix.dot(v)
            norm = DoubleTensorAlgebra { (u dot u).sqrt().value() }
            u = u.times(1.0 / norm)
        } else {
            u = svd1d(a, epsilon)
            v = matrix.transpose(0, 1).dot(u)
            norm = DoubleTensorAlgebra { (v dot v).sqrt().value() }
            v = v.times(1.0 / norm)
        }

        res.add(Triple(norm, u, v))
    }

    val s = res.map { it.first }.toDoubleArray()
    val uBuffer = res.map { it.second }.flatMap { it.mutableBuffer.array().toList() }.toDoubleArray()
    val vBuffer = res.map { it.third }.flatMap { it.mutableBuffer.array().toList() }.toDoubleArray()
    for (i in uBuffer.indices) {
        matrixU.mutableBuffer.array()[matrixU.bufferStart + i] = uBuffer[i]
    }
    for (i in s.indices) {
        matrixS.mutableBuffer.array()[matrixS.bufferStart + i] = s[i]
    }
    for (i in vBuffer.indices) {
        matrixV.mutableBuffer.array()[matrixV.bufferStart + i] = vBuffer[i]
    }
}

private fun pythag(a: Double, b: Double): Double {
    val at: Double = abs(a)
    val bt: Double = abs(b)
    val ct: Double
    val result: Double
    if (at > bt) {
        ct = bt / at
        result = at * sqrt(1.0 + ct * ct)
    } else if (bt > 0.0) {
        ct = at / bt
        result = bt * sqrt(1.0 + ct * ct)
    } else result = 0.0
    return result
}

private fun SIGN(a: Double, b: Double): Double {
    if (b >= 0.0)
        return abs(a)
    else
        return -abs(a)
}
internal fun MutableStructure2D<Double>.svdGolubKahanHelper(u: MutableStructure2D<Double>, w: BufferedTensor<Double>,
                                                            v: MutableStructure2D<Double>, iterations: Int) {
    val shape = this.shape
    val m = shape.component1()
    val n = shape.component2()
    var f = 0.0
    val rv1 = DoubleArray(n)
    var s = 0.0
    var scale = 0.0
    var anorm = 0.0
    var g = 0.0
    var l = 0
    val epsilon = 1e-10
    for (i in 0 until n) {
        /* left-hand reduction */
        l = i + 1
        rv1[i] = scale * g
        g = 0.0
        s = 0.0
        scale = 0.0
        if (i < m) {
            for (k in i until m) {
                scale += abs(this[k, i]);
            }
            if (abs(scale) > epsilon) {
                for (k in i until m) {
                    this[k, i] = (this[k, i] / scale)
                    s += this[k, i] * this[k, i]
                }
                f = this[i, i]
                if (f >= 0) {
                    g = (-1) * abs(sqrt(s))
                } else {
                    g = abs(sqrt(s))
                }
                val h = f * g - s
                this[i, i] = f - g
                if (i != n - 1) {
                    for (j in l until n) {
                        s = 0.0
                        for (k in i until m) {
                            s += this[k, i] * this[k, j]
                        }
                        f = s / h
                        for (k in i until m) {
                            this[k, j] += f * this[k, i]
                        }
                    }
                }
                for (k in i until m) {
                    this[k, i] = this[k, i] * scale
                }
            }
        }

        w.mutableBuffer.array()[w.bufferStart + i] = scale * g
        /* right-hand reduction */
        g = 0.0
        s = 0.0
        scale = 0.0
        if (i < m && i != n - 1) {
            for (k in l until n) {
                scale += abs(this[i, k])
            }
            if (abs(scale) > epsilon) {
                for (k in l until n) {
                    this[i, k] = this[i, k] / scale
                    s += this[i, k] * this[i, k]
                }
                f = this[i, l]
                if (f >= 0) {
                    g = (-1) * abs(sqrt(s))
                } else {
                    g = abs(sqrt(s))
                }
                val h = f * g - s
                this[i, l] = f - g
                for (k in l until n) {
                    rv1[k] = this[i, k] / h
                }
                if (i != m - 1) {
                    for (j in l until m) {
                        s = 0.0
                        for (k in l until n) {
                            s += this[j, k] * this[i, k]
                        }
                        for (k in l until n) {
                            this[j, k] += s * rv1[k]
                        }
                    }
                }
                for (k in l until n) {
                    this[i, k] = this[i, k] * scale
                }
            }
        }
        anorm = max(anorm, (abs(w.mutableBuffer.array()[w.bufferStart + i]) + abs(rv1[i])));
    }

    for (i in n - 1 downTo 0) {
        if (i < n - 1) {
            if (abs(g) > epsilon) {
                for (j in l until n) {
                    v[j, i] = (this[i, j] / this[i, l]) / g
                }
                for (j in l until n) {
                    s = 0.0
                    for (k in l until n)
                        s += this[i, k] * v[k, j]
                    for (k in l until n)
                        v[k, j] += s * v[k, i]
                }
            }
            for (j in l until n) {
                v[i, j] = 0.0
                v[j, i] = 0.0
            }
        }
        v[i, i] = 1.0
        g = rv1[i]
        l = i
    }

    for (i in min(n, m) - 1 downTo 0) {
        l = i + 1
        g =  w.mutableBuffer.array()[w.bufferStart + i]
        for (j in l until n) {
            this[i, j] = 0.0
        }
        if (abs(g) > epsilon) {
            g = 1.0 / g
            for (j in l until n) {
                s = 0.0
                for (k in l until m) {
                    s += this[k, i] * this[k, j]
                }
                f = (s / this[i, i]) * g
                for (k in i until m) {
                    this[k, j] += f * this[k, i]
                }
            }
            for (j in i until m) {
                this[j, i] *= g
            }
        } else {
            for (j in i until m) {
                this[j, i] = 0.0
            }
        }
        this[i, i] += 1.0
    }

    var flag = 0
    var nm = 0
    var c = 0.0
    var h = 0.0
    var y = 0.0
    var z = 0.0
    var x = 0.0
    for (k in n - 1 downTo 0) {
        for (its in 1 until iterations) {
            flag = 1
            for (newl in k downTo 0) {
                nm = newl - 1
                if (abs(rv1[newl]) + anorm == anorm) {
                    flag = 0
                    l = newl
                    break
                }
                if (abs(w.mutableBuffer.array()[w.bufferStart + nm]) + anorm == anorm) {
                    l = newl
                    break
                }
            }

            if (flag != 0) {
                c = 0.0
                s = 1.0
                for (i in l until k + 1) {
                    f = s * rv1[i]
                    rv1[i] = c * rv1[i]
                    if (abs(f) + anorm == anorm) {
                        break
                    }
                    g = w.mutableBuffer.array()[w.bufferStart + i]
                    h = pythag(f, g)
                    w.mutableBuffer.array()[w.bufferStart + i] = h
                    h = 1.0 / h
                    c = g * h
                    s = (-f) * h
                    for (j in 0 until m) {
                        y = this[j, nm]
                        z = this[j, i]
                        this[j, nm] = y * c + z * s
                        this[j, i] = z * c - y * s
                    }
                }
            }

            z =  w.mutableBuffer.array()[w.bufferStart + k]
            if (l == k) {
                if (z < 0.0) {
                    w.mutableBuffer.array()[w.bufferStart + k] = -z
                    for (j in 0 until n)
                        v[j, k] = -v[j, k]
                }
                break
            }

            x =  w.mutableBuffer.array()[w.bufferStart + l]
            nm = k - 1
            y =  w.mutableBuffer.array()[w.bufferStart + nm]
            g = rv1[nm]
            h = rv1[k]
            f = ((y - z) * (y + z) + (g - h) * (g + h)) / (2.0 * h * y)
            g = pythag(f, 1.0)
            f = ((x - z) * (x + z) + h * ((y / (f + SIGN(g, f))) - h)) / x
            c = 1.0
            s = 1.0

            var i = 0
            for (j in l until nm + 1) {
                i = j + 1
                g = rv1[i]
                y =  w.mutableBuffer.array()[w.bufferStart + i]
                h = s * g
                g = c * g
                z = pythag(f, h)
                rv1[j] = z
                c = f / z
                s = h / z
                f = x * c + g * s
                g = g * c - x * s
                h = y * s
                y *= c

                for (jj in 0 until n) {
                    x = v[jj, j];
                    z = v[jj, i];
                    v[jj, j] = x * c + z * s;
                    v[jj, i] = z * c - x * s;
                }
                z = pythag(f, h)
                w.mutableBuffer.array()[w.bufferStart + j] = z
                if (abs(z) > epsilon) {
                    z = 1.0 / z
                    c = f * z
                    s = h * z
                }
                f = c * g + s * y
                x = c * y - s * g
                for (jj in 0 until m) {
                    y = this[jj, j]
                    z = this[jj, i]
                    this[jj, j] = y * c + z * s
                    this[jj, i] = z * c - y * s
                }
            }
            rv1[l] = 0.0
            rv1[k] = f
            w.mutableBuffer.array()[w.bufferStart + k] = x
        }
    }

    for (i in 0 until m) {
        for (j in 0 until n) {
            u[j, i] = this[i, j]
        }
    }
}
