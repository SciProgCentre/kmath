package scientifik.kmath.linear

import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Ring
import scientifik.kmath.structures.*

/**
 * Basic implementation of Matrix space based on [NDStructure]
 */
class BufferMatrixContext<T : Any, R : Ring<T>>(
    override val elementContext: R,
    private val bufferFactory: BufferFactory<T>
) : GenericMatrixContext<T, R> {

    override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> T): BufferMatrix<T> {
        val buffer = bufferFactory(rows * columns) { offset -> initializer(offset / columns, offset % columns) }
        return BufferMatrix(rows, columns, buffer)
    }

    override fun point(size: Int, initializer: (Int) -> T): Point<T> = bufferFactory(size, initializer)

    companion object {

    }
}

@Suppress("OVERRIDE_BY_INLINE")
object RealMatrixContext : GenericMatrixContext<Double, RealField> {

    override val elementContext get() = RealField

    override inline fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> Double): Matrix<Double> {
        val buffer = RealBuffer(rows * columns) { offset -> initializer(offset / columns, offset % columns) }
        return BufferMatrix(rows, columns, buffer)
    }

    override inline fun point(size: Int, initializer: (Int) -> Double): Point<Double> = RealBuffer(size,initializer)
}

class BufferMatrix<T : Any>(
    override val rowNum: Int,
    override val colNum: Int,
    val buffer: Buffer<out T>,
    override val features: Set<MatrixFeature> = emptySet()
) : FeaturedMatrix<T> {

    init {
        if (buffer.size != rowNum * colNum) {
            error("Dimension mismatch for matrix structure")
        }
    }

    override val shape: IntArray get() = intArrayOf(rowNum, colNum)

    override fun suggestFeature(vararg features: MatrixFeature) =
        BufferMatrix(rowNum, colNum, buffer, this.features + features)

    override fun get(index: IntArray): T = get(index[0], index[1])

    override fun get(i: Int, j: Int): T = buffer[i * colNum + j]

    override fun elements(): Sequence<Pair<IntArray, T>> = sequence {
        for (i in 0 until rowNum) {
            for (j in 0 until colNum) {
                yield(intArrayOf(i, j) to get(i, j))
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return when (other) {
            is NDStructure<*> -> return NDStructure.equals(this, other)
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = buffer.hashCode()
        result = 31 * result + features.hashCode()
        return result
    }

    override fun toString(): String {
        return if (rowNum <= 5 && colNum <= 5) {
            "Matrix(rowsNum = $rowNum, colNum = $colNum, features=$features)\n" +
                    rows.asSequence().joinToString(prefix = "(", postfix = ")", separator = "\n ") {
                        it.asSequence().joinToString(separator = "\t") { it.toString() }
                    }
        } else {
            "Matrix(rowsNum = $rowNum, colNum = $colNum, features=$features)"
        }
    }
}

/**
 * Optimized dot product for real matrices
 */
infix fun BufferMatrix<Double>.dot(other: BufferMatrix<Double>): BufferMatrix<Double> {
    if (this.colNum != other.rowNum) error("Matrix dot operation dimension mismatch: ($rowNum, $colNum) x (${other.rowNum}, ${other.colNum})")

    val array = DoubleArray(this.rowNum * other.colNum)

    //convert to array to insure there is not memory indirection
    fun Buffer<out Double>.unsafeArray(): DoubleArray = if (this is RealBuffer) {
        array
    } else {
        DoubleArray(size) { get(it) }
    }

    val a = this.buffer.unsafeArray()
    val b = other.buffer.unsafeArray()

    for (i in (0 until rowNum)) {
        for (j in (0 until other.colNum)) {
            for (k in (0 until colNum)) {
                array[i * other.colNum + j] += a[i * colNum + k] * b[k * other.colNum + j]
            }
        }
    }

    val buffer = RealBuffer(array)
    return BufferMatrix(rowNum, other.colNum, buffer)
}