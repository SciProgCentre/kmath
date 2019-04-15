package scientifik.kmath.linear

import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.SpaceElement
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.asSequence
import kotlin.jvm.JvmName

typealias Point<T> = Buffer<T>


fun <T : Any, S : Space<T>> BufferVectorSpace<T, S>.produceElement(initializer: (Int) -> T): Vector<T, S> =
    BufferVector(this, produce(initializer))

@JvmName("produceRealElement")
fun BufferVectorSpace<Double, RealField>.produceElement(initializer: (Int) -> Double): Vector<Double, RealField> =
    BufferVector(this, produce(initializer))

/**
 * A point coupled to the linear space
 */
@Deprecated("Use VectorContext instead")
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

@Deprecated("Use VectorContext instead")
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

