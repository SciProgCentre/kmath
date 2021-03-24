package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.MutableStructure2D
import space.kscience.kmath.nd.Structure1D
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.tensors.LinearOpsTensorAlgebra
import kotlin.math.sqrt

public class DoubleLinearOpsTensorAlgebra :
    LinearOpsTensorAlgebra<Double, DoubleTensor, IntTensor>,
    DoubleTensorAlgebra() {

    override fun DoubleTensor.inv(): DoubleTensor {
        TODO("ANDREI")
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

        for ((lu, pivots) in luTensor.matrixSequence().zip(pivotsTensor.vectorSequence())){
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


        return Pair(luTensor, pivotsTensor)

    }

    override fun luPivot(luTensor: DoubleTensor, pivotsTensor: IntTensor): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
        //todo checks
        checkSquareMatrix(luTensor.shape)
        check(luTensor.shape.dropLast(1).toIntArray() contentEquals pivotsTensor.shape) { "Bed shapes (("} //todo rewrite

        val n = luTensor.shape.last()
        val pTensor = luTensor.zeroesLike()
        for ((p, pivot) in pTensor.matrixSequence().zip(pivotsTensor.vectorSequence())){
            for (i in 0 until n){
                p[i, pivot[i]] = 1.0
            }
        }

        val lTensor = luTensor.zeroesLike()
        val uTensor = luTensor.zeroesLike()

        for ((pairLU, lu) in lTensor.matrixSequence().zip(uTensor.matrixSequence()).zip(luTensor.matrixSequence())){
            val (l, u) = pairLU
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

        return Triple(pTensor, lTensor, uTensor)

    }

    override fun DoubleTensor.cholesky(): DoubleTensor {
        // todo checks
        checkSquareMatrix(shape)

        val n = shape.last()
        val lTensor = zeroesLike()

        for ((a, l) in this.matrixSequence().zip(lTensor.matrixSequence())) {
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

        return lTensor
    }

    override fun DoubleTensor.qr(): DoubleTensor {
        TODO("ANDREI")
    }

    override fun DoubleTensor.svd(): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
        TODO("ALYA")
    }

    override fun DoubleTensor.symEig(eigenvectors: Boolean): Pair<DoubleTensor, DoubleTensor> {
        TODO("ANDREI")
    }

    private fun luMatrixDet(lu: Structure2D<Double>, pivots: Structure1D<Int>): Double {
        val m = lu.shape[0]
        val sign = if((pivots[m] - m) % 2 == 0) 1.0 else -1.0
        var det = sign
        for (i in 0 until m){
            det *= lu[i, i]
        }
        return det
    }

    public fun DoubleTensor.detLU(): DoubleTensor {
        val (luTensor, pivotsTensor) = this.lu()
        val n = shape.size

        val detTensorShape = IntArray(n - 1) { i -> shape[i] }
        detTensorShape[n - 2] = 1
        val resBuffer =  DoubleArray(detTensorShape.reduce(Int::times)) { 0.0 }

        val detTensor = DoubleTensor(
            detTensorShape,
            resBuffer
        )

        luTensor.matrixSequence().zip(pivotsTensor.vectorSequence()).forEachIndexed { index, (luMatrix, pivots) ->
            resBuffer[index] = luMatrixDet(luMatrix, pivots)
        }

        return detTensor
    }

    private fun luMatrixInv(
        lu: Structure2D<Double>,
        pivots: Structure1D<Int>,
        invMatrix : MutableStructure2D<Double>
    ): Unit {
        val m = lu.shape[0]

        for (j in 0 until m) {
            for (i in 0 until m) {
                if (pivots[i] == j){
                    invMatrix[i, j] = 1.0
                }

                for (k in 0 until i){
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
        val (luTensor, pivotsTensor) = this.lu()
        val n = shape.size
        val invTensor = luTensor.zeroesLike()

        for (
        (luP, invMatrix) in
        luTensor.matrixSequence().zip(pivotsTensor.vectorSequence()).zip(invTensor.matrixSequence())
        ) {
            val (lu, pivots) = luP
            luMatrixInv(lu, pivots, invMatrix)
        }

        return invTensor
    }
}

public inline fun <R> DoubleLinearOpsTensorAlgebra(block: DoubleLinearOpsTensorAlgebra.() -> R): R =
    DoubleLinearOpsTensorAlgebra().block()