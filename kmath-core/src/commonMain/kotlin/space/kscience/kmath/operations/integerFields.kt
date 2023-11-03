/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.operations.Int16Field.div
import space.kscience.kmath.operations.Int32Field.div
import space.kscience.kmath.operations.Int64Field.div
import space.kscience.kmath.structures.Int16
import space.kscience.kmath.structures.Int32
import space.kscience.kmath.structures.Int64
import space.kscience.kmath.structures.MutableBufferFactory
import kotlin.math.roundToInt
import kotlin.math.roundToLong


/**
 * A [Int16] field with integer division and scale. The division operation is done according to [Short.div] rules.
 *
 * Scaling is done according to [Double.roundToInt] rules.
 *
 * All results are converted to Int16.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public object Int16Field : Field<Int16>, Norm<Int16, Int16>, NumericAlgebra<Int16> {
    override val bufferFactory: MutableBufferFactory<Int16> = MutableBufferFactory<Int16>()
    override val zero: Int16 get() = 0
    override val one: Int16 get() = 1

    override fun number(value: Number): Int16 = value.toShort()
    override fun add(left: Int16, right: Int16): Int16 = (left + right).toShort()
    override fun multiply(left: Int16, right: Int16): Int16 = (left * right).toShort()
    override fun norm(arg: Int16): Int16 = abs(arg)

    override fun scale(a: Int16, value: Double): Int16 = (a*value).roundToInt().toShort()

    override fun divide(left: Int16, right: Int16): Int16 = (left / right).toShort()

    override fun Int16.unaryMinus(): Int16 = (-this).toShort()
}

/**
 * A [Int32] field with integer division and scale. The division operation is done according to [Int.div] rules.
 *
 * Scaling is done according to [Double.roundToInt] rules.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public object Int32Field : Field<Int32>, Norm<Int32, Int32>, NumericAlgebra<Int32> {
    override val bufferFactory: MutableBufferFactory<Int> = MutableBufferFactory()

    override val zero: Int get() = 0
    override val one: Int get() = 1

    override fun number(value: Number): Int = value.toInt()
    override fun add(left: Int, right: Int): Int = left + right
    override fun multiply(left: Int, right: Int): Int = left * right
    override fun norm(arg: Int): Int = abs(arg)

    override fun scale(a: Int, value: Double): Int = (a*value).roundToInt()

    override fun divide(left: Int, right: Int): Int = left / right

    override fun Int.unaryMinus(): Int = -this
}

/**
 * A [Int64] field with integer division and scale. The division operation is done according to [Long.div] rules.
 *
 * Scaling is done according to [Double.roundToLong] rules.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public object Int64Field : Field<Int64>, Norm<Int64, Int64>, NumericAlgebra<Int64> {
    override val bufferFactory: MutableBufferFactory<Int64> = MutableBufferFactory()
    override val zero: Int64 get() = 0L
    override val one: Int64 get() = 1L

    override fun number(value: Number): Int64 = value.toLong()
    override fun add(left: Int64, right: Int64): Int64 = left + right
    override fun multiply(left: Int64, right: Int64): Int64 = left * right
    override fun norm(arg: Int64): Int64 = abs(arg)

    override fun scale(a: Int64, value: Double): Int64 = (a*value).roundToLong()

    override fun divide(left: Int64, right: Int64): Int64 = left / right

    override fun Int64.unaryMinus(): Int64 = -this
}