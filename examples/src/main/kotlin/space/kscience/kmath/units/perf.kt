/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.units

import space.kscience.kmath.operations.DoubleField
import kotlin.random.Random
import kotlin.time.measureTime

fun main() {
    var rng = Random(0)
    var sum1 = 0.0

    measureTime {
        repeat(10000000) { sum1 += rng.nextDouble() }
    }.also(::println)

    println(sum1)

    rng = Random(0)

    with(DoubleField.measurement()) {
        var sum2 = 0.0 * Pa

        measureTime {
            repeat(10000000) { sum2 += rng.nextDouble() * Pa }
        }.also(::println)

        println(sum2)
    }
}
