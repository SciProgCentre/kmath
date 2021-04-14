package space.kscience.kmath.geometry

import space.kscience.kmath.linear.Point
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.GroupElement
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.operations.invoke
import kotlin.math.sqrt

@OptIn(UnstableKMathAPI::class)
public interface Vector3D : Point<Double>, Vector, GroupElement<Vector3D, Euclidean3DSpace> {
    public val x: Double
    public val y: Double
    public val z: Double
    public override val context: Euclidean3DSpace get() = Euclidean3DSpace
    public override val size: Int get() = 3

    public override operator fun get(index: Int): Double = when (index) {
        1 -> x
        2 -> y
        3 -> z
        else -> error("Accessing outside of point bounds")
    }

    public override operator fun iterator(): Iterator<Double> = listOf(x, y, z).iterator()
}

@Suppress("FunctionName")
public fun Vector3D(x: Double, y: Double, z: Double): Vector3D = Vector3DImpl(x, y, z)

public val Vector3D.r: Double get() = Euclidean3DSpace { sqrt(norm()) }

private data class Vector3DImpl(
    override val x: Double,
    override val y: Double,
    override val z: Double,
) : Vector3D

public object Euclidean3DSpace : GeometrySpace<Vector3D>, ScaleOperations<Vector3D> {
    public override val zero: Vector3D by lazy { Vector3D(0.0, 0.0, 0.0) }

    public fun Vector3D.norm(): Double = sqrt(x * x + y * y + z * z)
    override fun Vector3D.unaryMinus(): Vector3D = Vector3D(-x, -y, -z)

    public override fun Vector3D.distanceTo(other: Vector3D): Double = (this - other).norm()

    public override fun add(a: Vector3D, b: Vector3D): Vector3D =
        Vector3D(a.x + b.x, a.y + b.y, a.z + b.z)

    public override fun scale(a: Vector3D, value: Double): Vector3D =
        Vector3D(a.x * value, a.y * value, a.z * value)

    public override fun Vector3D.dot(other: Vector3D): Double =
        x * other.x + y * other.y + z * other.z
}
