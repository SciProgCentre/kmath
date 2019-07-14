package scientifik.kmath.real

import scientifik.kmath.linear.MatrixContext
import scientifik.kmath.linear.RealMatrixContext.elementContext
import scientifik.kmath.linear.VirtualMatrix
import scientifik.kmath.operations.sum
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.Matrix
import scientifik.kmath.structures.asSequence
import kotlin.math.pow

// Initial implementation of these functions is taken from:
// https://github.com/thomasnield/numky/blob/master/src/main/kotlin/org/nield/numky/linear/DoubleOperators.kt

fun realMatrix(rowNum: Int, colNum: Int, initializer: (i: Int, j: Int) -> Double) = MatrixContext.real.produce(rowNum, colNum, initializer)

fun Sequence<DoubleArray>.toMatrix() = toList().let {
    MatrixContext.real.produce(it.size,it[0].size) { row, col -> it[row][col] }
}

operator fun Matrix<Double>.times(double: Double) = MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@times[row, col] * double
}

fun Matrix<Double>.square() =  MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@square[row,col].pow(2)
}

operator fun Matrix<Double>.plus(double: Double) = MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@plus[row,col] + double
}

operator fun Matrix<Double>.minus(double: Double) = MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@minus[row,col] - double
}

operator fun Matrix<Double>.div(double: Double) = MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@div[row,col] / double
}

operator fun Double.times(matrix: Matrix<Double>) = MatrixContext.real.produce(matrix.rowNum, matrix.colNum) { row, col ->
    matrix[row,col] * this
}

operator fun Double.plus(matrix: Matrix<Double>) = MatrixContext.real.produce(matrix.rowNum, matrix.colNum) { row, col ->
    matrix[row,col] + this
}

operator fun Double.minus(matrix: Matrix<Double>) = MatrixContext.real.produce(matrix.rowNum, matrix.colNum) { row, col ->
    matrix[row,col] - this
}

operator fun Double.div(matrix: Matrix<Double>) = MatrixContext.real.produce(matrix.rowNum, matrix.colNum) { row, col ->
    matrix[row,col] / this
}

operator fun Matrix<Double>.times(other: Matrix<Double>) = MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@times[row,col] * other[row,col]
}

operator fun Matrix<Double>.minus(other: Matrix<Double>) = MatrixContext.real.produce(rowNum, colNum) { row, col ->
    this@minus[row,col] - other[row,col]
}

operator fun Matrix<Double>.plus(other: Matrix<Double>) = MatrixContext.real.add(this,other)

fun Matrix<Double>.repeatStackVertical(n: Int) = VirtualMatrix(rowNum*n, colNum) { row, col ->
    get(if (row == 0) 0 else row % rowNum, col)
}

inline fun Matrix<Double>.appendColumn(crossinline  mapper: (Buffer<Double>) -> Double) =
        MatrixContext.real.produce(rowNum,colNum+1) { row,col ->
            if (col < colNum)
                this[row,col]
            else
                mapper(rows[row])
        }

fun Matrix<Double>.extractColumn(columnIndex: Int) = extractColumns(columnIndex..columnIndex)

fun Matrix<Double>.extractColumns(columnRange: IntRange) = MatrixContext.real.produce(rowNum, columnRange.count()) { row, col ->
    this@extractColumns[row, columnRange.start + col]
}

fun Matrix<Double>.sumByColumn() = MatrixContext.real.produce(1, colNum) { i, j ->
    val column = columns[j]
    with(elementContext) {
        sum(column.asSequence())
    }
}

fun Matrix<Double>.minByColumn() = MatrixContext.real.produce(1, colNum) { i, j ->
    val column = columns[j]
    column.asSequence().min()?:throw Exception("Cannot produce min on empty column")
}

fun Matrix<Double>.maxByColumn() = MatrixContext.real.produce(1, colNum) { i, j ->
    val column = columns[j]
    column.asSequence().max()?:throw Exception("Cannot produce min on empty column")
}

fun Matrix<Double>.averageByColumn() = MatrixContext.real.produce(1, colNum) { i, j ->
    val column = columns[j]
    column.asSequence().average()
}

fun Matrix<Double>.sum() = this.elements().map { (_,value) -> value  }.sum()

fun Matrix<Double>.min() = this.elements().map { (_,value) -> value  }.min()

fun Matrix<Double>.max() = this.elements().map { (_,value) -> value  }.max()

fun Matrix<Double>.average() = this.elements().map { (_,value) -> value  }.average()

fun Matrix<Double>.pow(n: Int) = MatrixContext.real.produce(rowNum, colNum) { i, j ->
    this@pow[i,j].pow(n)
}