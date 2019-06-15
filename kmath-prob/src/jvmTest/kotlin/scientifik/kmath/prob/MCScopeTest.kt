package scientifik.kmath.prob

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.HashSet
import kotlin.test.Test
import kotlin.test.assertEquals

data class RandomResult(val branch: String, val order: Int, val value: Int)

typealias ATest = suspend CoroutineScope.() -> Set<RandomResult>

class MCScopeTest {
    val test: ATest = {
        mc(1122) {
            val res = Collections.synchronizedSet(HashSet<RandomResult>())

            val job = launch {
                repeat(10) {
                    res.add(RandomResult("first", it, random.nextInt()))
                }
                launch {
                    "empty fork"
                }
            }
            launch {
                repeat(10) {
                    res.add(RandomResult("second", it, random.nextInt()))
                }
            }
            res
        }
    }


    @Test
    fun testGenerator() {
        val res1 = runBlocking(Dispatchers.Default) { test() }
        val res2 = runBlocking(newSingleThreadContext("test")) {test()}
        assertEquals(res1,res2)
    }
}