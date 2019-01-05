package scientifik.kmath.linear

import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.SpaceElement
import scientifik.kmath.structures.*


interface MatrixSpace<T : Any, R : Ring<T>> : Space<Matrix<T, R>> {
    /**
     * The ring context for matrix elements
     */
    val ring: R

    val rowNum: Int
    val colNum: Int

    val shape get() = intArrayOf(rowNum, colNum)

    /**
     * Produce a matrix with this context and given dimensions
     */
    fun produce(rows: Int = rowNum, columns: Int = colNum, initializer: (i: Int, j: Int) -> T): Matrix<T, R>

    /**
     * Produce a point compatible with matrix space
     */
    fun point(size: Int, initializer: (Int) -> T): Point<T>

    override val zero: Matrix<T, R> get() = produce { _, _ -> ring.zero }

    val one get() = produce { i, j -> if (i == j) ring.one else ring.zero }

    override fun add(a: Matrix<T, R>, b: Matrix<T, R>): Matrix<T, R> =
        produce(rowNum, colNum) { i, j -> ring.run { a[i, j] + b[i, j] } }

    override fun multiply(a: Matrix<T, R>, k: Double): Matrix<T, R> =
        produce(rowNum, colNum) { i, j -> ring.run { a[i, j] * k } }

    companion object {
        /**
         * Non-boxing double matrix
         */
        fun real(rows: Int, columns: Int): MatrixSpace<Double, RealField> =
            StructureMatrixSpace(rows, columns, RealField, DoubleBufferFactory)

        /**
         * A structured matrix with custom buffer
         */
        fun <T : Any, R : Ring<T>> buffered(
            rows: Int,
            columns: Int,
            ring: R,
            bufferFactory: BufferFactory<T> = ::boxingBuffer
        ): MatrixSpace<T, R> = StructureMatrixSpace(rows, columns, ring, bufferFactory)

        /**
         * Automatic buffered matrix, unboxed if it is possible
         */
        inline fun <reified T : Any, R : Ring<T>> smart(rows: Int, columns: Int, ring: R): MatrixSpace<T, R> =
            buffered(rows, columns, ring, ::autoBuffer)
    }
}


/**
 * Specialized 2-d structure
 */
interface Matrix<T : Any, R : Ring<T>> : NDStructure<T>, SpaceElement<Matrix<T, R>, Matrix<T, R>, MatrixSpace<T, R>> {
    operator fun get(i: Int, j: Int): T

    override fun get(index: IntArray): T = get(index[0], index[1])

    override val shape: IntArray get() = context.shape

    val numRows get() = context.rowNum
    val numCols get() = context.colNum

    //TODO replace by lazy buffers
    val rows: List<Point<T>>
        get() = (0 until numRows).map { i ->
            context.point(numCols) { j -> get(i, j) }
        }

    val columns: List<Point<T>>
        get() = (0 until numCols).map { j ->
            context.point(numRows) { i -> get(i, j) }
        }

    companion object {
        fun real(rows: Int, columns: Int, initializer: (Int, Int) -> Double) =
            MatrixSpace.real(rows, columns).produce(rows, columns, initializer)
    }
}

infix fun <T : Any, R : Ring<T>> Matrix<T, R>.dot(other: Matrix<T, R>): Matrix<T, R> {
    //TODO add typed error
    if (this.numCols != other.numRows) error("Matrix dot operation dimension mismatch: ($numRows, $numCols) x (${other.numRows}, ${other.numCols})")
    return context.produce(numRows, other.numCols) { i, j ->
        val row = rows[i]
        val column = other.columns[j]
        with(context.ring) {
            row.asSequence().zip(column.asSequence(), ::multiply).sum()
        }
    }
}

infix fun <T : Any, R : Ring<T>> Matrix<T, R>.dot(vector: Point<T>): Point<T> {
    //TODO add typed error
    if (this.numCols != vector.size) error("Matrix dot vector operation dimension mismatch: ($numRows, $numCols) x (${vector.size})")
    return context.point(numRows) { i ->
        val row = rows[i]
        with(context.ring) {
            row.asSequence().zip(vector.asSequence(), ::multiply).sum()
        }
    }
}

data class StructureMatrixSpace<T : Any, R : Ring<T>>(
    override val rowNum: Int,
    override val colNum: Int,
    override val ring: R,
    private val bufferFactory: BufferFactory<T>
) : MatrixSpace<T, R> {

    override val shape: IntArray = intArrayOf(rowNum, colNum)

    private val strides = DefaultStrides(shape)

    override fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> T): Matrix<T, R> {
        return if (rows == rowNum && columns == colNum) {
            val structure = ndStructure(strides, bufferFactory) { initializer(it[0], it[1]) }
            StructureMatrix(this, structure)
        } else {
            val context = StructureMatrixSpace(rows, columns, ring, bufferFactory)
            val structure = ndStructure(context.strides, bufferFactory) { initializer(it[0], it[1]) }
            StructureMatrix(context, structure)
        }
    }

    override fun point(size: Int, initializer: (Int) -> T): Point<T> = bufferFactory(size, initializer)
}

data class StructureMatrix<T : Any, R : Ring<T>>(
    override val context: StructureMatrixSpace<T, R>,
    val structure: NDStructure<T>
) : Matrix<T, R> {
    init {
        if (structure.shape.size != 2 || structure.shape[0] != context.rowNum || structure.shape[1] != context.colNum) {
            error("Dimension mismatch for structure, (${context.rowNum}, ${context.colNum}) expected, but ${structure.shape} found")
        }
    }

    override fun unwrap(): Matrix<T, R> = this

    override fun Matrix<T, R>.wrap(): Matrix<T, R> = this

    override val shape: IntArray get() = structure.shape

    override fun get(index: IntArray): T = structure[index]

    override fun get(i: Int, j: Int): T = structure[i, j]

    override fun elements(): Sequence<Pair<IntArray, T>> = structure.elements()
}

//TODO produce transposed matrix via reference without creating new space and structure
fun <T : Any, R : Ring<T>> Matrix<T, R>.transpose(): Matrix<T, R> =
    context.produce(numCols, numRows) { i, j -> get(j, i) }