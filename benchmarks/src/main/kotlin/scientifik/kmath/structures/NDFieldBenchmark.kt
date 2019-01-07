package scientifik.kmath.structures

import scientifik.kmath.operations.RealField
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val dim = 1000
    val n = 1000

    // automatically build context most suited for given type.
    val autoField = NDField.auto(intArrayOf(dim, dim), RealField)
    // specialized nd-field for Double. It works as generic Double field as well
    val specializedField = NDField.real(intArrayOf(dim, dim))
    //A field implementing lazy computations. All elements are computed on-demand
    val lazyField = NDField.lazy(intArrayOf(dim, dim), RealField)
    //A generic boxing field. It should be used for objects, not primitives.
    val genericField = NDField.buffered(intArrayOf(dim, dim), RealField)


    val autoTime = measureTimeMillis {
        autoField.run {
            var res = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    println("Buffered addition completed in $autoTime millis")

    val elementTime = measureTimeMillis {
        var res = genericField.one
        repeat(n) {
            res += 1.0
        }
    }

    println("Element addition completed in $elementTime millis")

    val specializedTime = measureTimeMillis {
        specializedField.run {
            var res = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    println("Specialized addition completed in $specializedTime millis")


    val lazyTime = measureTimeMillis {
        lazyField.run {
            val res = one.map {
                var c = 0.0
                repeat(n) {
                    c += 1.0
                }
                c
            }

            res.elements().forEach { it.second }
        }
    }

    println("Lazy addition completed in $lazyTime millis")

    val genericTime = measureTimeMillis {
        //genericField.run(action)
        genericField.run {
            var res: NDBuffer<Double> = one
            repeat(n) {
                res += 1.0
            }
        }
    }

    println("Generic addition completed in $genericTime millis")

}