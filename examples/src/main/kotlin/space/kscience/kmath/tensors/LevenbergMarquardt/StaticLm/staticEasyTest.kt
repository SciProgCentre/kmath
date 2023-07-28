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
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra
import space.kscience.kmath.tensors.core.LMInput
import space.kscience.kmath.tensors.core.levenbergMarquardt
import kotlin.math.roundToInt

fun main() {
    val startedData = getStartDataForFuncEasy()
    val inputData = LMInput(::funcEasyForLm,
        DoubleTensorAlgebra.ones(ShapeND(intArrayOf(4, 1))).as2D(),
        startedData.t,
        startedData.y_dat,
        startedData.weight,
        startedData.dp,
        startedData.p_min,
        startedData.p_max,
        startedData.opts[1].toInt(),
        doubleArrayOf(startedData.opts[2], startedData.opts[3], startedData.opts[4], startedData.opts[5]),
        doubleArrayOf(startedData.opts[6], startedData.opts[7], startedData.opts[8]),
        startedData.opts[9].toInt(),
        10,
        startedData.example_number)

    val result = DoubleTensorAlgebra.levenbergMarquardt(inputData)

    println("Parameters:")
    for (i in 0 until result.resultParameters.shape.component1()) {
        val x = (result.resultParameters[i, 0] * 10000).roundToInt() / 10000.0
        print("$x ")
    }
    println()

    println("Y true and y received:")
    var y_hat_after =  funcDifficultForLm(startedData.t, result.resultParameters, startedData.example_number)
    for (i in 0 until startedData.y_dat.shape.component1()) {
        val x = (startedData.y_dat[i, 0] * 10000).roundToInt() / 10000.0
        val y = (y_hat_after[i, 0] * 10000).roundToInt() / 10000.0
        println("$x $y")
    }

    println("Сhi_sq:")
    println(result.resultChiSq)
    println("Number of iterations:")
    println(result.iterations)
}