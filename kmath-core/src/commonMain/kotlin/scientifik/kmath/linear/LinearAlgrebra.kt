package scientifik.kmath.linear

import scientifik.kmath.operations.DoubleField
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Norm


/**
 * A group of methods to resolve equation A dot X = B, where A and B are matrices or vectors
 */
interface LinearSolver<T : Any, F : Field<T>> {
    fun solve(a: Matrix<T, F>, b: Matrix<T, F>): Matrix<T, F>
    fun solve(a: Matrix<T, F>, b: Vector<T, F>): Vector<T, F> = solve(a, b.toMatrix()).toVector()
    fun inverse(a: Matrix<T, F>): Matrix<T, F> = solve(a, Matrix.diagonal(a.rows, a.columns, a.context.field))
}

/**
 * Convert vector to array (copying content of array)
 */
fun <T : Any> Array<T>.toVector(field: Field<T>) = Vector.of(size, field) { this[it] }

fun DoubleArray.toVector() = Vector.ofReal(this.size) { this[it] }
fun List<Double>.toVector() = Vector.ofReal(this.size) { this[it] }

/**
 * Convert matrix to vector if it is possible
 */
fun <T : Any, F : Field<T>> Matrix<T, F>.toVector(): Vector<T, F> {
    return when {
        this.columns == 1 -> {
//            if (this is ArrayMatrix) {
//                //Reuse existing underlying array
//                ArrayVector(ArrayVectorSpace(rows, context.field, context.ndFactory), array)
//            } else {
//                //Generic vector
//                vector(rows, context.field) { get(it, 0) }
//            }
            Vector.of(rows, context.field) { get(it, 0) }
        }
        else -> error("Can't convert matrix with more than one column to vector")
    }
}

fun <T : Any, F : Field<T>> Vector<T, F>.toMatrix(): Matrix<T, F> {
//    return if (this is ArrayVector) {
//        //Reuse existing underlying array
//        ArrayMatrix(ArrayMatrixSpace(size, 1, context.field, context.ndFactory), array)
//    } else {
//        //Generic vector
//        matrix(size, 1, context.field) { i, j -> get(i) }
//    }
    return Matrix.of(size, 1, context.space) { i, _ -> get(i) }
}

object VectorL2Norm : Norm<Vector<out Number, *>, Double> {
    override fun norm(arg: Vector<out Number, *>): Double {
        return kotlin.math.sqrt(arg.sumByDouble { it.toDouble() })
    }
}

typealias RealVector = Vector<Double, DoubleField>
typealias RealMatrix = Matrix<Double, DoubleField>
