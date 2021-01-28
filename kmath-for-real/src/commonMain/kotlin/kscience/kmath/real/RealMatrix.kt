package kscience.kmath.real

import kscience.kmath.linear.*
import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.structures.Buffer
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

public fun RealMatrix.repeatStackVertical(n: Int): RealMatrix =
    VirtualMatrix(rowNum * n, colNum) { row, col ->
        get(if (row == 0) 0 else row % rowNum, col)
    }

/*
 *  Operations for matrix and real number
 */

public operator fun RealMatrix.times(double: Double): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum) { row, col ->
        this[row, col] * double
    }

public operator fun RealMatrix.plus(double: Double): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum) { row, col ->
        this[row, col] + double
    }

public operator fun RealMatrix.minus(double: Double): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum) { row, col ->
        this[row, col] - double
    }

public operator fun RealMatrix.div(double: Double): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum) { row, col ->
        this[row, col] / double
    }

public operator fun Double.times(matrix: RealMatrix): RealMatrix =
    MatrixContext.real.produce(matrix.rowNum, matrix.colNum) { row, col ->
        this * matrix[row, col]
    }

public operator fun Double.plus(matrix: RealMatrix): RealMatrix =
    MatrixContext.real.produce(matrix.rowNum, matrix.colNum) { row, col ->
        this + matrix[row, col]
    }

public operator fun Double.minus(matrix: RealMatrix): RealMatrix =
    MatrixContext.real.produce(matrix.rowNum, matrix.colNum) { row, col ->
        this - matrix[row, col]
    }

// TODO: does this operation make sense? Should it be 'this/matrix[row, col]'?
//operator fun Double.div(matrix: RealMatrix) = MatrixContext.real.produce(matrix.rowNum, matrix.colNum) {
//    row, col -> matrix[row, col] / this
//}

/*
 * Operations on two matrices (per-element!)
 */

@UnstableKMathAPI
public operator fun RealMatrix.times(other: RealMatrix): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum) { row, col -> this[row, col] * other[row, col] }

public operator fun RealMatrix.plus(other: RealMatrix): RealMatrix =
    MatrixContext.real.add(this, other)

public operator fun RealMatrix.minus(other: RealMatrix): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum) { row, col -> this[row, col] - other[row, col] }

/*
 *  Operations on columns
 */

public inline fun RealMatrix.appendColumn(crossinline mapper: (Buffer<Double>) -> Double): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum + 1) { row, col ->
        if (col < colNum)
            this[row, col]
        else
            mapper(rows[row])
    }

public fun RealMatrix.extractColumns(columnRange: IntRange): RealMatrix =
    MatrixContext.real.produce(rowNum, columnRange.count()) { row, col ->
        this[row, columnRange.first + col]
    }

public fun RealMatrix.extractColumn(columnIndex: Int): RealMatrix =
    extractColumns(columnIndex..columnIndex)

public fun RealMatrix.sumByColumn(): RealBuffer = RealBuffer(colNum) { j ->
    columns[j].asIterable().sum()
}

public fun RealMatrix.minByColumn(): RealBuffer = RealBuffer(colNum) { j ->
    columns[j].asIterable().minOrNull() ?: error("Cannot produce min on empty column")
}

public fun RealMatrix.maxByColumn(): RealBuffer = RealBuffer(colNum) { j ->
    columns[j].asIterable().maxOrNull() ?: error("Cannot produce min on empty column")
}

public fun RealMatrix.averageByColumn(): RealBuffer = RealBuffer(colNum) { j ->
    columns[j].asIterable().average()
}

/*
 * Operations processing all elements
 */

public fun RealMatrix.sum(): Double = elements().map { (_, value) -> value }.sum()
public fun RealMatrix.min(): Double? = elements().map { (_, value) -> value }.minOrNull()
public fun RealMatrix.max(): Double? = elements().map { (_, value) -> value }.maxOrNull()
public fun RealMatrix.average(): Double = elements().map { (_, value) -> value }.average()

public inline fun RealMatrix.map(crossinline transform: (Double) -> Double): RealMatrix =
    MatrixContext.real.produce(rowNum, colNum) { i, j ->
        transform(get(i, j))
    }

/**
 * Inverse a square real matrix using LUP decomposition
 */
public fun RealMatrix.inverseWithLup(): RealMatrix = MatrixContext.real.inverseWithLup(this)

//extended operations

public fun RealMatrix.pow(p: Double): RealMatrix = map { it.pow(p) }

public fun RealMatrix.pow(p: Int): RealMatrix = map { it.pow(p) }

public fun exp(arg: RealMatrix): RealMatrix = arg.map { kotlin.math.exp(it) }

public fun sqrt(arg: RealMatrix): RealMatrix = arg.map { kotlin.math.sqrt(it) }

public fun RealMatrix.square(): RealMatrix = map { it.pow(2) }

public fun sin(arg: RealMatrix): RealMatrix = arg.map { kotlin.math.sin(it) }

public fun cos(arg: RealMatrix): RealMatrix = arg.map { kotlin.math.cos(it) }

public fun tan(arg: RealMatrix): RealMatrix = arg.map { kotlin.math.tan(it) }

public fun ln(arg: RealMatrix): RealMatrix = arg.map { kotlin.math.ln(it) }

public fun log10(arg: RealMatrix): RealMatrix = arg.map { kotlin.math.log10(it) }