/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.linear.Point
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer
import kotlin.math.sqrt

public interface Vector3D : Point<Double>, Vector {
    public val x: Double
    public val y: Double
    public val z: Double
    override val size: Int get() = 3

    override operator fun get(index: Int): Double = when (index) {
        0 -> x
        1 -> y
        2 -> z
        else -> error("Accessing outside of point bounds")
    }

    override operator fun iterator(): Iterator<Double> = listOf(x, y, z).iterator()
}

public operator fun Vector3D.component1(): Double = x
public operator fun Vector3D.component2(): Double = y
public operator fun Vector3D.component3(): Double = z

public fun Buffer<Double>.asVector3D(): Vector3D = object : Vector3D {
    init {
        require(this@asVector3D.size == 3) { "Buffer of size 3 is required for Vector3D" }
    }

    override val x: Double get() = this@asVector3D[0]
    override val y: Double get() = this@asVector3D[1]
    override val z: Double get() = this@asVector3D[2]

    override fun toString(): String = this@asVector3D.toString()

}

public val Vector3D.r: Double get() = Euclidean3DSpace { norm() }

private data class Vector3DImpl(
    override val x: Double,
    override val y: Double,
    override val z: Double,
) : Vector3D


public fun Vector3D(x: Double, y: Double, z: Double): Vector3D = Vector3DImpl(x, y, z)

public object Euclidean3DSpace : GeometrySpace<Vector3D>, ScaleOperations<Vector3D> {
    override val zero: Vector3D by lazy { Vector3D(0.0, 0.0, 0.0) }

    public fun Vector3D.norm(): Double = sqrt(x * x + y * y + z * z)
    override fun Vector3D.unaryMinus(): Vector3D = Vector3D(-x, -y, -z)

    override fun Vector3D.distanceTo(other: Vector3D): Double = (this - other).norm()

    override fun add(left: Vector3D, right: Vector3D): Vector3D =
        Vector3D(left.x + right.x, left.y + right.y, left.z + right.z)

    override fun scale(a: Vector3D, value: Double): Vector3D =
        Vector3D(a.x * value, a.y * value, a.z * value)

    override fun Vector3D.dot(other: Vector3D): Double =
        x * other.x + y * other.y + z * other.z

    public val xAxis: Vector3D = Vector3D(1.0, 0.0, 0.0)
    public val yAxis: Vector3D = Vector3D(0.0, 1.0, 0.0)
    public val zAxis: Vector3D = Vector3D(0.0, 0.0, 1.0)
}
