/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.attributes.Attributes


/**
 * The matrix where each element is evaluated each time when is being accessed.
 *
 * @property generator the function that provides elements.
 */
public class VirtualMatrix<out T>(
    override val rowNum: Int,
    override val colNum: Int,
    override val attributes: Attributes = Attributes.EMPTY,
    public val generator: (i: Int, j: Int) -> T,
) : Matrix<T> {
    override operator fun get(i: Int, j: Int): T = generator(i, j)
}