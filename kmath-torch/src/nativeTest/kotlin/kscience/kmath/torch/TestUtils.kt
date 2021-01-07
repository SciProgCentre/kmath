package kscience.kmath.torch

import kotlin.test.Test
import kotlin.test.assertEquals


internal class TestUtils {
    @Test
    fun settingTorchThreadsCount() {
        val numThreads = 2
        setNumThreads(numThreads)
        assertEquals(numThreads, getNumThreads())
    }
}