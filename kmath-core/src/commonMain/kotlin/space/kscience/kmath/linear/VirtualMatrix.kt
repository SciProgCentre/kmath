/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.linear

/**
 * The matrix where each element is evaluated each time when is being accessed.
 *
 * @property generator the function that provides elements.
 */
public class VirtualMatrix<out T : Any>(
    override val rowNum: Int,
    override val colNum: Int,
    public val generator: (i: Int, j: Int) -> T,
) : Matrix<T> {

    override val shape: IntArray get() = intArrayOf(rowNum, colNum)

    override operator fun get(i: Int, j: Int): T = generator(i, j)
}
