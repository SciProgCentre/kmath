package space.kscience.kmath.tensors

public class DoubleLinearOpsTensorAlgebra :
    LinearOpsTensorAlgebra<Double, DoubleTensor, IntTensor>,
    DoubleTensorAlgebra() {

    override fun DoubleTensor.inv(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.lu(): Pair<DoubleTensor, IntTensor> {

        // todo checks

        val luTensor = this.copy()

        val n = this.shape.size
        val m = this.shape.last()
        val pivotsShape = IntArray(n - 1) { i -> this.shape[i] }
        val pivotsTensor = IntTensor(
            pivotsShape,
            IntArray(pivotsShape.reduce(Int::times)) { 0 } //todo default???
        )

        for ((lu, pivots) in luTensor.matrixSequence().zip(pivotsTensor.vectorSequence())){
            for (row in 0 until m) pivots[row] = row

            for (i in 0 until m) {
                var maxA = -1.0
                var iMax = i

                for (k in i until m) {
                    val absA = kotlin.math.abs(lu[k, i])
                    if (absA > maxA) {
                        maxA = absA
                        iMax = k
                    }
                }

                //todo check singularity

                if (iMax != i) {

                    val j = pivots[i]
                    pivots[i] = pivots[iMax]
                    pivots[iMax] = j

                    for (k in 0 until m) {
                        val tmp = lu[i, k]
                        lu[i, k] = lu[iMax, k]
                        lu[iMax, k] = tmp
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
        TODO("Not yet implemented")
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