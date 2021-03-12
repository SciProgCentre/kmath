package space.kscience.kmath.linear

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Ring


@UnstableKMathAPI
public fun <T : Any> LinearSpace<T, Ring<T>>.matrix(rows: Int, columns: Int, vararg elements: T): Matrix<T> {
    require(rows * columns == elements.size) { "The number of elements ${elements.size} is not equal $rows * $columns" }
    return buildMatrix(rows, columns) { i, j -> elements[i * columns + j] }
}

@UnstableKMathAPI
public fun <T : Any> LinearSpace<T, Ring<T>>.vector(vararg elements: T): Vector<T> {
    return buildVector(elements.size, elements::get)
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