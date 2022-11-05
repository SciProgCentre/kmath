/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core.internal

import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.IntBuffer
import space.kscience.kmath.structures.asBuffer
import space.kscience.kmath.structures.indices
import space.kscience.kmath.tensors.core.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

internal fun dotTo(
    a: BufferedTensor<Double>,
    b: BufferedTensor<Double>,
    res: BufferedTensor<Double>,
    l: Int, m: Int, n: Int,
) {
    val aBuffer = a.source
    val bBuffer = b.source
    val resBuffer = res.source

    for (i in 0 until l) {
        for (j in 0 until n) {
            var curr = 0.0
            for (k in 0 until m) {
                curr += aBuffer[i * m + k] * bBuffer[k * n + j]
            }
            resBuffer[i * n + j] = curr
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

internal fun <T> StructureND<T>.setUpPivots(): IntTensor {
    val n = this.shape.size
    val m = this.shape.last()
    val pivotsShape = IntArray(n - 1) { i -> this.shape[i] }
    pivotsShape[n - 2] = m + 1

    return IntTensor(
        ShapeND(pivotsShape),
        IntBuffer(pivotsShape.reduce(Int::times)) { 0 }
    )
}

internal fun DoubleTensorAlgebra.computeLU(
    tensor: StructureND<Double>,
    epsilon: Double,
): Pair<DoubleTensor, IntTensor>? {

    checkSquareMatrix(tensor.shape)
    val luTensor = tensor.copyToTensor()
    val pivotsTensor = tensor.setUpPivots()

    for ((lu, pivots) in luTensor.matrixSequence().zip(pivotsTensor.vectorSequence()))
        if (luHelper(lu.asDoubleTensor2D(), pivots.as1D(), epsilon))
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
    val qM = q.asDoubleTensor2D()
    val matrixT = matrix.transposed(0, 1)
    val qT = q.transposed(0, 1)

    for (j in 0 until n) {
        val v = matrixT.getTensor(j)
        val vv = v.asDoubleBuffer()
        if (j > 0) {
            for (i in 0 until j) {
                r[i, j] = (qT.getTensor(i) dot matrixT.getTensor(j)).value()
                for (k in 0 until n) {
                    val qTi = qT.getTensor(i).asDoubleBuffer()
                    vv[k] = vv[k] - r[i, j] * qTi[k]
                }
            }
        }
        r[j, j] = DoubleTensorAlgebra { sqrt((v dot v)).value() }
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
        b = a.transposed(0, 1).dot(a)
        v = DoubleTensor(ShapeND(m), DoubleBuffer.randomUnitVector(m, 0))
    } else {
        b = a.dot(a.transposed(0, 1))
        v = DoubleTensor(ShapeND(n), DoubleBuffer.randomUnitVector(n, 0))
    }

    var lastV: DoubleTensor
    while (true) {
        lastV = v
        v = b.dot(lastV)
        val norm = DoubleTensorAlgebra { sqrt((v dot v)).value() }
        v = v.times(1.0 / norm)
        if (abs(v.dot(lastV).value()) > 1 - epsilon) {
            return v
        }
    }
}

internal fun DoubleTensorAlgebra.svdHelper(
    matrix: DoubleTensor,
    USV: Triple<BufferedTensor<Double>, BufferedTensor<Double>, BufferedTensor<Double>>,
    m: Int, n: Int, epsilon: Double,
) {
    val res = ArrayList<Triple<Double, DoubleTensor, DoubleTensor>>(0)
    val (matrixU, matrixS, matrixV) = USV

    for (k in 0 until min(n, m)) {
        var a = matrix.copyToTensor()
        for ((singularValue, u, v) in res.slice(0 until k)) {
            val outerProduct = DoubleArray(u.shape[0] * v.shape[0])
            for (i in 0 until u.shape[0]) {
                for (j in 0 until v.shape[0]) {
                    outerProduct[i * v.shape[0] + j] = u.getTensor(i).value() * v.getTensor(j).value()
                }
            }
            a = a - singularValue.times(DoubleTensor(ShapeND(u.shape[0], v.shape[0]), outerProduct.asBuffer()))
        }
        var v: DoubleTensor
        var u: DoubleTensor
        var norm: Double
        if (n > m) {
            v = svd1d(a, epsilon)
            u = matrix.dot(v)
            norm = DoubleTensorAlgebra { sqrt((u dot u)).value() }
            u = u.times(1.0 / norm)
        } else {
            u = svd1d(a, epsilon)
            v = matrix.transposed(0, 1).dot(u)
            norm = DoubleTensorAlgebra { sqrt((v dot v)).value() }
            v = v.times(1.0 / norm)
        }

        res.add(Triple(norm, u, v))
    }

    val s = res.map { it.first }.toDoubleArray()
    val uBuffer = res.map { it.second.source }.concat()
    val vBuffer = res.map { it.third.source }.concat()
    for (i in uBuffer.indices) {
        matrixU.source[i] = uBuffer[i]
    }
    for (i in s.indices) {
        matrixS.source[i] = s[i]
    }
    for (i in vBuffer.indices) {
        matrixV.source[i] = vBuffer[i]
    }
}
