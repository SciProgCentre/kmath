/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("unused")

package space.kscience.kmath.structures

import space.kscience.kmath.complex.*
import space.kscience.kmath.linear.transpose
import space.kscience.kmath.nd.AlgebraND
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.nd.double
import space.kscience.kmath.operations.*
import kotlin.system.measureTimeMillis

fun main() {
    val dim = 1000
    val n = 1000

    val doubleField = AlgebraND.double(dim, dim)
    val complexField = doubleField.complex()

    val realTime = measureTimeMillis {
        doubleField {
            var res: StructureND<Double> = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    println("Real addition completed in $realTime millis")

    val complexTime = measureTimeMillis {
        complexField {
            var res: StructureND<Complex<Double>> = one
            repeat(n) {
                res += Complex(1.0, 0.0)
            }
        }
    }

    println("Complex addition completed in $complexTime millis")
}

fun complexExample() {
    //Create a context for 2-d structure with complex values
    AlgebraND.double(4, 8).complex().run {
        //a constant real-valued structure
        val x = one * 2.5
        operator fun Number.plus(other: Complex<Double>) = Complex(toDouble() + other.re, other.im)
        //a structure generator specific to this context
        val matrix = produce { (k, l) -> k + l * i }
        //Perform sum
        val sum = matrix + x + Complex(1.0,0.0)

        //Represent the sum as 2d-structure and transpose
        sum.as2D().transpose()
    }
}
