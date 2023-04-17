/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.VirtualBuffer
import space.kscience.kmath.structures.indices


public class BufferedLinearSpace<T, out A : Ring<T>>(
    private val bufferAlgebra: BufferAlgebra<T, A>,
) : LinearSpace<T, A> {
    override val elementAlgebra: A get() = bufferAlgebra.elementAlgebra

    private val ndAlgebra = BufferedRingOpsND(bufferAlgebra)

    override fun buildMatrix(rows: Int, columns: Int, initializer: A.(i: Int, j: Int) -> T): Matrix<T> =
        ndAlgebra.structureND(ShapeND(rows, columns)) { (i, j) -> elementAlgebra.initializer(i, j) }.as2D()

    override fun buildVector(size: Int, initializer: A.(Int) -> T): Point<T> =
        bufferAlgebra.buffer(size) { elementAlgebra.initializer(it) }

    @OptIn(PerformancePitfall::class)
    override fun Matrix<T>.unaryMinus(): Matrix<T> = ndAlgebra {
        asND().map { -it }.as2D()
    }

    override fun Matrix<T>.plus(other: Matrix<T>): Matrix<T> = ndAlgebra {
        require(shape.contentEquals(other.shape)) { "Shape mismatch on Matrix::plus. Expected $shape but found ${other.shape}" }
        asND().plus(other.asND()).as2D()
    }

    override fun Matrix<T>.minus(other: Matrix<T>): Matrix<T> = ndAlgebra {
        require(shape.contentEquals(other.shape)) { "Shape mismatch on Matrix::minus. Expected $shape but found ${other.shape}" }
        asND().minus(other.asND()).as2D()
    }

    private fun Buffer<T>.linearize() = if (this is VirtualBuffer) {
        buildVector(size) { get(it) }
    } else {
        this
    }

    @OptIn(PerformancePitfall::class)
    override fun Matrix<T>.dot(other: Matrix<T>): Matrix<T> {
        require(colNum == other.rowNum) { "Matrix dot operation dimension mismatch: ($rowNum, $colNum) x (${other.rowNum}, ${other.colNum})" }
        return elementAlgebra {
            val rows = this@dot.rows.map { it.linearize() }
            val columns = other.columns.map { it.linearize() }
            buildMatrix(rowNum, other.colNum) { i, j ->
                val r = rows[i]
                val c = columns[j]
                var res = zero
                for (l in r.indices) {
                    res += r[l] * c[l]
                }
                res
            }
        }
    }

    @OptIn(PerformancePitfall::class)
    override fun Matrix<T>.dot(vector: Point<T>): Point<T> {
        require(colNum == vector.size) { "Matrix dot vector operation dimension mismatch: ($rowNum, $colNum) x (${vector.size})" }
        return elementAlgebra {
            val rows = this@dot.rows.map { it.linearize() }
            buildVector(rowNum) { i ->
                val r = rows[i]
                var res = zero
                for (j in r.indices) {
                    res += r[j] * vector[j]
                }
                res
            }
        }
    }

    @OptIn(PerformancePitfall::class)
    override fun Matrix<T>.times(value: T): Matrix<T> = ndAlgebra {
        asND().map { it * value }.as2D()
    }
}


public val <T, A : Ring<T>> A.linearSpace: BufferedLinearSpace<T, A>
    get() = BufferedLinearSpace(BufferRingOps(this))
