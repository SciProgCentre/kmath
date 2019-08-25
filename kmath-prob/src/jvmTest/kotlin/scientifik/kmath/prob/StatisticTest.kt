package scientifik.kmath.prob

import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import scientifik.kmath.chains.flow
import scientifik.kmath.streaming.chunked
import kotlin.test.Test

class StatisticTest {
    //create a random number generator.
    val generator = DefaultGenerator(1)
    //Create a stateless chain from generator.
    val data = generator.chain { nextDouble() }
    //Convert a chaint to Flow and break it into chunks.
    val chunked = data.flow().chunked(1000)

    @Test
    fun testParallelMean() {
        runBlocking {
            val average = Mean.real
                .flow(chunked) //create a flow with results
                .drop(99) // Skip first 99 values and use one with total data
                .first() //get 1e5 data samples average
            println(average)
        }
    }
}