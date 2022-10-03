/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import kotlin.jvm.JvmInline
import kotlin.math.PI

public sealed interface Angle {
    public fun toRadians(): Radians
    public fun toDegrees(): Degrees

    public operator fun plus(other: Angle): Angle
    public operator fun minus(other: Angle): Angle

    public operator fun times(other: Number): Angle
    public operator fun div(other: Number): Angle
    public operator fun unaryMinus(): Angle
}

/**
 * Type safe radians
 */
@JvmInline
public value class Radians(public val value: Double) : Angle {
    override fun toRadians(): Radians = this
    override fun toDegrees(): Degrees = Degrees(value * 180 / PI)

    public override fun plus(other: Angle): Radians = Radians(value + other.toRadians().value)
    public override fun minus(other: Angle): Radians = Radians(value - other.toRadians().value)

    public override fun times(other: Number): Radians = Radians(value + other.toDouble())
    public override fun div(other: Number): Radians = Radians(value / other.toDouble())
    public override fun unaryMinus(): Radians = Radians(-value)
}

public fun sin(angle: Angle): Double = kotlin.math.sin(angle.toRadians().value)
public fun cos(angle: Angle): Double = kotlin.math.cos(angle.toRadians().value)
public fun tan(angle: Angle): Double = kotlin.math.tan(angle.toRadians().value)

public val Number.radians: Radians get() = Radians(toDouble())

/**
 * Type safe degrees
 */
@JvmInline
public value class Degrees(public val value: Double) : Angle {
    override fun toRadians(): Radians = Radians(value * PI / 180)
    override fun toDegrees(): Degrees = this

    public override fun plus(other: Angle): Degrees = Degrees(value + other.toDegrees().value)
    public override fun minus(other: Angle): Degrees = Degrees(value - other.toDegrees().value)

    public override fun times(other: Number): Degrees = Degrees(value + other.toDouble())
    public override fun div(other: Number): Degrees = Degrees(value / other.toDouble())
    public override fun unaryMinus(): Degrees = Degrees(-value)
}

public val Number.degrees: Degrees get() = Degrees(toDouble())

/**
 * A holder class for Pi representation in radians and degrees
 */
public object Pi {
    public val radians: Radians = Radians(PI)
    public val degrees: Degrees = radians.toDegrees()
}

public object PiTimes2 {
    public val radians: Radians = Radians(2 * PI)
    public val degrees: Degrees = radians.toDegrees()
}

public object PiDiv2 {
    public val radians: Radians = Radians(PI / 2)
    public val degrees: Degrees = radians.toDegrees()
}