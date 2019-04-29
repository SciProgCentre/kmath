package scientifik.kmath.streaming

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Test
import scientifik.kmath.async
import scientifik.kmath.collect
import scientifik.kmath.map
import java.util.concurrent.Executors


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@FlowPreview
class BufferFlowTest {

    val dispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

    @Test(timeout = 2000)
    fun map() {
        runBlocking {
            (1..20).asFlow().map( dispatcher) {
                println("Started $it on ${Thread.currentThread().name}")
                @Suppress("BlockingMethodInNonBlockingContext")
                Thread.sleep(200)
                it
            }.collect {
                println("Completed $it on ${Thread.currentThread().name}")
            }
        }
    }

    @Test(timeout = 2000)
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