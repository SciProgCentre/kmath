/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.distributions

import space.kscience.kmath.operations.DoubleField.pow
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.exp


/**
 * Zelen & Severo approximation for the standard normal CDF.
 * The error is bounded by 7.5 * 10e-8.
 * */
public fun zSNormalCDF(x: Double): Double {

    val t = 1 / (1 + 0.2316419 * x.absoluteValue)
    val summ = 0.319381530*t - 0.356563782*t.pow(2) + 1.781477937*t.pow(3) - 1.821255978*t.pow(4) + 1.330274429*t.pow(5)
    val temp = summ * exp(-x.absoluteValue.pow(2) / 2) / (2 * PI).pow(0.5)
    return if (x >= 0)  1 - temp else temp
}