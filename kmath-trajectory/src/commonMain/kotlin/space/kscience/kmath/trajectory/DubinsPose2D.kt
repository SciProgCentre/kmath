/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
@file:UseSerializers(Euclidean2DSpace.VectorSerializer::class)
package space.kscience.kmath.trajectory

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import space.kscience.kmath.geometry.DoubleVector2D
import space.kscience.kmath.geometry.Euclidean2DSpace
import space.kscience.kmath.geometry.Vector
import kotlin.math.atan2

/**
 * Combination of [Vector] and its view angle (clockwise from positive y-axis direction)
 */
@Serializable(DubinsPose2DSerializer::class)
public interface DubinsPose2D : DoubleVector2D {
    public val coordinates: DoubleVector2D
    public val bearing: Double
}

@Serializable
public class PhaseVector2D(
    override val coordinates: DoubleVector2D,
    public val velocity: DoubleVector2D,
) : DubinsPose2D, DoubleVector2D by coordinates {
    override val bearing: Double get() = atan2(velocity.x, velocity.y)
}

@Serializable
@SerialName("DubinsPose2D")
private class DubinsPose2DImpl(
    override val coordinates: DoubleVector2D,
    override val bearing: Double,
) : DubinsPose2D, DoubleVector2D by coordinates{

    override fun toString(): String = "DubinsPose2D(x=$x, y=$y, bearing=$bearing)"
}

public object DubinsPose2DSerializer: KSerializer<DubinsPose2D>{
    private val proxySerializer = DubinsPose2DImpl.serializer()

    override val descriptor: SerialDescriptor
        get() = proxySerializer.descriptor

    override fun deserialize(decoder: Decoder): DubinsPose2D {
        return decoder.decodeSerializableValue(proxySerializer)
    }

    override fun serialize(encoder: Encoder, value: DubinsPose2D) {
        val pose = value as? DubinsPose2DImpl ?: DubinsPose2DImpl(value.coordinates, value.bearing)
        encoder.encodeSerializableValue(proxySerializer, pose)
    }
}

public fun DubinsPose2D(coordinate: DoubleVector2D, theta: Double): DubinsPose2D = DubinsPose2DImpl(coordinate, theta)