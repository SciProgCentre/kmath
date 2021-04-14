package space.kscience.kmath.tensors.core

import space.kscience.kmath.tensors.LinearOpsTensorAlgebra
import space.kscience.kmath.nd.as1D
import space.kscience.kmath.nd.as2D
import kotlin.math.min


public class DoubleLinearOpsTensorAlgebra :
    LinearOpsTensorAlgebra<Double, DoubleTensor, IntTensor>,
    DoubleTensorAlgebra() {

    override fun DoubleTensor.inv(): DoubleTensor = invLU(1e-9)

    override fun DoubleTensor.det(): DoubleTensor = detLU(1e-9)

    public fun DoubleTensor.lu(epsilon: Double): Pair<DoubleTensor, IntTensor> =
        computeLU(this, epsilon) ?:
        throw RuntimeException("Tensor contains matrices which are singular at precision $epsilon")

    override fun DoubleTensor.lu(): Pair<DoubleTensor, IntTensor> = lu(1e-9)

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

    public fun DoubleTensor.cholesky(epsilon: Double): DoubleTensor {
        checkSquareMatrix(shape)
        checkPositiveDefinite(this, epsilon)

        val n = shape.last()
        val lTensor = zeroesLike()

        for ((a, l) in this.matrixSequence().zip(lTensor.matrixSequence()))
            for (i in 0 until n) choleskyHelper(a.as2D(), l.as2D(), n)

        return lTensor
    }

    override fun DoubleTensor.cholesky(): DoubleTensor = cholesky(1e-6)

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
        for (matrix in utv.matrixSequence())
            cleanSymHelper(matrix.as2D(), n)

        val eig = (utv dot s.view(shp)).view(s.shape)
        return Pair(eig, v)
    }

    public fun DoubleTensor.detLU(epsilon: Double = 1e-9): DoubleTensor {

        checkSquareMatrix(this.shape)
        val luTensor = this.copy()
        val pivotsTensor = this.setUpPivots()

        val n = shape.size

        val detTensorShape = IntArray(n - 1) { i -> shape[i] }
        detTensorShape[n - 2] = 1
        val resBuffer = DoubleArray(detTensorShape.reduce(Int::times)) { 0.0 }

        val detTensor = DoubleTensor(
            detTensorShape,
            resBuffer
        )

        luTensor.matrixSequence().zip(pivotsTensor.vectorSequence()).forEachIndexed { index, (lu, pivots) ->
            resBuffer[index] = if (luHelper(lu.as2D(), pivots.as1D(), epsilon))
                0.0 else luMatrixDet(lu.as2D(), pivots.as1D())
        }

        return detTensor
    }

    public fun DoubleTensor.invLU(epsilon: Double = 1e-9): DoubleTensor {
        val (luTensor, pivotsTensor) = lu(epsilon)
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