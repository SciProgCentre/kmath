/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

/**
 * A group of methods to solve for *X* in equation *X = A<sup>&minus;1</sup> &middot; B*, where *A* and *B* are
 * matrices or vectors.
 *
 * @param T the type of items.
 */
public interface LinearSolver<T : Any> {
    /**
     * Solve a dot x = b matrix equation and return x
     */
    public fun solve(a: Matrix<T>, b: Matrix<T>): Matrix<T>

    /**
     * Solve a dot x = b vector equation and return b
     */
    public fun solve(a: Matrix<T>, b: Point<T>): Point<T> = solve(a, b.asMatrix()).asVector()

    /**
     * Get inverse of a matrix
     */
    public fun inverse(matrix: Matrix<T>): Matrix<T>
}

