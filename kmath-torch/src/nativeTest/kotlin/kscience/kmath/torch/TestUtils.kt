package kscience.kmath.torch

import kotlin.test.*


internal val SEED = 987654
internal val TOLERANCE = 1e-6

internal fun testingSetSeed(device: Device = Device.CPU): Unit {
    TorchTensorRealAlgebra {
        setSeed(SEED)
        val normal = randNormal(IntArray(0), device = device).value()
        val uniform = randUniform(IntArray(0), device = device).value()
        setSeed(SEED)
        val nextNormal = randNormal(IntArray(0), device = device).value()
        val nextUniform = randUniform(IntArray(0), device = device).value()
        assertEquals(normal, nextNormal)
        assertEquals(uniform, nextUniform)
    }
}

internal class TestUtils {
    @Test
    fun testSetNumThreads() {
        val numThreads = 2
        setNumThreads(numThreads)
        assertEquals(numThreads, getNumThreads())
    }

    @Test
    fun testSetSeed() = testingSetSeed()
}