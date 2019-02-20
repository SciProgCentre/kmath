package scientifik.kmath.structures

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Ring
import scientifik.kmath.operations.Space
import kotlin.jvm.JvmName


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
    operator fun Function1<T, T>.invoke(structure: N) = map(structure) { value -> this@invoke(value) }
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

    operator fun N.plus(arg: T) = map(this) { value -> add(arg, value) }
    operator fun N.minus(arg: T) = map(this) { value -> add(arg, -value) }

    operator fun T.plus(arg: N) = map(arg) { value -> add(this@plus, value) }
    operator fun T.minus(arg: N) = map(arg) { value -> add(-this@minus, value) }
}

/**
 * An nd-ring over element ring
 */
interface NDRing<T, R : Ring<T>, N : NDStructure<T>> : Ring<N>, NDSpace<T, R, N> {

    /**
     * Element-by-element multiplication
     */
    override fun multiply(a: N, b: N): N = combine(a, b) { aValue, bValue -> multiply(aValue, bValue) }

    operator fun N.times(arg: T) = map(this) { value -> multiply(arg, value) }
    operator fun T.times(arg: N) = map(arg) { value -> multiply(this@times, value) }
}

/**
 * Field for n-dimensional structures.
 * @param shape - the list of dimensions of the array
 * @param elementField - operations field defined on individual array element
 * @param T - the type of the element contained in ND structure
 * @param F - field of structure elements
 * @param R - actual nd-element type of this field
 */
interface NDField<T, F : Field<T>, N : NDStructure<T>> : Field<N>, NDRing<T, F, N> {

    /**
     * Element-by-element division
     */
    override fun divide(a: N, b: N): N = combine(a, b) { aValue, bValue -> divide(aValue, bValue) }

    operator fun N.div(arg: T) = map(this) { value -> divide(arg, value) }
    operator fun T.div(arg: N) = map(arg) { divide(it, this@div) }

    companion object {

        private val realNDFieldCache = HashMap<IntArray, RealNDField>()

        /**
         * Create a nd-field for [Double] values or pull it from cache if it was created previously
         */
        fun real(vararg shape: Int) = realNDFieldCache.getOrPut(shape) { RealNDField(shape) }

        /**
         * Create a nd-field with boxing generic buffer
         */
        fun <T : Any, F : Field<T>> buffered(
            shape: IntArray,
            field: F,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing
        ) =
            BoxingNDField(shape, field, bufferFactory)

        /**
         * Create a most suitable implementation for nd-field using reified class.
         */
        @Suppress("UNCHECKED_CAST")
        inline fun <reified T : Any, F : Field<T>> auto(field: F, vararg shape: Int): BufferedNDField<T, F> =
            when {
                T::class == Double::class -> real(*shape) as BufferedNDField<T, F>
                else -> BoxingNDField(shape, field, Buffer.Companion::auto)
            }
    }
}
