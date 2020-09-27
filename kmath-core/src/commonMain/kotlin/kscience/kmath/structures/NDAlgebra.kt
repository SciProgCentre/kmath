package kscience.kmath.structures

import kscience.kmath.operations.Complex
import kscience.kmath.operations.Field
import kscience.kmath.operations.Ring
import kscience.kmath.operations.Space
import kotlin.native.concurrent.ThreadLocal

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
public interface NDAlgebra<T, C, N : NDStructure<T>> {
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
    public fun produce(initializer: C.(IntArray) -> T): N

    /**
     * Maps elements from one structure to another one by applying [transform] to them.
     */
    public fun map(arg: N, transform: C.(T) -> T): N

    /**
     * Maps elements from one structure to another one by applying [transform] to them alongside with their indices.
     */
    public fun mapIndexed(arg: N, transform: C.(index: IntArray, T) -> T): N

    /**
     * Combines two structures into one.
     */
    public fun combine(a: N, b: N, transform: C.(T, T) -> T): N

    /**
     * Checks if given element is consistent with this context.
     *
     * @param element the structure to check.
     * @return the valid structure.
     */
    public fun check(element: N): N {
        if (!element.shape.contentEquals(shape)) throw ShapeMismatchException(shape, element.shape)
        return element
    }

    /**
     * Checks if given elements are consistent with this context.
     *
     * @param elements the structures to check.
     * @return the array of valid structures.
     */
    public fun check(vararg elements: N): Array<out N> = elements
        .map(NDStructure<T>::shape)
        .singleOrNull { !shape.contentEquals(it) }
        ?.let { throw ShapeMismatchException(shape, it) }
        ?: elements

    /**
     * Element-wise invocation of function working on [T] on a [NDStructure].
     */
    public operator fun Function1<T, T>.invoke(structure: N): N = map(structure) { value -> this@invoke(value) }

    public companion object
}

/**
 * Space of [NDStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param S the type of space of structure elements.
 */
public interface NDSpace<T, S : Space<T>, N : NDStructure<T>> : Space<N>, NDAlgebra<T, S, N> {
    /**
     * Element-wise addition.
     *
     * @param a the addend.
     * @param b the augend.
     * @return the sum.
     */
    public override fun add(a: N, b: N): N = combine(a, b) { aValue, bValue -> add(aValue, bValue) }

    /**
     * Element-wise multiplication by scalar.
     *
     * @param a the multiplicand.
     * @param k the multiplier.
     * @return the product.
     */
    public override fun multiply(a: N, k: Number): N = map(a) { multiply(it, k) }

    // TODO move to extensions after KEEP-176

    /**
     * Adds an ND structure to an element of it.
     *
     * @receiver the addend.
     * @param arg the augend.
     * @return the sum.
     */
    public operator fun N.plus(arg: T): N = map(this) { value -> add(arg, value) }

    /**
     * Subtracts an element from ND structure of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    public operator fun N.minus(arg: T): N = map(this) { value -> add(arg, -value) }

    /**
     * Adds an element to ND structure of it.
     *
     * @receiver the addend.
     * @param arg the augend.
     * @return the sum.
     */
    public operator fun T.plus(arg: N): N = map(arg) { value -> add(this@plus, value) }

    /**
     * Subtracts an ND structure from an element of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    public operator fun T.minus(arg: N): N = map(arg) { value -> add(-this@minus, value) }

    public companion object
}

/**
 * Ring of [NDStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param R the type of ring of structure elements.
 */
public interface NDRing<T, R : Ring<T>, N : NDStructure<T>> : Ring<N>, NDSpace<T, R, N> {
    /**
     * Element-wise multiplication.
     *
     * @param a the multiplicand.
     * @param b the multiplier.
     * @return the product.
     */
    public override fun multiply(a: N, b: N): N = combine(a, b) { aValue, bValue -> multiply(aValue, bValue) }

    //TODO move to extensions after KEEP-176

    /**
     * Multiplies an ND structure by an element of it.
     *
     * @receiver the multiplicand.
     * @param arg the multiplier.
     * @return the product.
     */
    public operator fun N.times(arg: T): N = map(this) { value -> multiply(arg, value) }

    /**
     * Multiplies an element by a ND structure of it.
     *
     * @receiver the multiplicand.
     * @param arg the multiplier.
     * @return the product.
     */
    public operator fun T.times(arg: N): N = map(arg) { value -> multiply(this@times, value) }

    public companion object
}

/**
 * Field of [NDStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param F the type field of structure elements.
 */
public interface NDField<T, F : Field<T>, N : NDStructure<T>> : Field<N>, NDRing<T, F, N> {
    /**
     * Element-wise division.
     *
     * @param a the dividend.
     * @param b the divisor.
     * @return the quotient.
     */
    public override fun divide(a: N, b: N): N = combine(a, b) { aValue, bValue -> divide(aValue, bValue) }

    //TODO move to extensions after KEEP-176
    /**
     * Divides an ND structure by an element of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    public operator fun N.div(arg: T): N = map(this) { value -> divide(arg, value) }

    /**
     * Divides an element by an ND structure of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    public operator fun T.div(arg: N): N = map(arg) { divide(it, this@div) }

    @ThreadLocal
    public companion object {
        private val realNDFieldCache: MutableMap<IntArray, RealNDField> = hashMapOf()

        /**
         * Create a nd-field for [Double] values or pull it from cache if it was created previously.
         */
        public fun real(vararg shape: Int): RealNDField = realNDFieldCache.getOrPut(shape) { RealNDField(shape) }

        /**
         * Create an ND field with boxing generic buffer.
         */
        public fun <T : Any, F : Field<T>> boxing(
            field: F,
            vararg shape: Int,
            bufferFactory: BufferFactory<T> = Buffer.Companion::boxing
        ): BoxingNDField<T, F> = BoxingNDField(shape, field, bufferFactory)

        /**
         * Create a most suitable implementation for nd-field using reified class.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <reified T : Any, F : Field<T>> auto(field: F, vararg shape: Int): BufferedNDField<T, F> =
            when {
                T::class == Double::class -> real(*shape) as BufferedNDField<T, F>
                T::class == Complex::class -> complex(*shape) as BufferedNDField<T, F>
                else -> BoxingNDField(shape, field, Buffer.Companion::auto)
            }
    }
}
