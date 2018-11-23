package scientifik.kmath.linear

import scientifik.kmath.histogram.Point
import scientifik.kmath.operations.DoubleField
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.SpaceElement
import scientifik.kmath.structures.NDElement
import scientifik.kmath.structures.get

/**
 * A linear space for vectors.
 * Could be used on any point-like structure
 */
abstract class VectorSpace<T : Any>(val size: Int, val field: Field<T>) : Space<Point<T>> {

    abstract fun produce(initializer: (Int) -> T): Vector<T>

    override val zero: Vector<T> by lazy { produce { field.zero } }

    override fun add(a: Point<T>, b: Point<T>): Vector<T> = produce { with(field) { a[it] + b[it] } }

    override fun multiply(a: Point<T>, k: Double): Vector<T> = produce { with(field) { a[it] * k } }
}


/**
 * A point coupled to the linear space
 */
interface Vector<T : Any> : SpaceElement<Point<T>, VectorSpace<T>>, Point<T>, Iterable<T> {
    override val size: Int get() = context.size

    override operator fun plus(b: Point<T>): Vector<T> = context.add(self, b)
    override operator fun minus(b: Point<T>): Vector<T> = context.add(self, context.multiply(b, -1.0))
    override operator fun times(k: Number): Vector<T> = context.multiply(self, k.toDouble())
    override operator fun div(k: Number): Vector<T> = context.multiply(self, 1.0 / k.toDouble())

    companion object {
        /**
         * Create vector with custom field
         */
        fun <T : Any> of(size: Int, field: Field<T>, initializer: (Int) -> T) =
                ArrayVector(ArrayVectorSpace(size, field), initializer)

        /**
         * Create vector of [Double]
         */
        fun ofReal(size: Int, initializer: (Int) -> Double) =
                ArrayVector(ArrayVectorSpace(size, DoubleField, realNDFieldFactory), initializer)

        fun ofReal(vararg point: Double) = point.toVector()

        fun equals(v1: Vector<*>, v2: Vector<*>): Boolean {
            if (v1 === v2) return true
            if (v1.context != v2.context) return false
            for (i in 0 until v2.size) {
                if (v1[i] != v2[i]) return false
            }
            return true
        }
    }
}

class ArrayVectorSpace<T : Any>(
        size: Int,
        field: Field<T>,
        val ndFactory: NDFieldFactory<T> = genericNDFieldFactory(field)
) : VectorSpace<T>(size, field) {
    val ndField by lazy {
        ndFactory(intArrayOf(size))
    }

    override fun produce(initializer: (Int) -> T): Vector<T> = ArrayVector(this, initializer)
}

/**
 * Member of [ArrayMatrixSpace] which wraps 2-D array
 */
class ArrayMatrix<T : Any> internal constructor(override val context: ArrayMatrixSpace<T>, val element: NDElement<T>) : Matrix<T> {

    constructor(context: ArrayMatrixSpace<T>, initializer: (Int, Int) -> T) : this(context, context.ndField.produce { list -> initializer(list[0], list[1]) })

    override val rows: Int get() = context.rows

    override val columns: Int get() = context.columns

    override fun get(i: Int, j: Int): T {
        return element[i, j]
    }

    override val self: ArrayMatrix<T> get() = this
}


class ArrayVector<T : Any> internal constructor(override val context: VectorSpace<T>, val element: NDElement<T>) : Vector<T> {

    constructor(context: ArrayVectorSpace<T>, initializer: (Int) -> T) : this(context, context.ndField.produce { list -> initializer(list[0]) })

    init {
        if (context.size != element.shape[0]) {
            error("Array dimension mismatch")
        }
    }

    override fun get(index: Int): T {
        return element[index]
    }

    override val self: ArrayVector<T> get() = this

    override fun iterator(): Iterator<T> = (0 until size).map { element[it] }.iterator()

    override fun copy(): ArrayVector<T> = ArrayVector(context, element)

    override fun toString(): String = this.joinToString(prefix = "[", postfix = "]", separator = ", ") { it.toString() }
}

typealias RealVector = Vector<Double>