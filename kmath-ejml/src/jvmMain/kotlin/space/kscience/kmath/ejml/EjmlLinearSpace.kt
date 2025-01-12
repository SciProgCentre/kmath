/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ejml

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.linear.Inverted
import space.kscience.kmath.linear.LinearSpace
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.linear.Point
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.Float64

/**
 * [LinearSpace] implementation specialized for a certain EJML type.
 *
 * @param T the type of items in the matrices.
 * @param A the element context type.
 * @param M the EJML matrix type.
 * @author Iaroslav Postovalov
 */
public interface EjmlLinearSpace<T : Any, out A : Ring<T>, M : org.ejml.data.Matrix> : LinearSpace<T, A> {
    /**
     * Converts this matrix to EJML one.
     */
    public fun Matrix<T>.toEjml(): M

    /**
     * Converts this vector to EJML one.
     */
    public fun Point<T>.toEjml(): M

    public abstract override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: A.(i: Int, j: Int) -> T,
    ): EjmlMatrix<T, M>

    public abstract override fun buildVector(size: Int, initializer: A.(Int) -> T): EjmlVector<T, M>

    @UnstableKMathAPI
    public fun Structure2D<T>.inverted(): Matrix<Float64> =
        computeAttribute(this, Inverted()) ?: error("Can't invert matrix")
}
