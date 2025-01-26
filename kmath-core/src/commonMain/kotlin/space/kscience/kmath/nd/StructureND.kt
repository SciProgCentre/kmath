/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.attributes.Attribute
import space.kscience.attributes.AttributeContainer
import space.kscience.attributes.Attributes
import space.kscience.attributes.SafeType
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.linear.LinearSpace
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.Float64
import kotlin.math.abs

public interface StructureAttribute<T> : Attribute<T>

/**
 * Represents n-dimensional structure i.e., multidimensional container of items of the same type and size. The number
 * of dimensions and items in an array is defined by its shape, which is a sequence of non-negative integers that
 * specify the sizes of each dimension.
 *
 * StructureND is in general identity-free. [StructureND.contentEquals] should be used in tests to compare contents.
 *
 * @param T the type of items.
 */
public interface StructureND<out T> : AttributeContainer, WithShape {
    /**
     * The shape of structure i.e., non-empty sequence of non-negative integers that specify sizes of dimensions for
     * this structure.
     */
    override val shape: ShapeND

    /**
     * The count of dimensions in this structure. It should be equal to size of [shape].
     */
    public val dimension: Int get() = shape.size

    /**
     * Returns the value at the specified indices.
     *
     * @param index the indices.
     * @return the value.
     */
    @PerformancePitfall
    public operator fun get(index: IntArray): T

    /**
     * Returns the sequence of all the elements associated by their indices.
     *
     * @return the lazy sequence of pairs of indices to values.
     */
    @PerformancePitfall
    public fun elements(): Sequence<Pair<IntArray, T>> = indices.asSequence().map { it to get(it) }

    override val attributes: Attributes get() = Attributes.EMPTY

    public companion object {
        /**
         * Indicates whether some [StructureND] is equal to another one.
         */
        @PerformancePitfall
        public fun <T : Any> contentEquals(st1: StructureND<T>, st2: StructureND<T>): Boolean {
            if (st1 === st2) return true

            // fast comparison of buffers if possible
            if (st1 is BufferND && st2 is BufferND && st1.indices == st2.indices)
                return Buffer.contentEquals(st1.buffer, st2.buffer)

            //element by element comparison if it could not be avoided
            return st1.elements().all { (index, value) -> value == st2[index] }
        }

        @PerformancePitfall
        public fun contentEquals(
            st1: StructureND<Float64>,
            st2: StructureND<Float64>,
            tolerance: Double = 1e-11,
        ): Boolean {
            if (st1 === st2) return true

            // fast comparison of buffers if possible
            if (st1 is BufferND && st2 is BufferND && st1.indices == st2.indices)
                return Buffer.contentEquals(st1.buffer, st2.buffer)

            //element by element comparison if it could not be avoided
            return st1.elements().all { (index, value) -> abs(value - st2[index]) < tolerance }
        }

        /**
         * Debug output to string
         */
        @OptIn(PerformancePitfall::class)
        public fun toString(structure: StructureND<*>): String {
            val bufferRepr: String = when (structure.shape.size) {
                1 -> (0 until structure.shape[0]).map { structure[it] }
                    .joinToString(prefix = "[", postfix = "]", separator = ", ")

                2 -> (0 until structure.shape[0]).joinToString(
                    prefix = "[\n",
                    postfix = "\n]",
                    separator = ",\n"
                ) { i ->
                    (0 until structure.shape[1]).joinToString(prefix = "  [", postfix = "]", separator = ", ") { j ->
                        structure[i, j].toString()
                    }
                }

                else -> "..."
            }
            val className = structure::class.simpleName ?: "StructureND"

            return "$className(shape=${structure.shape}, buffer=$bufferRepr)"
        }

    }
}


/**
 * Creates a NDStructure with explicit buffer factory.
 *
 * Strides should be reused if possible.
 */
public fun <T> BufferND(
    type: SafeType<T>,
    strides: Strides,
    initializer: (IntArray) -> T,
): BufferND<T> = BufferND(strides, Buffer(type, strides.linearSize) { i -> initializer(strides.index(i)) })


public fun <T> BufferND(
    type: SafeType<T>,
    shape: ShapeND,
    initializer: (IntArray) -> T,
): BufferND<T> = BufferND(type, ColumnStrides(shape), initializer)

/**
 * Inline create NDStructure with non-boxing buffer implementation if it is possible
 */
public inline fun <reified T : Any> BufferND(
    strides: Strides,
    crossinline initializer: (IntArray) -> T,
): BufferND<T> = BufferND(strides, Buffer(strides.linearSize) { i -> initializer(strides.index(i)) })

public inline fun <reified T : Any> BufferND(
    shape: ShapeND,
    crossinline initializer: (IntArray) -> T,
): BufferND<T> = BufferND(ColumnStrides(shape), initializer)

public inline fun <reified T : Any> BufferND(
    vararg shape: Int,
    crossinline initializer: (IntArray) -> T,
): BufferND<T> = BufferND(ColumnStrides(ShapeND(shape)), initializer)

public fun <T : Any> BufferND(
    type: SafeType<T>,
    vararg shape: Int,
    initializer: (IntArray) -> T,
): BufferND<T> = BufferND(type, ColumnStrides(ShapeND(shape)), initializer)


/**
 * Indicates whether some [StructureND] is equal to another one.
 */
@PerformancePitfall
public fun <T : Comparable<T>> AlgebraND<T, Ring<T>>.contentEquals(
    st1: StructureND<T>,
    st2: StructureND<T>,
): Boolean = StructureND.contentEquals(st1, st2)

/**
 * Indicates whether some [StructureND] is equal to another one.
 */
@PerformancePitfall
public fun <T : Comparable<T>> LinearSpace<T, Ring<T>>.contentEquals(
    st1: StructureND<T>,
    st2: StructureND<T>,
): Boolean = StructureND.contentEquals(st1, st2)

/**
 * Indicates whether some [StructureND] is equal to another one with [absoluteTolerance].
 */
@PerformancePitfall
public fun <T : Comparable<T>> GroupOpsND<T, Ring<T>>.contentEquals(
    st1: StructureND<T>,
    st2: StructureND<T>,
    absoluteTolerance: T,
): Boolean = st1.elements().all { (index, value) -> elementAlgebra { (value - st2[index]) } < absoluteTolerance }

/**
 * Indicates whether some [StructureND] is equal to another one with [absoluteTolerance].
 */
@PerformancePitfall
public fun <T : Comparable<T>> LinearSpace<T, Ring<T>>.contentEquals(
    st1: StructureND<T>,
    st2: StructureND<T>,
    absoluteTolerance: T,
): Boolean = st1.elements().all { (index, value) -> elementAlgebra { (value - st2[index]) } < absoluteTolerance }

/**
 * Returns the value at the specified indices.
 *
 * @param index the indices.
 * @return the value.
 */
@PerformancePitfall
public operator fun <T> StructureND<T>.get(vararg index: Int): T = get(index)

public operator fun StructureND<Float64>.get(vararg index: Int): Double = getDouble(index)

public operator fun StructureND<Int>.get(vararg index: Int): Int = getInt(index)

//@UnstableKMathAPI
//public inline fun <reified T : StructureFeature> StructureND<*>.getFeature(): T? = getFeature(T::class)

/**
 * Represents mutable [StructureND].
 */
public interface MutableStructureND<T> : StructureND<T> {
    /**
     * Inserts an item at the specified indices.
     *
     * @param index the indices.
     * @param value the value.
     */
    public operator fun set(index: IntArray, value: T)
}

/**
 * Set value at specified indices
 */
@PerformancePitfall
public operator fun <T> MutableStructureND<T>.set(vararg index: Int, value: T) {
    set(index, value)
}