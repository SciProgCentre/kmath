package kscience.kmath.linear

import kscience.kmath.operations.Ring
import kscience.kmath.structures.*

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

    public companion object
}

public class BufferMatrix<T : Any>(
    public override val rowNum: Int,
    public override val colNum: Int,
    public val buffer: Buffer<out T>,
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
