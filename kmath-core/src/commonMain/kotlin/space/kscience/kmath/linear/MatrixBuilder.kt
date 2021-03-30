package space.kscience.kmath.linear

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Ring

public class MatrixBuilder<T : Any, A : Ring<T>>(
    public val linearSpace: LinearSpace<T, A>,
    public val rows: Int,
    public val columns: Int,
) {
    public operator fun invoke(vararg elements: T): Matrix<T> {
        require(rows * columns == elements.size) { "The number of elements ${elements.size} is not equal $rows * $columns" }
        return linearSpace.buildMatrix(rows, columns) { i, j -> elements[i * columns + j] }
    }

    //TODO add specific matrix builder functions like diagonal, etc
}

/**
 * Create a matrix builder with given number of rows and columns
 */
@UnstableKMathAPI
public fun <T : Any, A : Ring<T>> LinearSpace<T, A>.matrix(rows: Int, columns: Int): MatrixBuilder<T, A> =
    MatrixBuilder(this, rows, columns)

@UnstableKMathAPI
public fun <T : Any> LinearSpace<T, Ring<T>>.vector(vararg elements: T): Point<T> {
    return buildVector(elements.size) { elements[it] }
}

public inline fun <T : Any> LinearSpace<T, Ring<T>>.row(
    size: Int,
    crossinline builder: (Int) -> T,
): Matrix<T> = buildMatrix(1, size) { _, j -> builder(j) }

public fun <T : Any> LinearSpace<T, Ring<T>>.row(vararg values: T): Matrix<T> = row(values.size, values::get)

public inline fun <T : Any> LinearSpace<T, Ring<T>>.column(
    size: Int,
    crossinline builder: (Int) -> T,
): Matrix<T> = buildMatrix(size, 1) { i, _ -> builder(i) }

public fun <T : Any> LinearSpace<T, Ring<T>>.column(vararg values: T): Matrix<T> = column(values.size, values::get)