/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.jafama

import space.kscience.kmath.operations.invoke

fun main() {
    val a = 2.0
    val b = StrictJafamaDoubleField { exp(a) }
    println(JafamaDoubleField { b + a })
    println(StrictJafamaDoubleField { ln(b) })
}
