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
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.ScaleOperations
import kotlin.math.pow
import kotlin.math.sqrt


public typealias DoubleVector2D = Vector2D<Double>
public typealias Float64Vector2D = Vector2D<Double>

public val Vector2D<Double>.r: Double get() = Float64Space2D.norm(this)


/**
 * 2D Euclidean space
 */
public object Float64Space2D : GeometrySpace<DoubleVector2D>, ScaleOperations<DoubleVector2D> {

    @Serializable
    @SerialName("Float64Vector2D")
    private data class Vector2DImpl(
        override val x: Double,
        override val y: Double,
    ) : DoubleVector2D

    public object VectorSerializer : KSerializer<DoubleVector2D> {
        private val proxySerializer = Vector2DImpl.serializer()
        override val descriptor: SerialDescriptor get() = proxySerializer.descriptor

        override fun deserialize(decoder: Decoder): DoubleVector2D = decoder.decodeSerializableValue(proxySerializer)

        override fun serialize(encoder: Encoder, value: DoubleVector2D) {
            val vector = value as? Vector2DImpl ?: Vector2DImpl(value.x, value.y)
            encoder.encodeSerializableValue(proxySerializer, vector)
        }
    }

    public fun vector(x: Number, y: Number): DoubleVector2D = Vector2DImpl(x.toDouble(), y.toDouble())

    override val zero: DoubleVector2D by lazy { vector(0.0, 0.0) }

    override fun norm(arg: DoubleVector2D): Double = sqrt(arg.x.pow(2) + arg.y.pow(2))

    override fun DoubleVector2D.unaryMinus(): DoubleVector2D = vector(-x, -y)

    override fun DoubleVector2D.distanceTo(other: DoubleVector2D): Double = norm(this - other)
    override fun add(left: DoubleVector2D, right: DoubleVector2D): DoubleVector2D =
        vector(left.x + right.x, left.y + right.y)

    override fun scale(a: DoubleVector2D, value: Double): DoubleVector2D = vector(a.x * value, a.y * value)
    override fun DoubleVector2D.dot(other: DoubleVector2D): Double = x * other.x + y * other.y

    public val xAxis: DoubleVector2D = vector(1.0, 0.0)
    public val yAxis: DoubleVector2D = vector(0.0, 1.0)
}

public fun Float64Vector2D(x: Number, y: Number): Float64Vector2D = Float64Space2D.vector(x, y)

public val Float64Field.euclidean2D: Float64Space2D get() = Float64Space2D