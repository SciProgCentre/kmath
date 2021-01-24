package kscience.kmath.nd

import kscience.kmath.operations.Field
import kscience.kmath.operations.Ring
import kscience.kmath.operations.Space
import kscience.kmath.structures.*

/**
 * An exception is thrown when the expected ans actual shape of NDArray differs.
 *
 * @property expected the expected shape.
 * @property actual the actual shape.
 */
public class ShapeMismatchException(public val expected: IntArray, public val actual: IntArray) :
    RuntimeException("Shape ${actual.contentToString()} doesn't fit in expected shape ${expected.contentToString()}.")

/**
 * The base interface for all ND-algebra implementations.
 *
 * @param T the type of ND-structure element.
 * @param C the type of the element context.
 * @param N the type of the structure.
 */
public interface NDAlgebra<T, C> {
    /**
     * The shape of ND-structures this algebra operates on.
     */
    public val shape: IntArray

    /**
     * The algebra over elements of ND structure.
     */
    public val elementContext: C

    /**
     * Produces a new [N] structure using given initializer function.
     */
    public fun produce(initializer: C.(IntArray) -> T): NDStructure<T>

    /**
     * Maps elements from one structure to another one by applying [transform] to them.
     */
    public fun NDStructure<T>.map(transform: C.(T) -> T): NDStructure<T>

    /**
     * Maps elements from one structure to another one by applying [transform] to them alongside with their indices.
     */
    public fun NDStructure<T>.mapIndexed(transform: C.(index: IntArray, T) -> T): NDStructure<T>

    /**
     * Combines two structures into one.
     */
    public fun combine(a: NDStructure<T>, b: NDStructure<T>, transform: C.(T, T) -> T): NDStructure<T>

    /**
     * Element-wise invocation of function working on [T] on a [NDStructure].
     */
    public operator fun Function1<T, T>.invoke(structure: NDStructure<T>): NDStructure<T> =
        structure.map() { value -> this@invoke(value) }

    public companion object
}

/**
 * Checks if given elements are consistent with this context.
 *
 * @param structures the structures to check.
 * @return the array of valid structures.
 */
internal fun <T, C> NDAlgebra<T, C>.checkShape(vararg structures: NDStructure<T>): Array<out NDStructure<T>> = structures
    .map(NDStructure<T>::shape)
    .singleOrNull { !shape.contentEquals(it) }
    ?.let<IntArray, Array<out NDStructure<T>>> { throw ShapeMismatchException(shape, it) }
    ?: structures

/**
 * Checks if given element is consistent with this context.
 *
 * @param element the structure to check.
 * @return the valid structure.
 */
internal fun <T, C> NDAlgebra<T, C>.checkShape(element: NDStructure<T>): NDStructure<T> {
    if (!element.shape.contentEquals(shape)) throw ShapeMismatchException(shape, element.shape)
    return element
}

/**
 * Space of [NDStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param S the type of space of structure elements.
 */
public interface NDSpace<T, S : Space<T>> : Space<NDStructure<T>>, NDAlgebra<T, S> {
    /**
     * Element-wise addition.
     *
     * @param a the addend.
     * @param b the augend.
     * @return the sum.
     */
    public override fun add(a: NDStructure<T>, b: NDStructure<T>): NDStructure<T> =
        combine(a, b) { aValue, bValue -> add(aValue, bValue) }

    /**
     * Element-wise multiplication by scalar.
     *
     * @param a the multiplicand.
     * @param k the multiplier.
     * @return the product.
     */
    public override fun multiply(a: NDStructure<T>, k: Number): NDStructure<T> = a.map() { multiply(it, k) }

    // TODO move to extensions after KEEP-176

    /**
     * Adds an ND structure to an element of it.
     *
     * @receiver the addend.
     * @param arg the augend.
     * @return the sum.
     */
    public operator fun NDStructure<T>.plus(arg: T): NDStructure<T> = this.map() { value -> add(arg, value) }

    /**
     * Subtracts an element from ND structure of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    public operator fun NDStructure<T>.minus(arg: T): NDStructure<T> = this.map() { value -> add(arg, -value) }

    /**
     * Adds an element to ND structure of it.
     *
     * @receiver the addend.
     * @param arg the augend.
     * @return the sum.
     */
    public operator fun T.plus(arg: NDStructure<T>): NDStructure<T> = arg.map() { value -> add(this@plus, value) }

    /**
     * Subtracts an ND structure from an element of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    public operator fun T.minus(arg: NDStructure<T>): NDStructure<T> = arg.map() { value -> add(-this@minus, value) }

    public companion object
}

/**
 * Ring of [NDStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param R the type of ring of structure elements.
 */
public interface NDRing<T, R : Ring<T>> : Ring<NDStructure<T>>, NDSpace<T, R> {
    /**
     * Element-wise multiplication.
     *
     * @param a the multiplicand.
     * @param b the multiplier.
     * @return the product.
     */
    public override fun multiply(a: NDStructure<T>, b: NDStructure<T>): NDStructure<T> =
        combine(a, b) { aValue, bValue -> multiply(aValue, bValue) }

    //TODO move to extensions after KEEP-176

    /**
     * Multiplies an ND structure by an element of it.
     *
     * @receiver the multiplicand.
     * @param arg the multiplier.
     * @return the product.
     */
    public operator fun NDStructure<T>.times(arg: T): NDStructure<T> = this.map() { value -> multiply(arg, value) }

    /**
     * Multiplies an element by a ND structure of it.
     *
     * @receiver the multiplicand.
     * @param arg the multiplier.
     * @return the product.
     */
    public operator fun T.times(arg: NDStructure<T>): NDStructure<T> = arg.map() { value -> multiply(this@times, value) }

    public companion object
}

/**
 * Field of [NDStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param F the type field of structure elements.
 */
public interface NDField<T, F : Field<T>> : Field<NDStructure<T>>, NDRing<T, F> {
    /**
     * Element-wise division.
     *
     * @param a the dividend.
     * @param b the divisor.
     * @return the quotient.
     */
    public override fun divide(a: NDStructure<T>, b: NDStructure<T>): NDStructure<T> =
        combine(a, b) { aValue, bValue -> divide(aValue, bValue) }

    //TODO move to extensions after KEEP-176
    /**
     * Divides an ND structure by an element of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    public operator fun NDStructure<T>.div(arg: T): NDStructure<T> = this.map() { value -> divide(arg, value) }

    /**
     * Divides an element by an ND structure of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    public operator fun T.div(arg: NDStructure<T>): NDStructure<T> = arg.map() { divide(it, this@div) }

//    @ThreadLocal
//    public companion object {
//        private val realNDFieldCache: MutableMap<IntArray, RealNDField> = hashMapOf()
//
//        /**
//         * Create a nd-field for [Double] values or pull it from cache if it was created previously.
//         */
//        public fun real(vararg shape: Int): RealNDField = realNDFieldCache.getOrPut(shape) { RealNDField(shape) }
//
//        /**
//         * Create an ND field with boxing generic buffer.
//         */
//        public fun <T : Any, F : Field<T>> boxing(
//            field: F,
//            vararg shape: Int,
//            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing,
//        ): BufferedNDField<T, F> = BufferedNDField(shape, field, bufferFactory)
//
//        /**
//         * Create a most suitable implementation for nd-field using reified class.
//         */
//        @Suppress("UNCHECKED_CAST")
//        public inline fun <reified T : Any, F : Field<T>> auto(field: F, vararg shape: Int): NDField<T, F> =
//            when {
//                T::class == Double::class -> real(*shape) as NDField<T, F>
//                T::class == Complex::class -> complex(*shape) as BufferedNDField<T, F>
//                else -> BoxingNDField(shape, field, Buffer.Companion::auto)
//            }
//    }
}
