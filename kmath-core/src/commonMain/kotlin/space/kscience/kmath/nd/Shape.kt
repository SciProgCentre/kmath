/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.misc.UnsafeKMathAPI
import kotlin.jvm.JvmInline

/**
 * A read-only ND shape
 */
@JvmInline
public value class Shape(@PublishedApi internal val array: IntArray) {
    public val size: Int get() = array.size
    public operator fun get(index: Int): Int = array[index]
    override fun toString(): String = array.contentToString()
}

public inline fun Shape.forEach(block: (value: Int) -> Unit): Unit = array.forEach(block)

public inline fun Shape.forEachIndexed(block: (index: Int, value: Int) -> Unit): Unit = array.forEachIndexed(block)

public infix fun Shape.contentEquals(other: Shape): Boolean = array.contentEquals(other.array)

public fun Shape.contentHashCode(): Int = array.contentHashCode()

public val Shape.indices: IntRange get() = array.indices
public val Shape.linearSize: Int get() = array.reduce(Int::times)

public fun Shape.slice(range: IntRange): Shape = Shape(array.sliceArray(range))

public fun Shape.last(): Int = array.last()

/**
 * A shape including last [n] dimensions of this shape
 */
public fun Shape.last(n: Int): Shape = Shape(array.copyOfRange(size - n, size))

public fun Shape.first(): Int = array.first()

/**
 * A shape including first [n] dimensions of this shape
 */
public fun Shape.first(n: Int): Shape = Shape(array.copyOfRange(0, n))

public operator fun Shape.plus(add: IntArray): Shape = Shape(array + add)

public operator fun Shape.plus(add: Shape): Shape = Shape(array + add.array)

public fun Shape.isEmpty(): Boolean = size == 0
public fun Shape.isNotEmpty(): Boolean = size > 0

public fun Shape.transposed(i: Int, j: Int): Shape = Shape(array.copyOf().apply {
    val ith = get(i)
    val jth = get(j)
    set(i, jth)
    set(j, ith)
})

public operator fun Shape.component1(): Int = get(0)
public operator fun Shape.component2(): Int = get(1)
public operator fun Shape.component3(): Int = get(2)

/**
 * Convert to array with protective copy
 */
public fun Shape.toArray(): IntArray = array.copyOf()

@UnsafeKMathAPI
public fun Shape.asArray(): IntArray = array

public fun Shape.asList(): List<Int> = array.asList()


/**
 * An exception is thrown when the expected and actual shape of NDArray differ.
 *
 * @property expected the expected shape.
 * @property actual the actual shape.
 */
public class ShapeMismatchException(public val expected: Shape, public val actual: Shape) :
    RuntimeException("Shape $actual doesn't fit in expected shape ${expected}.")

public class IndexOutOfShapeException(public val shape: Shape, public val index: IntArray) :
    RuntimeException("Index ${index.contentToString()} is out of shape ${shape}")

public fun Shape(shapeFirst: Int, vararg shapeRest: Int): Shape = Shape(intArrayOf(shapeFirst, *shapeRest))

public interface WithShape {
    public val shape: Shape

    public val indices: ShapeIndexer get() = ColumnStrides(shape)
}

internal fun requireIndexInShape(index: IntArray, shape: Shape) {
    if (index.size != shape.size) throw IndexOutOfShapeException(shape, index)
    shape.forEachIndexed { axis, axisShape ->
        if (index[axis] !in 0 until axisShape) throw IndexOutOfShapeException(shape, index)
    }
}
