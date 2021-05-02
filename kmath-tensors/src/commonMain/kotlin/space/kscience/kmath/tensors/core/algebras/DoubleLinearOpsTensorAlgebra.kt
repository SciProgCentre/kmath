/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core.algebras

import space.kscience.kmath.tensors.api.LinearOpsTensorAlgebra
import space.kscience.kmath.nd.as1D
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.tensors.api.Tensor
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


public object DoubleLinearOpsTensorAlgebra :
    LinearOpsTensorAlgebra<Double>,
    DoubleTensorAlgebra() {

    override fun Tensor<Double>.inv(): DoubleTensor = invLU(1e-9)

    override fun Tensor<Double>.det(): DoubleTensor = detLU(1e-9)

    public fun Tensor<Double>.luFactor(epsilon: Double): Pair<DoubleTensor, IntTensor> =
        computeLU(tensor, epsilon)
            ?: throw IllegalArgumentException("Tensor contains matrices which are singular at precision $epsilon")

    public fun Tensor<Double>.luFactor(): Pair<DoubleTensor, IntTensor> = luFactor(1e-9)

    public fun luPivot(
        luTensor: Tensor<Double>,
        pivotsTensor: Tensor<Int>
    ): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
        checkSquareMatrix(luTensor.shape)
        check(
            luTensor.shape.dropLast(2).toIntArray() contentEquals pivotsTensor.shape.dropLast(1).toIntArray() ||
                    luTensor.shape.last() == pivotsTensor.shape.last() - 1
        ) { "Inappropriate shapes of input tensors" }

        val n = luTensor.shape.last()
        val pTensor = luTensor.zeroesLike()
        pTensor
            .matrixSequence()
            .zip(pivotsTensor.tensor.vectorSequence())
            .forEach { (p, pivot) -> pivInit(p.as2D(), pivot.as1D(), n) }

        val lTensor = luTensor.zeroesLike()
        val uTensor = luTensor.zeroesLike()

        lTensor.matrixSequence()
            .zip(uTensor.matrixSequence())
            .zip(luTensor.tensor.matrixSequence())
            .forEach { (pairLU, lu) ->
                val (l, u) = pairLU
                luPivotHelper(l.as2D(), u.as2D(), lu.as2D(), n)
            }

        return Triple(pTensor, lTensor, uTensor)
    }

    public fun Tensor<Double>.cholesky(epsilon: Double): DoubleTensor {
        checkSquareMatrix(shape)
        checkPositiveDefinite(tensor, epsilon)

        val n = shape.last()
        val lTensor = zeroesLike()

        for ((a, l) in tensor.matrixSequence().zip(lTensor.matrixSequence()))
            for (i in 0 until n) choleskyHelper(a.as2D(), l.as2D(), n)

        return lTensor
    }

    override fun Tensor<Double>.cholesky(): DoubleTensor = cholesky(1e-6)

    override fun Tensor<Double>.qr(): Pair<DoubleTensor, DoubleTensor> {
        checkSquareMatrix(shape)
        val qTensor = zeroesLike()
        val rTensor = zeroesLike()
        tensor.matrixSequence()
            .zip((qTensor.matrixSequence()
                .zip(rTensor.matrixSequence()))).forEach { (matrix, qr) ->
            val (q, r) = qr
            qrHelper(matrix.asTensor(), q.asTensor(), r.as2D())
        }

        return qTensor to rTensor
    }

    override fun Tensor<Double>.svd(): Triple<DoubleTensor, DoubleTensor, DoubleTensor> =
        svd(epsilon = 1e-10)

    public fun Tensor<Double>.svd(epsilon: Double): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
        val size = tensor.linearStructure.dim
        val commonShape = tensor.shape.sliceArray(0 until size - 2)
        val (n, m) = tensor.shape.sliceArray(size - 2 until size)
        val uTensor = zeros(commonShape + intArrayOf(min(n, m), n))
        val sTensor = zeros(commonShape + intArrayOf(min(n, m)))
        val vTensor = zeros(commonShape + intArrayOf(min(n, m), m))

        tensor.matrixSequence()
            .zip(uTensor.matrixSequence()
                .zip(sTensor.vectorSequence()
                    .zip(vTensor.matrixSequence()))).forEach { (matrix, USV) ->
                val matrixSize = matrix.shape.reduce { acc, i -> acc * i }
                val curMatrix = DoubleTensor(
                    matrix.shape,
                    matrix.mutableBuffer.array().slice(matrix.bufferStart until matrix.bufferStart + matrixSize)
                        .toDoubleArray()
                )
                svdHelper(curMatrix, USV, m, n, epsilon)
            }

        return Triple(uTensor.transpose(), sTensor, vTensor.transpose())
    }

    override fun Tensor<Double>.symEig(): Pair<DoubleTensor, DoubleTensor> =
        symEig(epsilon = 1e-15)

    //For information: http://hua-zhou.github.io/teaching/biostatm280-2017spring/slides/16-eigsvd/eigsvd.html
    public fun Tensor<Double>.symEig(epsilon: Double): Pair<DoubleTensor, DoubleTensor> {
        checkSymmetric(tensor, epsilon)
        val (u, s, v) = tensor.svd(epsilon)
        val shp = s.shape + intArrayOf(1)
        val utv = u.transpose() dot v
        val n = s.shape.last()
        for (matrix in utv.matrixSequence())
            cleanSymHelper(matrix.as2D(), n)

        val eig = (utv dot s.view(shp)).view(s.shape)
        return eig to v
    }

    public fun Tensor<Double>.detLU(epsilon: Double = 1e-9): DoubleTensor {

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

    public fun Tensor<Double>.invLU(epsilon: Double = 1e-9): DoubleTensor {
        val (luTensor, pivotsTensor) = luFactor(epsilon)
        val invTensor = luTensor.zeroesLike()

        val seq = luTensor.matrixSequence().zip(pivotsTensor.vectorSequence()).zip(invTensor.matrixSequence())
        for ((luP, invMatrix) in seq) {
            val (lu, pivots) = luP
            luMatrixInv(lu.as2D(), pivots.as1D(), invMatrix.as2D())
        }

        return invTensor
    }

    public fun Tensor<Double>.lu(epsilon: Double = 1e-9): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
        val (lu, pivots) = this.luFactor(epsilon)
        return luPivot(lu, pivots)
    }

    override fun Tensor<Double>.lu(): Triple<DoubleTensor, DoubleTensor, DoubleTensor> = lu(1e-9)

}