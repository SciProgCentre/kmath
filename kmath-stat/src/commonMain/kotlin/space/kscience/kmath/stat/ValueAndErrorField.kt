/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.operations.Field
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A combination of a random [value] and its [dispersion].
 *
 * [dispersion] must be positive.
 */
public data class ValueAndError(val value: Double, val dispersion: Double) {
    init {
        require(dispersion >= 0) { "Dispersion must be non-negative" }
    }

    val error: Double get() = sqrt(dispersion)
}

/**
 * An algebra for double value + its error combination. The multiplication assumes linear error propagation
 */
public object ValueAndErrorField : Field<ValueAndError> {

    override val zero: ValueAndError = ValueAndError(0.0, 0.0)

    override val one: ValueAndError = ValueAndError(1.0, 0.0)

    override fun add(left: ValueAndError, right: ValueAndError): ValueAndError =
        ValueAndError(left.value + right.value, left.dispersion + right.dispersion)

    override fun ValueAndError.unaryMinus(): ValueAndError =
        ValueAndError(-value, dispersion)

    //TODO study performance impact of pow(2). On JVM it does not exist: https://stackoverflow.com/questions/29144275/xx-vs-math-powx-2-java-performance

    override fun multiply(left: ValueAndError, right: ValueAndError): ValueAndError {
        val value = left.value * right.value
        val dispersion = (left.dispersion / left.value.pow(2) + right.dispersion / right.value.pow(2)) * value.pow(2)
        return ValueAndError(value, dispersion)
    }

    override fun divide(left: ValueAndError, right: ValueAndError): ValueAndError {
        val value = left.value / right.value
        val dispersion = (left.dispersion / left.value.pow(2) + right.dispersion / right.value.pow(2)) * value.pow(2)
        return ValueAndError(value, dispersion)
    }

    override fun scale(a: ValueAndError, value: Double): ValueAndError =
        ValueAndError(a.value * value, a.dispersion * value.pow(2))
}