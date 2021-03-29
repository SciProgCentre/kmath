package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.MutableStructure1D
import space.kscience.kmath.nd.MutableStructure2D
import space.kscience.kmath.nd.as1D
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.tensors.LinearOpsTensorAlgebra
import kotlin.math.sqrt

public class DoubleLinearOpsTensorAlgebra :
    LinearOpsTensorAlgebra<Double, DoubleTensor, IntTensor>,
    DoubleTensorAlgebra() {

    override fun DoubleTensor.inv(): DoubleTensor = invLU()

    override fun DoubleTensor.det(): DoubleTensor = detLU()

    private inline fun luHelper(lu: MutableStructure2D<Double>, pivots: MutableStructure1D<Int>, m: Int) {
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

    override fun DoubleTensor.lu(): Pair<DoubleTensor, IntTensor> {

        checkSquareMatrix(shape)

        val luTensor = copy()

        val n = shape.size
        val m = shape.last()
        val pivotsShape = IntArray(n - 1) { i -> shape[i] }
        pivotsShape[n - 2] = m + 1

        val pivotsTensor = IntTensor(
            pivotsShape,
            IntArray(pivotsShape.reduce(Int::times)) { 0 }
        )

        for ((lu, pivots) in luTensor.matrixSequence().zip(pivotsTensor.vectorSequence()))
            luHelper(lu.as2D(), pivots.as1D(), m)

        return Pair(luTensor, pivotsTensor)

    }

    private inline fun pivInit(
        p: MutableStructure2D<Double>,
        pivot: MutableStructure1D<Int>,
        n: Int
    ) {
        for (i in 0 until n) {
            p[i, pivot[i]] = 1.0
        }
    }

    private inline fun luPivotHelper(
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

    override fun luPivot(
        luTensor: DoubleTensor,
        pivotsTensor: IntTensor
    ): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
        //todo checks
        checkSquareMatrix(luTensor.shape)
        check(
            luTensor.shape.dropLast(1).toIntArray() contentEquals pivotsTensor.shape
        ) { "Bed shapes ((" } //todo rewrite

        val n = luTensor.shape.last()
        val pTensor = luTensor.zeroesLike()
        for ((p, pivot) in pTensor.matrixSequence().zip(pivotsTensor.vectorSequence()))
            pivInit(p.as2D(), pivot.as1D(), n)

        val lTensor = luTensor.zeroesLike()
        val uTensor = luTensor.zeroesLike()

        for ((pairLU, lu) in lTensor.matrixSequence().zip(uTensor.matrixSequence())
            .zip(luTensor.matrixSequence())) {
            val (l, u) = pairLU
            luPivotHelper(l.as2D(), u.as2D(), lu.as2D(), n)
        }

        return Triple(pTensor, lTensor, uTensor)

    }

    private inline fun choleskyHelper(
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

    override fun DoubleTensor.cholesky(): DoubleTensor {
        // todo checks
        checkSquareMatrix(shape)

        val n = shape.last()
        val lTensor = zeroesLike()

        for ((a, l) in this.matrixSequence().zip(lTensor.matrixSequence()))
            for (i in 0 until n) choleskyHelper(a.as2D(), l.as2D(), n)

        return lTensor
    }

    private fun matrixQR(
        matrix: Structure2D<Double>,
        q: MutableStructure2D<Double>,
        r: MutableStructure2D<Double>
    ) {

    }

    override fun DoubleTensor.qr(): Pair<DoubleTensor, DoubleTensor> {
        TODO("ANDREI")
    }

    override fun DoubleTensor.svd(): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
        TODO("ALYA")
    }

    override fun DoubleTensor.symEig(eigenvectors: Boolean): Pair<DoubleTensor, DoubleTensor> {
        TODO("ANDREI")
    }

    private fun luMatrixDet(luTensor: MutableStructure2D<Double>, pivotsTensor: MutableStructure1D<Int>): Double {
        val lu = luTensor.as2D()
        val pivots = pivotsTensor.as1D()
        val m = lu.shape[0]
        val sign = if ((pivots[m] - m) % 2 == 0) 1.0 else -1.0
        return (0 until m).asSequence().map { lu[it, it] }.fold(sign) { left, right -> left * right }
    }

    public fun DoubleTensor.detLU(): DoubleTensor {
        val (luTensor, pivotsTensor) = lu()
        val n = shape.size

        val detTensorShape = IntArray(n - 1) { i -> shape[i] }
        detTensorShape[n - 2] = 1
        val resBuffer = DoubleArray(detTensorShape.reduce(Int::times)) { 0.0 }

        val detTensor = DoubleTensor(
            detTensorShape,
            resBuffer
        )

        luTensor.matrixSequence().zip(pivotsTensor.vectorSequence()).forEachIndexed { index, (lu, pivots) ->
            resBuffer[index] = luMatrixDet(lu.as2D(), pivots.as1D())
        }

        return detTensor
    }

    private fun luMatrixInv(
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

    public fun DoubleTensor.invLU(): DoubleTensor {
        val (luTensor, pivotsTensor) = lu()
        val invTensor = luTensor.zeroesLike()

        val seq = luTensor.matrixSequence().zip(pivotsTensor.vectorSequence()).zip(invTensor.matrixSequence())
        for ((luP, invMatrix) in seq) {
            val (lu, pivots) = luP
            luMatrixInv(lu.as2D(), pivots.as1D(), invMatrix.as2D())
        }

        return invTensor
    }
}

public inline fun <R> DoubleLinearOpsTensorAlgebra(block: DoubleLinearOpsTensorAlgebra.() -> R): R =
    DoubleLinearOpsTensorAlgebra().block()