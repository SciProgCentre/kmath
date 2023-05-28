/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.LevenbergMarquardt.StaticLm

import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.nd.component1
import space.kscience.kmath.tensors.LevenbergMarquardt.funcDifficultForLm
import space.kscience.kmath.tensors.LevenbergMarquardt.funcEasyForLm
import space.kscience.kmath.tensors.LevenbergMarquardt.getStartDataForFuncEasy
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra
import space.kscience.kmath.tensors.core.internal.LMSettings
import kotlin.math.roundToInt

fun main() {
    val startedData = getStartDataForFuncEasy()

    val result = DoubleTensorAlgebra.lm(
        ::funcEasyForLm,
        DoubleTensorAlgebra.ones(ShapeND(intArrayOf(4, 1))).as2D(),
        startedData.t,
        startedData.y_dat,
        startedData.weight,
        startedData.dp,
        startedData.p_min,
        startedData.p_max,
        startedData.consts,
        startedData.opts,
        10,
        startedData.example_number
    )

    println("Parameters:")
    for (i in 0 until result.result_parameters.shape.component1()) {
        val x = (result.result_parameters[i, 0] * 10000).roundToInt() / 10000.0
        print("$x ")
    }
    println()

    println("Y true and y received:")
    var y_hat_after =  funcDifficultForLm(startedData.t, result.result_parameters, LMSettings(0, 0, startedData.example_number))
    for (i in 0 until startedData.y_dat.shape.component1()) {
        val x = (startedData.y_dat[i, 0] * 10000).roundToInt() / 10000.0
        val y = (y_hat_after[i, 0] * 10000).roundToInt() / 10000.0
        println("$x $y")
    }

    println("Сhi_sq:")
    println(result.result_chi_sq)
    println("Number of iterations:")
    println(result.iterations)
}