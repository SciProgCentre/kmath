/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.Floa64FieldOpsND
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.nd.asND
import space.kscience.kmath.operations.Float64BufferOps
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.structures.Float64Buffer
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.map

public object Float64LinearSpace : LinearSpace<Double, Float64Field> {

    override val elementAlgebra: Float64Field get() = Float64Field

    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: Float64Field.(i: Int, j: Int) -> Double,
    ): Matrix<Float64> = Floa64FieldOpsND.structureND(ShapeND(rows, columns)) { (i, j) ->
        Float64Field.initializer(i, j)
    }.as2D()

    override fun buildVector(size: Int, initializer: Float64Field.(Int) -> Double): Float64Buffer =
        Float64Buffer(size) { Float64Field.initializer(it) }

    override fun Matrix<Float64>.unaryMinus(): Matrix<Float64> = Floa64FieldOpsND {
        asND().map { -it }.as2D()
    }

    override fun Matrix<Float64>.plus(other: Matrix<Float64>): Matrix<Float64> = Floa64FieldOpsND {
        require(shape == other.shape) { "Shape mismatch on Matrix::plus. Expected $shape but found ${other.shape}" }
        asND().plus(other.asND()).as2D()
    }

    override fun Matrix<Float64>.minus(other: Matrix<Float64>): Matrix<Float64> = Floa64FieldOpsND {
        require(shape == other.shape) { "Shape mismatch on Matrix::minus. Expected $shape but found ${other.shape}" }
        asND().minus(other.asND()).as2D()
    }

    // Create a continuous in-memory representation of this vector for better memory layout handling
    private fun Buffer<Float64>.linearize() = if (this is Float64Buffer) {
        this.array
    } else {
        DoubleArray(size) { get(it) }
    }

    @OptIn(PerformancePitfall::class)
    override fun Matrix<Float64>.dot(other: Matrix<Float64>): Matrix<Float64> {
        require(colNum == other.rowNum) { "Matrix dot operation dimension mismatch: ($rowNum, $colNum) x (${other.rowNum}, ${other.colNum})" }
        val rows = this@dot.rows.map { it.linearize() }
        val columns = other.columns.map { it.linearize() }
        val indices = 0 until this.colNum
        return buildMatrix(rowNum, other.colNum) { i, j ->
            val r = rows[i]
            val c = columns[j]
            var res = 0.0
            for (l in indices) {
                res += r[l] * c[l]
            }
            res
        }
    }

    @OptIn(PerformancePitfall::class)
    override fun Matrix<Float64>.dot(vector: Point<Float64>): Float64Buffer {
        require(colNum == vector.size) { "Matrix dot vector operation dimension mismatch: ($rowNum, $colNum) x (${vector.size})" }
        val rows = this@dot.rows.map { it.linearize() }
        val indices = 0 until this.colNum
        return Float64Buffer(rowNum) { i ->
            val r = rows[i]
            var res = 0.0
            for (j in indices) {
                res += r[j] * vector[j]
            }
            res
        }

    }

    override fun Matrix<Float64>.times(value: Double): Matrix<Float64> = Floa64FieldOpsND {
        asND().map { it * value }.as2D()
    }

    public override fun Point<Float64>.plus(other: Point<Float64>): Float64Buffer = Float64BufferOps.run {
        this@plus + other
    }

    public override fun Point<Float64>.minus(other: Point<Float64>): Float64Buffer = Float64BufferOps.run {
        this@minus - other
    }

    public override fun Point<Float64>.times(value: Double): Float64Buffer = Float64BufferOps.run {
        scale(this@times, value)
    }

    public operator fun Point<Float64>.div(value: Double): Float64Buffer = Float64BufferOps.run {
        scale(this@div, 1.0 / value)
    }

    public override fun Double.times(v: Point<Float64>): Float64Buffer = v * this


}

public val Float64Field.linearSpace: Float64LinearSpace get() = Float64LinearSpace
