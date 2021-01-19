package kscience.kmath.torch

import kotlin.test.*


internal class TestUtils {
    @Test
    fun testSetNumThreads() {
        TorchTensorLongAlgebra {
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