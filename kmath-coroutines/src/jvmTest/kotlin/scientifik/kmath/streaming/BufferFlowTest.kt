package scientifik.kmath.streaming

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test

@InternalCoroutinesApi
class BufferFlowTest {

    @Test(timeout = 2000)
    fun mapParallel() {
        runBlocking {
            (1..20).asFlow().mapParallel {
                Thread.sleep(200)
                it
            }.collect {
                println("Completed $it")
            }
        }
    }

}