/*
 * Copyright 2018-2024 KMath contributors.
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
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.structures.MutableBufferFactory
import kotlin.math.pow
import kotlin.math.sqrt


public typealias Float64Vector2D = Vector2D<Float64>

@Deprecated("Use Float64Vector2D", ReplaceWith("Float64Vector2D"))
public typealias DoubleVector2D = Float64Vector2D


/**
 * 2D Euclidean space
 */
public object Float64Space2D : GeometrySpace<Vector2D<Float64>, Float64>, ScaleOperations<Vector2D<Float64>> {


    @Serializable
    @SerialName("Float64Vector2D")
    private data class Vector2DImpl(
        override val x: Double,
        override val y: Double,
    ) : Float64Vector2D

    public object VectorSerializer : KSerializer<Float64Vector2D> {
        private val proxySerializer = Vector2DImpl.serializer()
        override val descriptor: SerialDescriptor get() = proxySerializer.descriptor

        override fun deserialize(decoder: Decoder): Float64Vector2D = decoder.decodeSerializableValue(proxySerializer)

        override fun serialize(encoder: Encoder, value: Float64Vector2D) {
            val vector = value as? Vector2DImpl ?: Vector2DImpl(value.x, value.y)
            encoder.encodeSerializableValue(proxySerializer, vector)
        }
    }

    public fun vector(x: Number, y: Number): Float64Vector2D = Vector2DImpl(x.toDouble(), y.toDouble())

    override val zero: Float64Vector2D by lazy { vector(0.0, 0.0) }

    override fun norm(arg: Vector2D<Float64>): Double = sqrt(arg.x.pow(2) + arg.y.pow(2))

    override fun Vector2D<Float64>.unaryMinus(): Float64Vector2D = vector(-x, -y)

    override fun Vector2D<Float64>.distanceTo(other: Vector2D<Float64>): Double = norm(this - other)
    override fun add(left: Vector2D<Float64>, right: Vector2D<Float64>): Float64Vector2D =
        vector(left.x + right.x, left.y + right.y)

    override fun scale(a: Vector2D<Float64>, value: Double): Float64Vector2D = vector(a.x * value, a.y * value)
    override fun Vector2D<Float64>.dot(other: Vector2D<Float64>): Double = x * other.x + y * other.y

    public val xAxis: Float64Vector2D = vector(1.0, 0.0)
    public val yAxis: Float64Vector2D = vector(0.0, 1.0)

    override val defaultPrecision: Double = 1e-6

    override val bufferFactory: MutableBufferFactory<Vector2D<Float64>> = MutableBufferFactory()
}

public fun Float64Vector2D(x: Number, y: Number): Float64Vector2D = Float64Space2D.vector(x, y)

public val Float64Vector2D.r: Float64 get() = Float64Space2D.norm(this)

public val Float64Field.euclidean2D: Float64Space2D get() = Float64Space2D