package scientifik.kmath.linear

import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.Matrix
import scientifik.kmath.structures.VirtualBuffer

typealias Point<T> = Buffer<T>

/**
 * A group of methods to resolve equation A dot X = B, where A and B are matrices or vectors
 */
interface LinearSolver<T : Any> {
    fun solve(a: Matrix<T>, b: Matrix<T>): Matrix<T>
    fun solve(a: Matrix<T>, b: Point<T>): Point<T> = solve(a, b.asMatrix()).asPoint()
    fun inverse(a: Matrix<T>): Matrix<T>
}

/**
 * Convert matrix to vector if it is possible
 */
fun <T : Any> Matrix<T>.asPoint(): Point<T> =
    if (this.colNum == 1) {
        VirtualBuffer(rowNum) { get(it, 0) }
    } else {
        error("Can't convert matrix with more than one column to vector")
    }

fun <T : Any> Point<T>.asMatrix(): VirtualMatrix<T> = VirtualMatrix(size, 1) { i, _ -> get(i) }
