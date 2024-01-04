/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry.euclidean3d

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import space.kscience.kmath.geometry.GeometrySpace
import space.kscience.kmath.geometry.Vector3D
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Norm
import space.kscience.kmath.operations.ScaleOperations
import kotlin.math.pow
import kotlin.math.sqrt

internal fun leviCivita(i: Int, j: Int, k: Int): Int = when {
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

public typealias DoubleVector3D = Vector3D<Double>
public typealias Float64Vector3D = Vector3D<Double>

public val DoubleVector3D.r: Double get() = Float64Space3D.norm(this)

public object Float64Space3D : GeometrySpace<DoubleVector3D>, ScaleOperations<DoubleVector3D>,
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

    /**
     * Compute vector product of [first] and [second]. The basis is assumed to be right-handed.
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
     * Vector product with the right basis
     */
    public infix fun DoubleVector3D.cross(other: DoubleVector3D): Vector3D<Double> = vectorProduct(this, other)

    public val xAxis: DoubleVector3D = vector(1.0, 0.0, 0.0)
    public val yAxis: DoubleVector3D = vector(0.0, 1.0, 0.0)
    public val zAxis: DoubleVector3D = vector(0.0, 0.0, 1.0)
}

public val Float64Field.euclidean3D: Float64Space3D get() = Float64Space3D
