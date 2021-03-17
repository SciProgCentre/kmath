package space.kscience.kmath.linear

import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.VirtualBuffer
import space.kscience.kmath.structures.indices


public class BufferedLinearSpace<T : Any, A : Ring<T>>(
    override val elementAlgebra: A,
    private val bufferFactory: BufferFactory<T>,
) : LinearSpace<T, A> {

    private fun ndRing(
        rows: Int,
        cols: Int,
    ): BufferedRingND<T, A> = AlgebraND.ring(elementAlgebra, bufferFactory, rows, cols)

    override fun buildMatrix(rows: Int, columns: Int, initializer: A.(i: Int, j: Int) -> T): Matrix<T> =
        ndRing(rows, columns).produce { (i, j) -> elementAlgebra.initializer(i, j) }.as2D()

    override fun buildVector(size: Int, initializer: A.(Int) -> T): Point<T> =
        bufferFactory(size) { elementAlgebra.initializer(it) }

    override fun Matrix<T>.unaryMinus(): Matrix<T> = ndRing(rowNum, colNum).run {
        unwrap().map { -it }.as2D()
    }

    override fun Matrix<T>.plus(other: Matrix<T>): Matrix<T> = ndRing(rowNum, colNum).run {
        require(shape.contentEquals(other.shape)) { "Shape mismatch on Matrix::plus. Expected $shape but found ${other.shape}" }
        unwrap().plus(other.unwrap()).as2D()
    }

    override fun Matrix<T>.minus(other: Matrix<T>): Matrix<T> = ndRing(rowNum, colNum).run {
        require(shape.contentEquals(other.shape)) { "Shape mismatch on Matrix::minus. Expected $shape but found ${other.shape}" }
        unwrap().minus(other.unwrap()).as2D()
    }

    private fun Buffer<T>.linearize() = if (this is VirtualBuffer) {
        buildVector(size) { get(it) }
    } else {
        this
    }

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

    override fun Matrix<T>.times(value: T): Matrix<T> = ndRing(rowNum, colNum).run {
        unwrap().map { it * value }.as2D()
    }
}