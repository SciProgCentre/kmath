package space.kscience.kmath.stat

import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

import space.kscience.kmath.streaming.chunked
import kotlin.test.Test

internal class StatisticTest {
    //create a random number generator.
    val generator = RandomGenerator.default(1)

    //Create a stateless chain from generator.
    val data = generator.chain { nextDouble() }

    //Convert a chain to Flow and break it into chunks.
    val chunked = data.chunked(1000)

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
