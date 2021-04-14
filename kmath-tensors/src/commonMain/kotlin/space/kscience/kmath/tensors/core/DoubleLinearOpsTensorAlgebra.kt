package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.LinearOpsTensorAlgebra
import space.kscience.kmath.nd.as1D
import space.kscience.kmath.nd.as2D
import kotlin.math.min


public class DoubleLinearOpsTensorAlgebra :
    LinearOpsTensorAlgebra<Double, DoubleTensor, IntTensor>,
    DoubleTensorAlgebra() {

    override fun DoubleTensor.inv(): DoubleTensor = invLU()

    override fun DoubleTensor.det(): DoubleTensor = detLU()

    internal fun DoubleTensor.luForDet(forDet: Boolean = false): Pair<DoubleTensor, IntTensor> {
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
            try {
                luHelper(lu.as2D(), pivots.as1D(), m)
            } catch (e: RuntimeException) {
                if (forDet) {
                    lu.as2D()[intArrayOf(0, 0)] = 0.0
                } else {
                    throw IllegalStateException("LUP decomposition can't be performed")
                }
            }


        return Pair(luTensor, pivotsTensor)
    }

    override fun DoubleTensor.lu(): Pair<DoubleTensor, IntTensor> {
        return luForDet(false)
    }

    override fun luPivot(
        luTensor: DoubleTensor,
        pivotsTensor: IntTensor
    ): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
        //todo checks
        checkSquareMatrix(luTensor.shape)
        check(
            luTensor.shape.dropLast(2).toIntArray() contentEquals pivotsTensor.shape.dropLast(1).toIntArray() ||
                    luTensor.shape.last() == pivotsTensor.shape.last() - 1
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

    override fun DoubleTensor.cholesky(): DoubleTensor {
        checkSymmetric(this)
        checkSquareMatrix(shape)
        checkPositiveDefinite(this)

        val n = shape.last()
        val lTensor = zeroesLike()

        for ((a, l) in this.matrixSequence().zip(lTensor.matrixSequence()))
            for (i in 0 until n) choleskyHelper(a.as2D(), l.as2D(), n)

        return lTensor
    }

    override fun DoubleTensor.qr(): Pair<DoubleTensor, DoubleTensor> {
        checkSquareMatrix(shape)
        val qTensor = zeroesLike()
        val rTensor = zeroesLike()
        val seq = matrixSequence().zip((qTensor.matrixSequence().zip(rTensor.matrixSequence())))
        for ((matrix, qr) in seq) {
            val (q, r) = qr
            qrHelper(matrix.asTensor(), q.asTensor(), r.as2D())
        }
        return qTensor to rTensor
    }

    override fun DoubleTensor.svd(): Triple<DoubleTensor, DoubleTensor, DoubleTensor> =
        svd(epsilon = 1e-10)

    public fun DoubleTensor.svd(epsilon: Double): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
        val size = this.linearStructure.dim
        val commonShape = this.shape.sliceArray(0 until size - 2)
        val (n, m) = this.shape.sliceArray(size - 2 until size)
        val resU = zeros(commonShape + intArrayOf(min(n, m), n))
        val resS = zeros(commonShape + intArrayOf(min(n, m)))
        val resV = zeros(commonShape + intArrayOf(min(n, m), m))

        for ((matrix, USV) in this.matrixSequence()
            .zip(resU.matrixSequence().zip(resS.vectorSequence().zip(resV.matrixSequence())))) {
            val size = matrix.shape.reduce { acc, i -> acc * i }
            val curMatrix = DoubleTensor(
                matrix.shape,
                matrix.buffer.array().slice(matrix.bufferStart until matrix.bufferStart + size).toDoubleArray()
            )
            svdHelper(curMatrix, USV, m, n, epsilon)
        }
        return Triple(resU.transpose(), resS, resV.transpose())
    }

    override fun DoubleTensor.symEig(): Pair<DoubleTensor, DoubleTensor> =
        symEig(epsilon = 1e-15)

    //http://hua-zhou.github.io/teaching/biostatm280-2017spring/slides/16-eigsvd/eigsvd.html
    public fun DoubleTensor.symEig(epsilon: Double): Pair<DoubleTensor, DoubleTensor> {
        checkSymmetric(this, epsilon)
        val (u, s, v) = this.svd(epsilon)
        val shp = s.shape + intArrayOf(1)
        val utv = u.transpose() dot v
        val n = s.shape.last()
        for( matrix in utv.matrixSequence())
            cleanSymHelper(matrix.as2D(),n)

        val eig = (utv dot s.view(shp)).view(s.shape)
        return Pair(eig, v)
    }

    public fun DoubleTensor.detLU(): DoubleTensor {
        val (luTensor, pivotsTensor) = luForDet(forDet = true)
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

    public fun DoubleTensor.invLU(): DoubleTensor {
        //TODO("Andrei the det is non-zero")
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