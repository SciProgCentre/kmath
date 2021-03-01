package space.kscience.kmath.linear

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.SpaceOperations
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.operations.sum
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory
import space.kscience.kmath.structures.asSequence
import kotlin.reflect.KClass

/**
 * Basic operations on matrices. Operates on [Matrix].
 *
 * @param T the type of items in the matrices.
 * @param M the type of operated matrices.
 */
public interface MatrixContext<T : Any, out M : Matrix<T>> : SpaceOperations<Matrix<T>> {
    /**
     * Produces a matrix with this context and given dimensions.
     */
    public fun produce(rows: Int, columns: Int, initializer: (i: Int, j: Int) -> T): M

    /**
     * Produces a point compatible with matrix space (and possibly optimized for it).
     */
    public fun point(size: Int, initializer: (Int) -> T): Point<T> = Buffer.boxing(size, initializer)

    @Suppress("UNCHECKED_CAST")
    public override fun binaryOperationFunction(operation: String): (left: Matrix<T>, right: Matrix<T>) -> M =
        when (operation) {
            "dot" -> { left, right -> left dot right }
            else -> super.binaryOperationFunction(operation) as (Matrix<T>, Matrix<T>) -> M
        }

    /**
     * Computes the dot product of this matrix and another one.
     *
     * @receiver the multiplicand.
     * @param other the multiplier.
     * @return the dot product.
     */
    public infix fun Matrix<T>.dot(other: Matrix<T>): M

    /**
     * Computes the dot product of this matrix and a vector.
     *
     * @receiver the multiplicand.
     * @param vector the multiplier.
     * @return the dot product.
     */
    public infix fun Matrix<T>.dot(vector: Point<T>): Point<T>

    /**
     * Multiplies a matrix by its element.
     *
     * @receiver the multiplicand.
     * @param value the multiplier.
     * @receiver the product.
     */
    public operator fun Matrix<T>.times(value: T): M

    /**
     * Multiplies an element by a matrix of it.
     *
     * @receiver the multiplicand.
     * @param m the multiplier.
     * @receiver the product.
     */
    public operator fun T.times(m: Matrix<T>): M = m * this

    /**
     * Gets a feature from the matrix. This function may return some additional features to
     * [kscience.kmath.nd.NDStructure.getFeature].
     *
     * @param F the type of feature.
     * @param m the matrix.
     * @param type the [KClass] instance of [F].
     * @return a feature object or `null` if it isn't present.
     */
    @UnstableKMathAPI
    public fun <F : Any> getFeature(m: Matrix<T>, type: KClass<F>): F? = m.getFeature(type)

    public companion object {

        /**
         * A structured matrix with custom buffer
         */
        public fun <T : Any, R : Ring<T>> buffered(
            ring: R,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing,
        ): GenericMatrixContext<T, R, BufferMatrix<T>> = BufferMatrixContext(ring, bufferFactory)

        /**
         * Automatic buffered matrix, unboxed if it is possible
         */
        public inline fun <reified T : Any, R : Ring<T>> auto(ring: R): GenericMatrixContext<T, R, BufferMatrix<T>> =
            buffered(ring, Buffer.Companion::auto)
    }
}

/**
 * Gets a feature from the matrix. This function may return some additional features to
 * [kscience.kmath.nd.NDStructure.getFeature].
 *
 * @param T the type of items in the matrices.
 * @param M the type of operated matrices.
 * @param F the type of feature.
 * @receiver the [MatrixContext] of [T].
 * @param m the matrix.
 * @return a feature object or `null` if it isn't present.
 */
@UnstableKMathAPI
public inline fun <T : Any, reified F : Any> MatrixContext<T, *>.getFeature(m: Matrix<T>): F? =
    getFeature(m, F::class)

/**
 * Partial implementation of [MatrixContext] for matrices of [Ring].
 *
 * @param T the type of items in the matrices.
 * @param R the type of ring of matrix elements.
 * @param M the type of operated matrices.
 */
public interface GenericMatrixContext<T : Any, R : Ring<T>, out M : Matrix<T>> : MatrixContext<T, M> {
    /**
     * The ring over matrix elements.
     */
    public val elementContext: R

    public override infix fun Matrix<T>.dot(other: Matrix<T>): M {
        //TODO add typed error
        require(colNum == other.rowNum) { "Matrix dot operation dimension mismatch: ($rowNum, $colNum) x (${other.rowNum}, ${other.colNum})" }

        return produce(rowNum, other.colNum) { i, j ->
            val row = rows[i]
            val column = other.columns[j]
            elementContext { sum(row.asSequence().zip(column.asSequence(), ::multiply)) }
        }
    }

    public override infix fun Matrix<T>.dot(vector: Point<T>): Point<T> {
        //TODO add typed error
        require(colNum == vector.size) { "Matrix dot vector operation dimension mismatch: ($rowNum, $colNum) x (${vector.size})" }

        return point(rowNum) { i ->
            val row = rows[i]
            elementContext { sum(row.asSequence().zip(vector.asSequence(), ::multiply)) }
        }
    }

    public override operator fun Matrix<T>.unaryMinus(): M =
        produce(rowNum, colNum) { i, j -> elementContext { -get(i, j) } }

    public override fun add(a: Matrix<T>, b: Matrix<T>): M {
        require(a.rowNum == b.rowNum && a.colNum == b.colNum) {
            "Matrix operation dimension mismatch. [${a.rowNum},${a.colNum}] + [${b.rowNum},${b.colNum}]"
        }

        return produce(a.rowNum, a.colNum) { i, j -> elementContext { a[i, j] + b[i, j] } }
    }

    public override operator fun Matrix<T>.minus(b: Matrix<T>): M {
        require(rowNum == b.rowNum && colNum == b.colNum) {
            "Matrix operation dimension mismatch. [$rowNum,$colNum] - [${b.rowNum},${b.colNum}]"
        }

        return produce(rowNum, colNum) { i, j -> elementContext { get(i, j) + b[i, j] } }
    }

    public override fun multiply(a: Matrix<T>, k: Number): M =
        produce(a.rowNum, a.colNum) { i, j -> elementContext { a[i, j] * k } }

    public override operator fun Matrix<T>.times(value: T): M =
        produce(rowNum, colNum) { i, j -> elementContext { get(i, j) * value } }
}
