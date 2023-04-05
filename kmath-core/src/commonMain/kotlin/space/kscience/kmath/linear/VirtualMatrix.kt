/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.nd.ShapeND


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

    override val shape: ShapeND get() = ShapeND(rowNum, colNum)

    override operator fun get(i: Int, j: Int): T = generator(i, j)
}

public fun <T : Any> MatrixBuilder<T, *>.virtual(generator: (i: Int, j: Int) -> T): VirtualMatrix<T> =
    VirtualMatrix(rows, columns, generator)
