package kscience.kmath.real

import kscience.kmath.linear.MatrixContext
import kscience.kmath.linear.RealMatrixContext.elementContext
import kscience.kmath.linear.VirtualMatrix
import kscience.kmath.operations.invoke
import kscience.kmath.operations.sum
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.Matrix
import kscience.kmath.structures.RealBuffer
import kscience.kmath.structures.asIterable
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

public typealias RealMatrix = Matrix<Double>

public fun realMatrix(rowNum: Int, colNum: Int, initializer: (i: Int, j: Int) -> Double): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum, initializer)

public fun Array<DoubleArray>.toMatrix(): RealMatrix {
    return MatrixContext.real.produce(size, this[0].size) { row, col -> this[row][col] }
}

public fun Sequence<DoubleArray>.toMatrix(): RealMatrix = toList().let {
    MatrixContext.real.produce(it.size, it[0].size) { row, col -> it[row][col] }
}

public fun Matrix<Double>.repeatStackVertical(n: Int): RealMatrix =
    VirtualMatrix(rowNum * n, colNum) { row, col ->
        get(if (row == 0) 0 else row % rowNum, col)
    }

/*
 *  Operations for matrix and real number
 */

public operator fun Matrix<Double>.times(double: Double): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum) { row, col ->
        this[row, col] * double
    }

public operator fun Matrix<Double>.plus(double: Double): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum) { row, col ->
        this[row, col] + double
    }

public operator fun Matrix<Double>.minus(double: Double): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum) { row, col ->
        this[row, col] - double
    }

public operator fun Matrix<Double>.div(double: Double): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum) { row, col ->
        this[row, col] / double
    }

public operator fun Double.times(matrix: Matrix<Double>): RealMatrix =
    MatrixContext.real.produce(matrix.rowNum, matrix.colNum) { row, col ->
        this * matrix[row, col]
    }

public operator fun Double.plus(matrix: Matrix<Double>): RealMatrix =
    MatrixContext.real.produce(matrix.rowNum, matrix.colNum) { row, col ->
        this + matrix[row, col]
    }

public operator fun Double.minus(matrix: Matrix<Double>): RealMatrix =
    MatrixContext.real.produce(matrix.rowNum, matrix.colNum) { row, col ->
        this - matrix[row, col]
    }

// TODO: does this operation make sense? Should it be 'this/matrix[row, col]'?
//operator fun Double.div(matrix: Matrix<Double>) = MatrixContext.real.produce(matrix.rowNum, matrix.colNum) {
//    row, col -> matrix[row, col] / this
//}

/*
 *  Per-element (!) square and power operations
 */

public fun Matrix<Double>.square(): RealMatrix = MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this[row, col].pow(2)
}

public fun Matrix<Double>.pow(n: Int): RealMatrix = MatrixContext.real.produce(rowNum, colNum) { i, j ->
    this[i, j].pow(n)
}

/*
 * Operations on two matrices (per-element!)
 */

public operator fun Matrix<Double>.times(other: Matrix<Double>): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum) { row, col -> this[row, col] * other[row, col] }

public operator fun Matrix<Double>.plus(other: Matrix<Double>): RealMatrix =
    MatrixContext.real.add(this, other)

public operator fun Matrix<Double>.minus(other: Matrix<Double>): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum) { row, col -> this[row, col] - other[row, col] }

/*
 *  Operations on columns
 */

public inline fun Matrix<Double>.appendColumn(crossinline mapper: (Buffer<Double>) -> Double): Matrix<Double> =
    MatrixContext.real.produce(rowNum, colNum + 1) { row, col ->
        if (col < colNum)
            this[row, col]
        else
            mapper(rows[row])
    }

public fun Matrix<Double>.extractColumns(columnRange: IntRange): RealMatrix =
    MatrixContext.real.produce(rowNum, columnRange.count()) { row, col ->
        this[row, columnRange.first + col]
    }

public fun Matrix<Double>.extractColumn(columnIndex: Int): RealMatrix =
    extractColumns(columnIndex..columnIndex)

public fun Matrix<Double>.sumByColumn(): RealBuffer = RealBuffer(colNum) { j ->
    val column = columns[j]
    elementContext { sum(column.asIterable()) }
}

public fun Matrix<Double>.minByColumn(): RealBuffer = RealBuffer(colNum) { j ->
    columns[j].asIterable().minOrNull() ?: error("Cannot produce min on empty column")
}

public fun Matrix<Double>.maxByColumn(): RealBuffer = RealBuffer(colNum) { j ->
    columns[j].asIterable().maxOrNull() ?: error("Cannot produce min on empty column")
}

public fun Matrix<Double>.averageByColumn(): RealBuffer = RealBuffer(colNum) { j ->
    columns[j].asIterable().average()
}

/*
 * Operations processing all elements
 */

public fun Matrix<Double>.sum(): Double = elements().map { (_, value) -> value }.sum()
public fun Matrix<Double>.min(): Double? = elements().map { (_, value) -> value }.minOrNull()
public fun Matrix<Double>.max(): Double? = elements().map { (_, value) -> value }.maxOrNull()
public fun Matrix<Double>.average(): Double = elements().map { (_, value) -> value }.average()
