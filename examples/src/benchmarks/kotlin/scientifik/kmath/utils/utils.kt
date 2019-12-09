package scientifik.kmath.utils

import kotlin.system.measureTimeMillis

internal inline fun measureAndPrint(title: String, block: () -> Unit) {
    val time = measureTimeMillis(block)
    println("$title completed in $time millis")
}