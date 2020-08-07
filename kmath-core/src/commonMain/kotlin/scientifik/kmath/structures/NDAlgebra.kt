package scientifik.kmath.structures

import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.Space


/**
 * An exception is thrown when the expected ans actual shape of NDArray differs
 */
class ShapeMismatchException(val expected: IntArray, val actual: IntArray) : RuntimeException()


/**
 * The base interface for all nd-algebra implementations
 * @param T the type of nd-structure element
 * @param C the type of the element context
 * @param N the type of the structure
 */
interface NDAlgebra<T, C, N : NDStructure<T>> {
    val shape: IntArray
    val elementContext: C

    /**
     * Produce a new [N] structure using given initializer function
     */
    fun produce(initializer: C.(IntArray) -> T): N

    /**
     * Map elements from one structure to another one
     */
    fun map(arg: N, transform: C.(T) -> T): N

    /**
     * Map indexed elements
     */
    fun mapIndexed(arg: N, transform: C.(index: IntArray, T) -> T): N

    /**
     * Combine two structures into one
     */
    fun combine(a: N, b: N, transform: C.(T, T) -> T): N

    /**
     * Check if given elements are consistent with this context
     */
    fun check(vararg elements: N) {
        elements.forEach {
            if (!shape.contentEquals(it.shape)) {
                throw ShapeMismatchException(shape, it.shape)
            }
        }
    }

    /**
     * element-by-element invoke a function working on [T] on a [NDStructure]
     */
    operator fun Function1<T, T>.invoke(structure: N): N = map(structure) { value -> this@invoke(value) }

    companion object
}

/**
 * An nd-space over element space
 */
interface NDSpace<T, S : Space<T>, N : NDStructure<T>> : Space<N>, NDAlgebra<T, S, N> {
    /**
     * Element-by-element addition
     */
    override fun add(a: N, b: N): N = combine(a, b) { aValue, bValue -> add(aValue, bValue) }

    /**
     * Multiply all elements by constant
     */
    override fun multiply(a: N, k: Number): N = map(a) { multiply(it, k) }

    //TODO move to extensions after KEEP-176
    operator fun N.plus(arg: T): N = map(this) { value -> add(arg, value) }

    operator fun N.minus(arg: T): N = map(this) { value -> add(arg, -value) }

    operator fun T.plus(arg: N): N = map(arg) { value -> add(this@plus, value) }
    operator fun T.minus(arg: N): N = map(arg) { value -> add(-this@minus, value) }

    companion object
}

/**
 * An nd-ring over element ring
 */
interface NDRing<T, R : Ring<T>, N : NDStructure<T>> : Ring<N>, NDSpace<T, R, N> {

    /**
     * Element-by-element multiplication
     */
    override fun multiply(a: N, b: N): N = combine(a, b) { aValue, bValue -> multiply(aValue, bValue) }

    //TODO move to extensions after KEEP-176
    operator fun N.times(arg: T): N = map(this) { value -> multiply(arg, value) }

    operator fun T.times(arg: N): N = map(arg) { value -> multiply(this@times, value) }

    companion object
}

/**
 * Field of [NDStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param F field of structure elements.
 */
interface NDField<T, F : Field<T>, N : NDStructure<T>> : Field<N>, NDRing<T, F, N> {

    /**
     * Element-by-element division
     */
    override fun divide(a: N, b: N): N = combine(a, b) { aValue, bValue -> divide(aValue, bValue) }

    //TODO move to extensions after KEEP-176
    operator fun N.div(arg: T): N = map(this) { value -> divide(arg, value) }

    operator fun T.div(arg: N): N = map(arg) { divide(it, this@div) }

    companion object {

        private val realNDFieldCache = HashMap<IntArray, RealNDField>()

        /**
         * Create a nd-field for [Double] values or pull it from cache if it was created previously
         */
        fun real(vararg shape: Int): RealNDField = realNDFieldCache.getOrPut(shape) { RealNDField(shape) }

        /**
         * Create a nd-field with boxing generic buffer
         */
        fun <T : Any, F : Field<T>> boxing(
            field: F,
            vararg shape: Int,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing
        ): BoxingNDField<T, F> = BoxingNDField(shape, field, bufferFactory)

        /**
         * Create a most suitable implementation for nd-field using reified class.
         */
        @Suppress("UNCHECKED_CAST")
        inline fun <reified T : Any, F : Field<T>> auto(field: F, vararg shape: Int): BufferedNDField<T, F> =
            when {
                T::class == Double::class -> real(*shape) as BufferedNDField<T, F>
                T::class == Complex::class -> complex(*shape) as BufferedNDField<T, F>
                else -> BoxingNDField(shape, field, Buffer.Companion::auto)
            }
    }
}
