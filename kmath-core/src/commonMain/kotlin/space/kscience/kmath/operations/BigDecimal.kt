/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.UnstableKMathAPI
import kotlin.math.abs
import kotlin.math.min

/**
 * Decimal value with unfixed length
 *
 * The value is computed as `intValue*10^scale`.
 */
@UnstableKMathAPI
public data class BigDecimal(val intValue: BigInt, val scale: Int)

/**
 * Convert current [BigInt] to a [BigDecimal] with scale 0.
 */
@UnstableKMathAPI
public fun BigInt.asDecimal(): BigDecimal = BigDecimal(this, 0)

/**
 * A field for [BigDecimal] on top of [BigIntField]
 */
@UnstableKMathAPI
public object BigDecimalField : Field<BigDecimal> {

    override val one: BigDecimal = BigIntField.one.asDecimal()
    override val zero: BigDecimal = BigIntField.zero.asDecimal()

    internal val ten: BigInt = BigIntField.number(10)

    internal fun pow10(power: UInt): BigInt = ten.pow(power)

    public fun Number.toDecimal(): BigDecimal = number(this)

    /**
     * Rescale a [BigDecimal] to have [targetScale]. If the target scale is larger than the current scale, loss of precision is possible.
     */
    public fun BigDecimal.rescale(targetScale: Int): BigDecimal = if (targetScale == scale) {
        this
    } else {
        val scaleDif = scale - targetScale
        if (scaleDif > 0) {
            BigDecimal(BigIntField.multiply(intValue, pow10(scaleDif.toUInt())), targetScale)
        } else {
            BigDecimal(BigIntField.divide(intValue, pow10(abs(scaleDif).toUInt())), targetScale)
        }
    }

    public fun BigDecimal.pow10(power: Int): BigDecimal = BigDecimal(intValue, scale + power)

    override fun add(left: BigDecimal, right: BigDecimal): BigDecimal = if (left.scale == right.scale) {
        BigDecimal(BigIntField.add(left.intValue, right.intValue), left.scale)
    } else {
        val minScale = min(left.scale, right.scale)
        //rescale both to a minimal scale
        add(left.rescale(minScale), right.rescale(minScale))
    }

    override fun BigDecimal.unaryMinus(): BigDecimal = BigDecimal(-intValue, scale)

    override fun scale(a: BigDecimal, value: Double): BigDecimal = with(BigIntField) {
        BigDecimal(a.intValue * value, a.scale)
    }

    override fun divide(left: BigDecimal, right: BigDecimal): BigDecimal =
        BigDecimal(BigIntField.divide(left.intValue, right.intValue), left.scale - right.scale)

    override fun multiply(left: BigDecimal, right: BigDecimal): BigDecimal =
        BigDecimal(BigIntField.multiply(left.intValue, right.intValue), left.scale + right.scale)


    public operator fun Double.times(other: BigDecimal): BigDecimal = toDecimal() * other

    public operator fun Double.div(other: BigDecimal): BigDecimal = toDecimal() / other

    public operator fun Double.plus(other: BigDecimal): BigDecimal = toDecimal() + other

    public operator fun Double.minus(other: BigDecimal): BigDecimal = toDecimal() - other


    public operator fun BigDecimal.times(other: Double): BigDecimal = this * other.toDecimal()

    public operator fun BigDecimal.div(other: Double): BigDecimal = this / other.toDecimal()

    public operator fun BigDecimal.plus(other: Double): BigDecimal = this + other.toDecimal()

    public operator fun BigDecimal.minus(other: Double): BigDecimal = this - other.toDecimal()
}