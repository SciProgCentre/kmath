/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ejml

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.linear.*
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Ring

/**
 * [LinearSpace] implementation specialized for a certain EJML type.
 *
 * @param T the type of items in the matrices.
 * @param A the element context type.
 * @param M the EJML matrix type.
 * @author Iaroslav Postovalov
 */
public abstract class EjmlLinearSpace<T : Any, out A : Ring<T>, out M : org.ejml.data.Matrix> : LinearSpace<T, A> {
    /**
     * Converts this matrix to EJML one.
     */
    public abstract fun Matrix<T>.toEjml(): EjmlMatrix<T, M>

    /**
     * Converts this vector to EJML one.
     */
    public abstract fun Point<T>.toEjml(): EjmlVector<T, M>

    public abstract override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: A.(i: Int, j: Int) -> T,
    ): EjmlMatrix<T, M>

    public abstract override fun buildVector(size: Int, initializer: A.(Int) -> T): EjmlVector<T, M>

    @UnstableKMathAPI
    public fun EjmlMatrix<T, *>.inverted(): Matrix<Double> =
        attributeForOrNull(this, Float64Field.linearSpace.Inverted)
}
