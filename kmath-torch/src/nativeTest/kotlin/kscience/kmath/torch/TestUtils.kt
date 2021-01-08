package kscience.kmath.torch

import kotlin.test.*


internal class TestUtils {
    @Test
    fun settingTorchThreadsCount() {
        val numThreads = 2
        setNumThreads(numThreads)
        assertEquals(numThreads, getNumThreads())
    }
    @Test
    fun seedSetting() = TorchTensorFloatAlgebra {
        setSeed(987654)
        val tensorA = randn(intArrayOf(2,3))
        setSeed(987654)
        val tensorB = randn(intArrayOf(2,3))
        assertTrue(tensorA.asBuffer().contentEquals(tensorB.asBuffer()))
    }
}