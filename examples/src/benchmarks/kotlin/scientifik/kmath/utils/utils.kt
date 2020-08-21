package scientifik.kmath.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.system.measureTimeMillis

@OptIn(ExperimentalContracts::class)
internal inline fun measureAndPrint(title: String, block: () -> Unit) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    val time = measureTimeMillis(block)
    println("$title completed in $time millis")
}
