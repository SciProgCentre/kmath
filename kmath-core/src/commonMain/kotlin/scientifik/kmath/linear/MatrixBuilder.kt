package scientifik.kmath.linear

import scientifik.kmath.structures.Structure2D
import scientifik.kmath.structures.asBuffer

class MatrixBuilder<T : Any>(val rows: Int, val columns: Int) {
    operator fun invoke(vararg elements: T): FeaturedMatrix<T> {
        if (rows * columns != elements.size) error("The number of elements ${elements.size} is not equal $rows * $columns")
        val buffer = elements.asBuffer()
        return BufferMatrix(rows, columns, buffer)
    }
}

fun <T : Any> Structure2D.Companion.build(rows: Int, columns: Int): MatrixBuilder<T> = MatrixBuilder(rows, columns)