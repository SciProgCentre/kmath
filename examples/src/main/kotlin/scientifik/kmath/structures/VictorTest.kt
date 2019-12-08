package scientifik.kmath.structures

import org.jetbrains.bio.viktor.F64Array
import scientifik.kmath.operations.RealField
import scientifik.kmath.viktor.ViktorNDField

fun main() {
    val dim = 1000
    val n = 400

    // automatically build context most suited for given type.
    val autoField = NDField.auto(RealField, dim, dim)

    val viktorField = ViktorNDField(intArrayOf(dim, dim))

    autoField.run {
        var res = one
        repeat(n/2) {
            res += 1.0
        }
    }

    measureAndPrint("Automatic field addition") {
        autoField.run {
            var res = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    viktorField.run {
        var res = one
        repeat(n/2) {
            res += one
        }
    }

    measureAndPrint("Viktor field addition") {
        viktorField.run {
            var res = one
            repeat(n) {
                res += one
            }
        }
    }

    measureAndPrint("Raw Viktor") {
        val one = F64Array.full(init = 1.0, shape = *intArrayOf(dim, dim))
        var res = one
        repeat(n) {
            res = res + one
        }
    }
}