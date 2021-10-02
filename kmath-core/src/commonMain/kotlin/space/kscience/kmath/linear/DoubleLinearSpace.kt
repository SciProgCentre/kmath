/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.nd.DoubleFieldND
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.nd.asND
import space.kscience.kmath.operations.DoubleBufferOperations
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.indices

public object DoubleLinearSpace : LinearSpace<Double, DoubleField> {

    override val elementAlgebra: DoubleField get() = DoubleField

    private fun ndRing(
        rows: Int,
        cols: Int,
    ): DoubleFieldND = DoubleFieldND(intArrayOf(rows, cols))

    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: DoubleField.(i: Int, j: Int) -> Double
    ): Matrix<Double> = ndRing(rows, columns).produce { (i, j) -> DoubleField.initializer(i, j) }.as2D()


    override fun buildVector(size: Int, initializer: DoubleField.(Int) -> Double): DoubleBuffer =
        DoubleBuffer(size) { DoubleField.initializer(it) }

    override fun Matrix<Double>.unaryMinus(): Matrix<Double> = ndRing(rowNum, colNum).run {
        asND().map { -it }.as2D()
    }

    override fun Matrix<Double>.plus(other: Matrix<Double>): Matrix<Double> = ndRing(rowNum, colNum).run {
        require(shape.contentEquals(other.shape)) { "Shape mismatch on Matrix::plus. Expected $shape but found ${other.shape}" }
        asND().plus(other.asND()).as2D()
    }

    override fun Matrix<Double>.minus(other: Matrix<Double>): Matrix<Double> = ndRing(rowNum, colNum).run {
        require(shape.contentEquals(other.shape)) { "Shape mismatch on Matrix::minus. Expected $shape but found ${other.shape}" }
        asND().minus(other.asND()).as2D()
    }

    // Create a continuous in-memory representation of this vector for better memory layout handling
    private fun Buffer<Double>.linearize() = if (this is DoubleBuffer) {
        this
    } else {
        DoubleBuffer(size) { get(it) }
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

    override fun Matrix<Double>.times(value: Double): Matrix<Double> = ndRing(rowNum, colNum).run {
        asND().map { it * value }.as2D()
    }

    public override fun Point<Double>.plus(other: Point<Double>): DoubleBuffer = DoubleBufferOperations.run {
        this@plus + other
    }

    public override fun Point<Double>.minus(other: Point<Double>): DoubleBuffer = DoubleBufferOperations.run {
        this@minus - other
    }

    public override fun Point<Double>.times(value: Double): DoubleBuffer = DoubleBufferOperations.run {
        scale(this@times, value)
    }

    public operator fun Point<Double>.div(value: Double): DoubleBuffer = DoubleBufferOperations.run {
        scale(this@div, 1.0 / value)
    }

    public override fun Double.times(v: Point<Double>): DoubleBuffer = v * this


}

public val DoubleField.linearSpace: DoubleLinearSpace get() = DoubleLinearSpace
