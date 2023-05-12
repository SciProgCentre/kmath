/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(PerformancePitfall::class)
@file:Suppress("unused")

package space.kscience.kmath.real

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.linear.*
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.asIterable
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.DoubleBuffer
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

public fun realMatrix(rowNum: Int, colNum: Int, initializer: DoubleField.(i: Int, j: Int) -> Double): RealMatrix =
    Double.algebra.linearSpace.buildMatrix(rowNum, colNum, initializer)

@OptIn(UnstableKMathAPI::class)
public fun realMatrix(rowNum: Int, colNum: Int): MatrixBuilder<Double, DoubleField> =
    Double.algebra.linearSpace.matrix(rowNum, colNum)

public fun Array<DoubleArray>.toMatrix(): RealMatrix {
    return Double.algebra.linearSpace.buildMatrix(size, this[0].size) { row, col -> this@toMatrix[row][col] }
}

public fun Sequence<DoubleArray>.toMatrix(): RealMatrix = toList().let {
    Double.algebra.linearSpace.buildMatrix(it.size, it[0].size) { row, col -> it[row][col] }
}

public fun RealMatrix.repeatStackVertical(n: Int): RealMatrix =
    VirtualMatrix(rowNum * n, colNum) { row, col ->
        get(if (row == 0) 0 else row % rowNum, col)
    }

/*
 *  Operations for matrix and real number
 */

public operator fun RealMatrix.times(double: Double): RealMatrix =
    Double.algebra.linearSpace.buildMatrix(rowNum, colNum) { row, col ->
        get(row, col) * double
    }

public operator fun RealMatrix.plus(double: Double): RealMatrix =
    Double.algebra.linearSpace.buildMatrix(rowNum, colNum) { row, col ->
        get(row, col) + double
    }

public operator fun RealMatrix.minus(double: Double): RealMatrix =
    Double.algebra.linearSpace.buildMatrix(rowNum, colNum) { row, col ->
        get(row, col) - double
    }

public operator fun RealMatrix.div(double: Double): RealMatrix =
    Double.algebra.linearSpace.buildMatrix(rowNum, colNum) { row, col ->
        get(row, col) / double
    }

public operator fun Double.times(matrix: RealMatrix): RealMatrix =
    Double.algebra.linearSpace.buildMatrix(matrix.rowNum, matrix.colNum) { row, col ->
        this@times * matrix[row, col]
    }

public operator fun Double.plus(matrix: RealMatrix): RealMatrix =
    Double.algebra.linearSpace.buildMatrix(matrix.rowNum, matrix.colNum) { row, col ->
        this@plus + matrix[row, col]
    }

public operator fun Double.minus(matrix: RealMatrix): RealMatrix =
    Double.algebra.linearSpace.buildMatrix(matrix.rowNum, matrix.colNum) { row, col ->
        this@minus - matrix[row, col]
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
    Double.algebra.linearSpace.buildMatrix(rowNum, colNum) { row, col -> this@times[row, col] * other[row, col] }

public operator fun RealMatrix.plus(other: RealMatrix): RealMatrix =
    Double.algebra.linearSpace.run { this@plus + other }

public operator fun RealMatrix.minus(other: RealMatrix): RealMatrix =
    Double.algebra.linearSpace.buildMatrix(rowNum, colNum) { row, col -> this@minus[row, col] - other[row, col] }

/*
 *  Operations on columns
 */

public inline fun RealMatrix.appendColumn(crossinline mapper: (Buffer<Double>) -> Double): RealMatrix =
    Double.algebra.linearSpace.buildMatrix(rowNum, colNum + 1) { row, col ->
        if (col < colNum)
            get(row, col)
        else
            mapper(rows[row])
    }

public fun RealMatrix.extractColumns(columnRange: IntRange): RealMatrix =
    Double.algebra.linearSpace.buildMatrix(rowNum, columnRange.count()) { row, col ->
        this@extractColumns[row, columnRange.first + col]
    }

public fun RealMatrix.extractColumn(columnIndex: Int): RealMatrix =
    extractColumns(columnIndex..columnIndex)

public fun RealMatrix.sumByColumn(): DoubleBuffer = DoubleBuffer(colNum) { j ->
    columns[j].sum()
}

public fun RealMatrix.minByColumn(): DoubleBuffer = DoubleBuffer(colNum) { j ->
    columns[j].asIterable().minOrNull() ?: error("Cannot produce min on empty column")
}

public fun RealMatrix.maxByColumn(): DoubleBuffer = DoubleBuffer(colNum) { j ->
    columns[j].asIterable().maxOrNull() ?: error("Cannot produce min on empty column")
}

public fun RealMatrix.averageByColumn(): DoubleBuffer = DoubleBuffer(colNum) { j ->
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
    Double.algebra.linearSpace.buildMatrix(rowNum, colNum) { i, j ->
        transform(get(i, j))
    }

/**
 * Inverse a square real matrix using LUP decomposition
 */
public fun RealMatrix.inverseWithLup(): RealMatrix = Double.algebra.linearSpace.lupSolver().inverse(this)

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
