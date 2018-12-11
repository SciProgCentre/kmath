package scientifik.kmath.structures

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

expect fun <R> runBlocking(context: CoroutineContext = EmptyCoroutineContext, function: suspend CoroutineScope.()->R): R

val Dispatchers.Math: CoroutineDispatcher get() = Dispatchers.Default