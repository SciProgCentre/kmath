/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import space.kscience.kmath.linear.Point
import space.kscience.kmath.operations.Norm
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.structures.Buffer
import kotlin.math.pow
import kotlin.math.sqrt

public interface Vector3D<T> : Point<T>, Vector {
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

public typealias DoubleVector3D = Vector3D<Double>
public typealias Float64Vector3D = Vector3D<Double>

public val DoubleVector3D.r: Double get() = Euclidean3DSpace.norm(this)

public object Euclidean3DSpace : GeometrySpace<DoubleVector3D>, ScaleOperations<DoubleVector3D>,
    Norm<DoubleVector3D, Double> {

    @Serializable
    @SerialName("Float64Vector3D")
    private data class Vector3DImpl(
        override val x: Double,
        override val y: Double,
        override val z: Double,
    ) : DoubleVector3D

    public object VectorSerializer : KSerializer<DoubleVector3D> {
        private val proxySerializer = Vector3DImpl.serializer()
        override val descriptor: SerialDescriptor get() = proxySerializer.descriptor

        override fun deserialize(decoder: Decoder): DoubleVector3D = decoder.decodeSerializableValue(proxySerializer)

        override fun serialize(encoder: Encoder, value: DoubleVector3D) {
            val vector = value as? Vector3DImpl ?: Vector3DImpl(value.x, value.y, value.z)
            encoder.encodeSerializableValue(proxySerializer, vector)
        }
    }

    public fun vector(x: Double, y: Double, z: Double): DoubleVector3D =
        Vector3DImpl(x, y, z)

    public fun vector(x: Number, y: Number, z: Number): DoubleVector3D =
        vector(x.toDouble(), y.toDouble(), z.toDouble())

    override val zero: DoubleVector3D by lazy { vector(0.0, 0.0, 0.0) }

    override fun norm(arg: DoubleVector3D): Double = sqrt(arg.x.pow(2) + arg.y.pow(2) + arg.z.pow(2))

    public fun DoubleVector3D.norm(): Double = norm(this)

    override fun DoubleVector3D.unaryMinus(): DoubleVector3D = vector(-x, -y, -z)

    override fun DoubleVector3D.distanceTo(other: DoubleVector3D): Double = (this - other).norm()

    override fun add(left: DoubleVector3D, right: DoubleVector3D): DoubleVector3D =
        vector(left.x + right.x, left.y + right.y, left.z + right.z)

    override fun scale(a: DoubleVector3D, value: Double): DoubleVector3D =
        vector(a.x * value, a.y * value, a.z * value)

    override fun DoubleVector3D.dot(other: DoubleVector3D): Double =
        x * other.x + y * other.y + z * other.z

    private fun leviCivita(i: Int, j: Int, k: Int): Int = when {
        // even permutation
        i == 0 && j == 1 && k == 2 -> 1
        i == 1 && j == 2 && k == 0 -> 1
        i == 2 && j == 0 && k == 1 -> 1
        // odd permutations
        i == 2 && j == 1 && k == 0 -> -1
        i == 0 && j == 2 && k == 1 -> -1
        i == 1 && j == 0 && k == 2 -> -1

        else -> 0
    }

    /**
     * Compute vector product of [first] and [second]. The basis assumed to be right-handed.
     */
    public fun vectorProduct(
        first: DoubleVector3D,
        second: DoubleVector3D,
    ): DoubleVector3D {
        var x = 0.0
        var y = 0.0
        var z = 0.0

        for (j in (0..2)) {
            for (k in (0..2)) {
                x += leviCivita(0, j, k) * first[j] * second[k]
                y += leviCivita(1, j, k) * first[j] * second[k]
                z += leviCivita(2, j, k) * first[j] * second[k]
            }
        }

        return vector(x, y, z)
    }

    /**
     * Vector product with right basis
     */
    public infix fun DoubleVector3D.cross(other: DoubleVector3D): Vector3D<Double> = vectorProduct(this, other)

    public val xAxis: DoubleVector3D = vector(1.0, 0.0, 0.0)
    public val yAxis: DoubleVector3D = vector(0.0, 1.0, 0.0)
    public val zAxis: DoubleVector3D = vector(0.0, 0.0, 1.0)
}
