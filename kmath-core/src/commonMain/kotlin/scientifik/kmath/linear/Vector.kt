package scientifik.kmath.linear

import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.SpaceElement
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.BufferFactory
import scientifik.kmath.structures.asSequence

typealias Point<T> = Buffer<T>

/**
 * A linear space for vectors.
 * Could be used on any point-like structure
 */
interface VectorSpace<T : Any, S : Space<T>> : Space<Point<T>> {

    val size: Int

    val space: S

    fun produce(initializer: (Int) -> T): Point<T>

    /**
     * Produce a space-element of this vector space for expressions
     */
    fun produceElement(initializer: (Int) -> T): Vector<T, S>

    override val zero: Point<T> get() = produce { space.zero }

    override fun add(a: Point<T>, b: Point<T>): Point<T> = produce { with(space) { a[it] + b[it] } }

    override fun multiply(a: Point<T>, k: Double): Point<T> = produce { with(space) { a[it] * k } }

    //TODO add basis

    companion object {

        private val realSpaceCache = HashMap<Int, BufferVectorSpace<Double, RealField>>()

        /**
         * Non-boxing double vector space
         */
        fun real(size: Int): BufferVectorSpace<Double, RealField> {
            return realSpaceCache.getOrPut(size) { BufferVectorSpace(size, RealField, Buffer.DoubleBufferFactory) }
        }

        /**
         * A structured vector space with custom buffer
         */
        fun <T : Any, S : Space<T>> buffered(
            size: Int,
            space: S,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing
        ): VectorSpace<T, S> = BufferVectorSpace(size, space, bufferFactory)

        /**
         * Automatic buffered vector, unboxed if it is possible
         */
        inline fun <reified T : Any, S : Space<T>> smart(size: Int, space: S): VectorSpace<T, S> =
            buffered(size, space, Buffer.Companion::auto)
    }
}


/**
 * A point coupled to the linear space
 */
interface Vector<T : Any, S : Space<T>> : SpaceElement<Point<T>, Vector<T, S>, VectorSpace<T, S>>, Point<T> {
    override val size: Int get() = context.size

    override operator fun plus(b: Point<T>): Vector<T, S> = context.add(this, b).wrap()
    override operator fun minus(b: Point<T>): Vector<T, S> = context.add(this, context.multiply(b, -1.0)).wrap()
    override operator fun times(k: Number): Vector<T, S> = context.multiply(this, k.toDouble()).wrap()
    override operator fun div(k: Number): Vector<T, S> = context.multiply(this, 1.0 / k.toDouble()).wrap()

    companion object {
        /**
         * Create vector with custom field
         */
        fun <T : Any, S : Space<T>> generic(size: Int, field: S, initializer: (Int) -> T): Vector<T, S> =
            VectorSpace.buffered(size, field).produceElement(initializer)

        fun real(size: Int, initializer: (Int) -> Double): Vector<Double, RealField> =
            VectorSpace.real(size).produceElement(initializer)

        fun ofReal(vararg elements: Double): Vector<Double, RealField> =
            VectorSpace.real(elements.size).produceElement { elements[it] }

    }
}

data class BufferVectorSpace<T : Any, S : Space<T>>(
    override val size: Int,
    override val space: S,
    val bufferFactory: BufferFactory<T>
) : VectorSpace<T, S> {
    override fun produce(initializer: (Int) -> T) = bufferFactory(size, initializer)
    override fun produceElement(initializer: (Int) -> T): Vector<T, S> = BufferVector(this, produce(initializer))
}


data class BufferVector<T : Any, S : Space<T>>(override val context: VectorSpace<T, S>, val buffer: Buffer<T>) :
    Vector<T, S> {

    init {
        if (context.size != buffer.size) {
            error("Array dimension mismatch")
        }
    }

    override fun get(index: Int): T {
        return buffer[index]
    }

    override fun unwrap(): Point<T> = this

    override fun Point<T>.wrap(): Vector<T, S> = BufferVector(context, this)

    override fun iterator(): Iterator<T> = (0 until size).map { buffer[it] }.iterator()

    override fun toString(): String =
        this.asSequence().joinToString(prefix = "[", postfix = "]", separator = ", ") { it.toString() }
}

