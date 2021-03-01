package space.kscience.kmath.torch

import kotlin.test.Test


internal class TestAutograd {
    @Test
    fun testAutoGrad() = TorchTensorFloatAlgebra {
        withChecks {
            withCuda { device ->
                testingAutoGrad(device)
            }
        }
    }

    @Test
    fun testBatchedAutoGrad() = TorchTensorFloatAlgebra {
        withChecks {
            withCuda { device ->
                testingBatchedAutoGrad(device)
            }
        }
    }
}