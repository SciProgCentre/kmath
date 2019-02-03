package scientifik.kmath.structures

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

actual fun <R> runBlocking(
    context: CoroutineContext,
    function: suspend CoroutineScope.() -> R
): R {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}