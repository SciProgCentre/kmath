/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.LevenbergMarquardt.StaticLm

import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.nd.component1
import space.kscience.kmath.tensors.LevenbergMarquardt.funcDifficultForLm
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.div
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra
import space.kscience.kmath.tensors.core.internal.LMSettings
import kotlin.math.roundToInt

fun main() {
    val NData = 200
    var t_example = DoubleTensorAlgebra.ones(ShapeND(intArrayOf(NData, 1))).as2D()
    for (i in 0 until NData) {
        t_example[i, 0] = t_example[i, 0] * (i + 1) - 104
    }

    val Nparams = 15
    var p_example = DoubleTensorAlgebra.ones(ShapeND(intArrayOf(Nparams, 1))).as2D()
    for (i in 0 until Nparams) {
        p_example[i, 0] = p_example[i, 0] + i - 25
    }

    val settings = LMSettings(0, 0, 1)

    var y_hat =  funcDifficultForLm(t_example, p_example, settings)

    var p_init = DoubleTensorAlgebra.zeros(ShapeND(intArrayOf(Nparams, 1))).as2D()
    for (i in 0 until Nparams) {
        p_init[i, 0] = (p_example[i, 0] + 0.9)
    }

    var t = t_example
    val y_dat = y_hat
    val weight = BroadcastDoubleTensorAlgebra.fromArray(
        ShapeND(intArrayOf(1, 1)), DoubleArray(1) { 1.0 / Nparams * 1.0 - 0.085 }
    ).as2D()
    val dp = BroadcastDoubleTensorAlgebra.fromArray(
        ShapeND(intArrayOf(1, 1)), DoubleArray(1) { -0.01 }
    ).as2D()
    var p_min = DoubleTensorAlgebra.ones(ShapeND(intArrayOf(Nparams, 1)))
    p_min = p_min.div(1.0 / -50.0)
    val p_max = DoubleTensorAlgebra.ones(ShapeND(intArrayOf(Nparams, 1)))
    p_min = p_min.div(1.0 / 50.0)
    val consts = BroadcastDoubleTensorAlgebra.fromArray(
        ShapeND(intArrayOf(1, 1)), doubleArrayOf(0.0)
    ).as2D()
    val opts = doubleArrayOf(3.0, 10000.0, 1e-6, 1e-6, 1e-6, 1e-6, 1e-2, 11.0, 9.0, 1.0)
//    val opts = doubleArrayOf(3.0, 10000.0, 1e-6, 1e-6, 1e-6, 1e-6, 1e-3, 11.0, 9.0, 1.0)

    val result = DoubleTensorAlgebra.lm(
        ::funcDifficultForLm,
        p_init.as2D(),
        t,
        y_dat,
        weight,
        dp,
        p_min.as2D(),
        p_max.as2D(),
        consts,
        opts,
        10,
        1
    )

    println("Parameters:")
    for (i in 0 until result.result_parameters.shape.component1()) {
        val x = (result.result_parameters[i, 0] * 10000).roundToInt() / 10000.0
        print("$x ")
    }
    println()

    println("Y true and y received:")
    var y_hat_after =  funcDifficultForLm(t_example, result.result_parameters, settings)
    for (i in 0 until y_hat.shape.component1()) {
        val x = (y_hat[i, 0] * 10000).roundToInt() / 10000.0
        val y = (y_hat_after[i, 0] * 10000).roundToInt() / 10000.0
        println("$x $y")
    }

    println("Сhi_sq:")
    println(result.result_chi_sq)
    println("Number of iterations:")
    println(result.iterations)
}