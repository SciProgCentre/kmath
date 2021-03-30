package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.MutableStructure1D
import space.kscience.kmath.nd.MutableStructure2D
import space.kscience.kmath.nd.as1D
import space.kscience.kmath.nd.as2D
import kotlin.math.sqrt


internal inline fun <T> BufferedTensor<T>.vectorSequence(): Sequence<BufferedTensor<T>> = sequence {
    val n = shape.size
    val vectorOffset = shape[n - 1]
    val vectorShape = intArrayOf(shape.last())
    for (offset in 0 until numel step vectorOffset) {
        val vector = BufferedTensor(vectorShape, buffer, offset)
        yield(vector)
    }
}

internal inline fun <T> BufferedTensor<T>.matrixSequence(): Sequence<BufferedTensor<T>> = sequence {
    check(shape.size >= 2) { "todo" }
    val n = shape.size
    val matrixOffset = shape[n - 1] * shape[n - 2]
    val matrixShape = intArrayOf(shape[n - 2], shape[n - 1])
    for (offset in 0 until numel step matrixOffset) {
        val matrix = BufferedTensor(matrixShape, buffer, offset)
        yield(matrix)
    }
}

internal inline fun <T> BufferedTensor<T>.forEachVector(vectorAction: (BufferedTensor<T>) -> Unit): Unit {
    for (vector in vectorSequence()) {
        vectorAction(vector)
    }
}

internal inline fun <T> BufferedTensor<T>.forEachMatrix(matrixAction: (BufferedTensor<T>) -> Unit): Unit {
    for (matrix in matrixSequence()) {
        matrixAction(matrix)
    }
}


internal inline fun dotHelper(
    a: MutableStructure2D<Double>,
    b: MutableStructure2D<Double>,
    res: MutableStructure2D<Double>,
    l: Int, m: Int, n: Int
) {
    for (i in 0 until l) {
        for (j in 0 until n) {
            var curr = 0.0
            for (k in 0 until m) {
                curr += a[i, k] * b[k, j]
            }
            res[i, j] = curr
        }
    }
}

internal inline fun luHelper(lu: MutableStructure2D<Double>, pivots: MutableStructure1D<Int>, m: Int) {
    for (row in 0 until m) pivots[row] = row

    for (i in 0 until m) {
        var maxVal = -1.0
        var maxInd = i

        for (k in i until m) {
            val absA = kotlin.math.abs(lu[k, i])
            if (absA > maxVal) {
                maxVal = absA
                maxInd = k
            }
        }

        //todo check singularity

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
}

internal inline fun pivInit(
    p: MutableStructure2D<Double>,
    pivot: MutableStructure1D<Int>,
    n: Int
) {
    for (i in 0 until n) {
        p[i, pivot[i]] = 1.0
    }
}

internal inline fun luPivotHelper(
    l: MutableStructure2D<Double>,
    u: MutableStructure2D<Double>,
    lu: MutableStructure2D<Double>,
    n: Int
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

internal inline fun choleskyHelper(
    a: MutableStructure2D<Double>,
    l: MutableStructure2D<Double>,
    n: Int
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

internal inline fun luMatrixDet(luTensor: MutableStructure2D<Double>, pivotsTensor: MutableStructure1D<Int>): Double {
    val lu = luTensor.as2D()
    val pivots = pivotsTensor.as1D()
    val m = lu.shape[0]
    val sign = if ((pivots[m] - m) % 2 == 0) 1.0 else -1.0
    return (0 until m).asSequence().map { lu[it, it] }.fold(sign) { left, right -> left * right }
}

internal inline fun luMatrixInv(
    lu: MutableStructure2D<Double>,
    pivots: MutableStructure1D<Int>,
    invMatrix: MutableStructure2D<Double>
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

internal inline fun DoubleLinearOpsTensorAlgebra.qrHelper(
    matrix: DoubleTensor,
    q: DoubleTensor,
    r: MutableStructure2D<Double>
) {
    checkSquareMatrix(matrix.shape)
    val n = matrix.shape[0]
    val qM = q.as2D()
    val matrixT = matrix.transpose(0,1)
    val qT = q.transpose(0,1)

    for (j in 0 until n) {
        val v = matrixT[j]
        val vv = v.as1D()
        if (j > 0) {
            for (i in 0 until j) {
                r[i, j] = qT[i].dot(matrixT[j]).value()
                for (k in 0 until n) {
                    val qTi = qT[i].as1D()
                    vv[k] = vv[k] - r[i, j] * qTi[k]
                }
            }
        }
        r[j, j] = DoubleAnalyticTensorAlgebra { v.dot(v).sqrt().value() }
        for (i in 0 until n) {
            qM[i, j] = vv[i] / r[j, j]
        }
    }
}
