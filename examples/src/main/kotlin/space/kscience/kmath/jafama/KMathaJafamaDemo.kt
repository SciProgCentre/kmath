/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.jafama

import net.jafama.FastMath


fun main(){
    val a = JafamaDoubleField.number(2.0)
    val b = StrictJafamaDoubleField.power(FastMath.E,a)

    println(JafamaDoubleField.add(b,a))
    println(StrictJafamaDoubleField.ln(b))
}
