package space.kscience.kmath.linear

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.DoubleBuffer
import kotlin.reflect.KClass

/**
 * Alias for [Structure2D] with more familiar name.
 *
 * @param T the type of items.
 */
public typealias Matrix<T> = Structure2D<T>

/**
 * Alias or using [Buffer] as a point/vector in a many-dimensional space.
 */
public typealias Point<T> = Buffer<T>

/**
 * Basic operations on matrices and vectors. Operates on [Matrix].
 *
 * @param T the type of items in the matrices.
 * @param M the type of operated matrices.
 */
public interface LinearSpace<T : Any, out A : Ring<T>> {
    public val elementAlgebra: A

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
     * Get a feature of the structure in this scope. Structure features take precedence other context features
     *
     * @param F the type of feature.
     * @param structure the structure.
     * @param type the [KClass] instance of [F].
     * @return a feature object or `null` if it isn't present.
     */
    @UnstableKMathAPI
    public fun <F : Any> getFeature(structure: Matrix<T>, type: KClass<F>): F? = structure.getFeature(type)

    public companion object {

        /**
         * A structured matrix with custom buffer
         */
        public fun <T : Any, A : Ring<T>> buffered(
            algebra: A,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing,
        ): LinearSpace<T, A> = BufferedLinearSpace(algebra, bufferFactory)

        public val real: LinearSpace<Double, DoubleField> = buffered(DoubleField, ::DoubleBuffer)

        /**
         * Automatic buffered matrix, unboxed if it is possible
         */
        public inline fun <reified T : Any, A : Ring<T>> auto(ring: A): LinearSpace<T, A> =
            buffered(ring, Buffer.Companion::auto)
    }
}

/**
 * Get a feature of the structure in this scope. Structure features take precedence other context features
 *
 * @param T the type of items in the matrices.
 * @param F the type of feature.
 * @return a feature object or `null` if it isn't present.
 */
@UnstableKMathAPI
public inline fun <T : Any, reified F : Any> LinearSpace<T, *>.getFeature(structure: Matrix<T>): F? =
    getFeature(structure, F::class)


public operator fun <LS : LinearSpace<*, *>, R> LS.invoke(block: LS.() -> R): R = run(block)

