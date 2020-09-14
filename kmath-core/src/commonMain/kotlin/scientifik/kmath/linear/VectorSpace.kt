package scientifik.kmath.linear

import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.Space
import scientifik.kmath.operations.invoke
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.BufferFactory

/**
 * A linear space for vectors.
 * Could be used on any point-like structure
 */
interface VectorSpace<T : Any, S : Space<T>> : Space<Point<T>> {
    val size: Int
    val space: S
    override val zero: Point<T> get() = produce { space.zero }

    fun produce(initializer: (Int) -> T): Point<T>

    /**
     * Produce a space-element of this vector space for expressions
     */
    //fun produceElement(initializer: (Int) -> T): Vector<T, S>

    override fun add(a: Point<T>, b: Point<T>): Point<T> = produce { space { a[it] + b[it] } }

    override fun multiply(a: Point<T>, k: Number): Point<T> = produce { space { a[it] * k } }

    //TODO add basis

    companion object {
        private val realSpaceCache: MutableMap<Int, BufferVectorSpace<Double, RealField>> = hashMapOf()

        /**
         * Non-boxing double vector space
         */
        fun real(size: Int): BufferVectorSpace<Double, RealField> = realSpaceCache.getOrPut(size) {
            BufferVectorSpace(
                size,
                RealField,
                Buffer.Companion::auto
            )
        }

        /**
         * A structured vector space with custom buffer
         */
        fun <T : Any, S : Space<T>> buffered(
            size: Int,
            space: S,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing
        ): BufferVectorSpace<T, S> = BufferVectorSpace(size, space, bufferFactory)

        /**
         * Automatic buffered vector, unboxed if it is possible
         */
        inline fun <reified T : Any, S : Space<T>> auto(size: Int, space: S): VectorSpace<T, S> =
            buffered(size, space, Buffer.Companion::auto)
    }
}


class BufferVectorSpace<T : Any, S : Space<T>>(
    override val size: Int,
    override val space: S,
    val bufferFactory: BufferFactory<T>
) : VectorSpace<T, S> {
    override fun produce(initializer: (Int) -> T): Buffer<T> = bufferFactory(size, initializer)
    //override fun produceElement(initializer: (Int) -> T): Vector<T, S> = BufferVector(this, produce(initializer))
}
