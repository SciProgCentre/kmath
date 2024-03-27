/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.attributes.*
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.BufferRingOps
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer

/**
 * Alias for [Structure2D] with more familiar name.
 *
 * @param T the type of items.
 */
public typealias Matrix<T> = Structure2D<T>
public typealias MutableMatrix<T> = MutableStructure2D<T>

/**
 * Alias or using [Buffer] as a point/vector in a many-dimensional space.
 *
 * @param T the type of elements contained in the buffer.
 */
public typealias Point<T> = Buffer<T>

/**
 * Basic operations on matrices and vectors.
 *
 * @param T the type of items in the matrices.
 * @param A the type of ring over [T].
 */
public interface LinearSpace<T, out A : Ring<T>> : MatrixScope<T> {
    public val elementAlgebra: A

    override val type: SafeType<T> get() = elementAlgebra.type

    /**
     * Produces a matrix with this context and given dimensions.
     */
    public fun buildMatrix(rows: Int, columns: Int, initializer: A.(i: Int, j: Int) -> T): Matrix<T>

    /**
     * Produces a point compatible with matrix space (and possibly optimized for it).
     */
    public fun buildVector(size: Int, initializer: A.(Int) -> T): Point<T>

    public operator fun Matrix<T>.unaryMinus(): Matrix<T> = buildMatrix(rowNum, colNum) { i, j ->
        -get(i, j)
    }

    public operator fun Point<T>.unaryMinus(): Point<T> = buildVector(size) {
        -get(it)
    }

    /**
     * Matrix sum
     */
    public operator fun Matrix<T>.plus(other: Matrix<T>): Matrix<T> = buildMatrix(rowNum, colNum) { i, j ->
        get(i, j) + other[i, j]
    }


    /**
     * Vector sum
     */
    public operator fun Point<T>.plus(other: Point<T>): Point<T> = buildVector(size) {
        get(it) + other[it]
    }

    /**
     * Matrix subtraction
     */
    public operator fun Matrix<T>.minus(other: Matrix<T>): Matrix<T> = buildMatrix(rowNum, colNum) { i, j ->
        get(i, j) - other[i, j]
    }

    /**
     * Vector subtraction
     */
    public operator fun Point<T>.minus(other: Point<T>): Point<T> = buildVector(size) {
        get(it) - other[it]
    }


    /**
     * Computes the dot product of this matrix and another one.
     *
     * @receiver the multiplicand.
     * @param other the multiplier.
     * @return the dot product.
     */
    public infix fun Matrix<T>.dot(other: Matrix<T>): Matrix<T> {
        require(colNum == other.rowNum) { "Matrix dot operation dimension mismatch: ($rowNum, $colNum) x (${other.rowNum}, ${other.colNum})" }
        return elementAlgebra {
            buildMatrix(rowNum, other.colNum) { i, j ->
                var res = zero
                for (l in 0 until colNum) {
                    res += this@dot[i, l] * other[l, j]
                }
                res
            }
        }
    }

    /**
     * Computes the dot product of this matrix and a vector.
     *
     * @receiver the multiplicand.
     * @param vector the multiplier.
     * @return the dot product.
     */
    public infix fun Matrix<T>.dot(vector: Point<T>): Point<T> {
        require(colNum == vector.size) { "Matrix dot vector operation dimension mismatch: ($rowNum, $colNum) x (${vector.size})" }
        return elementAlgebra {
            buildVector(rowNum) { i ->
                var res = one
                for (j in 0 until colNum) {
                    res += this@dot[i, j] * vector[j]
                }
                res
            }
        }
    }

    /**
     * Multiplies a matrix by its element.
     *
     * @receiver the multiplicand.
     * @param value the multiplier.
     * @receiver the product.
     */
    public operator fun Matrix<T>.times(value: T): Matrix<T> =
        buildMatrix(rowNum, colNum) { i, j -> get(i, j) * value }

    /**
     * Multiplies an element by a matrix of it.
     *
     * @receiver the multiplicand.
     * @param m the multiplier.
     * @receiver the product.
     */
    public operator fun T.times(m: Matrix<T>): Matrix<T> = m * this

    /**
     * Multiplies a vector by its element.
     *
     * @receiver the multiplicand.
     * @param value the multiplier.
     * @receiver the product.
     */
    public operator fun Point<T>.times(value: T): Point<T> =
        buildVector(size) { i -> get(i) * value }

    /**
     * Multiplies an element by a vector of it.
     *
     * @receiver the multiplicand.
     * @param v the multiplier.
     * @receiver the product.
     */
    public operator fun T.times(v: Point<T>): Point<T> = v * this

    /**
     * Compute an [attribute] value for given [structure]. Return null if the attribute could not be computed.
     */
    public fun <V, A : StructureAttribute<V>> computeAttribute(structure: Structure2D<T>, attribute: A): V? = null

    @UnstableKMathAPI
    public fun <V, A : StructureAttribute<V>> Structure2D<T>.getOrComputeAttribute(attribute: A): V? {
        return attributes[attribute] ?: computeAttribute(this, attribute)
    }

    /**
     * If the structure holds given [attribute] return itself. Otherwise, return a new [Matrix] that contains a computed attribute.
     *
     * This method is used to compute and cache attribute inside the structure. If one needs an attribute only once,
     * better use [StructureND.getOrComputeAttribute].
     */
    @UnstableKMathAPI
    public fun <V : Any, A : StructureAttribute<V>> Matrix<T>.withComputedAttribute(
        attribute: A,
    ): Matrix<T>? {
        return if (attributes[attribute] != null) {
            this
        } else {
            val value = computeAttribute(this, attribute) ?: return null
            if (this is MatrixWrapper) {
                MatrixWrapper(this, attributes.withAttribute(attribute, value))
            } else {
                MatrixWrapper(this, Attributes(attribute, value))
            }
        }
    }

    public companion object {

        /**
         * A structured matrix with custom buffer
         */
        public fun <T : Any, A : Ring<T>> buffered(
            algebra: A,
        ): LinearSpace<T, A> = BufferedLinearSpace(BufferRingOps(algebra))

    }
}


public inline operator fun <LS : LinearSpace<*, *>, R> LS.invoke(block: LS.() -> R): R = run(block)


/**
 * Convert matrix to vector if it is possible.
 */
public fun <T> Matrix<T>.asVector(): Point<T> =
    if (this.colNum == 1) as1D()
    else error("Can't convert matrix with more than one column to vector")

/**
 * Creates an n &times; 1 [VirtualMatrix], where n is the size of the given buffer.
 *
 * @param T the type of elements contained in the buffer.
 * @receiver a buffer.
 * @return the new matrix.
 */
public fun <T> Point<T>.asMatrix(): VirtualMatrix<T> = VirtualMatrix(size, 1) { i, _ -> get(i) }