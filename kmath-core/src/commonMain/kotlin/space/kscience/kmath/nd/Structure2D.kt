/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.MutableListBuffer
import space.kscience.kmath.structures.VirtualBuffer
import kotlin.jvm.JvmInline
import kotlin.reflect.KClass

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
     * The buffer of rows of this structure. It gets elements from the structure dynamically.
     */
    @PerformancePitfall
    public val rows: List<Buffer<T>>
        get() = List(rowNum) { i -> VirtualBuffer(colNum) { j -> get(i, j) } }

    /**
     * The buffer of columns of this structure. It gets elements from the structure dynamically.
     */
    @PerformancePitfall
    public val columns: List<Buffer<T>>
        get() = List(colNum) { j -> VirtualBuffer(rowNum) { i -> get(i, j) } }

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
        get() = List(rowNum) { i -> MutableListBuffer(colNum) { j -> get(i, j) } }

    /**
     * The buffer of columns of this structure. It gets elements from the structure dynamically.
     */
    @PerformancePitfall
    override val columns: List<MutableBuffer<T>>
        get() = List(colNum) { j -> MutableListBuffer(rowNum) { i -> get(i, j) } }
}

/**
 * A 2D wrapper for nd-structure
 */
@JvmInline
private value class Structure2DWrapper<out T>(val structure: StructureND<T>) : Structure2D<T> {
    override val shape: ShapeND get() = structure.shape

    override val rowNum: Int get() = shape[0]
    override val colNum: Int get() = shape[1]

    @PerformancePitfall
    override operator fun get(i: Int, j: Int): T = structure[i, j]

    override fun <F : StructureFeature> getFeature(type: KClass<out F>): F? = structure.getFeature(type)

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> = structure.elements()
}

/**
 * A 2D wrapper for a mutable nd-structure
 */
private class MutableStructure2DWrapper<T>(val structure: MutableStructureND<T>) : MutableStructure2D<T> {
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

