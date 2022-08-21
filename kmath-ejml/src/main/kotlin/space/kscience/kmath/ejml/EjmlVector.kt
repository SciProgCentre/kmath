/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ejml

import org.ejml.data.Matrix
import space.kscience.kmath.linear.Point

/**
 * [Point] implementation based on EJML [Matrix].
 *
 * @param T the type of elements contained in the buffer.
 * @param M the type of EJML matrix.
 * @property origin The underlying matrix, must have only one row.
 * @author Iaroslav Postovalov
 */
public abstract class EjmlVector<out T, out M : Matrix>(public open val origin: M) : Point<T> {
    override val size: Int
        get() = origin.numCols

    override operator fun iterator(): Iterator<T> = object : Iterator<T> {
        private var cursor: Int = 0

        override fun next(): T {
            cursor += 1
            return this@EjmlVector[cursor - 1]
        }

        override fun hasNext(): Boolean = cursor < origin.numCols * origin.numRows
    }

    override fun toString(): String = "EjmlVector(origin=$origin)"
}
