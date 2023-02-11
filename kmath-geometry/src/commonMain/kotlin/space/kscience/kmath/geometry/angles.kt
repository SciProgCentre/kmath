/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.jvm.JvmInline
import kotlin.math.PI
import kotlin.math.floor

@Serializable(AngleSerializer::class)
public sealed interface Angle : Comparable<Angle> {
    public fun toRadians(): Radians
    public fun toDegrees(): Degrees

    public operator fun plus(other: Angle): Angle
    public operator fun minus(other: Angle): Angle

    public operator fun times(other: Number): Angle
    public operator fun div(other: Number): Angle
    public operator fun div(other: Angle): Double
    public operator fun unaryMinus(): Angle

    public companion object {
        public val zero: Radians = Radians(0.0)
        public val pi: Radians = Radians(PI)
        public val piTimes2: Radians = Radians(PI * 2)
        public val piDiv2: Radians = Radians(PI / 2)
    }
}


public object AngleSerializer : KSerializer<Angle> {
    override val descriptor: SerialDescriptor get() = Double.serializer().descriptor

    override fun deserialize(decoder: Decoder): Angle = decoder.decodeDouble().degrees

    override fun serialize(encoder: Encoder, value: Angle) {
        encoder.encodeDouble(value.degrees)
    }
}

/**
 * Type safe radians
 */
@Serializable
@JvmInline
public value class Radians(public val value: Double) : Angle {
    override fun toRadians(): Radians = this
    override fun toDegrees(): Degrees = Degrees(value * 180 / PI)

    public override fun plus(other: Angle): Radians = Radians(value + other.radians)
    public override fun minus(other: Angle): Radians = Radians(value - other.radians)

    public override fun times(other: Number): Radians = Radians(value * other.toDouble())
    public override fun div(other: Number): Radians = Radians(value / other.toDouble())
    override fun div(other: Angle): Double = value / other.radians

    public override fun unaryMinus(): Radians = Radians(-value)

    override fun compareTo(other: Angle): Int = value.compareTo(other.radians)
}

public fun sin(angle: Angle): Double = kotlin.math.sin(angle.toRadians().value)
public fun cos(angle: Angle): Double = kotlin.math.cos(angle.toRadians().value)
public fun tan(angle: Angle): Double = kotlin.math.tan(angle.toRadians().value)

public val Number.radians: Radians get() = Radians(toDouble())

public val Angle.radians: Double get() = toRadians().value

/**
 * Type safe degrees
 */
@JvmInline
public value class Degrees(public val value: Double) : Angle {
    override fun toRadians(): Radians = Radians(value * PI / 180)
    override fun toDegrees(): Degrees = this

    public override fun plus(other: Angle): Degrees = Degrees(value + other.degrees)
    public override fun minus(other: Angle): Degrees = Degrees(value - other.degrees)

    public override fun times(other: Number): Degrees = Degrees(value * other.toDouble())
    public override fun div(other: Number): Degrees = Degrees(value / other.toDouble())
    override fun div(other: Angle): Double = value / other.degrees

    public override fun unaryMinus(): Degrees = Degrees(-value)

    override fun compareTo(other: Angle): Int = value.compareTo(other.degrees)
}

public val Number.degrees: Degrees get() = Degrees(toDouble())

public val Angle.degrees: Double get() = toDegrees().value

/**
 * Normalized angle 2 PI range symmetric around [center]. By default, uses (0, 2PI) range.
 */
public fun Angle.normalized(center: Angle = Angle.pi): Angle =
    this - Angle.piTimes2 * floor((radians + PI - center.radians) / PI / 2)

public fun abs(angle: Angle): Angle = if (angle < Angle.zero) -angle else angle