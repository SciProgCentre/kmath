/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry.euclidean2d

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import space.kscience.kmath.geometry.GeometrySpace
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.operations.Float32Field
import space.kscience.kmath.structures.Float32
import kotlin.math.pow
import kotlin.math.sqrt

@Serializable(Float32Space2D.VectorSerializer::class)
public interface Float32Vector2D : Vector2D<Float>


public object Float32Space2D : GeometrySpace<Float32Vector2D, Float32> {

    @Serializable
    @SerialName("Float32Vector2D")
    private data class Vector2DImpl(
        override val x: Float,
        override val y: Float,
    ) : Float32Vector2D

    public object VectorSerializer : KSerializer<Float32Vector2D> {
        private val proxySerializer = Vector2DImpl.serializer()
        override val descriptor: SerialDescriptor get() = proxySerializer.descriptor

        override fun deserialize(decoder: Decoder): Float32Vector2D = decoder.decodeSerializableValue(proxySerializer)

        override fun serialize(encoder: Encoder, value: Float32Vector2D) {
            val vector = value as? Vector2DImpl ?: Vector2DImpl(value.x, value.y)
            encoder.encodeSerializableValue(proxySerializer, vector)
        }
    }

    public fun vector(x: Float, y: Float): Float32Vector2D =
        Vector2DImpl(x, y)

    public fun vector(x: Number, y: Number): Float32Vector2D =
        vector(x.toFloat(), y.toFloat())

    override val zero: Float32Vector2D by lazy { vector(0f, 0f) }

    override fun norm(arg: Float32Vector2D): Float32 = sqrt(arg.x.pow(2) + arg.y.pow(2))

    public fun Float32Vector2D.norm(): Float32 = norm(this)

    override fun Float32Vector2D.unaryMinus(): Float32Vector2D = vector(-x, -y)

    override fun Float32Vector2D.distanceTo(other: Float32Vector2D): Float32 = (this - other).norm()

    override fun add(left: Float32Vector2D, right: Float32Vector2D): Float32Vector2D =
        vector(left.x + right.x, left.y + right.y)

    override fun scale(a: Float32Vector2D, value: Double): Float32Vector2D =
        vector(a.x * value, a.y * value)

    override fun Float32Vector2D.dot(other: Float32Vector2D): Double =
        (x * other.x + y * other.y).toDouble()

    public val xAxis: Float32Vector2D = vector(1.0, 0.0)
    public val yAxis: Float32Vector2D = vector(0.0, 1.0)

    override val defaultPrecision: Float32 = 1e-3f
}

public fun Float32Vector2D(x: Number, y: Number): Float32Vector2D = Float32Space2D.vector(x, y)

public val Float32Field.euclidean2D: Float32Space2D get() = Float32Space2D
