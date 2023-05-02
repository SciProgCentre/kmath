/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.UnsafeKMathAPI
import kotlin.jvm.JvmInline

/**
 * A read-only ND shape
 */
@JvmInline
public value class ShapeND(@PublishedApi internal val array: IntArray) {
    public val size: Int get() = array.size
    public operator fun get(index: Int): Int = array[index]
    override fun toString(): String = array.contentToString()
}

public inline fun ShapeND.forEach(block: (value: Int) -> Unit): Unit = array.forEach(block)

public inline fun ShapeND.forEachIndexed(block: (index: Int, value: Int) -> Unit): Unit = array.forEachIndexed(block)

public infix fun ShapeND.contentEquals(other: ShapeND): Boolean = array.contentEquals(other.array)

public fun ShapeND.contentHashCode(): Int = array.contentHashCode()

public val ShapeND.indices: IntRange get() = array.indices
public val ShapeND.linearSize: Int get() = array.reduce(Int::times)

public fun ShapeND.slice(range: IntRange): ShapeND = ShapeND(array.sliceArray(range))

public fun ShapeND.last(): Int = array.last()

/**
 * A shape including last [n] dimensions of this shape
 */
public fun ShapeND.last(n: Int): ShapeND = ShapeND(array.copyOfRange(size - n, size))

public fun ShapeND.first(): Int = array.first()

/**
 * A shape including first [n] dimensions of this shape
 */
public fun ShapeND.first(n: Int): ShapeND = ShapeND(array.copyOfRange(0, n))

public operator fun ShapeND.plus(add: IntArray): ShapeND = ShapeND(array + add)

public operator fun ShapeND.plus(add: ShapeND): ShapeND = ShapeND(array + add.array)

public fun ShapeND.isEmpty(): Boolean = size == 0
public fun ShapeND.isNotEmpty(): Boolean = size > 0

public fun ShapeND.transposed(i: Int, j: Int): ShapeND = ShapeND(array.copyOf().apply {
    val ith = get(i)
    val jth = get(j)
    set(i, jth)
    set(j, ith)
})

public operator fun ShapeND.component1(): Int = get(0)
public operator fun ShapeND.component2(): Int = get(1)
public operator fun ShapeND.component3(): Int = get(2)

/**
 * Convert to array with protective copy
 */
public fun ShapeND.toArray(): IntArray = array.copyOf()

@UnsafeKMathAPI
public fun ShapeND.asArray(): IntArray = array

public fun ShapeND.asList(): List<Int> = array.asList()


/**
 * An exception is thrown when the expected and actual shape of NDArray differ.
 *
 * @property expected the expected shape.
 * @property actual the actual shape.
 */
public class ShapeMismatchException(public val expected: ShapeND, public val actual: ShapeND) :
    RuntimeException("Shape $actual doesn't fit in expected shape ${expected}.")

public class IndexOutOfShapeException(public val shape: ShapeND, public val index: IntArray) :
    RuntimeException("Index ${index.contentToString()} is out of shape ${shape}")

public fun ShapeND(shapeFirst: Int, vararg shapeRest: Int): ShapeND = ShapeND(intArrayOf(shapeFirst, *shapeRest))

public interface WithShape {
    public val shape: ShapeND

    public val indices: ShapeIndexer get() = ColumnStrides(shape)
}

internal fun requireIndexInShape(index: IntArray, shape: ShapeND) {
    if (index.size != shape.size) throw IndexOutOfShapeException(shape, index)
    shape.forEachIndexed { axis, axisShape ->
        if (index[axis] !in 0 until axisShape) throw IndexOutOfShapeException(shape, index)
    }
}
