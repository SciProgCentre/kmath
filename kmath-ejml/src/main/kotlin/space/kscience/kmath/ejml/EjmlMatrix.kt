/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ejml

import org.ejml.data.Matrix
import space.kscience.kmath.nd.Structure2D

/**
 * [space.kscience.kmath.linear.Matrix] implementation based on EJML [Matrix].
 *
 * @param T the type of elements contained in the buffer.
 * @param M the type of EJML matrix.
 * @property origin The underlying EJML matrix.
 * @author Iaroslav Postovalov
 */
public abstract class EjmlMatrix<out T, out M : Matrix>(public open val origin: M) : Structure2D<T> {
    override val rowNum: Int get() = origin.numRows
    override val colNum: Int get() = origin.numCols
}
