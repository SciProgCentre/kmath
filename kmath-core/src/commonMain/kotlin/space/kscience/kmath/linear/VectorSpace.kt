package space.kscience.kmath.linear

import space.kscience.kmath.operations.RealField
import space.kscience.kmath.operations.Space
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory

/**
 * A linear space for vectors.
 * Could be used on any point-like structure
 */
public interface VectorSpace<T : Any, S : Space<T>> : Space<Point<T>> {
    public val size: Int
    public val space: S
    override val zero: Point<T> get() = produce { space.zero }

    public fun produce(initializer: S.(Int) -> T): Point<T>

    /**
     * Produce a space-element of this vector space for expressions
     */
    //fun produceElement(initializer: (Int) -> T): Vector<T, S>

    override fun add(a: Point<T>, b: Point<T>): Point<T> = produce { space { a[it] + b[it] } }

    override fun multiply(a: Point<T>, k: Number): Point<T> = produce { space { a[it] * k } }

    //TODO add basis

    public companion object {
        private val realSpaceCache: MutableMap<Int, BufferVectorSpace<Double, RealField>> = hashMapOf()

        /**
         * Non-boxing double vector space
         */
        public fun real(size: Int): BufferVectorSpace<Double, RealField> = realSpaceCache.getOrPut(size) {
            BufferVectorSpace(
                size,
                RealField,
                Buffer.Companion::auto
            )
        }

        /**
         * A structured vector space with custom buffer
         */
        public fun <T : Any, S : Space<T>> buffered(
            size: Int,
            space: S,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing,
        ): BufferVectorSpace<T, S> = BufferVectorSpace(size, space, bufferFactory)

        /**
         * Automatic buffered vector, unboxed if it is possible
         */
        public inline fun <reified T : Any, S : Space<T>> auto(size: Int, space: S): VectorSpace<T, S> =
            buffered(size, space, Buffer.Companion::auto)
    }
}


public class BufferVectorSpace<T : Any, S : Space<T>>(
    override val size: Int,
    override val space: S,
    public val bufferFactory: BufferFactory<T>,
) : VectorSpace<T, S> {
    override fun produce(initializer: S.(Int) -> T): Buffer<T> = bufferFactory(size) { space.initializer(it) }
    //override fun produceElement(initializer: (Int) -> T): Vector<T, S> = BufferVector(this, produce(initializer))
}
