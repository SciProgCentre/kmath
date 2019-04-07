package scientifik.kmath.linear

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Norm
import scientifik.kmath.operations.RealField
import scientifik.kmath.structures.Matrix
import scientifik.kmath.structures.VirtualBuffer
import scientifik.kmath.structures.asSequence


/**
 * A group of methods to resolve equation A dot X = B, where A and B are matrices or vectors
 */
interface LinearSolver<T : Any> {
    fun solve(a: Matrix<T>, b: Matrix<T>): Matrix<T>
    fun solve(a: Matrix<T>, b: Point<T>): Point<T> = solve(a, b.toMatrix()).asPoint()
    fun inverse(a: Matrix<T>): Matrix<T>
}

/**
 * Convert vector to array (copying content of array)
 */
fun <T : Any> Array<T>.toVector(field: Field<T>) = Vector.generic(size, field) { this[it] }

fun DoubleArray.toVector() = Vector.real(this.size) { this[it] }
fun List<Double>.toVector() = Vector.real(this.size) { this[it] }

object VectorL2Norm : Norm<Point<out Number>, Double> {
    override fun norm(arg: Point<out Number>): Double =
        kotlin.math.sqrt(arg.asSequence().sumByDouble { it.toDouble() })
}

typealias RealVector = Vector<Double, RealField>
typealias RealMatrix = Matrix<Double>

/**
 * Convert matrix to vector if it is possible
 */
fun <T : Any> Matrix<T>.asPoint(): Point<T> =
    if (this.colNum == 1) {
        VirtualBuffer(rowNum) { get(it, 0) }
    } else {
        error("Can't convert matrix with more than one column to vector")
    }

fun <T : Any> Point<T>.toMatrix() = VirtualMatrix(size, 1) { i, _ -> get(i) }