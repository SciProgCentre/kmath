/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.linear.Point

public interface Vector2D<T> : Point<T> {
    public val x: T
    public val y: T
    override val size: Int get() = 2

    override operator fun get(index: Int): T = when (index) {
        0 -> x
        1 -> y
        else -> error("Accessing outside of point bounds")
    }

    override operator fun iterator(): Iterator<T> = iterator {
        yield(x)
        yield(y)
    }
}


public operator fun <T> Vector2D<T>.component1(): T = x
public operator fun <T> Vector2D<T>.component2(): T = y