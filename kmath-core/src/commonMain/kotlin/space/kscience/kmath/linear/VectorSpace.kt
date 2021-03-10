package space.kscience.kmath.linear

import space.kscience.kmath.operations.Group
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.BufferFactory

/**
 * A linear space for vectors.
 * Could be used on any point-like structure
 */
public interface VectorSpace<T : Any, A : Ring<T>> : Group<Point<T>> {
    public val size: Int
    public val algebra: A
    override val zero: Point<T> get() = produce { algebra.zero }

    public fun produce(initializer: A.(Int) -> T): Point<T>

    override fun add(a: Point<T>, b: Point<T>): Point<T> = produce { algebra { a[it] + b[it] } }

    public fun scale(a: Point<T>, scale: T): Point<T> = produce { algebra { a[it] * scale } }

    override fun Point<T>.unaryMinus(): Point<T> = produce { -get(it) }

    //TODO add basis

    public companion object {
        private val realSpaceCache: MutableMap<Int, BufferVectorGroup<Double, RealField>> = hashMapOf()

        /**
         * Non-boxing double vector space
         */
        public fun real(size: Int): BufferVectorGroup<Double, RealField> = realSpaceCache.getOrPut(size) {
            BufferVectorGroup(
                size,
                RealField,
                Buffer.Companion::auto
            )
        }

        /**
         * A structured vector space with custom buffer
         */
        public fun <T : Any, A : Ring<T>> buffered(
            size: Int,
            space: A,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing,
        ): BufferVectorGroup<T, A> = BufferVectorGroup(size, space, bufferFactory)

        /**
         * Automatic buffered vector, unboxed if it is possible
         */
        public inline fun <reified T : Any, A : Ring<T>> auto(size: Int, space: A): VectorSpace<T, A> =
            buffered(size, space, Buffer.Companion::auto)
    }
}


public class BufferVectorGroup<T : Any, A : Ring<T>>(
    override val size: Int,
    override val algebra: A,
    public val bufferFactory: BufferFactory<T>,
) : VectorSpace<T, A> {
    override fun produce(initializer: A.(Int) -> T): Buffer<T> = bufferFactory(size) { algebra.initializer(it) }
}
