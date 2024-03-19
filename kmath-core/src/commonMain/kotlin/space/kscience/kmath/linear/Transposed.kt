/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.attributes.Attributes


public class TransposedMatrix<T>(public val origin: Matrix<T>) : Matrix<T> {

    override val rowNum: Int get() = origin.colNum

    override val colNum: Int get() = origin.rowNum

    override fun get(i: Int, j: Int): T = origin[j, i]

    override val attributes: Attributes get() = Attributes.EMPTY
}


/**
 * Create a virtual transposed matrix without copying anything. `A.transpose().transpose() === A`
 */
public fun <T> Matrix<T>.transposed(): Matrix<T> = (this as? TransposedMatrix<T>)?.origin ?: TransposedMatrix(this)