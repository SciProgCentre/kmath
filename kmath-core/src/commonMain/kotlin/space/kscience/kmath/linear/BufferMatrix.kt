package space.kscience.kmath.linear

import space.kscience.kmath.nd.NDStructure
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.asSequence

/**
 * Alias for [Structure2D] with more familiar name.
 *
 * @param T the type of items.
 */
public typealias Matrix<T> = Structure2D<T>

/**
 * Basic implementation of Matrix space based on [NDStructure]
 */
public class BufferMatrixContext<T : Any, R : Ring<T>>(
    public override val elementContext: R,
    private val bufferFactory: BufferFactory<T>,
) : GenericMatrixContext<T, R, BufferMatrix<T>> {
    public override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> T): BufferMatrix<T> {
        val buffer = bufferFactory(rows * columns) { offset -> initializer(offset / columns, offset % columns) }
        return BufferMatrix(rows, columns, buffer)
    }

    public override fun point(size: Int, initializer: (Int) -> T): Point<T> = bufferFactory(size, initializer)

    private fun Matrix<T>.toBufferMatrix(): BufferMatrix<T> = if (this is BufferMatrix) this else {
        produce(rowNum, colNum) { i, j -> get(i, j) }
    }

    public fun one(rows: Int, columns: Int): Matrix<Double> = VirtualMatrix(rows, columns) { i, j ->
        if (i == j) 1.0 else 0.0
    } + DiagonalFeature

    public override infix fun Matrix<T>.dot(other: Matrix<T>): BufferMatrix<T> {
        require(colNum == other.rowNum) { "Matrix dot operation dimension mismatch: ($rowNum, $colNum) x (${other.rowNum}, ${other.colNum})" }
        val bufferMatrix = toBufferMatrix()
        val otherBufferMatrix = other.toBufferMatrix()
        return elementContext {
            produce(rowNum, other.colNum) { i, j ->
                var res = one
                for (l in 0 until colNum) {
                    res += bufferMatrix[i, l] * otherBufferMatrix[l, j]
                }
                res
            }
        }
    }

    public override infix fun Matrix<T>.dot(vector: Point<T>): Point<T> {
        require(colNum == vector.size) { "Matrix dot vector operation dimension mismatch: ($rowNum, $colNum) x (${vector.size})" }
        val bufferMatrix = toBufferMatrix()
        return elementContext {
            bufferFactory(rowNum) { i ->
                var res = one
                for (j in 0 until colNum) {
                    res += bufferMatrix[i, j] * vector[j]
                }
                res
            }
        }
    }

    override fun add(a: Matrix<T>, b: Matrix<T>): BufferMatrix<T> {
        require(a.rowNum == b.rowNum) { "Row number mismatch in matrix addition. Left side: ${a.rowNum}, right side: ${b.rowNum}" }
        require(a.colNum == b.colNum) { "Column number mismatch in matrix addition. Left side: ${a.colNum}, right side: ${b.colNum}" }
        val aBufferMatrix = a.toBufferMatrix()
        val bBufferMatrix = b.toBufferMatrix()
        return elementContext {
            produce(a.rowNum, a.colNum) { i, j ->
                aBufferMatrix[i, j] + bBufferMatrix[i, j]
            }
        }
    }

    override fun multiply(a: Matrix<T>, k: Number): BufferMatrix<T> {
        val aBufferMatrix = a.toBufferMatrix()
        return elementContext {
            produce(a.rowNum, a.colNum) { i, j -> aBufferMatrix[i, j] * k.toDouble() }
        }
    }

    public companion object
}

public class BufferMatrix<T : Any>(
    public override val rowNum: Int,
    public override val colNum: Int,
    public val buffer: Buffer<T>,
) : Matrix<T> {

    init {
        require(buffer.size == rowNum * colNum) { "Dimension mismatch for matrix structure" }
    }

    override val shape: IntArray get() = intArrayOf(rowNum, colNum)

    public override operator fun get(index: IntArray): T = get(index[0], index[1])
    public override operator fun get(i: Int, j: Int): T = buffer[i * colNum + j]

    public override fun elements(): Sequence<Pair<IntArray, T>> = sequence {
        for (i in 0 until rowNum) for (j in 0 until colNum) yield(intArrayOf(i, j) to get(i, j))
    }

    public override fun equals(other: Any?): Boolean {
        if (this === other) return true

        return when (other) {
            is NDStructure<*> -> NDStructure.contentEquals(this, other)
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = rowNum
        result = 31 * result + colNum
        result = 31 * result + buffer.hashCode()
        return result
    }

    public override fun toString(): String {
        return if (rowNum <= 5 && colNum <= 5)
            "Matrix(rowsNum = $rowNum, colNum = $colNum)\n" +
                    rows.asSequence().joinToString(prefix = "(", postfix = ")", separator = "\n ") { buffer ->
                        buffer.asSequence().joinToString(separator = "\t") { it.toString() }
                    }
        else "Matrix(rowsNum = $rowNum, colNum = $colNum)"
    }


}
