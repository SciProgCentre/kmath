package scientifik.kmath.linear

import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.BufferFactory
import scientifik.kmath.structures.Structure2D
import scientifik.kmath.structures.asBuffer

class MatrixBuilder(val rows: Int, val columns: Int) {
    operator fun <T : Any> invoke(vararg elements: T): FeaturedMatrix<T> {
        require(rows * columns == elements.size) { "The number of elements ${elements.size} is not equal $rows * $columns" }
        val buffer = elements.asBuffer()
        return BufferMatrix(rows, columns, buffer)
    }

    //TODO add specific matrix builder functions like diagonal, etc
}

fun Structure2D.Companion.build(rows: Int, columns: Int): MatrixBuilder = MatrixBuilder(rows, columns)

fun <T : Any> Structure2D.Companion.row(vararg values: T): FeaturedMatrix<T> {
    val buffer = values.asBuffer()
    return BufferMatrix(1, values.size, buffer)
}

inline fun <reified T : Any> Structure2D.Companion.row(
    size: Int,
    factory: BufferFactory<T> = Buffer.Companion::auto,
    noinline builder: (Int) -> T
): FeaturedMatrix<T> {
    val buffer = factory(size, builder)
    return BufferMatrix(1, size, buffer)
}

fun <T : Any> Structure2D.Companion.column(vararg values: T): FeaturedMatrix<T> {
    val buffer = values.asBuffer()
    return BufferMatrix(values.size, 1, buffer)
}

inline fun <reified T : Any> Structure2D.Companion.column(
    size: Int,
    factory: BufferFactory<T> = Buffer.Companion::auto,
    noinline builder: (Int) -> T
): FeaturedMatrix<T> {
    val buffer = factory(size, builder)
    return BufferMatrix(size, 1, buffer)
}
