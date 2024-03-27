/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.MutableBufferFactory

/**
 * The base interface for all ND-algebra implementations.
 *
 * @param T the type of ND-structure element.
 * @param C the type of the element context.
 */
public interface AlgebraND<T, out C : Algebra<T>> : Algebra<StructureND<T>> {
    /**
     * The algebra over elements of ND structure.
     */
    public val elementAlgebra: C

    /**
     * Produces a new [MutableStructureND] using given initializer function.
     */
    public fun mutableStructureND(shape: ShapeND, initializer: C.(IntArray) -> T): MutableStructureND<T>

    /**
     * Produces a new [StructureND] using given initializer function.
     */
    public fun structureND(shape: ShapeND, initializer: C.(IntArray) -> T): StructureND<T> =
        mutableStructureND(shape, initializer)

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
     * Get an attribute value for the structure in this scope. Structure features take precedence other context features.
     *
     * @param structure the structure.
     * @param attribute to be computed.
     * @return a feature object or `null` if it isn't present.
     */
    @UnstableKMathAPI
    public fun <T, A : StructureAttribute<T>> attributeFor(structure: StructureND<*>, attribute: A): T? =
        structure.attributes[attribute]

    public companion object
}

/**
 * Space of [StructureND].
 *
 * @param T the type of the element contained in ND structure.
 * @param A the type of group over structure elements.
 */
public interface GroupOpsND<T, out A : GroupOps<T>> : GroupOps<StructureND<T>>, AlgebraND<T, A> {
    override val bufferFactory: MutableBufferFactory<StructureND<T>> get() = MutableBufferFactory<StructureND<T>>()

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

    // TODO implement using context receivers

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