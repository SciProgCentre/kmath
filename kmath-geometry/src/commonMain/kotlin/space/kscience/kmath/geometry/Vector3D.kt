/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.linear.Point
import space.kscience.kmath.structures.Buffer

public interface Vector3D<T> : Point<T> {
    public val x: T
    public val y: T
    public val z: T
    override val size: Int get() = 3

    override operator fun get(index: Int): T = when (index) {
        0 -> x
        1 -> y
        2 -> z
        else -> error("Accessing outside of point bounds")
    }

    override operator fun iterator(): Iterator<T> = listOf(x, y, z).iterator()
}

public operator fun <T> Vector3D<T>.component1(): T = x
public operator fun <T> Vector3D<T>.component2(): T = y
public operator fun <T> Vector3D<T>.component3(): T = z

public fun <T> Buffer<T>.asVector3D(): Vector3D<T> = object : Vector3D<T> {
    init {
        require(this@asVector3D.size == 3) { "Buffer of size 3 is required for Vector3D" }
    }

    override val x: T get() = this@asVector3D[0]
    override val y: T get() = this@asVector3D[1]
    override val z: T get() = this@asVector3D[2]

    override fun toString(): String = this@asVector3D.toString()
}