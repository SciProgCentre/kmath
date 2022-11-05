/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.api

import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.Field

/**
 * Common linear algebra operations. Operates on [Tensor].
 *
 * @param T the type of items closed under division in the tensors.
 */
public interface LinearOpsTensorAlgebra<T, A : Field<T>> : TensorPartialDivisionAlgebra<T, A> {

    /**
     * Computes the determinant of a square matrix input, or of each square matrix in a batched input.
     * For more information: https://pytorch.org/docs/stable/linalg.html#torch.linalg.det
     *
     * @return the determinant.
     */
    public fun StructureND<T>.det(): StructureND<T>

    /**
     * Computes the multiplicative inverse matrix of a square matrix input, or of each square matrix in a batched input.
     * Given a square matrix `A`, return the matrix `AInv` satisfying
     * `A dot AInv == AInv dot A == eye(a.shape[0])`.
     * For more information: https://pytorch.org/docs/stable/linalg.html#torch.linalg.inv
     *
     * @return the multiplicative inverse of a matrix.
     */
    public fun StructureND<T>.inv(): StructureND<T>

    /**
     * Cholesky decomposition.
     *
     * Computes the Cholesky decomposition of a Hermitian (or symmetric for real-valued matrices)
     * positive-definite matrix or the Cholesky decompositions for a batch of such matrices.
     * Each decomposition has the form:
     * Given a tensor `input`, return the tensor `L` satisfying `input = L dot LH`,
     * where `L` is a lower-triangular matrix and `LH` is the conjugate transpose of `L`,
     * which is just a transpose for the case of real-valued input matrices.
     * For more information: https://pytorch.org/docs/stable/linalg.html#torch.linalg.cholesky
     *
     * @receiver the `input`.
     * @return the batch of `L` matrices.
     */
    public fun cholesky(structureND: StructureND<T>): StructureND<T>

    /**
     * QR decomposition.
     *
     * Computes the QR decomposition of a matrix or a batch of matrices, and returns a pair `Q to R` of tensors.
     * Given a tensor `input`, return tensors `Q to R` satisfying `input == Q dot R`,
     * with `Q` being an orthogonal matrix or batch of orthogonal matrices
     * and `R` being an upper triangular matrix or batch of upper triangular matrices.
     * For more information: https://pytorch.org/docs/stable/linalg.html#torch.linalg.qr
     *
     * @receiver the `input`.
     * @return pair of `Q` and `R` tensors.
     */
    public fun qr(structureND: StructureND<T>): Pair<StructureND<T>, StructureND<T>>

    /**
     * LUP decomposition
     *
     * Computes the LUP decomposition of a matrix or a batch of matrices.
     * Given a tensor `input`, return tensors (P, L, U) satisfying `P dot input = L dot  U`,
     * with `P` being a permutation matrix or batch of matrices,
     * `L` being a lower triangular matrix or batch of matrices,
     * `U` being an upper triangular matrix or batch of matrices.
     *
     * @receiver the `input`.
     * @return triple of P, L and U tensors
     */
    public fun lu(structureND: StructureND<T>): Triple<StructureND<T>, StructureND<T>, StructureND<T>>

    /**
     * Singular Value Decomposition.
     *
     * Computes the singular value decomposition of either a matrix or batch of matrices `input`.
     * The singular value decomposition is represented as a triple `Triple(U, S, V)`,
     * such that `input = U dot diagonalEmbedding(S) dot VH`,
     * where `VH` is the conjugate transpose of V.
     * If `input` is a batch of tensors, then `U`, `S`, and `VH` are also batched with the same batch dimensions as
     * `input`.
     * For more information: https://pytorch.org/docs/stable/linalg.html#torch.linalg.svd
     *
     * @receiver the `input`.
     * @return triple `Triple(U, S, V)`.
     */
    public fun svd(structureND: StructureND<T>): Triple<StructureND<T>, StructureND<T>, StructureND<T>>

    /**
     * Returns eigenvalues and eigenvectors of a real symmetric matrix `input` or a batch of real symmetric matrices,
     * represented by a pair `eigenvalues to eigenvectors`.
     * For more information: https://pytorch.org/docs/stable/generated/torch.symeig.html
     *
     * @receiver the `input`.
     * @return a pair `eigenvalues to eigenvectors`
     */
    public fun symEig(structureND: StructureND<T>): Pair<StructureND<T>, StructureND<T>>

}
