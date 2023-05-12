/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.DoubleBufferOps
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.DoubleBuffer

public object DoubleLinearSpace : LinearSpace<Double, DoubleField> {

    override val elementAlgebra: DoubleField get() = DoubleField

    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: DoubleField.(i: Int, j: Int) -> Double
    ): Matrix<Double> = DoubleFieldOpsND.structureND(ShapeND(rows, columns)) { (i, j) ->
        DoubleField.initializer(i, j)
    }.as2D()

    override fun buildVector(size: Int, initializer: DoubleField.(Int) -> Double): DoubleBuffer =
        DoubleBuffer(size) { DoubleField.initializer(it) }

    override fun Matrix<Double>.unaryMinus(): Matrix<Double> = DoubleFieldOpsND {
        asND().map { -it }.as2D()
    }

    override fun Matrix<Double>.plus(other: Matrix<Double>): Matrix<Double> = DoubleFieldOpsND {
        require(shape.contentEquals(other.shape)) { "Shape mismatch on Matrix::plus. Expected $shape but found ${other.shape}" }
        asND().plus(other.asND()).as2D()
    }

    override fun Matrix<Double>.minus(other: Matrix<Double>): Matrix<Double> = DoubleFieldOpsND {
        require(shape.contentEquals(other.shape)) { "Shape mismatch on Matrix::minus. Expected $shape but found ${other.shape}" }
        asND().minus(other.asND()).as2D()
    }

    // Create a continuous in-memory representation of this vector for better memory layout handling
    private fun Buffer<Double>.linearize() = if (this is DoubleBuffer) {
        this.array
    } else {
        DoubleArray(size) { get(it) }
    }

    @OptIn(PerformancePitfall::class)
    override fun Matrix<Double>.dot(other: Matrix<Double>): Matrix<Double> {
        require(colNum == other.rowNum) { "Matrix dot operation dimension mismatch: ($rowNum, $colNum) x (${other.rowNum}, ${other.colNum})" }
        val rows = this@dot.rows.map { it.linearize() }
        val columns = other.columns.map { it.linearize() }
        return buildMatrix(rowNum, other.colNum) { i, j ->
            val r = rows[i]
            val c = columns[j]
            var res = 0.0
            for (l in r.indices) {
                res += r[l] * c[l]
            }
            res
        }
    }

    @OptIn(PerformancePitfall::class)
    override fun Matrix<Double>.dot(vector: Point<Double>): DoubleBuffer {
        require(colNum == vector.size) { "Matrix dot vector operation dimension mismatch: ($rowNum, $colNum) x (${vector.size})" }
        val rows = this@dot.rows.map { it.linearize() }
        return DoubleBuffer(rowNum) { i ->
            val r = rows[i]
            var res = 0.0
            for (j in r.indices) {
                res += r[j] * vector[j]
            }
            res
        }

    }

    override fun Matrix<Double>.times(value: Double): Matrix<Double> = DoubleFieldOpsND {
        asND().map { it * value }.as2D()
    }

    public override fun Point<Double>.plus(other: Point<Double>): DoubleBuffer = DoubleBufferOps.run {
        this@plus + other
    }

    public override fun Point<Double>.minus(other: Point<Double>): DoubleBuffer = DoubleBufferOps.run {
        this@minus - other
    }

    public override fun Point<Double>.times(value: Double): DoubleBuffer = DoubleBufferOps.run {
        scale(this@times, value)
    }

    public operator fun Point<Double>.div(value: Double): DoubleBuffer = DoubleBufferOps.run {
        scale(this@div, 1.0 / value)
    }

    public override fun Double.times(v: Point<Double>): DoubleBuffer = v * this


}

public val DoubleField.linearSpace: DoubleLinearSpace get() = DoubleLinearSpace
