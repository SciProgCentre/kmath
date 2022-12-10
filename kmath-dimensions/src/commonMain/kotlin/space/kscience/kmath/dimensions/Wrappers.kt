/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.dimensions

import space.kscience.kmath.linear.*
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.algebra
import kotlin.jvm.JvmInline

/**
 * A matrix with compile-time controlled dimension
 */
public interface DMatrix<out T, R : Dimension, C : Dimension> : Structure2D<T> {
    public companion object {
        /**
         * Coerces a regular matrix to a matrix with type-safe dimensions and throws an error if coercion failed
         */
        public inline fun <T, reified R : Dimension, reified C : Dimension> coerce(structure: Structure2D<T>): DMatrix<T, R, C> {
            require(structure.rowNum == Dimension.dim<R>()) {
                "Row number mismatch: expected ${Dimension.dim<R>()} but found ${structure.rowNum}"
            }

            require(structure.colNum == Dimension.dim<C>()) {
                "Column number mismatch: expected ${Dimension.dim<C>()} but found ${structure.colNum}"
            }

            return DMatrixWrapper(structure)
        }

        /**
         * The same as [DMatrix.coerce] but without dimension checks. Use with caution.
         */
        public fun <T, R : Dimension, C : Dimension> coerceUnsafe(structure: Structure2D<T>): DMatrix<T, R, C> =
            DMatrixWrapper(structure)
    }
}

/**
 * An inline wrapper for a Matrix
 */
@JvmInline
public value class DMatrixWrapper<out T, R : Dimension, C : Dimension>(
    private val structure: Structure2D<T>,
) : DMatrix<T, R, C> {
    override val shape: ShapeND get() = structure.shape
    override val rowNum: Int get() = shape[0]
    override val colNum: Int get() = shape[1]
    override operator fun get(i: Int, j: Int): T = structure[i, j]
}

/**
 * Dimension-safe point
 */
public interface DPoint<out T, D : Dimension> : Point<T> {
    public companion object {
        public inline fun <T, reified D : Dimension> coerce(point: Point<T>): DPoint<T, D> {
            require(point.size == Dimension.dim<D>()) {
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
@JvmInline
public value class DPointWrapper<out T, D : Dimension>(public val point: Point<T>) :
    DPoint<T, D> {
    override val size: Int get() = point.size

    override operator fun get(index: Int): T = point[index]

    override operator fun iterator(): Iterator<T> = point.iterator()
}


/**
 * Basic operations on dimension-safe matrices. Operates on [Matrix]
 */
@JvmInline
public value class DMatrixContext<T : Any, out A : Ring<T>>(public val context: LinearSpace<T, A>) {
    public inline fun <reified R : Dimension, reified C : Dimension> Matrix<T>.coerce(): DMatrix<T, R, C> {
        require(rowNum == Dimension.dim<R>()) {
            "Row number mismatch: expected ${Dimension.dim<R>()} but found $rowNum"
        }

        require(colNum == Dimension.dim<C>()) {
            "Column number mismatch: expected ${Dimension.dim<C>()} but found $colNum"
        }

        return DMatrix.coerceUnsafe(this)
    }

    /**
     * Produce a matrix with this context and given dimensions
     */
    public inline fun <reified R : Dimension, reified C : Dimension> produce(
        noinline initializer: A.(i: Int, j: Int) -> T,
    ): DMatrix<T, R, C> {
        val rows = Dimension.dim<R>()
        val cols = Dimension.dim<C>()
        return context.buildMatrix(rows, cols, initializer).coerce()
    }

    public inline fun <reified D : Dimension> point(noinline initializer: A.(Int) -> T): DPoint<T, D> {
        val size = Dimension.dim<D>()

        return DPoint.coerceUnsafe(
            context.buildVector(
                size,
                initializer
            )
        )
    }

    public inline infix fun <reified R1 : Dimension, reified C1 : Dimension, reified C2 : Dimension> DMatrix<T, R1, C1>.dot(
        other: DMatrix<T, C1, C2>,
    ): DMatrix<T, R1, C2> = context.run { this@dot dot other }.coerce()

    public inline infix fun <reified R : Dimension, reified C : Dimension> DMatrix<T, R, C>.dot(vector: DPoint<T, C>): DPoint<T, R> =
        DPoint.coerceUnsafe(context.run { this@dot dot vector })

    public inline operator fun <reified R : Dimension, reified C : Dimension> DMatrix<T, R, C>.times(value: T): DMatrix<T, R, C> =
        context.run { this@times.times(value) }.coerce()

    public inline operator fun <reified R : Dimension, reified C : Dimension> T.times(m: DMatrix<T, R, C>): DMatrix<T, R, C> =
        m * this

    public inline operator fun <reified R : Dimension, reified C : Dimension> DMatrix<T, C, R>.plus(other: DMatrix<T, C, R>): DMatrix<T, C, R> =
        context.run { this@plus + other }.coerce()

    public inline operator fun <reified R : Dimension, reified C : Dimension> DMatrix<T, C, R>.minus(other: DMatrix<T, C, R>): DMatrix<T, C, R> =
        context.run { this@minus + other }.coerce()

    public inline operator fun <reified R : Dimension, reified C : Dimension> DMatrix<T, C, R>.unaryMinus(): DMatrix<T, C, R> =
        context.run { this@unaryMinus.unaryMinus() }.coerce()

    public inline fun <reified R : Dimension, reified C : Dimension> DMatrix<T, C, R>.transpose(): DMatrix<T, R, C> =
        context.run { (this@transpose as Matrix<T>).transpose() }.coerce()

    public companion object {
        public val real: DMatrixContext<Double, DoubleField> = DMatrixContext(Double.algebra.linearSpace)
    }
}


/**
 * A square unit matrix
 */
public inline fun <reified D : Dimension> DMatrixContext<Double, DoubleField>.one(): DMatrix<Double, D, D> =
    produce { i, j ->
        if (i == j) 1.0 else 0.0
    }

public inline fun <reified R : Dimension, reified C : Dimension> DMatrixContext<Double, DoubleField>.zero(): DMatrix<Double, R, C> =
    produce { _, _ ->
        0.0
    }
