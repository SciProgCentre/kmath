/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.LevenbergMarquardt.StreamingLm

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import space.kscience.kmath.nd.MutableStructure2D
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.nd.component1
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.tensors.LevenbergMarquardt.StartDataLm
import space.kscience.kmath.tensors.core.BroadcastDoubleTensorAlgebra.zeros
import space.kscience.kmath.tensors.core.DoubleTensorAlgebra
import space.kscience.kmath.tensors.core.LMInput
import space.kscience.kmath.tensors.core.levenbergMarquardt
import kotlin.random.Random

fun streamLm(
    lm_func: (MutableStructure2D<Float64>, MutableStructure2D<Float64>, Int) -> (MutableStructure2D<Float64>),
    startData: StartDataLm, launchFrequencyInMs: Long, numberOfLaunches: Int,
): Flow<MutableStructure2D<Float64>> = flow {

    var example_number = startData.example_number
    var p_init = startData.p_init
    var t = startData.t
    var y_dat = startData.y_dat
    val weight = startData.weight
    val dp = startData.dp
    val p_min = startData.p_min
    val p_max = startData.p_max
    val opts = startData.opts

    var steps = numberOfLaunches
    val isEndless = (steps <= 0)

    val inputData = LMInput(
        lm_func,
        p_init,
        t,
        y_dat,
        weight,
        dp,
        p_min,
        p_max,
        opts[1].toInt(),
        doubleArrayOf(opts[2], opts[3], opts[4], opts[5]),
        doubleArrayOf(opts[6], opts[7], opts[8]),
        opts[9].toInt(),
        10,
        example_number
    )

    while (isEndless || steps > 0) {
        val result = DoubleTensorAlgebra.levenbergMarquardt(inputData)
        emit(result.resultParameters)
        delay(launchFrequencyInMs)
        inputData.realValues = generateNewYDat(y_dat, 0.1)
        inputData.startParameters = result.resultParameters
        if (!isEndless) steps -= 1
    }
}

fun generateNewYDat(y_dat: MutableStructure2D<Float64>, delta: Double): MutableStructure2D<Float64> {
    val n = y_dat.shape.component1()
    val y_dat_new = zeros(ShapeND(intArrayOf(n, 1))).as2D()
    for (i in 0 until n) {
        val randomEps = Random.nextDouble(delta + delta) - delta
        y_dat_new[i, 0] = y_dat[i, 0] + randomEps
    }
    return y_dat_new
}