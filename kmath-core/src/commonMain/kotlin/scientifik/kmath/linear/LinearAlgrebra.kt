package scientifik.kmath.linear

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Norm
import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Ring
import scientifik.kmath.structures.Buffer.Companion.boxing
import scientifik.kmath.structures.asSequence



/**
 * A group of methods to resolve equation A dot X = B, where A and B are matrices or vectors
 */
interface LinearSolver<T : Any, F : Field<T>> {
    fun solve(a: Matrix<T, F>, b: Matrix<T, F>): Matrix<T, F>
    fun solve(a: Matrix<T, F>, b: Vector<T, F>): Vector<T, F> = solve(a, b.toMatrix()).toVector()
    fun inverse(a: Matrix<T, F>): Matrix<T, F> = solve(a, a.context.one)
}

/**
 * Convert vector to array (copying content of array)
 */
fun <T : Any> Array<T>.toVector(field: Field<T>) = Vector.generic(size, field) { this[it] }

fun DoubleArray.toVector() = Vector.real(this.size) { this[it] }
fun List<Double>.toVector() = Vector.real(this.size) { this[it] }

/**
 * Convert matrix to vector if it is possible
 */
fun <T : Any, F : Ring<T>> Matrix<T, F>.toVector(): Vector<T, F> {
    return if (this.numCols == 1) {
//            if (this is ArrayMatrix) {
//                //Reuse existing underlying array
//                ArrayVector(ArrayVectorSpace(rows, context.field, context.ndFactory), array)
//            } else {
//                //Generic vector
//                vector(rows, context.field) { get(it, 0) }
//            }
        Vector.generic(numRows, context.ring) { get(it, 0) }
    } else error("Can't convert matrix with more than one column to vector")
}

fun <T : Any, R : Ring<T>> Vector<T, R>.toMatrix(): Matrix<T, R> {
//    val context = StructureMatrixContext(size, 1, context.space)
//
//    return if (this is ArrayVector) {
//        //Reuse existing underlying array
//        StructureMatrix(context,this.buffer)
//    } else {
//        //Generic vector
//        matrix(size, 1, context.field) { i, j -> get(i) }
//    }
    //return Matrix.of(size, 1, context.space) { i, _ -> get(i) }
    return StructureMatrixSpace(size, 1, context.space, ::boxing).produce { i, _ -> get(i) }
}

object VectorL2Norm : Norm<Vector<out Number, *>, Double> {
    override fun norm(arg: Vector<out Number, *>): Double {
        return kotlin.math.sqrt(arg.asSequence().sumByDouble { it.toDouble() })
    }
}

typealias RealVector = Vector<Double, RealField>
typealias RealMatrix = Matrix<Double, RealField>
