/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core.algebras

import space.kscience.kmath.tensors.api.LinearOpsTensorAlgebra
import space.kscience.kmath.nd.as1D
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.tensors.api.TensorStructure
import space.kscience.kmath.tensors.core.*
import space.kscience.kmath.tensors.core.checkSquareMatrix
import space.kscience.kmath.tensors.core.choleskyHelper
import space.kscience.kmath.tensors.core.cleanSymHelper
import space.kscience.kmath.tensors.core.luHelper
import space.kscience.kmath.tensors.core.luMatrixDet
import space.kscience.kmath.tensors.core.luMatrixInv
import space.kscience.kmath.tensors.core.luPivotHelper
import space.kscience.kmath.tensors.core.pivInit
import kotlin.math.min


public class DoubleLinearOpsTensorAlgebra :
    LinearOpsTensorAlgebra<Double>,
    DoubleTensorAlgebra() {

    override fun TensorStructure<Double>.inv(): DoubleTensor = invLU(1e-9)

    override fun TensorStructure<Double>.det(): DoubleTensor = detLU(1e-9)

    public fun TensorStructure<Double>.lu(epsilon: Double): Pair<DoubleTensor, IntTensor> =
        computeLU(tensor, epsilon) ?:
        throw RuntimeException("Tensor contains matrices which are singular at precision $epsilon")

    override fun TensorStructure<Double>.lu(): Pair<DoubleTensor, IntTensor> = lu(1e-9)

    override fun luPivot(
        luTensor: TensorStructure<Double>,
        pivotsTensor: TensorStructure<Int>
    ): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
        checkSquareMatrix(luTensor.shape)
        check(
            luTensor.shape.dropLast(2).toIntArray() contentEquals pivotsTensor.shape.dropLast(1).toIntArray() ||
                    luTensor.shape.last() == pivotsTensor.shape.last() - 1
        ) { "Inappropriate shapes of input tensors" }

        val n = luTensor.shape.last()
        val pTensor = luTensor.zeroesLike()
        for ((p, pivot) in pTensor.matrixSequence().zip(pivotsTensor.tensor.vectorSequence()))
            pivInit(p.as2D(), pivot.as1D(), n)

        val lTensor = luTensor.zeroesLike()
        val uTensor = luTensor.zeroesLike()

        for ((pairLU, lu) in lTensor.matrixSequence().zip(uTensor.matrixSequence())
            .zip(luTensor.tensor.matrixSequence())) {
            val (l, u) = pairLU
            luPivotHelper(l.as2D(), u.as2D(), lu.as2D(), n)
        }

        return Triple(pTensor, lTensor, uTensor)

    }

    public fun TensorStructure<Double>.cholesky(epsilon: Double): DoubleTensor {
        checkSquareMatrix(shape)
        checkPositiveDefinite(tensor, epsilon)

        val n = shape.last()
        val lTensor = zeroesLike()

        for ((a, l) in tensor.matrixSequence().zip(lTensor.matrixSequence()))
            for (i in 0 until n) choleskyHelper(a.as2D(), l.as2D(), n)

        return lTensor
    }

    override fun TensorStructure<Double>.cholesky(): DoubleTensor = cholesky(1e-6)

    override fun TensorStructure<Double>.qr(): Pair<DoubleTensor, DoubleTensor> {
        checkSquareMatrix(shape)
        val qTensor = zeroesLike()
        val rTensor = zeroesLike()
        val seq = tensor.matrixSequence().zip((qTensor.matrixSequence().zip(rTensor.matrixSequence())))
        for ((matrix, qr) in seq) {
            val (q, r) = qr
            qrHelper(matrix.asTensor(), q.asTensor(), r.as2D())
        }
        return qTensor to rTensor
    }

    override fun TensorStructure<Double>.svd(): Triple<DoubleTensor, DoubleTensor, DoubleTensor> =
        svd(epsilon = 1e-10)

    public fun TensorStructure<Double>.svd(epsilon: Double): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
        val size = tensor.linearStructure.dim
        val commonShape = tensor.shape.sliceArray(0 until size - 2)
        val (n, m) = tensor.shape.sliceArray(size - 2 until size)
        val resU = zeros(commonShape + intArrayOf(min(n, m), n))
        val resS = zeros(commonShape + intArrayOf(min(n, m)))
        val resV = zeros(commonShape + intArrayOf(min(n, m), m))

        for ((matrix, USV) in tensor.matrixSequence()
            .zip(resU.matrixSequence().zip(resS.vectorSequence().zip(resV.matrixSequence())))) {
            val matrixSize = matrix.shape.reduce { acc, i -> acc * i }
            val curMatrix = DoubleTensor(
                matrix.shape,
                matrix.buffer.array().slice(matrix.bufferStart until matrix.bufferStart + matrixSize).toDoubleArray()
            )
            svdHelper(curMatrix, USV, m, n, epsilon)
        }
        return Triple(resU.transpose(), resS, resV.transpose())
    }

    override fun TensorStructure<Double>.symEig(): Pair<DoubleTensor, DoubleTensor> =
        symEig(epsilon = 1e-15)

    //http://hua-zhou.github.io/teaching/biostatm280-2017spring/slides/16-eigsvd/eigsvd.html
    public fun TensorStructure<Double>.symEig(epsilon: Double): Pair<DoubleTensor, DoubleTensor> {
        checkSymmetric(tensor, epsilon)
        val (u, s, v) = tensor.svd(epsilon)
        val shp = s.shape + intArrayOf(1)
        val utv = u.transpose() dot v
        val n = s.shape.last()
        for (matrix in utv.matrixSequence())
            cleanSymHelper(matrix.as2D(), n)

        val eig = (utv dot s.view(shp)).view(s.shape)
        return Pair(eig, v)
    }

    public fun TensorStructure<Double>.detLU(epsilon: Double = 1e-9): DoubleTensor {

        checkSquareMatrix(tensor.shape)
        val luTensor = tensor.copy()
        val pivotsTensor = tensor.setUpPivots()

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

    public fun TensorStructure<Double>.invLU(epsilon: Double = 1e-9): DoubleTensor {
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