package space.kscience.kmath.tensors

public class RealLinearOpsTensorAlgebra :
    LinearOpsTensorAlgebra<Double, DoubleTensor>,
    RealTensorAlgebra()
{
    override fun eye(n: Int): DoubleTensor {
        val shape = intArrayOf(n, n)
        val buffer = DoubleArray(n * n) { 0.0 }
        val res = DoubleTensor(shape, buffer)
        for (i in 0 until n) {
            res[intArrayOf(i, i)] = 1.0
        }
        return res
    }


    override fun DoubleTensor.dot(other: DoubleTensor): DoubleTensor {
        TODO("Alya")
    }

    override fun diagonalEmbedding(diagonalEntries: DoubleTensor, offset: Int, dim1: Int, dim2: Int): DoubleTensor {
        TODO("Alya")
    }


    override fun DoubleTensor.lu(): Pair<DoubleTensor, IntTensor> {
        // todo checks
        val lu = this.copy()
        val m = this.shape[0]
        val pivot = IntArray(m)


        // Initialize permutation array and parity
        for (row in 0 until m) pivot[row] = row
        var even = true

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

                val j = pivot[i]
                pivot[i] = pivot[iMax]
                pivot[iMax] = j
                even != even

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
        return Pair(lu, IntTensor(intArrayOf(m), pivot))
    }

    override fun luPivot(lu: DoubleTensor, pivots: IntTensor): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
        // todo checks
        val n = lu.shape[0]
        val p = lu.zeroesLike()
        pivots.buffer.unsafeToIntArray().forEachIndexed { i, pivot ->
            p[i, pivot] = 1.0
        }
        val l = lu.zeroesLike()
        val u = lu.zeroesLike()

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
        return Triple(p, l, u)
    }

    override fun DoubleTensor.det(): DoubleTensor {
        TODO("Not yet implemented")
    }

    override fun DoubleTensor.inv(): DoubleTensor {
        TODO("Not yet implemented")
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

public inline fun <R> RealLinearOpsTensorAlgebra(block: RealLinearOpsTensorAlgebra.() -> R): R =
    RealLinearOpsTensorAlgebra().block()