/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

/**
 * An exception is thrown when the expected and actual shape of NDArray differ.
 *
 * @property expected the expected shape.
 * @property actual the actual shape.
 */
public class ShapeMismatchException(public val expected: IntArray, public val actual: IntArray) :
    RuntimeException("Shape ${actual.contentToString()} doesn't fit in expected shape ${expected.contentToString()}.")

public class IndexOutOfShapeException(public val shape: Shape, public val index: IntArray) :
    RuntimeException("Index ${index.contentToString()} is out of shape ${shape.contentToString()}")

public typealias Shape = IntArray

public fun Shape(shapeFirst: Int, vararg shapeRest: Int): Shape = intArrayOf(shapeFirst, *shapeRest)

public interface WithShape {
    public val shape: Shape

    public val indices: ShapeIndexer get() = ColumnStrides(shape)
}

internal fun requireIndexInShape(index: IntArray, shape: Shape) {
    if (index.size != shape.size) throw IndexOutOfShapeException(index, shape)
    shape.forEachIndexed { axis, axisShape ->
        if (index[axis] !in 0 until axisShape) throw IndexOutOfShapeException(index, shape)
    }
}
