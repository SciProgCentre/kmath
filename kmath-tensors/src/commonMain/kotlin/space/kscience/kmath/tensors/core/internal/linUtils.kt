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
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.div
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.dot
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.minus
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.times
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.transposed
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra.Companion.plus
import kotlin.math.abs
import kotlin.math.max
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

data class LMSettings (
    var iteration:Int,
    var func_calls: Int,
    var example_number:Int
)

/* matrix -> column of all elemnets */
fun make_column(tensor: MutableStructure2D<Double>) : MutableStructure2D<Double> {
    val shape = intArrayOf(tensor.shape.component1() * tensor.shape.component2(), 1)
    var buffer = DoubleArray(tensor.shape.component1() * tensor.shape.component2())
    for (i in 0 until tensor.shape.component1()) {
        for (j in 0 until tensor.shape.component2()) {
            buffer[i * tensor.shape.component2() + j] = tensor[i, j]
        }
    }
    var column = BroadcastDoubleTensorAlgebra.fromArray(ShapeND(shape), buffer).as2D()
    return column
}

/* column length */
fun length(column: MutableStructure2D<Double>) : Int {
    return column.shape.component1()
}

fun MutableStructure2D<Double>.abs() {
    for (i in 0 until this.shape.component1()) {
        for (j in 0 until this.shape.component2()) {
            this[i, j] = abs(this[i, j])
        }
    }
}

fun abs(input: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val tensor = BroadcastDoubleTensorAlgebra.ones(
        ShapeND(
            intArrayOf(
                input.shape.component1(),
                input.shape.component2()
            )
        )
    ).as2D()
    for (i in 0 until tensor.shape.component1()) {
        for (j in 0 until tensor.shape.component2()) {
            tensor[i, j] = abs(input[i, j])
        }
    }
    return tensor
}

fun diag(input: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val tensor = BroadcastDoubleTensorAlgebra.ones(ShapeND(intArrayOf(input.shape.component1(), 1))).as2D()
    for (i in 0 until tensor.shape.component1()) {
        tensor[i, 0] = input[i, i]
    }
    return tensor
}

fun make_matrx_with_diagonal(column: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val size = column.shape.component1()
    val tensor = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(size, size))).as2D()
    for (i in 0 until size) {
        tensor[i, i] = column[i, 0]
    }
    return tensor
}

fun lm_eye(size: Int): MutableStructure2D<Double> {
    val column = BroadcastDoubleTensorAlgebra.ones(ShapeND(intArrayOf(size, 1))).as2D()
    return make_matrx_with_diagonal(column)
}

fun largest_element_comparison(a: MutableStructure2D<Double>, b: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val a_sizeX = a.shape.component1()
    val a_sizeY = a.shape.component2()
    val b_sizeX = b.shape.component1()
    val b_sizeY = b.shape.component2()
    val tensor = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(max(a_sizeX, b_sizeX), max(a_sizeY, b_sizeY)))).as2D()
    for (i in 0 until tensor.shape.component1()) {
        for (j in 0 until tensor.shape.component2()) {
            if (i < a_sizeX && i < b_sizeX && j < a_sizeY && j < b_sizeY) {
                tensor[i, j] = max(a[i, j], b[i, j])
            }
            else if (i < a_sizeX && j < a_sizeY) {
                tensor[i, j] = a[i, j]
            }
            else {
                tensor[i, j] = b[i, j]
            }
        }
    }
    return tensor
}

fun smallest_element_comparison(a: MutableStructure2D<Double>, b: MutableStructure2D<Double>): MutableStructure2D<Double> {
    val a_sizeX = a.shape.component1()
    val a_sizeY = a.shape.component2()
    val b_sizeX = b.shape.component1()
    val b_sizeY = b.shape.component2()
    val tensor = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(max(a_sizeX, b_sizeX), max(a_sizeY, b_sizeY)))).as2D()
    for (i in 0 until tensor.shape.component1()) {
        for (j in 0 until tensor.shape.component2()) {
            if (i < a_sizeX && i < b_sizeX && j < a_sizeY && j < b_sizeY) {
                tensor[i, j] = min(a[i, j], b[i, j])
            }
            else if (i < a_sizeX && j < a_sizeY) {
                tensor[i, j] = a[i, j]
            }
            else {
                tensor[i, j] = b[i, j]
            }
        }
    }
    return tensor
}

fun get_zero_indices(column: MutableStructure2D<Double>, epsilon: Double = 0.000001): MutableStructure2D<Double>? {
    var idx = emptyArray<Double>()
    for (i in 0 until column.shape.component1()) {
        if (abs(column[i, 0]) > epsilon) {
            idx += (i + 1.0)
        }
    }
    if (idx.size > 0) {
        return BroadcastDoubleTensorAlgebra.fromArray(ShapeND(intArrayOf(idx.size, 1)), idx.toDoubleArray()).as2D()
    }
    return null
}

fun feval(func: (MutableStructure2D<Double>,  MutableStructure2D<Double>, LMSettings) ->  MutableStructure2D<Double>,
          t: MutableStructure2D<Double>, p: MutableStructure2D<Double>, settings: LMSettings)
        : MutableStructure2D<Double>
{
    return func(t, p, settings)
}

fun lm_matx(func: (MutableStructure2D<Double>, MutableStructure2D<Double>, LMSettings) -> MutableStructure2D<Double>,
            t: MutableStructure2D<Double>, p_old: MutableStructure2D<Double>, y_old: MutableStructure2D<Double>,
            dX2: Int, J_input: MutableStructure2D<Double>, p: MutableStructure2D<Double>,
            y_dat: MutableStructure2D<Double>, weight: MutableStructure2D<Double>, dp:MutableStructure2D<Double>, settings:LMSettings) : Array<MutableStructure2D<Double>>
{
    // default: dp = 0.001

    val Npnt = length(y_dat)               // number of data points
    val Npar = length(p)                   // number of parameters

    val y_hat = feval(func, t, p, settings)          // evaluate model using parameters 'p'
    settings.func_calls += 1

    var J = J_input

    if (settings.iteration % (2 * Npar) == 0 || dX2 > 0) {
        J = lm_FD_J(func, t, p, y_hat, dp, settings).as2D() // finite difference
    }
    else {
        J = lm_Broyden_J(p_old, y_old, J, p, y_hat).as2D() // rank-1 update
    }

    val delta_y = y_dat.minus(y_hat)

    val Chi_sq = delta_y.transposed().dot( delta_y.times(weight) ).as2D()
    val JtWJ = J.transposed().dot ( J.times( weight.dot(BroadcastDoubleTensorAlgebra.ones(ShapeND(intArrayOf(1, Npar)))) ) ).as2D()
    val JtWdy = J.transposed().dot( weight.times(delta_y) ).as2D()

    return arrayOf(JtWJ,JtWdy,Chi_sq,y_hat,J)
}

fun lm_Broyden_J(p_old: MutableStructure2D<Double>, y_old: MutableStructure2D<Double>, J_input: MutableStructure2D<Double>,
                 p: MutableStructure2D<Double>, y: MutableStructure2D<Double>): MutableStructure2D<Double> {
    var J = J_input.copyToTensor()

    val h = p.minus(p_old)
    val increase = y.minus(y_old).minus( J.dot(h) ).dot(h.transposed()).div( (h.transposed().dot(h)).as2D()[0, 0] )
    J = J.plus(increase)

    return J.as2D()
}

fun lm_FD_J(func: (MutableStructure2D<Double>, MutableStructure2D<Double>, settings: LMSettings) -> MutableStructure2D<Double>,
            t: MutableStructure2D<Double>, p: MutableStructure2D<Double>, y: MutableStructure2D<Double>,
            dp: MutableStructure2D<Double>, settings: LMSettings): MutableStructure2D<Double> {
    // default: dp = 0.001 * ones(1,n)

    val m = length(y)              // number of data points
    val n = length(p)              // number of parameters

    val ps = p.copyToTensor().as2D()
    val J = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(m, n))).as2D()  // initialize Jacobian to Zero
    val del = BroadcastDoubleTensorAlgebra.zeros(ShapeND(intArrayOf(n, 1))).as2D()

    for (j in 0 until n) {

        del[j, 0] = dp[j, 0] * (1 + abs(p[j, 0])) // parameter perturbation
        p[j, 0] = ps[j, 0] + del[j, 0]            // perturb parameter p(j)

        val epsilon = 0.0000001
        if (kotlin.math.abs(del[j, 0]) > epsilon) {
            val y1 = feval(func, t, p, settings)
            settings.func_calls += 1

            if (dp[j, 0] < 0) { // backwards difference
                for (i in 0 until J.shape.component1()) {
                    J[i, j] = (y1.as2D().minus(y).as2D())[i, 0] / del[j, 0]
                }
            }
            else {
                // Do tests for it
                println("Potential mistake")
                p[j, 0] = ps[j, 0] - del[j, 0] // central difference, additional func call
                for (i in 0 until J.shape.component1()) {
                    J[i, j] = (y1.as2D().minus(feval(func, t, p, settings)).as2D())[i, 0] / (2 * del[j, 0])
                }
                settings.func_calls += 1
            }
        }

        p[j, 0] = ps[j, 0] // restore p(j)
    }

    return J.as2D()
}
