package space.kscience.kmath.linear

import space.kscience.kmath.nd.as1D
import space.kscience.kmath.structures.Buffer

public typealias Point<T> = Buffer<T>

/**
 * A group of methods to resolve equation A dot X = B, where A and B are matrices or vectors
 */
public interface LinearSolver<T : Any> {
    public fun solve(a: Matrix<T>, b: Matrix<T>): Matrix<T>
    public fun solve(a: Matrix<T>, b: Point<T>): Point<T> = solve(a, b.asMatrix()).asVector()
    public fun inverse(a: Matrix<T>): Matrix<T>
}

/**
 * Convert matrix to vector if it is possible
 */
public fun <T : Any> Matrix<T>.asVector(): Vector<T> =
    if (this.colNum == 1)
        as1D()
    else
        error("Can't convert matrix with more than one column to vector")

public fun <T : Any> Point<T>.asMatrix(): VirtualMatrix<T> = VirtualMatrix(size, 1) { i, _ -> get(i) }
