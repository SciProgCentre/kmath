/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.covariance
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.internal.*
import kotlin.math.min
import kotlin.math.sign


/**
 * Returns a tensor of random numbers drawn from normal distributions with `0.0` mean and `1.0` standard deviation.
 *
 * @param shape the desired shape for the output tensor.
 * @param seed the random seed of the pseudo-random number generator.
 * @return tensor of a given shape filled with numbers from the normal distribution
 * with `0.0` mean and `1.0` standard deviation.
 */
public fun DoubleTensorAlgebra.randomNormal(shape: ShapeND, seed: Long = 0): DoubleTensor =
    fromBuffer(shape, DoubleBuffer.randomNormals(shape.linearSize, seed))

/**
 * Returns a tensor with the same shape as `input` of random numbers drawn from normal distributions
 * with `0.0` mean and `1.0` standard deviation.
 *
 * @receiver the `input`.
 * @param seed the random seed of the pseudo-random number generator.
 * @return a tensor with the same shape as `input` filled with numbers from the normal distribution
 * with `0.0` mean and `1.0` standard deviation.
 */
public fun DoubleTensorAlgebra.randomNormalLike(structure: WithShape, seed: Long = 0): DoubleTensor =
    DoubleTensor(structure.shape, DoubleBuffer.randomNormals(structure.shape.linearSize, seed))

/**
 * Concatenates a sequence of tensors with equal shapes along the first dimension.
 *
 * @param tensors the [List] of tensors with same shapes to concatenate
 * @return tensor with concatenation result
 */
public fun stack(tensors: List<Tensor<Double>>): DoubleTensor {
    check(tensors.isNotEmpty()) { "List must have at least 1 element" }
    val shape = tensors[0].shape
    check(tensors.all { it.shape contentEquals shape }) { "Tensors must have same shapes" }
    val resShape = ShapeND(tensors.size) + shape
//        val resBuffer: List<Double> = tensors.flatMap {
//            it.asDoubleTensor().source.array.drop(it.asDoubleTensor().bufferStart)
//                .take(it.asDoubleTensor().linearSize)
//        }
    val resBuffer = tensors.map { it.asDoubleTensor().source }.concat()
    return DoubleTensor(resShape, resBuffer)
}

/**
 * Computes the LU factorization of a matrix or batches of matrices `input`.
 * Returns a tuple containing the LU factorization and pivots of `input`.
 *
 * @param epsilon permissible error when comparing the determinant of a matrix with zero default is 1e-9
 * @return pair of `factorization` and `pivots`.
 * The `factorization` has the shape ``(*, m, n)``, where``(*, m, n)`` is the shape of the `input` tensor.
 * The `pivots`  has the shape ``(∗, min(m, n))``. `pivots` stores all the intermediate transpositions of rows.
 */
public fun DoubleTensorAlgebra.luFactor(
    structureND: StructureND<Double>,
    epsilon: Double = 1e-9,
): Pair<DoubleTensor, IntTensor> =
    computeLU(structureND, epsilon)
        ?: throw IllegalArgumentException("Tensor contains matrices which are singular at precision $epsilon")


/**
 * Unpacks the data and pivots from a LU factorization of a tensor.
 * Given a tensor [luTensor], return tensors `Triple(P, L, U)` satisfying `P dot luTensor = L dot U`,
 * with `P` being a permutation matrix or batch of matrices,
 * `L` being a lower triangular matrix or batch of matrices,
 * `U` being an upper triangular matrix or batch of matrices.
 *
 * @param luTensor the packed LU factorization data
 * @param pivotsTensor the packed LU factorization pivots
 * @return triple of `P`, `L` and `U` tensors
 */
public fun DoubleTensorAlgebra.luPivot(
    luTensor: StructureND<Double>,
    pivotsTensor: Tensor<Int>,
): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
    checkSquareMatrix(luTensor.shape)
    check(
        luTensor.shape.first(luTensor.shape.size - 2) contentEquals pivotsTensor.shape.first(pivotsTensor.shape.size - 1) ||
                luTensor.shape.last() == pivotsTensor.shape.last() - 1
    ) { "Inappropriate shapes of input tensors" }

    val n = luTensor.shape.last()
    val pTensor = zeroesLike(luTensor)
    pTensor
        .matrixSequence()
        .zip(pivotsTensor.asIntTensor().vectorSequence())
        .forEach { (p, pivot) -> pivInit(p.asDoubleTensor2D(), pivot.as1D(), n) }

    val lTensor = zeroesLike(luTensor)
    val uTensor = zeroesLike(luTensor)

    lTensor.matrixSequence()
        .zip(uTensor.matrixSequence())
        .zip(luTensor.asDoubleTensor().matrixSequence())
        .forEach { (pairLU, lu) ->
            val (l, u) = pairLU
            luPivotHelper(l.asDoubleTensor2D(), u.asDoubleTensor2D(), lu.asDoubleTensor2D(), n)
        }

    return Triple(pTensor, lTensor, uTensor)
}


/**
 * LUP decomposition.
 *
 * Computes the LUP decomposition of a matrix or a batch of matrices.
 * Given a tensor `input`, return tensors `Triple(P, L, U)` satisfying `P dot input == L dot U`,
 * with `P` being a permutation matrix or batch of matrices,
 * `L` being a lower triangular matrix or batch of matrices,
 * `U` being an upper triangular matrix or batch of matrices.
 *
 * @param epsilon permissible error when comparing the determinant of a matrix with zero.
 * @return triple of `P`, `L` and `U` tensors.
 */
public fun DoubleTensorAlgebra.lu(
    structureND: StructureND<Double>,
    epsilon: Double = 1e-9,
): Triple<DoubleTensor, DoubleTensor, DoubleTensor> {
    val (lu, pivots) = luFactor(structureND, epsilon)
    return luPivot(lu, pivots)
}


/**
 * QR decomposition.
 *
 * Computes the QR decomposition of a matrix or a batch of matrices, and returns a pair `Q to R` of tensors.
 * Given a tensor `input`, return tensors `Q to R` satisfying `input == Q dot R`,
 * with `Q` being an orthogonal matrix or batch of orthogonal matrices
 * and `R` being an upper triangular matrix or batch of upper triangular matrices.
 *
 * @receiver the `input`.
 * @param epsilon the permissible error when comparing tensors for equality. The default is 1e-6
 * Used when checking the positive definiteness of the input matrix or matrices.
 * @return a pair of `Q` and `R` tensors.
 */
public fun DoubleTensorAlgebra.cholesky(structureND: StructureND<Double>, epsilon: Double = 1e-6): DoubleTensor {
    checkSquareMatrix(structureND.shape)
    checkPositiveDefinite(structureND.asDoubleTensor(), epsilon)

    val n = structureND.shape.last()
    val lTensor = zeroesLike(structureND)

    for ((a, l) in structureND.asDoubleTensor().matrixSequence().zip(lTensor.matrixSequence()))
        for (i in 0 until n) choleskyHelper(a.asDoubleTensor2D(), l.asDoubleTensor2D(), n)

    return lTensor
}


/**
 * Singular Value Decomposition.
 *
 * Computes the singular value decomposition of either a matrix or batch of matrices `input`.
 * The singular value decomposition is represented as a triple `Triple(U, S, V)`,
 * such that `input == U dot diagonalEmbedding(S) dot V.transpose()`.
 * If `input` is a batch of tensors, then U, S, and Vh are also batched with the same batch dimensions as `input.
 *
 * @receiver the `input`.
 * @param epsilon permissible error when calculating the dot product of vectors
 * i.e., the precision with which the cosine approaches 1 in an iterative algorithm.
 * @return a triple `Triple(U, S, V)`.
 */
public fun DoubleTensorAlgebra.svd(
    structureND: StructureND<Double>,
    epsilon: Double,
): Triple<StructureND<Double>, StructureND<Double>, StructureND<Double>> {
    val size = structureND.dimension
    val commonShape = structureND.shape.slice(0 until size - 2)
    val (n, m) = structureND.shape.slice(size - 2 until size)
    val uTensor = zeros(commonShape + ShapeND(min(n, m), n))
    val sTensor = zeros(commonShape + ShapeND(min(n, m)))
    val vTensor = zeros(commonShape + ShapeND(min(n, m), m))

    val matrices = structureND.asDoubleTensor().matrices
    val uTensors = uTensor.matrices
    val sTensorVectors = sTensor.vectors
    val vTensors = vTensor.matrices

    for (index in matrices.indices) {
        val matrix = matrices[index]
        val usv = Triple(
            uTensors[index],
            sTensorVectors[index],
            vTensors[index]
        )
        val matrixSize = matrix.shape.linearSize
        val curMatrix = DoubleTensor(
            matrix.shape,
            matrix.source.view(0, matrixSize)
        )
        svdHelper(curMatrix, usv, m, n, epsilon)
    }

    return Triple(uTensor.transposed(), sTensor, vTensor.transposed())
}

/**
 * Returns eigenvalues and eigenvectors of a real symmetric matrix input or a batch of real symmetric matrices,
 * represented by a pair `eigenvalues to eigenvectors`.
 *
 * @param epsilon the permissible error when comparing tensors for equality
 * and when the cosine approaches 1 in the SVD algorithm.
 * @return a pair `eigenvalues to eigenvectors`.
 */
public fun DoubleTensorAlgebra.symEigSvd(
    structureND: StructureND<Double>,
    epsilon: Double,
): Pair<DoubleTensor, StructureND<Double>> {
    //TODO optimize conversion
    checkSymmetric(structureND.asDoubleTensor(), epsilon)

    fun MutableStructure2D<Double>.cleanSym(n: Int) {
        for (i in 0 until n) {
            for (j in 0 until n) {
                if (i == j) {
                    this[i, j] = sign(this[i, j])
                } else {
                    this[i, j] = 0.0
                }
            }
        }
    }

    val (u, s, v) = svd(structureND, epsilon)
    val shp = s.shape + intArrayOf(1)
    val utv = u.transposed() matmul v
    val n = s.shape.last()
    for (matrix in utv.matrixSequence()) {
        matrix.asDoubleTensor2D().cleanSym(n)
    }

    val eig = (utv dot s.asDoubleTensor().view(shp)).view(s.shape)
    return eig to v
}

public fun DoubleTensorAlgebra.symEigJacobi(
    structureND: StructureND<Double>,
    maxIteration: Int,
    epsilon: Double,
): Pair<DoubleTensor, DoubleTensor> {
    //TODO optimize conversion
    checkSymmetric(structureND.asDoubleTensor(), epsilon)

    val size = structureND.dimension
    val eigenvectors = zeros(structureND.shape)
    val eigenvalues = zeros(structureND.shape.slice(0 until size - 1))

    var eigenvalueStart = 0
    var eigenvectorStart = 0
    for (matrix in structureND.asDoubleTensor().matrixSequence()) {
        val matrix2D = matrix.asDoubleTensor2D()
        val (d, v) = matrix2D.jacobiHelper(maxIteration, epsilon)

        for (i in 0 until matrix2D.rowNum) {
            for (j in 0 until matrix2D.colNum) {
                eigenvectors.source[eigenvectorStart + i * matrix2D.rowNum + j] = v[i, j]
            }
        }

        for (i in 0 until matrix2D.rowNum) {
            eigenvalues.source[eigenvalueStart + i] = d[i]
        }

        eigenvalueStart += structureND.shape.last()
        eigenvectorStart += structureND.shape.last() * structureND.shape.last()
    }

    return eigenvalues to eigenvectors
}

/**
 * Computes the determinant of a square matrix input, or of each square matrix in a batched input
 * using LU factorization algorithm.
 *
 * @param epsilon the error in the LU algorithm&mdash;permissible error when comparing the determinant of a matrix
 * with zero.
 * @return the determinant.
 */
public fun DoubleTensorAlgebra.detLU(structureND: StructureND<Double>, epsilon: Double = 1e-9): DoubleTensor {
    checkSquareMatrix(structureND.shape)
    //TODO check for unnecessary copies
    val luTensor = structureND.copyToTensor()
    val pivotsTensor = structureND.setUpPivots()

    val n = structureND.shape.size

    val detTensorShape = ShapeND(IntArray(n - 1) { i -> structureND.shape[i] }.apply {
        set(n - 2, 1)
    })

    val resBuffer = DoubleBuffer(detTensorShape.linearSize) { 0.0 }

    val detTensor = DoubleTensor(
        detTensorShape,
        resBuffer
    )

    luTensor.matrixSequence().zip(pivotsTensor.vectorSequence()).forEachIndexed { index, (lu, pivots) ->
        resBuffer[index] = if (luHelper(lu.asDoubleTensor2D(), pivots.as1D(), epsilon))
            0.0 else luMatrixDet(lu.asDoubleTensor2D(), pivots.as1D())
    }

    return detTensor
}

/**
 * Computes the multiplicative inverse matrix of a square matrix input, or of each square matrix in a batched input
 * using LU factorization algorithm.
 * Given a square matrix `a`, return the matrix `aInv` satisfying
 * `a dot aInv == aInv dot a == eye(a.shape[0])`.
 *
 * @param epsilon error in the LU algorithm&mdash;permissible error when comparing the determinant of a matrix with zero
 * @return the multiplicative inverse of a matrix.
 */
public fun DoubleTensorAlgebra.invLU(structureND: StructureND<Double>, epsilon: Double = 1e-9): DoubleTensor {
    val (luTensor, pivotsTensor) = luFactor(structureND, epsilon)
    val invTensor = zeroesLike(luTensor)

    //TODO replace sequence with a cycle
    val seq = luTensor.matrixSequence().zip(pivotsTensor.vectorSequence()).zip(invTensor.matrixSequence())
    for ((luP, invMatrix) in seq) {
        val (lu, pivots) = luP
        luMatrixInv(lu.asDoubleTensor2D(), pivots.as1D(), invMatrix.asDoubleTensor2D())
    }

    return invTensor
}

/**
 * Returns the covariance matrix `M` of given vectors.
 *
 * `M[i, j]` contains covariance of `i`-th and `j`-th given vectors
 *
 * @param vectors the [List] of 1-dimensional tensors with same shape
 * @return `M`.
 */
public fun DoubleTensorAlgebra.covariance(vectors: List<Buffer<Double>>): DoubleTensor {
    check(vectors.isNotEmpty()) { "List must have at least 1 element" }
    val n = vectors.size
    val m = vectors[0].size
    check(vectors.all { it.size == m }) { "Vectors must have same shapes" }
    val resTensor = DoubleTensor(
        ShapeND(n, n),
        DoubleBuffer(n * n) { 0.0 }
    )
    for (i in 0 until n) {
        for (j in 0 until n) {
            resTensor[intArrayOf(i, j)] = bufferAlgebra.covariance(vectors[i], vectors[j])
        }
    }
    return resTensor
}