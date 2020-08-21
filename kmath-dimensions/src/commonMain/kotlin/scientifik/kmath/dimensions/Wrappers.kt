package scientifik.kmath.dimensions

import scientifik.kmath.linear.GenericMatrixContext
import scientifik.kmath.linear.MatrixContext
import scientifik.kmath.linear.Point
import scientifik.kmath.linear.transpose
import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.invoke
import scientifik.kmath.structures.Matrix
import scientifik.kmath.structures.Structure2D

/**
 * A matrix with compile-time controlled dimension
 */
interface DMatrix<T, R : Dimension, C : Dimension> : Structure2D<T> {
    companion object {
        /**
         * Coerces a regular matrix to a matrix with type-safe dimensions and throws a error if coercion failed
         */
        inline fun <T, reified R : Dimension, reified C : Dimension> coerce(structure: Structure2D<T>): DMatrix<T, R, C> {
            if (structure.rowNum != Dimension.dim<R>().toInt()) {
                error("Row number mismatch: expected ${Dimension.dim<R>()} but found ${structure.rowNum}")
            }
            if (structure.colNum != Dimension.dim<C>().toInt()) {
                error("Column number mismatch: expected ${Dimension.dim<C>()} but found ${structure.colNum}")
            }
            return DMatrixWrapper(structure)
        }

        /**
         * The same as [coerce] but without dimension checks. Use with caution
         */
        fun <T, R : Dimension, C : Dimension> coerceUnsafe(structure: Structure2D<T>): DMatrix<T, R, C> {
            return DMatrixWrapper(structure)
        }
    }
}

/**
 * An inline wrapper for a Matrix
 */
inline class DMatrixWrapper<T, R : Dimension, C : Dimension>(
    val structure: Structure2D<T>
) : DMatrix<T, R, C> {
    override val shape: IntArray get() = structure.shape
    override operator fun get(i: Int, j: Int): T = structure[i, j]
}

/**
 * Dimension-safe point
 */
interface DPoint<T, D : Dimension> : Point<T> {
    companion object {
        inline fun <T, reified D : Dimension> coerce(point: Point<T>): DPoint<T, D> {
            if (point.size != Dimension.dim<D>().toInt()) {
                error("Vector dimension mismatch: expected ${Dimension.dim<D>()}, but found ${point.size}")
            }
            return DPointWrapper(point)
        }

        fun <T, D : Dimension> coerceUnsafe(point: Point<T>): DPoint<T, D> {
            return DPointWrapper(point)
        }
    }
}

/**
 * Dimension-safe point wrapper
 */
inline class DPointWrapper<T, D : Dimension>(val point: Point<T>) :
    DPoint<T, D> {
    override val size: Int get() = point.size

    override operator fun get(index: Int): T = point[index]

    override operator fun iterator(): Iterator<T> = point.iterator()
}


/**
 * Basic operations on dimension-safe matrices. Operates on [Matrix]
 */
inline class DMatrixContext<T : Any, Ri : Ring<T>>(val context: GenericMatrixContext<T, Ri>) {

    inline fun <reified R : Dimension, reified C : Dimension> Matrix<T>.coerce(): DMatrix<T, R, C> {
        check(
            rowNum == Dimension.dim<R>().toInt()
        ) { "Row number mismatch: expected ${Dimension.dim<R>()} but found $rowNum" }

        check(
            colNum == Dimension.dim<C>().toInt()
        ) { "Column number mismatch: expected ${Dimension.dim<C>()} but found $colNum" }

        return DMatrix.coerceUnsafe(this)
    }

    /**
     * Produce a matrix with this context and given dimensions
     */
    inline fun <reified R : Dimension, reified C : Dimension> produce(noinline initializer: (i: Int, j: Int) -> T): DMatrix<T, R, C> {
        val rows = Dimension.dim<R>()
        val cols = Dimension.dim<C>()
        return context.produce(rows.toInt(), cols.toInt(), initializer).coerce<R, C>()
    }

    inline fun <reified D : Dimension> point(noinline initializer: (Int) -> T): DPoint<T, D> {
        val size = Dimension.dim<D>()

        return DPoint.coerceUnsafe(
            context.point(
                size.toInt(),
                initializer
            )
        )
    }

    inline infix fun <reified R1 : Dimension, reified C1 : Dimension, reified C2 : Dimension> DMatrix<T, R1, C1>.dot(
        other: DMatrix<T, C1, C2>
    ): DMatrix<T, R1, C2> = context { this@dot dot other }.coerce()

    inline infix fun <reified R : Dimension, reified C : Dimension> DMatrix<T, R, C>.dot(vector: DPoint<T, C>): DPoint<T, R> =
        DPoint.coerceUnsafe(context { this@dot dot vector })

    inline operator fun <reified R : Dimension, reified C : Dimension> DMatrix<T, R, C>.times(value: T): DMatrix<T, R, C> =
        context { this@times.times(value) }.coerce()

    inline operator fun <reified R : Dimension, reified C : Dimension> T.times(m: DMatrix<T, R, C>): DMatrix<T, R, C> =
        m * this

    inline operator fun <reified R : Dimension, reified C : Dimension> DMatrix<T, C, R>.plus(other: DMatrix<T, C, R>): DMatrix<T, C, R> =
        context { this@plus + other }.coerce()

    inline operator fun <reified R : Dimension, reified C : Dimension> DMatrix<T, C, R>.minus(other: DMatrix<T, C, R>): DMatrix<T, C, R> =
        context { this@minus + other }.coerce()

    inline operator fun <reified R : Dimension, reified C : Dimension> DMatrix<T, C, R>.unaryMinus(): DMatrix<T, C, R> =
        context { this@unaryMinus.unaryMinus() }.coerce()

    inline fun <reified R : Dimension, reified C : Dimension> DMatrix<T, C, R>.transpose(): DMatrix<T, R, C> =
        context { (this@transpose as Matrix<T>).transpose() }.coerce()

    /**
     * A square unit matrix
     */
    inline fun <reified D : Dimension> one(): DMatrix<T, D, D> = produce { i, j ->
        if (i == j) context.elementContext.one else context.elementContext.zero
    }

    inline fun <reified R : Dimension, reified C : Dimension> zero(): DMatrix<T, R, C> = produce { _, _ ->
        context.elementContext.zero
    }

    companion object {
        val real: DMatrixContext<Double, RealField> = DMatrixContext(MatrixContext.real)
    }
}
