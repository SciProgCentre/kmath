package scientifik.kmath.prob

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class MCScopeTest {
    @Test
    fun testGenerator(){
        runBlocking(Dispatchers.Default) {
            mc(1122){
                launch {
                    repeat(9){
                        println("first:${random.nextInt()}")
                    }
                    assertEquals(690819834,random.nextInt())
                }
                launch {
                    repeat(9){
                        println("second:${random.nextInt()}")
                    }
                    assertEquals(691997530,random.nextInt())
                }
            }
        }
    }
}