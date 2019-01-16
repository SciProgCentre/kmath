package scientifik.kmath.linear

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Norm
import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Ring
import scientifik.kmath.structures.asSequence


/**
 * A group of methods to resolve equation A dot X = B, where A and B are matrices or vectors
 */
interface LinearSolver<T : Any, R : Ring<T>> : MatrixContext<T, R> {
    /**
     * Convert matrix to vector if it is possible
     */
    fun Matrix<T>.toVector(): Point<T> =
        if (this.colNum == 1) {
            point(rowNum){ get(it, 0) }
        } else error("Can't convert matrix with more than one column to vector")

    fun Point<T>.toMatrix(): Matrix<T> = produce(size, 1) { i, _ -> get(i) }

    fun solve(a: Matrix<T>, b: Matrix<T>): Matrix<T>
    fun solve(a: Matrix<T>, b: Point<T>): Point<T> = solve(a, b.toMatrix()).toVector()
    fun inverse(a: Matrix<T>): Matrix<T> = solve(a, one(a.rowNum, a.colNum))
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
