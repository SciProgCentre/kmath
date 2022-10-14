/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import org.nd4j.linalg.factory.Nd4j
import space.kscience.kmath.nd.*
import space.kscience.kmath.nd4j.nd4j
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.viktor.ViktorFieldND
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
    val shape = ShapeND(dim, dim)


    // specialized nd-field for Double. It works as generic Double field as well.
    val doubleField = DoubleField.ndAlgebra
    //A generic field. It should be used for objects, not primitives.
    val genericField = BufferedFieldOpsND(DoubleField)
    // Nd4j specialized field.
    val nd4jField = DoubleField.nd4j
    //viktor field
    val viktorField = ViktorFieldND(dim, dim)
    //parallel processing based on Java Streams
    val parallelField = DoubleField.ndStreaming(dim, dim)

    measureAndPrint("Boxing addition") {
        genericField {
            var res: StructureND<Double> = one(shape)
            repeat(n) { res += 1.0 }
        }
    }

    measureAndPrint("Specialized addition") {
        doubleField {
            var res: StructureND<Double> = one(shape)
            repeat(n) { res += 1.0 }
        }
    }

    measureAndPrint("Nd4j specialized addition") {
        nd4jField {
            var res: StructureND<Double> = one(shape)
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

    measureAndPrint("Lazy addition") {
        val res = doubleField.one(shape).mapAsync(GlobalScope) {
            var c = 0.0
            repeat(n) {
                c += 1.0
            }
            c
        }

        res.elements().forEach { it.second }
    }
}
