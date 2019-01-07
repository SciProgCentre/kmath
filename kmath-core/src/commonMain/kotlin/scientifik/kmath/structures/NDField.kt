package scientifik.kmath.structures

import scientifik.kmath.operations.AbstractField
import scientifik.kmath.operations.Field
import scientifik.kmath.structures.Buffer.Companion.boxing

/**
 * An exception is thrown when the expected ans actual shape of NDArray differs
 */
class ShapeMismatchException(val expected: IntArray, val actual: IntArray) : RuntimeException()

/**
 * Field for n-dimensional arrays.
 * @param shape - the list of dimensions of the array
 * @param elementField - operations field defined on individual array element
 * @param T - the type of the element contained in ND structure
 * @param F - field over structure elements
 * @param R - actual nd-element type of this field
 */
interface NDField<T, F : Field<T>, N : NDStructure<T>> : Field<N> {

    val shape: IntArray
    val elementField: F

    fun produce(initializer: F.(IntArray) -> T): N
    fun map(arg: N, transform: F.(T) -> T): N
    fun mapIndexed(arg: N, transform: F.(index: IntArray, T) -> T): N
    fun combine(a: N, b: N, transform: F.(T, T) -> T): N

    companion object {
        /**
         * Create a nd-field for [Double] values
         */
        fun real(shape: IntArray) = RealNDField(shape)
        /**
         * Create a nd-field with boxing generic buffer
         */
        fun <T : Any, F : Field<T>> buffered(shape: IntArray, field: F) =
            BufferNDField(shape, field, Buffer.Companion::boxing)

        /**
         * Create a most suitable implementation for nd-field using reified class.
         */
        @Suppress("UNCHECKED_CAST")
        inline fun <reified T : Any, F : Field<T>> auto(shape: IntArray, field: F): StridedNDField<T, F> =
            when {
                T::class == Double::class -> real(shape) as StridedNDField<T, F>
                else -> BufferNDField(shape, field, Buffer.Companion::auto)
            }
    }
}


abstract class AbstractNDField<T, F : Field<T>, N : NDStructure<T>>(
    override val shape: IntArray,
    override val elementField: F
) : AbstractField<N>(), NDField<T, F, N> {
    override val zero: N  by lazy { produce { zero } }

    override val one: N by lazy { produce { one } }

    operator fun Function1<T, T>.invoke(structure: N) = map(structure) { value -> this@invoke(value) }
    operator fun N.plus(arg: T) = map(this) { value -> elementField.run { arg + value } }
    operator fun N.minus(arg: T) = map(this) { value -> elementField.run { arg - value } }
    operator fun N.times(arg: T) = map(this) { value -> elementField.run { arg * value } }
    operator fun N.div(arg: T) = map(this) { value -> elementField.run { arg / value } }

    operator fun T.plus(arg: N) = arg + this
    operator fun T.minus(arg: N) = arg - this
    operator fun T.times(arg: N) = arg * this
    operator fun T.div(arg: N) = arg / this


    /**
     * Element-by-element addition
     */
    override fun add(a: N, b: N): N =
        combine(a, b) { aValue, bValue -> aValue + bValue }

    /**
     * Multiply all elements by cinstant
     */
    override fun multiply(a: N, k: Double): N =
        map(a) { it * k }


    /**
     * Element-by-element multiplication
     */
    override fun multiply(a: N, b: N): N =
        combine(a, b) { aValue, bValue -> aValue * bValue }

    /**
     * Element-by-element division
     */
    override fun divide(a: N, b: N): N =
        combine(a, b) { aValue, bValue -> aValue / bValue }

    /**
     * Check if given objects are compatible with this context. Throw exception if they are not
     */
    open fun check(vararg elements: N) {
        elements.forEach {
            if (!shape.contentEquals(it.shape)) {
                throw ShapeMismatchException(shape, it.shape)
            }
        }
    }
}