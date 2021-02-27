/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.units

import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.units.*

fun main() {
    var res = with(DoubleField.measurement()) {
        val a = (2.0 * kg) / (3.0 * m) / (2.0 * with(MeasureAlgebra) { s * s })
        val b = (23.0 * Pa)
        a + b
    }

    println(res)

    res = with(DoubleField.measurement()) {
        val a = (2.0 * G(m)) + (3.0 * M(m))
        val b = (3.0 * au)
        a + b
    }

    println(res)

    res = with(DoubleField.measurement()) {
        val P = 100000.0 * Pa
        val V = 0.0227 * (m pow 3)
        val nu = 1.0 * mol
        val T = 273.0 * K
        P * V / (nu * T)
    }

    println(res)
}
