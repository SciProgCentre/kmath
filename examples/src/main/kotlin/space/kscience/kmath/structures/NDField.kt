/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import org.nd4j.linalg.factory.Nd4j
import space.kscience.kmath.nd.*
import space.kscience.kmath.nd4j.Nd4jArrayField
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.viktor.ViktorNDField
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.system.measureTimeMillis

internal inline fun measureAndPrint(title: String, block: () -> Unit) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    val time = measureTimeMillis(block)
    println("$title completed in $time millis")
}

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    // initializing Nd4j
    Nd4j.zeros(0)
    val dim = 1000
    val n = 1000

    // automatically build context most suited for given type.
    val autoField = AlgebraND.auto(DoubleField, dim, dim)
    // specialized nd-field for Double. It works as generic Double field as well
    val realField = AlgebraND.double(dim, dim)
    //A generic boxing field. It should be used for objects, not primitives.
    val boxingField = AlgebraND.field(DoubleField, Buffer.Companion::boxing, dim, dim)
    // Nd4j specialized field.
    val nd4jField = Nd4jArrayField.real(dim, dim)
    //viktor field
    val viktorField = ViktorNDField(dim, dim)
    //parallel processing based on Java Streams
    val parallelField = AlgebraND.realWithStream(dim, dim)

    measureAndPrint("Boxing addition") {
        boxingField {
            var res: StructureND<Double> = one
            repeat(n) { res += 1.0 }
        }
    }

    measureAndPrint("Specialized addition") {
        realField {
            var res: StructureND<Double> = one
            repeat(n) { res += 1.0 }
        }
    }

    measureAndPrint("Nd4j specialized addition") {
        nd4jField {
            var res: StructureND<Double> = one
            repeat(n) { res += 1.0 }
        }
    }

    measureAndPrint("Viktor addition") {
        viktorField {
            var res: StructureND<Double> = one
            repeat(n) { res += 1.0 }
        }
    }

    measureAndPrint("Parallel stream addition") {
        parallelField {
            var res: StructureND<Double> = one
            repeat(n) { res += 1.0 }
        }
    }

    measureAndPrint("Automatic field addition") {
        autoField {
            var res: StructureND<Double> = one
            repeat(n) { res += 1.0 }
        }
    }

    measureAndPrint("Lazy addition") {
        val res = realField.one.mapAsync(GlobalScope) {
            var c = 0.0
            repeat(n) {
                c += 1.0
            }
            c
        }

        res.elements().forEach { it.second }
    }
}
