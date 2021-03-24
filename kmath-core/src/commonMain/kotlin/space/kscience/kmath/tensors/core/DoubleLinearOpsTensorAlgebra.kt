package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.LinearOpsTensorAlgebra
import kotlin.math.sqrt

public class DoubleLinearOpsTensorAlgebra :
    LinearOpsTensorAlgebra<Double, DoubleTensor, IntTensor>,
    DoubleTensorAlgebra() {

    override fun DoubleTensor.inv(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.lu(): Pair<DoubleTensor, IntTensor> {

        checkSquareMatrix(shape)

        val luTensor = copy()

        val n = shape.size
        val m = shape.last()
        val pivotsShape = IntArray(n - 1) { i -> shape[i] }
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
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.svd(): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.symEig(eigenvectors: Boolean): Pair<DoubleTensor, DoubleTensor> {
        TODO("Not yet implemented")
    }

}

public inline fun <R> DoubleLinearOpsTensorAlgebra(block: DoubleLinearOpsTensorAlgebra.() -> R): R =
    DoubleLinearOpsTensorAlgebra().block()