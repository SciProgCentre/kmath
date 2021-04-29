/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.api

/**
 * Common linear algebra operations. Operates on [TensorStructure].
 *
 * @param T the type of items in the tensors.
 */
public interface LinearOpsTensorAlgebra<T> :
    TensorPartialDivisionAlgebra<T> {

    /**
     * Computes the determinant of a square matrix input, or of each square matrix in a batched input.
     * For more information: https://pytorch.org/docs/stable/linalg.html#torch.linalg.det
     *
     * @return the determinant.
     */
    public fun TensorStructure<T>.det(): TensorStructure<T>

    /**
     * Computes the multiplicative inverse matrix of a square matrix input, or of each square matrix in a batched input.
     * Given a square matrix `a`, return the matrix `aInv` satisfying
     * ``a.dot(aInv) = aInv.dot(a) = eye(a.shape[0])``.
     * For more information: https://pytorch.org/docs/stable/linalg.html#torch.linalg.inv
     *
     * @return the multiplicative inverse of a matrix.
     */
    public fun TensorStructure<T>.inv(): TensorStructure<T>

    /**
     * Cholesky decomposition.
     *
     * Computes the Cholesky decomposition of a Hermitian (or symmetric for real-valued matrices)
     * positive-definite matrix or the Cholesky decompositions for a batch of such matrices.
     * Each decomposition has the form:
     * Given a tensor `input`, return the tensor `L` satisfying ``input = L * L.H``,
     * where L is a lower-triangular matrix and L.H is the conjugate transpose of L,
     * which is just a transpose for the case of real-valued input matrices.
     * For more information: https://pytorch.org/docs/stable/linalg.html#torch.linalg.cholesky
     *
     * @return the batch of L matrices.
     */
    public fun TensorStructure<T>.cholesky(): TensorStructure<T>

    /**
     * QR decomposition.
     *
     * Computes the QR decomposition of a matrix or a batch of matrices, and returns a namedtuple `(Q, R)` of tensors.
     * Given a tensor `input`, return tensors (Q, R) satisfying ``input = Q * R``,
     * with `Q` being an orthogonal matrix or batch of orthogonal matrices
     * and `R` being an upper triangular matrix or batch of upper triangular matrices.
     * For more information: https://pytorch.org/docs/stable/linalg.html#torch.linalg.qr
     *
     * @return tuple of Q and R tensors.
     */
    public fun TensorStructure<T>.qr(): Pair<TensorStructure<T>, TensorStructure<T>>

    /**
     * TODO('Andrew')
     * For more information: https://pytorch.org/docs/stable/generated/torch.lu.html
     *
     * @return ...
     */
    public fun TensorStructure<T>.lu(): Pair<TensorStructure<T>, TensorStructure<Int>>

    /**
     * TODO('Andrew')
     * For more information: https://pytorch.org/docs/stable/generated/torch.lu_unpack.html
     *
     * @param luTensor ...
     * @param pivotsTensor ...
     * @return ...
     */
    public fun luPivot(luTensor: TensorStructure<T>, pivotsTensor: TensorStructure<Int>):
            Triple<TensorStructure<T>, TensorStructure<T>, TensorStructure<T>>

    /**
     * Singular Value Decomposition.
     *
     * Computes the singular value decomposition of either a matrix or batch of matrices `input`.
     * The singular value decomposition is represented as a namedtuple `(U, S, V)`,
     * such that ``input = U.dot(diagonalEmbedding(S).dot(V.T))``.
     * If input is a batch of tensors, then U, S, and Vh are also batched with the same batch dimensions as input.
     * For more information: https://pytorch.org/docs/stable/linalg.html#torch.linalg.svd
     *
     * @return the determinant.
     */
    public fun TensorStructure<T>.svd(): Triple<TensorStructure<T>, TensorStructure<T>, TensorStructure<T>>

    /**
     * Returns eigenvalues and eigenvectors of a real symmetric matrix input or a batch of real symmetric matrices,
     * represented by a namedtuple (eigenvalues, eigenvectors).
     * For more information: https://pytorch.org/docs/stable/generated/torch.symeig.html
     *
     * @return a namedtuple (eigenvalues, eigenvectors)
     */
    public fun TensorStructure<T>.symEig(): Pair<TensorStructure<T>, TensorStructure<T>>

}