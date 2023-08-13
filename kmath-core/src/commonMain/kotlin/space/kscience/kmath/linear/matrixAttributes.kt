/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(UnstableKMathAPI::class)
@file:Suppress("UnusedReceiverParameter")

package space.kscience.kmath.linear

import space.kscience.attributes.*
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.StructureAttribute

/**
 * A marker interface representing some properties of matrices or additional transformations of them. Features are used
 * to optimize matrix operations performance in some cases or retrieve the APIs.
 */
public interface MatrixAttribute<T> : StructureAttribute<T>

/**
 * Matrices with this feature are considered to have only diagonal non-zero elements.
 */
public interface IsDiagonal : MatrixAttribute<Unit>, FlagAttribute {
    public companion object : IsDiagonal
}

/**
 * Matrices with this feature have all zero elements.
 */
public object IsZero : IsDiagonal

/**
 * Matrices with this feature have unit elements on diagonal and zero elements in all other places.
 */
public object IsUnit : IsDiagonal

/**
 * Matrices with this feature can be inverted.
 *
 * @param T the type of matrices' items.
 */
public class Inverted<T>(type: SafeType<Matrix<T>>) :
    PolymorphicAttribute<Matrix<T>>(type),
    MatrixAttribute<Matrix<T>>

public val <T> MatrixOperations<T>.Inverted: Inverted<T> get() = Inverted(safeTypeOf())

/**
 * Matrices with this feature can compute their determinant.
 *
 * @param T the type of matrices' items.
 */
public class Determinant<T> : MatrixAttribute<T>

public val <T> MatrixOperations<T>.Determinant: Determinant<T> get() = Determinant()

/**
 * Matrices with this feature are lower triangular ones.
 */
public object LowerTriangular : MatrixAttribute<Unit>, FlagAttribute

/**
 * Matrices with this feature are upper triangular ones.
 */
public object UpperTriangular : MatrixAttribute<Unit>, FlagAttribute

/**
 * Matrices with this feature support LU factorization: *a = [l] &middot; [u]* where *a* is the owning matrix.
 * @param l The lower triangular matrix in this decomposition. It may have [LowerTriangular].
 * @param u The upper triangular matrix in this decomposition. It may have [UpperTriangular].
 */
public data class LUDecomposition<T>(val l: Matrix<T>, val u: Matrix<T>)

/**
 * Matrices with this feature support LU factorization: *a = [l] &middot; [u]* where *a* is the owning matrix.
 *
 * @param T the type of matrices' items.
 */
public class LuDecompositionAttribute<T>(type: SafeType<LUDecomposition<T>>) :
    PolymorphicAttribute<LUDecomposition<T>>(type),
    MatrixAttribute<LUDecomposition<T>>

public val <T> MatrixOperations<T>.LU: LuDecompositionAttribute<T> get() = LuDecompositionAttribute(safeTypeOf())


/**
 * Matrices with this feature are orthogonal ones: *a &middot; a<sup>T</sup> = u* where *a* is the owning matrix, *u*
 * is the unit matrix ([IsUnit]).
 */
public object OrthogonalAttribute : MatrixAttribute<Unit>, FlagAttribute


public interface QRDecomposition<out T> {
    /**
     * The orthogonal matrix in this decomposition. It may have [OrthogonalAttribute].
     */
    public val q: Matrix<T>

    /**
     * The upper triangular matrix in this decomposition. It may have [UpperTriangular].
     */
    public val r: Matrix<T>
}

/**
 * Matrices with this feature support QR factorization: *a = [QR.q] &middot; [QR.r]* where *a* is the owning matrix.
 *
 * @param T the type of matrices' items.
 */
public class QRDecompositionAttribute<T>(type: SafeType<QRDecomposition<T>>) :
    PolymorphicAttribute<QRDecomposition<T>>(type),
    MatrixAttribute<QRDecomposition<T>>

public val <T> MatrixOperations<T>.QR: QRDecompositionAttribute<T>
    get() = QRDecompositionAttribute(safeTypeOf())

public interface CholeskyDecomposition<T> {
    /**
     * The triangular matrix in this decomposition. It may have either [UpperTriangular] or [LowerTriangular].
     */
    public val l: Matrix<T>
}

/**
 * Matrices with this feature support Cholesky factorization: *a = [l] &middot; [l]<sup>H</sup>* where *a* is the
 * owning matrix.
 *
 * @param T the type of matrices' items.
 */
public class CholeskyDecompositionAttribute<T>(type: SafeType<CholeskyDecomposition<T>>) :
    PolymorphicAttribute<CholeskyDecomposition<T>>(type),
    MatrixAttribute<CholeskyDecomposition<T>>

public val <T> MatrixOperations<T>.Cholesky: CholeskyDecompositionAttribute<T>
    get() = CholeskyDecompositionAttribute(safeTypeOf())

public interface SingularValueDecomposition<T> {
    /**
     * The matrix in this decomposition. It is unitary, and it consists of left singular vectors.
     */
    public val u: Matrix<T>

    /**
     * The matrix in this decomposition. Its main diagonal elements are singular values.
     */
    public val s: Matrix<T>

    /**
     * The matrix in this decomposition. It is unitary, and it consists of right singular vectors.
     */
    public val v: Matrix<T>

    /**
     * The buffer of singular values for this SVD.
     */
    public val singularValues: Point<T>
}

/**
 * Matrices with this feature support SVD: *a = [u] &middot; [s] &middot; [v]<sup>H</sup>* where *a* is the owning
 * matrix.
 *
 * @param T the type of matrices' items.
 */
public class SVDAttribute<T>(type: SafeType<SingularValueDecomposition<T>>) :
    PolymorphicAttribute<SingularValueDecomposition<T>>(type),
    MatrixAttribute<SingularValueDecomposition<T>>

public val <T> MatrixOperations<T>.SVD: SVDAttribute<T>
    get() = SVDAttribute(safeTypeOf())


//TODO add sparse matrix feature
