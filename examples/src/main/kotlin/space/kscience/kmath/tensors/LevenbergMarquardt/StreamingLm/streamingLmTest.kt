/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.LevenbergMarquardt.StreamingLm

import space.kscience.kmath.nd.*
import space.kscience.kmath.tensors.LevenbergMarquardt.funcEasyForLm
import space.kscience.kmath.tensors.LevenbergMarquardt.getStartDataForFuncEasy
import kotlin.math.roundToInt

suspend fun main(){
    val startData = getStartDataForFuncEasy()
    // Создание потока:
    val lmFlow = streamLm(::funcEasyForLm, startData, 1000,  10)
    // Запуск потока
    lmFlow.collect { parameters ->
        for (i in 0 until parameters.shape.component1()) {
            val x = (parameters[i, 0] * 10000).roundToInt() / 10000.0
            print("$x ")
            if (i == parameters.shape.component1() - 1) println()
        }
    }
}