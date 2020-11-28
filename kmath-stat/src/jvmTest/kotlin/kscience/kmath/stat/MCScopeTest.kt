package kscience.kmath.stat

import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashSet
import kotlin.test.Test
import kotlin.test.assertEquals

data class RandomResult(val branch: String, val order: Int, val value: Int)

typealias ATest = suspend CoroutineScope.() -> Set<RandomResult>

class MCScopeTest {
    val simpleTest: ATest = {
        mcScope(1111) {
            val res = Collections.synchronizedSet(HashSet<RandomResult>())

            launch {
                //println(random)
                repeat(10) {
                    delay(10)
                    res.add(RandomResult("first", it, random.nextInt()))
                }
                launch {
                    //empty fork
                }
            }

            launch {
                //println(random)
                repeat(10) {
                    delay(10)
                    res.add(RandomResult("second", it, random.nextInt()))
                }
            }


            res
        }
    }

    val testWithJoin: ATest = {
        mcScope(1111) {
            val res = Collections.synchronizedSet(HashSet<RandomResult>())

            val job = launch {
                repeat(10) {
                    delay(10)
                    res.add(RandomResult("first", it, random.nextInt()))
                }
            }
            launch {
                repeat(10) {
                    delay(10)
                    if (it == 4) job.join()
                    res.add(RandomResult("second", it, random.nextInt()))
                }
            }

            res
        }
    }


    fun compareResult(test: ATest) {
        val res1 = runBlocking(Dispatchers.Default) { test() }
        val res2 = runBlocking(newSingleThreadContext("test")) { test() }
        assertEquals(
            res1.find { it.branch == "first" && it.order == 7 }?.value,
            res2.find { it.branch == "first" && it.order == 7 }?.value
        )
        assertEquals(res1, res2)
    }

    @Test
    fun testParallel() {
        compareResult(simpleTest)
    }


    @Test
    fun testConditionalJoin() {
        compareResult(testWithJoin)
    }
}