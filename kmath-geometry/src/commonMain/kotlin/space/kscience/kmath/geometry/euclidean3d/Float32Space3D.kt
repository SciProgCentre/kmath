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
import space.kscience.kmath.operations.Float32Field
import space.kscience.kmath.structures.Float32
import space.kscience.kmath.structures.MutableBufferFactory
import kotlin.math.pow
import kotlin.math.sqrt


public typealias Float32Vector3D = Vector3D<Float>


public object Float32Space3D : GeometrySpace<Vector3D<Float32>, Float32> {

    @Serializable
    @SerialName("Float32Vector3D")
    private data class Vector3DImpl(
        override val x: Float,
        override val y: Float,
        override val z: Float,
    ) : Float32Vector3D

    public object VectorSerializer : KSerializer<Float32Vector3D> {
        private val proxySerializer = Vector3DImpl.serializer()
        override val descriptor: SerialDescriptor get() = proxySerializer.descriptor

        override fun deserialize(decoder: Decoder): Float32Vector3D = decoder.decodeSerializableValue(proxySerializer)

        override fun serialize(encoder: Encoder, value: Float32Vector3D) {
            val vector = value as? Vector3DImpl ?: Vector3DImpl(value.x, value.y, value.z)
            encoder.encodeSerializableValue(proxySerializer, vector)
        }
    }

    public fun vector(x: Float, y: Float, z: Float): Float32Vector3D =
        Vector3DImpl(x, y, z)

    public fun vector(x: Number, y: Number, z: Number): Float32Vector3D =
        vector(x.toFloat(), y.toFloat(), z.toFloat())

    override val zero: Float32Vector3D by lazy { vector(0.0, 0.0, 0.0) }

    override fun norm(arg: Vector3D<Float32>): Float32 = sqrt(arg.x.pow(2) + arg.y.pow(2) + arg.z.pow(2))

    public fun Vector3D<Float32>.norm(): Float32 = norm(this)

    override fun Vector3D<Float32>.unaryMinus(): Float32Vector3D = vector(-x, -y, -z)

    override fun Vector3D<Float32>.distanceTo(other: Vector3D<Float32>): Float32 = (this - other).norm()

    override fun add(left: Vector3D<Float32>, right: Vector3D<Float32>): Float32Vector3D =
        vector(left.x + right.x, left.y + right.y, left.z + right.z)

    override fun scale(a: Vector3D<Float32>, value: Double): Float32Vector3D =
        vector(a.x * value, a.y * value, a.z * value)

    override fun Vector3D<Float32>.dot(other: Vector3D<Float32>): Double =
        (x * other.x + y * other.y + z * other.z).toDouble()

    /**
     * Compute vector product of [first] and [second]. The basis is assumed to be right-handed.
     */
    public fun vectorProduct(
        first: Float32Vector3D,
        second: Float32Vector3D,
    ): Float32Vector3D {
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
     * Vector product in a right-handed basis
     */
    public infix fun Float32Vector3D.cross(other: Float32Vector3D): Float32Vector3D = vectorProduct(this, other)

    public val xAxis: Float32Vector3D = vector(1.0, 0.0, 0.0)
    public val yAxis: Float32Vector3D = vector(0.0, 1.0, 0.0)
    public val zAxis: Float32Vector3D = vector(0.0, 0.0, 1.0)

    override val defaultPrecision: Float32 = 1e-3f

    override val bufferFactory: MutableBufferFactory<Vector3D<Float32>> = MutableBufferFactory()
}

public fun Float32Vector3D(x: Number, y: Number, z: Number): Float32Vector3D = Float32Space3D.vector(x, y, z)

public val Float32Field.euclidean3D: Float32Space3D get() = Float32Space3D