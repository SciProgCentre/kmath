/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.structures

import space.kscience.kmath.nd.BufferND
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.mapToBuffer
import kotlin.system.measureTimeMillis

private inline fun <T, reified R : Any> BufferND<T>.mapToBufferND(
    bufferFactory: BufferFactory<R> = BufferFactory.auto(),
    crossinline block: (T) -> R,
): BufferND<R> = BufferND(indices, buffer.mapToBuffer(bufferFactory, block))

@Suppress("UNUSED_VARIABLE")
fun main() {
    val n = 6000
    val structure = StructureND.buffered(ShapeND(n, n), Buffer.Companion::auto) { 1.0 }
    structure.mapToBufferND { it + 1 } // warm-up
    val time1 = measureTimeMillis { val res = structure.mapToBufferND { it + 1 } }
    println("Structure mapping finished in $time1 millis")
    val array = DoubleArray(n * n) { 1.0 }

    val time2 = measureTimeMillis {
        val target = DoubleArray(n * n)
        val res = array.forEachIndexed { index, value -> target[index] = value + 1 }
    }

    println("Array mapping finished in $time2 millis")

    val buffer = DoubleBuffer(DoubleArray(n * n) { 1.0 })

    val time3 = measureTimeMillis {
        val target = DoubleBuffer(DoubleArray(n * n))
        val res = array.forEachIndexed { index, value ->
            target[index] = value + 1
        }
    }
    println("Buffer mapping finished in $time3 millis")
}