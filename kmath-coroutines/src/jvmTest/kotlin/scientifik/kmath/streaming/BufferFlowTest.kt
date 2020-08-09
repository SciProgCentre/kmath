package scientifik.kmath.streaming

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import org.junit.jupiter.api.Timeout
import scientifik.kmath.coroutines.async
import scientifik.kmath.coroutines.collect
import scientifik.kmath.coroutines.mapParallel
import java.util.concurrent.Executors
import kotlin.test.Test


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@FlowPreview
class BufferFlowTest {
    val dispatcher: CoroutineDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

    @Test
    @Timeout(2000)
    fun map() {
        runBlocking {
            (1..20).asFlow().mapParallel(dispatcher) {
                println("Started $it on ${Thread.currentThread().name}")
                @Suppress("BlockingMethodInNonBlockingContext")
                Thread.sleep(200)
                it
            }.collect {
                println("Completed $it on ${Thread.currentThread().name}")
            }
        }
    }

    @Test
    @Timeout(2000)
    fun async() {
        runBlocking {
            (1..20).asFlow().async(dispatcher) {
                println("Started $it on ${Thread.currentThread().name}")
                @Suppress("BlockingMethodInNonBlockingContext")
                Thread.sleep(200)
                it
            }.collect(4) {
                println("Completed $it on ${Thread.currentThread().name}")
            }
        }
    }

}