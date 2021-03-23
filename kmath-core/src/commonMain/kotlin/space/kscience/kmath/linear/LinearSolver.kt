package space.kscience.kmath.linear

import space.kscience.kmath.nd.as1D

/**
 * A group of methods to solve for *X* in equation *X = A <sup>-1</sup> &middot; B*, where *A* and *B* are matrices or
 * vectors.
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

/**
 * Convert matrix to vector if it is possible.
 */
public fun <T : Any> Matrix<T>.asVector(): Point<T> =
    if (this.colNum == 1)
        as1D()
    else
        error("Can't convert matrix with more than one column to vector")

/**
 * Creates an n &times; 1 [VirtualMatrix], where n is the size of the given buffer.
 *
 * @param T the type of elements contained in the buffer.
 * @receiver a buffer.
 * @return the new matrix.
 */
public fun <T : Any> Point<T>.asMatrix(): VirtualMatrix<T> = VirtualMatrix(size, 1) { i, _ -> get(i) }
