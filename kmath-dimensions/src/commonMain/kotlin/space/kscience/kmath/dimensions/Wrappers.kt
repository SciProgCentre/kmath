package space.kscience.kmath.dimensions

import space.kscience.kmath.linear.*
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.operations.invoke

/**
 * A matrix with compile-time controlled dimension
 */
public interface DMatrix<T, R : Dimension, C : Dimension> : Structure2D<T> {
    public companion object {
        /**
         * Coerces a regular matrix to a matrix with type-safe dimensions and throws a error if coercion failed
         */
        public inline fun <T, reified R : Dimension, reified C : Dimension> coerce(structure: Structure2D<T>): DMatrix<T, R, C> {
            require(structure.rowNum == Dimension.dim<R>().toInt()) {
                "Row number mismatch: expected ${Dimension.dim<R>()} but found ${structure.rowNum}"
            }

            require(structure.colNum == Dimension.dim<C>().toInt()) {
                "Column number mismatch: expected ${Dimension.dim<C>()} but found ${structure.colNum}"
            }

            return DMatrixWrapper(structure)
        }

        /**
         * The same as [DMatrix.coerce] but without dimension checks. Use with caution
         */
        public fun <T, R : Dimension, C : Dimension> coerceUnsafe(structure: Structure2D<T>): DMatrix<T, R, C> =
            DMatrixWrapper(structure)
    }
}

/**
 * An inline wrapper for a Matrix
 */
public inline class DMatrixWrapper<T, R : Dimension, C : Dimension>(
    private val structure: Structure2D<T>,
) : DMatrix<T, R, C> {
    override val shape: IntArray get() = structure.shape
    override val rowNum: Int get() = shape[0]
    override val colNum: Int get() = shape[1]
    override operator fun get(i: Int, j: Int): T = structure[i, j]
}

/**
 * Dimension-safe point
 */
public interface DPoint<T, D : Dimension> : Point<T> {
    public companion object {
        public inline fun <T, reified D : Dimension> coerce(point: Point<T>): DPoint<T, D> {
            require(point.size == Dimension.dim<D>().toInt()) {
                "Vector dimension mismatch: expected ${Dimension.dim<D>()}, but found ${point.size}"
            }

            return DPointWrapper(point)
        }

        public fun <T, D : Dimension> coerceUnsafe(point: Point<T>): DPoint<T, D> = DPointWrapper(point)
    }
}

/**
 * Dimension-safe point wrapper
 */
public inline class DPointWrapper<T, D : Dimension>(public val point: Point<T>) :
    DPoint<T, D> {
    override val size: Int get() = point.size

    override operator fun get(index: Int): T = point[index]

    override operator fun iterator(): Iterator<T> = point.iterator()
}


/**
 * Basic operations on dimension-safe matrices. Operates on [Matrix]
 */
public inline class DMatrixContext<T : Any>(public val context: MatrixContext<T, Matrix<T>>) {
    public inline fun <reified R : Dimension, reified C : Dimension> Matrix<T>.coerce(): DMatrix<T, R, C> {
        require(rowNum == Dimension.dim<R>().toInt()) {
            "Row number mismatch: expected ${Dimension.dim<R>()} but found $rowNum"
        }

        require(colNum == Dimension.dim<C>().toInt()) {
            "Column number mismatch: expected ${Dimension.dim<C>()} but found $colNum"
        }

        return DMatrix.coerceUnsafe(this)
    }

    /**
     * Produce a matrix with this context and given dimensions
     */
    public inline fun <reified R : Dimension, reified C : Dimension> produce(noinline initializer: (i: Int, j: Int) -> T): DMatrix<T, R, C> {
        val rows = Dimension.dim<R>()
        val cols = Dimension.dim<C>()
        return context.produce(rows.toInt(), cols.toInt(), initializer).coerce<R, C>()
    }

    public inline fun <reified D : Dimension> point(noinline initializer: (Int) -> T): DPoint<T, D> {
        val size = Dimension.dim<D>()

        return DPoint.coerceUnsafe(
            context.point(
                size.toInt(),
                initializer
            )
        )
    }

    public inline infix fun <reified R1 : Dimension, reified C1 : Dimension, reified C2 : Dimension> DMatrix<T, R1, C1>.dot(
        other: DMatrix<T, C1, C2>,
    ): DMatrix<T, R1, C2> = context { this@dot dot other }.coerce()

    public inline infix fun <reified R : Dimension, reified C : Dimension> DMatrix<T, R, C>.dot(vector: DPoint<T, C>): DPoint<T, R> =
        DPoint.coerceUnsafe(context { this@dot dot vector })

    public inline operator fun <reified R : Dimension, reified C : Dimension> DMatrix<T, R, C>.times(value: T): DMatrix<T, R, C> =
        context { this@times.times(value) }.coerce()

    public inline operator fun <reified R : Dimension, reified C : Dimension> T.times(m: DMatrix<T, R, C>): DMatrix<T, R, C> =
        m * this

    public inline operator fun <reified R : Dimension, reified C : Dimension> DMatrix<T, C, R>.plus(other: DMatrix<T, C, R>): DMatrix<T, C, R> =
        context { this@plus + other }.coerce()

    public inline operator fun <reified R : Dimension, reified C : Dimension> DMatrix<T, C, R>.minus(other: DMatrix<T, C, R>): DMatrix<T, C, R> =
        context { this@minus + other }.coerce()

    public inline operator fun <reified R : Dimension, reified C : Dimension> DMatrix<T, C, R>.unaryMinus(): DMatrix<T, C, R> =
        context { this@unaryMinus.unaryMinus() }.coerce()

    public inline fun <reified R : Dimension, reified C : Dimension> DMatrix<T, C, R>.transpose(): DMatrix<T, R, C> =
        context { (this@transpose as Matrix<T>).transpose() }.coerce()

    public companion object {
        public val real: DMatrixContext<Double> = DMatrixContext(MatrixContext.real)
    }
}


/**
 * A square unit matrix
 */
public inline fun <reified D : Dimension> DMatrixContext<Double>.one(): DMatrix<Double, D, D> = produce { i, j ->
    if (i == j) 1.0 else 0.0
}

public inline fun <reified R : Dimension, reified C : Dimension> DMatrixContext<Double>.zero(): DMatrix<Double, R, C> =
    produce { _, _ ->
        0.0
    }