package kscience.kmath.torch

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


internal class TestUtils {
    @Test
    fun settingTorchThreadsCount(){
        val numThreads = 2
        setNumThreads(numThreads)
        assertEquals(numThreads, getNumThreads())
    }
}