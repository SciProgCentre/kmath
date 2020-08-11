package scientifik.kmath.structures

import kotlinx.coroutines.GlobalScope
import scientifik.kmath.operations.RealField
import kotlin.system.measureTimeMillis

internal inline fun measureAndPrint(title: String, block: () -> Unit) {
    val time = measureTimeMillis(block)
    println("$title completed in $time millis")
}


fun main() {
    val dim = 1000
    val n = 1000

    // automatically build context most suited for given type.
    val autoField = NDField.auto(RealField, dim, dim)
    // specialized nd-field for Double. It works as generic Double field as well
    val specializedField = NDField.real(dim, dim)
    //A generic boxing field. It should be used for objects, not primitives.
    val genericField = NDField.boxing(RealField, dim, dim)

    measureAndPrint("Automatic field addition") {
        autoField.run {
            var res: NDBuffer<Double> = one
            repeat(n) {
                res += number(1.0)
            }
        }
    }

    measureAndPrint("Element addition") {
        var res = genericField.one
        repeat(n) {
            res += 1.0
        }
    }

    measureAndPrint("Specialized addition") {
        specializedField.run {
            var res: NDBuffer<Double> = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    measureAndPrint("Lazy addition") {
        val res = specializedField.one.mapAsync(GlobalScope) {
            var c = 0.0
            repeat(n) {
                c += 1.0
            }
            c
        }

        res.elements().forEach { it.second }
    }

    measureAndPrint("Generic addition") {
        //genericField.run(action)
        genericField.run {
            var res: NDBuffer<Double> = one
            repeat(n) {
                res += one // con't avoid using `one` due to resolution ambiguity
            }
        }
    }

}