/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*
import kotlin.reflect.KClass

/**
 * An exception is thrown when the expected and actual shape of NDArray differ.
 *
 * @property expected the expected shape.
 * @property actual the actual shape.
 */
public class ShapeMismatchException(public val expected: IntArray, public val actual: IntArray) :
    RuntimeException("Shape ${actual.contentToString()} doesn't fit in expected shape ${expected.contentToString()}.")

public typealias Shape = IntArray

public fun Shape(shapeFirst: Int, vararg shapeRest: Int): Shape = intArrayOf(shapeFirst, *shapeRest)

public interface WithShape {
    public val shape: Shape

    public val indices: ShapeIndexer get() = DefaultStrides(shape)
}

/**
 * The base interface for all ND-algebra implementations.
 *
 * @param T the type of ND-structure element.
 * @param C the type of the element context.
 */
public interface AlgebraND<T, out C : Algebra<T>>: Algebra<StructureND<T>> {
    /**
     * The algebra over elements of ND structure.
     */
    public val elementAlgebra: C

    /**
     * Produces a new [StructureND] using given initializer function.
     */
    public fun structureND(shape: Shape, initializer: C.(IntArray) -> T): StructureND<T>

    /**
     * Maps elements from one structure to another one by applying [transform] to them.
     */
    @PerformancePitfall("Very slow on remote execution algebras")
    public fun StructureND<T>.map(transform: C.(T) -> T): StructureND<T> = structureND(shape) { index ->
        elementAlgebra.transform(get(index))
    }

    /**
     * Maps elements from one structure to another one by applying [transform] to them alongside with their indices.
     */
    @PerformancePitfall("Very slow on remote execution algebras")
    public fun StructureND<T>.mapIndexed(transform: C.(index: IntArray, T) -> T): StructureND<T> =
        structureND(shape) { index ->
            elementAlgebra.transform(index, get(index))
        }

    /**
     * Combines two structures into one.
     */
    @PerformancePitfall("Very slow on remote execution algebras")
    public fun zip(left: StructureND<T>, right: StructureND<T>, transform: C.(T, T) -> T): StructureND<T> {
        require(left.shape.contentEquals(right.shape)) {
            "Expected left and right of the same shape, but left - ${left.shape} and right - ${right.shape}"
        }
        return structureND(left.shape) { index ->
            elementAlgebra.transform(left[index], right[index])
        }
    }

    /**
     * Element-wise invocation of function working on [T] on a [StructureND].
     */
    @PerformancePitfall
    public operator fun Function1<T, T>.invoke(structure: StructureND<T>): StructureND<T> =
        structure.map { value -> this@invoke(value) }

    /**
     * Get a feature of the structure in this scope. Structure features take precedence other context features.
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
 * Get a feature of the structure in this scope. Structure features take precedence other context features.
 *
 * @param T the type of items in the matrices.
 * @param F the type of feature.
 * @return a feature object or `null` if it isn't present.
 */
@UnstableKMathAPI
public inline fun <T : Any, reified F : StructureFeature> AlgebraND<T, *>.getFeature(structure: StructureND<T>): F? =
    getFeature(structure, F::class)

/**
 * Space of [StructureND].
 *
 * @param T the type of the element contained in ND structure.
 * @param A the type of group over structure elements.
 */
public interface GroupOpsND<T, out A : GroupOps<T>> : GroupOps<StructureND<T>>, AlgebraND<T, A> {
    /**
     * Element-wise addition.
     *
     * @param left the augend.
     * @param right the addend.
     * @return the sum.
     */
    @OptIn(PerformancePitfall::class)
    override fun add(left: StructureND<T>, right: StructureND<T>): StructureND<T> =
        zip(left, right) { aValue, bValue -> add(aValue, bValue) }

    // TODO move to extensions after KEEP-176

    /**
     * Adds an ND structure to an element of it.
     *
     * @receiver the augend.
     * @param arg the addend.
     * @return the sum.
     */
    @OptIn(PerformancePitfall::class)
    public operator fun StructureND<T>.plus(arg: T): StructureND<T> = this.map { value -> add(arg, value) }

    /**
     * Subtracts an element from ND structure of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    @OptIn(PerformancePitfall::class)
    public operator fun StructureND<T>.minus(arg: T): StructureND<T> = this.map { value -> add(arg, -value) }

    /**
     * Adds an element to ND structure of it.
     *
     * @receiver the augend.
     * @param arg the addend.
     * @return the sum.
     */
    @OptIn(PerformancePitfall::class)
    public operator fun T.plus(arg: StructureND<T>): StructureND<T> = arg.map { value -> add(this@plus, value) }

    /**
     * Subtracts an ND structure from an element of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    @OptIn(PerformancePitfall::class)
    public operator fun T.minus(arg: StructureND<T>): StructureND<T> = arg.map { value -> add(-this@minus, value) }

    public companion object
}

public interface GroupND<T, out A : Group<T>> : Group<StructureND<T>>, GroupOpsND<T, A>, WithShape {
    override val zero: StructureND<T> get() = structureND(shape) { elementAlgebra.zero }
}

/**
 * Ring of [StructureND].
 *
 * @param T the type of the element contained in ND structure.
 * @param A the type of ring over structure elements.
 */
public interface RingOpsND<T, out A : RingOps<T>> : RingOps<StructureND<T>>, GroupOpsND<T, A> {
    /**
     * Element-wise multiplication.
     *
     * @param left the multiplicand.
     * @param right the multiplier.
     * @return the product.
     */
    @OptIn(PerformancePitfall::class)
    override fun multiply(left: StructureND<T>, right: StructureND<T>): StructureND<T> =
        zip(left, right) { aValue, bValue -> multiply(aValue, bValue) }

    //TODO move to extensions with context receivers

    /**
     * Multiplies an ND structure by an element of it.
     *
     * @receiver the multiplicand.
     * @param arg the multiplier.
     * @return the product.
     */
    @OptIn(PerformancePitfall::class)
    public operator fun StructureND<T>.times(arg: T): StructureND<T> = this.map { value -> multiply(arg, value) }

    /**
     * Multiplies an element by a ND structure of it.
     *
     * @receiver the multiplicand.
     * @param arg the multiplier.
     * @return the product.
     */
    @OptIn(PerformancePitfall::class)
    public operator fun T.times(arg: StructureND<T>): StructureND<T> = arg.map { value -> multiply(this@times, value) }

    public companion object
}

public interface RingND<T, out A : Ring<T>> : Ring<StructureND<T>>, RingOpsND<T, A>, GroupND<T, A>, WithShape {
    override val one: StructureND<T> get() = structureND(shape) { elementAlgebra.one }
}


/**
 * Field of [StructureND].
 *
 * @param T the type of the element contained in ND structure.
 * @param A the type field over structure elements.
 */
public interface FieldOpsND<T, out A : Field<T>> :
    FieldOps<StructureND<T>>,
    RingOpsND<T, A>,
    ScaleOperations<StructureND<T>> {
    /**
     * Element-wise division.
     *
     * @param left the dividend.
     * @param right the divisor.
     * @return the quotient.
     */
    @OptIn(PerformancePitfall::class)
    override fun divide(left: StructureND<T>, right: StructureND<T>): StructureND<T> =
        zip(left, right) { aValue, bValue -> divide(aValue, bValue) }

    //TODO move to extensions after https://github.com/Kotlin/KEEP/blob/master/proposals/context-receivers.md
    /**
     * Divides an ND structure by an element of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    @OptIn(PerformancePitfall::class)
    public operator fun StructureND<T>.div(arg: T): StructureND<T> = this.map { value -> divide(arg, value) }

    /**
     * Divides an element by an ND structure of it.
     *
     * @receiver the dividend.
     * @param arg the divisor.
     * @return the quotient.
     */
    @OptIn(PerformancePitfall::class)
    public operator fun T.div(arg: StructureND<T>): StructureND<T> = arg.map { divide(it, this@div) }

    @OptIn(PerformancePitfall::class)
    override fun scale(a: StructureND<T>, value: Double): StructureND<T> = a.map { scale(it, value) }
}

public interface FieldND<T, out A : Field<T>> : Field<StructureND<T>>, FieldOpsND<T, A>, RingND<T, A>, WithShape {
    override val one: StructureND<T> get() = structureND(shape) { elementAlgebra.one }
}