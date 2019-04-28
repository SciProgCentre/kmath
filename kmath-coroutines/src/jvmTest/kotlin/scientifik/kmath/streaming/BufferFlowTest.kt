package scientifik.kmath.streaming

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import org.junit.Test
import scientifik.kmath.async
import scientifik.kmath.collect

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@FlowPreview
class BufferFlowTest {

    @Test(timeout = 2000)
    fun mapParallel() {
        runBlocking {
            (1..20).asFlow().async(Dispatchers.Default) {
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