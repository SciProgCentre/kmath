package space.kscience.kmath.nd

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.*
import kotlin.reflect.KClass

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
public interface AlgebraND<T, C : Algebra<T>> {
    /**
     * The shape of ND-structures this algebra operates on.
     */
    public val shape: IntArray

    /**
     * The algebra over elements of ND structure.
     */
    public val elementContext: C

    /**
     * Produces a new NDStructure using given initializer function.
     */
    public fun produce(initializer: C.(IntArray) -> T): StructureND<T>

    /**
     * Maps elements from one structure to another one by applying [transform] to them.
     */
    public fun StructureND<T>.map(transform: C.(T) -> T): StructureND<T>

    /**
     * Maps elements from one structure to another one by applying [transform] to them alongside with their indices.
     */
    public fun StructureND<T>.mapIndexed(transform: C.(index: IntArray, T) -> T): StructureND<T>

    /**
     * Combines two structures into one.
     */
    public fun combine(a: StructureND<T>, b: StructureND<T>, transform: C.(T, T) -> T): StructureND<T>

    /**
     * Element-wise invocation of function working on [T] on a [StructureND].
     */
    public operator fun Function1<T, T>.invoke(structure: StructureND<T>): StructureND<T> =
        structure.map { value -> this@invoke(value) }

    /**
     * Get a feature of the structure in this scope. Structure features take precedence other context features
     *
     * @param F the type of feature.
     * @param structure the structure.
     * @param type the [KClass] instance of [F].
     * @return a feature object or `null` if it isn't present.
     */
    @UnstableKMathAPI
    public fun <F : StructureFeature> getFeature(structure: StructureND<T>, type: KClass<out F>): F? =
        structure.getFeature(type)

    public companion object
}


/**
 * Get a feature of the structure in this scope. Structure features take precedence other context features
 *
 * @param T the type of items in the matrices.
 * @param F the type of feature.
 * @return a feature object or `null` if it isn't present.
 */
@UnstableKMathAPI
public inline fun <T : Any, reified F : StructureFeature> AlgebraND<T, *>.getFeature(structure: StructureND<T>): F? =
    getFeature(structure, F::class)

/**
 * Checks if given elements are consistent with this context.
 *
 * @param structures the structures to check.
 * @return the array of valid structures.
 */
internal fun <T, C : Algebra<T>> AlgebraND<T, C>.checkShape(vararg structures: StructureND<T>): Array<out StructureND<T>> =
    structures
        .map(StructureND<T>::shape)
        .singleOrNull { !shape.contentEquals(it) }
        ?.let<IntArray, Array<out StructureND<T>>> { throw ShapeMismatchException(shape, it) }
        ?: structures

/**
 * Checks if given element is consistent with this context.
 *
 * @param element the structure to check.
 * @return the valid structure.
 */
internal fun <T, C : Algebra<T>> AlgebraND<T, C>.checkShape(element: StructureND<T>): StructureND<T> {
    if (!element.shape.contentEquals(shape)) throw ShapeMismatchException(shape, element.shape)
    return element
}

/**
 * Space of [StructureND].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param S the type of space of structure elements.
 */
public interface GroupND<T, S : Group<T>> : Group<StructureND<T>>, AlgebraND<T, S> {
    /**
     * Element-wise addition.
     *
     * @param a the augend.
     * @param b the addend.
     * @return the sum.
     */
    public override fun add(a: StructureND<T>, b: StructureND<T>): StructureND<T> =
        combine(a, b) { aValue, bValue -> add(aValue, bValue) }

//    /**
//     * Element-wise multiplication by scalar.
//     *
//     * @param a the multiplicand.
//     * @param k the multiplier.
//     * @return the product.
//     */
//    public override fun multiply(a: NDStructure<T>, k: Number): NDStructure<T> =  a.map { multiply(it, k) }

    // TODO move to extensions after KEEP-176

    /**
     * Adds an ND structure to an element of it.
     *
     * @receiver the augend.
     * @param arg the addend.
     * @return the sum.
     */
    public operator fun StructureND<T>.plus(arg: T): StructureND<T> = this.map { value -> add(arg, value) }

    /**
     * Subtracts an element from ND structure of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    public operator fun StructureND<T>.minus(arg: T): StructureND<T> = this.map { value -> add(arg, -value) }

    /**
     * Adds an element to ND structure of it.
     *
     * @receiver the augend.
     * @param arg the addend.
     * @return the sum.
     */
    public operator fun T.plus(arg: StructureND<T>): StructureND<T> = arg.map { value -> add(this@plus, value) }

    /**
     * Subtracts an ND structure from an element of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    public operator fun T.minus(arg: StructureND<T>): StructureND<T> = arg.map { value -> add(-this@minus, value) }

    public companion object
}

/**
 * Ring of [StructureND].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param R the type of ring of structure elements.
 */
public interface RingND<T, R : Ring<T>> : Ring<StructureND<T>>, GroupND<T, R> {
    /**
     * Element-wise multiplication.
     *
     * @param a the multiplicand.
     * @param b the multiplier.
     * @return the product.
     */
    public override fun multiply(a: StructureND<T>, b: StructureND<T>): StructureND<T> =
        combine(a, b) { aValue, bValue -> multiply(aValue, bValue) }

    //TODO move to extensions after KEEP-176

    /**
     * Multiplies an ND structure by an element of it.
     *
     * @receiver the multiplicand.
     * @param arg the multiplier.
     * @return the product.
     */
    public operator fun StructureND<T>.times(arg: T): StructureND<T> = this.map { value -> multiply(arg, value) }

    /**
     * Multiplies an element by a ND structure of it.
     *
     * @receiver the multiplicand.
     * @param arg the multiplier.
     * @return the product.
     */
    public operator fun T.times(arg: StructureND<T>): StructureND<T> = arg.map { value -> multiply(this@times, value) }

    public companion object
}

/**
 * Field of [StructureND].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param F the type field of structure elements.
 */
public interface FieldND<T, F : Field<T>> : Field<StructureND<T>>, RingND<T, F>, ScaleOperations<StructureND<T>> {
    /**
     * Element-wise division.
     *
     * @param a the dividend.
     * @param b the divisor.
     * @return the quotient.
     */
    public override fun divide(a: StructureND<T>, b: StructureND<T>): StructureND<T> =
        combine(a, b) { aValue, bValue -> divide(aValue, bValue) }

    //TODO move to extensions after KEEP-176
    /**
     * Divides an ND structure by an element of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    public operator fun StructureND<T>.div(arg: T): StructureND<T> = this.map { value -> divide(arg, value) }

    /**
     * Divides an element by an ND structure of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    public operator fun T.div(arg: StructureND<T>): StructureND<T> = arg.map { divide(it, this@div) }

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
