package scientifik.kmath.real

import scientifik.kmath.linear.MatrixContext
import scientifik.kmath.linear.RealMatrixContext.elementContext
import scientifik.kmath.linear.VirtualMatrix
import scientifik.kmath.operations.sum
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.Matrix
import scientifik.kmath.structures.asSequence
import kotlin.math.pow

/*
 *  Functions for convenient "numpy-like" operations with Double matrices.
 *
 *  Initial implementation of these functions is taken from:
 *    https://github.com/thomasnield/numky/blob/master/src/main/kotlin/org/nield/numky/linear/DoubleOperators.kt
 *
 */

/*
 *  Functions that help create a real (Double) matrix
 */

fun realMatrix(rowNum: Int, colNum: Int, initializer: (i: Int, j: Int) -> Double) =
        MatrixContext.real.produce(rowNum, colNum, initializer)

fun Sequence<DoubleArray>.toMatrix() = toList().let {
    MatrixContext.real.produce(it.size, it[0].size) { row, col -> it[row][col] }
}

fun Matrix<Double>.repeatStackVertical(n: Int) = VirtualMatrix(rowNum*n, colNum) {
    row, col -> get(if (row == 0) 0 else row % rowNum, col)
}

/*
 *  Operations for matrix and real number
 */

operator fun Matrix<Double>.times(double: Double) = MatrixContext.real.produce(rowNum, colNum) {
    row, col -> this[row, col] * double
}

operator fun Matrix<Double>.plus(double: Double) = MatrixContext.real.produce(rowNum, colNum) {
    row, col -> this[row, col] + double
}

operator fun Matrix<Double>.minus(double: Double) = MatrixContext.real.produce(rowNum, colNum) {
    row, col -> this[row, col] - double
}

operator fun Matrix<Double>.div(double: Double) = MatrixContext.real.produce(rowNum, colNum) {
    row, col -> this[row, col] / double
}

operator fun Double.times(matrix: Matrix<Double>) = MatrixContext.real.produce(matrix.rowNum, matrix.colNum) {
    row, col -> this * matrix[row, col]
}

operator fun Double.plus(matrix: Matrix<Double>) = MatrixContext.real.produce(matrix.rowNum, matrix.colNum) {
    row, col -> this + matrix[row, col]
}

operator fun Double.minus(matrix: Matrix<Double>) = MatrixContext.real.produce(matrix.rowNum, matrix.colNum) {
    row, col -> this - matrix[row, col]
}

// TODO: does this operation make sense? Should it be 'this/matrix[row, col]'?
//operator fun Double.div(matrix: Matrix<Double>) = MatrixContext.real.produce(matrix.rowNum, matrix.colNum) {
//    row, col -> matrix[row, col] / this
//}

/*
 *  Per-element (!) square and power operations
 */

fun Matrix<Double>.square() = MatrixContext.real.produce(rowNum, colNum) {
    row, col -> this[row, col].pow(2)
}

fun Matrix<Double>.pow(n: Int) = MatrixContext.real.produce(rowNum, colNum) {
    i, j -> this[i, j].pow(n)
}

/*
 * Operations on two matrices (per-element!)
 */

operator fun Matrix<Double>.times(other: Matrix<Double>) = MatrixContext.real.produce(rowNum, colNum) {
    row, col -> this[row, col] * other[row, col]
}

operator fun Matrix<Double>.plus(other: Matrix<Double>) = MatrixContext.real.add(this, other)

operator fun Matrix<Double>.minus(other: Matrix<Double>) = MatrixContext.real.produce(rowNum, colNum) {
    row, col -> this[row,col] - other[row,col]
}

/*
 *  Operations on columns
 */

inline fun Matrix<Double>.appendColumn(crossinline mapper: (Buffer<Double>) -> Double) =
    MatrixContext.real.produce(rowNum,colNum+1) {
        row, col ->
            if (col < colNum)
                this[row, col]
            else
                mapper(rows[row])
    }

fun Matrix<Double>.extractColumns(columnRange: IntRange) = MatrixContext.real.produce(rowNum, columnRange.count()) {
    row, col -> this[row, columnRange.first + col]
}

fun Matrix<Double>.extractColumn(columnIndex: Int) = extractColumns(columnIndex..columnIndex)

fun Matrix<Double>.sumByColumn() = MatrixContext.real.produce(1, colNum) { _, j ->
    val column = columns[j]
    with(elementContext) {
        sum(column.asSequence())
    }
}

fun Matrix<Double>.minByColumn() = MatrixContext.real.produce(1, colNum) {
    _, j -> columns[j].asSequence().min() ?: throw Exception("Cannot produce min on empty column")
}

fun Matrix<Double>.maxByColumn() = MatrixContext.real.produce(1, colNum) {
    _, j -> columns[j].asSequence().max() ?: throw Exception("Cannot produce min on empty column")
}

fun Matrix<Double>.averageByColumn() = MatrixContext.real.produce(1, colNum) {
    _, j -> columns[j].asSequence().average()
}

/*
 * Operations processing all elements
 */

fun Matrix<Double>.sum() = elements().map { (_, value) -> value }.sum()

fun Matrix<Double>.min() = elements().map { (_, value) -> value }.min()

fun Matrix<Double>.max() = elements().map { (_, value) -> value }.max()

fun Matrix<Double>.average() = elements().map { (_, value) -> value }.average()
