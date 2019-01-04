package scientifik.kmath.structures

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

actual fun <R> runBlocking(context: CoroutineContext, function: suspend CoroutineScope.() -> R): R =
    kotlinx.coroutines.runBlocking(context, function)