/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.LevenbergMarquardt.StreamingLm

import space.kscience.kmath.nd.*
import space.kscience.kmath.tensors.LevenbergMarquardt.*
import kotlin.math.roundToInt

suspend fun main(){
    val startData = getStartDataForFuncDifficult()
    // Создание потока:
    val lmFlow = streamLm(::funcDifficultForLm, startData, 0,  100)
    var initialTime = System.currentTimeMillis()
    var lastTime: Long
    val launches = mutableListOf<Long>()
    // Запуск потока
    lmFlow.collect { parameters ->
        lastTime = System.currentTimeMillis()
        launches.add(lastTime - initialTime)
        initialTime = lastTime
        for (i in 0 until parameters.shape.component1()) {
            val x = (parameters[i, 0] * 10000).roundToInt() / 10000.0
            print("$x ")
            if (i == parameters.shape.component1() - 1) println()
        }
    }

    println("Average without first is: ${launches.subList(1, launches.size - 1).average()}")
}