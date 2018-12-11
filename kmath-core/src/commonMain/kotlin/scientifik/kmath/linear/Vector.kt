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
abstract class VectorSpace<T : Any, S : Space<T>>(val size: Int, val space: S) : Space<Point<T>> {

    abstract fun produce(initializer: (Int) -> T): Vector<T, S>

    override val zero: Vector<T, S> by lazy { produce { space.zero } }

    override fun add(a: Point<T>, b: Point<T>): Vector<T, S> = produce { with(space) { a[it] + b[it] } }

    override fun multiply(a: Point<T>, k: Double): Vector<T, S> = produce { with(space) { a[it] * k } }
}


/**
 * A point coupled to the linear space
 */
interface Vector<T : Any, S : Space<T>> : SpaceElement<Point<T>, VectorSpace<T, S>>, Point<T>, Iterable<T> {
    override val size: Int get() = context.size

    override operator fun plus(b: Point<T>): Vector<T, S> = context.add(self, b)
    override operator fun minus(b: Point<T>): Vector<T, S> = context.add(self, context.multiply(b, -1.0))
    override operator fun times(k: Number): Vector<T, S> = context.multiply(self, k.toDouble())
    override operator fun div(k: Number): Vector<T, S> = context.multiply(self, 1.0 / k.toDouble())

    companion object {
        /**
         * Create vector with custom field
         */
        fun <T : Any, F : Field<T>> of(size: Int, field: F, initializer: (Int) -> T) =
                ArrayVector(ArrayVectorSpace(size, field), initializer)

        private val realSpaceCache = HashMap<Int, ArrayVectorSpace<Double, DoubleField>>()

        private fun getRealSpace(size: Int): ArrayVectorSpace<Double, DoubleField> {
            return realSpaceCache.getOrPut(size){ArrayVectorSpace(size, DoubleField, realNDFieldFactory)}
        }

        /**
         * Create vector of [Double]
         */
        fun ofReal(size: Int, initializer: (Int) -> Double) =
                ArrayVector(getRealSpace(size), initializer)

        fun ofReal(vararg point: Double) = point.toVector()

        fun equals(v1: Vector<*, *>, v2: Vector<*, *>): Boolean {
            if (v1 === v2) return true
            if (v1.context != v2.context) return false
            for (i in 0 until v2.size) {
                if (v1[i] != v2[i]) return false
            }
            return true
        }
    }
}

class ArrayVectorSpace<T : Any, F : Field<T>>(
        size: Int,
        field: F,
        val ndFactory: NDFieldFactory<T, F> = genericNDFieldFactory(field)
) : VectorSpace<T, F>(size, field) {
    val ndField by lazy {
        ndFactory(intArrayOf(size))
    }

    override fun produce(initializer: (Int) -> T): Vector<T, F> = ArrayVector(this, initializer)
}


class ArrayVector<T : Any, F : Field<T>> internal constructor(override val context: VectorSpace<T, F>, val element: NDElement<T, F>) : Vector<T, F> {

    constructor(context: ArrayVectorSpace<T, F>, initializer: (Int) -> T) : this(context, context.ndField.produce { list -> initializer(list[0]) })

    init {
        if (context.size != element.shape[0]) {
            error("Array dimension mismatch")
        }
    }

    override fun get(index: Int): T {
        return element[index]
    }

    override val self: ArrayVector<T, F> get() = this

    override fun iterator(): Iterator<T> = (0 until size).map { element[it] }.iterator()

    override fun toString(): String = this.joinToString(prefix = "[", postfix = "]", separator = ", ") { it.toString() }
}

