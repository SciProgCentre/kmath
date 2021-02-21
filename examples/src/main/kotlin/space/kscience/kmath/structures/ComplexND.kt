@file:Suppress("unused")

package space.kscience.kmath.structures

import space.kscience.kmath.complex.*
import space.kscience.kmath.linear.transpose
import space.kscience.kmath.nd.NDAlgebra
import space.kscience.kmath.nd.NDStructure
import space.kscience.kmath.nd.as2D
import space.kscience.kmath.nd.real
import space.kscience.kmath.operations.invoke
import kotlin.system.measureTimeMillis

fun main() {
    val dim = 1000
    val n = 1000

    val realField = NDAlgebra.real(dim, dim)
    val complexField: ComplexNDField = NDAlgebra.complex(dim, dim)

    val realTime = measureTimeMillis {
        realField {
            var res: NDStructure<Double> = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    println("Real addition completed in $realTime millis")

    val complexTime = measureTimeMillis {
        complexField {
            var res: NDStructure<Complex> = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    println("Complex addition completed in $complexTime millis")
}

fun complexExample() {
    //Create a context for 2-d structure with complex values
    ComplexField {
        nd(4, 8) {
            //a constant real-valued structure
            val x = one * 2.5
            operator fun Number.plus(other: Complex) = Complex(this.toDouble() + other.re, other.im)
            //a structure generator specific to this context
            val matrix = produce { (k, l) -> k + l * i }
            //Perform sum
            val sum = matrix + x + 1.0

            //Represent the sum as 2d-structure and transpose
            sum.as2D().transpose()
        }
    }
}
