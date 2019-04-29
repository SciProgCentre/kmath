package scientifik.kmath.streaming

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Test
import scientifik.kmath.async
import scientifik.kmath.collect
import scientifik.kmath.map


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@FlowPreview
class BufferFlowTest {

    @Test(timeout = 2000)
    fun concurrentMap() {
        runBlocking {
            (1..20).asFlow().map(4) {
                println("Started $it")
                @Suppress("BlockingMethodInNonBlockingContext")
                Thread.sleep(200)
                it
            }.collect {
                println("Completed $it")
            }
        }
    }

    @Test(timeout = 2000)
    fun mapParallel() {
        runBlocking {
            (1..20).asFlow().async {
                println("Started $it")
                @Suppress("BlockingMethodInNonBlockingContext")
                Thread.sleep(200)
                it
            }.collect(4) {
                println("Completed $it")
            }
        }
    }

}