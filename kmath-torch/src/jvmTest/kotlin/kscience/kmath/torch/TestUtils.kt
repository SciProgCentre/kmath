package kscience.kmath.torch

import kotlin.test.*


class TestUtils {

    @Test
    fun testSetNumThreads() {
        val numThreads = 2
        setNumThreads(numThreads)
        assertEquals(numThreads, getNumThreads())
    }

    @Test
    fun testCPD() {
        runCPD()
    }
}