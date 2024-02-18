/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.attributes.Attributes
import space.kscience.attributes.SafeType
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.VirtualBuffer
import kotlin.jvm.JvmInline

/**
 * A structure that is guaranteed to be two-dimensional.
 *
 * @param T the type of items.
 */
public interface Structure2D<out T> : StructureND<T> {
    /**
     * The number of rows in this structure.
     */
    public val rowNum: Int

    /**
     * The number of columns in this structure.
     */
    public val colNum: Int

    override val shape: ShapeND get() = ShapeND(rowNum, colNum)

    /**
     * The buffer of rows for this structure. It gets elements from the structure dynamically.
     */
    @PerformancePitfall
    public val rows: List<Buffer<T>>
        get() = List(rowNum) { i -> VirtualBuffer(type, colNum) { j -> get(i, j) } }

    /**
     * The buffer of columns for this structure. It gets elements from the structure dynamically.
     */
    @PerformancePitfall
    public val columns: List<Buffer<T>>
        get() = List(colNum) { j -> VirtualBuffer(type, rowNum) { i -> get(i, j) } }

    /**
     * Retrieves an element from the structure by two indices.
     *
     * @param i the first index.
     * @param j the second index.
     * @return an element.
     */
    public operator fun get(i: Int, j: Int): T

    @PerformancePitfall
    override operator fun get(index: IntArray): T {
        require(index.size == 2) { "Index dimension mismatch. Expected 2 but found ${index.size}" }
        return get(index[0], index[1])
    }

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = sequence {
        for (i in 0 until rowNum)
            for (j in 0 until colNum) yield(intArrayOf(i, j) to get(i, j))
    }

    public companion object
}

/**
 * A linear accessor for a [MutableStructureND]
 */
@OptIn(PerformancePitfall::class)
public class MutableStructureNDAccessorBuffer<T>(
    public val structure: MutableStructureND<T>,
    override val size: Int,
    private val indexer: (Int) -> IntArray,
) : MutableBuffer<T> {

    override val type: SafeType<T> get() = structure.type

    override fun set(index: Int, value: T) {
        structure[indexer(index)] = value
    }

    override fun get(index: Int): T = structure[indexer(index)]

    override fun toString(): String = "AccessorBuffer(structure=$structure, size=$size)"

    override fun copy(): MutableBuffer<T> = MutableBuffer(type, size, ::get)
}

/**
 * Represents mutable [Structure2D].
 */
public interface MutableStructure2D<T> : Structure2D<T>, MutableStructureND<T> {
    /**
     * Inserts an item at the specified indices.
     *
     * @param i the first index.
     * @param j the second index.
     * @param value the value.
     */
    public operator fun set(i: Int, j: Int, value: T)

    /**
     * The buffer of rows of this structure. It gets elements from the structure dynamically.
     */
    @PerformancePitfall
    override val rows: List<MutableBuffer<T>>
        get() = List(rowNum) { i ->
            MutableStructureNDAccessorBuffer(this, colNum) { j -> intArrayOf(i, j) }
        }

    /**
     * The buffer of columns for this structure. It gets elements from the structure dynamically.
     */
    @PerformancePitfall
    override val columns: List<MutableBuffer<T>>
        get() = List(colNum) { j ->
            MutableStructureNDAccessorBuffer(this, rowNum) { i -> intArrayOf(i, j) }
        }
}

/**
 * A 2D wrapper for nd-structure
 */
@JvmInline
private value class Structure2DWrapper<out T>(val structure: StructureND<T>) : Structure2D<T> {

    override val type: SafeType<T> get() = structure.type

    override val shape: ShapeND get() = structure.shape

    override val rowNum: Int get() = shape[0]
    override val colNum: Int get() = shape[1]

    @PerformancePitfall
    override operator fun get(i: Int, j: Int): T = structure[i, j]

    override val attributes: Attributes
        get() = structure.attributes

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = structure.elements()
}

/**
 * A 2D wrapper for a mutable nd-structure
 */
private class MutableStructure2DWrapper<T>(val structure: MutableStructureND<T>) : MutableStructure2D<T> {

    override val type: SafeType<T> get() = structure.type

    override val shape: ShapeND get() = structure.shape

    override val rowNum: Int get() = shape[0]
    override val colNum: Int get() = shape[1]

    @PerformancePitfall
    override operator fun get(i: Int, j: Int): T = structure[i, j]

    @PerformancePitfall
    override fun set(index: IntArray, value: T) {
        structure[index] = value
    }

    @PerformancePitfall
    override operator fun set(i: Int, j: Int, value: T) {
        structure[intArrayOf(i, j)] = value
    }

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = structure.elements()

    override fun equals(other: Any?): Boolean = false

    override fun hashCode(): Int = 0

    override fun toString(): String {
        return StructureND.toString(structure)
    }
}

/**
 * Represents a [StructureND] as [Structure2D]. Throws runtime error in case of dimension mismatch.
 */
public fun <T> StructureND<T>.as2D(): Structure2D<T> = this as? Structure2D<T> ?: when (shape.size) {
    2 -> Structure2DWrapper(this)
    else -> error("Can't create 2d-structure from ${shape.size}d-structure")
}

/**
 * Represents a [StructureND] as [Structure2D]. Throws runtime error in case of dimension mismatch.
 */
public fun <T> MutableStructureND<T>.as2D(): MutableStructure2D<T> =
    this as? MutableStructure2D<T> ?: when (shape.size) {
        2 -> MutableStructure2DWrapper(this)
        else -> error("Can't create 2d-structure from ${shape.size}d-structure")
    }

/**
 * Expose inner [StructureND] if possible
 */
internal fun <T> Structure2D<T>.asND(): StructureND<T> =
    if (this is Structure2DWrapper) structure
    else this

internal fun <T> MutableStructure2D<T>.asND(): MutableStructureND<T> =
    if (this is MutableStructure2DWrapper) structure else this

