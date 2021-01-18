package kscience.kmath.torch

import kotlin.test.*


internal class TestUtils {
    @Test
    fun testSetNumThreads() {
        TorchTensorIntAlgebra {
            testingSetNumThreads()
        }
    }

    @Test
    fun testSeedSetting() = TorchTensorFloatAlgebra {
        withCuda {
            device -> testingSetSeed(device)
        }
    }
}