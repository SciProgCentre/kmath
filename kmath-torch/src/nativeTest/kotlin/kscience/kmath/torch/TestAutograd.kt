package kscience.kmath.torch

import kotlin.test.*


internal class TestAutograd {
    @Test
    fun testAutoGrad() = TorchTensorFloatAlgebra {
        withCuda { device ->
            testingAutoGrad(device)
        }
    }

    @Test
    fun testBatchedAutoGrad() = TorchTensorFloatAlgebra {
        withCuda { device ->
            testingBatchedAutoGrad(device)
        }
    }
}