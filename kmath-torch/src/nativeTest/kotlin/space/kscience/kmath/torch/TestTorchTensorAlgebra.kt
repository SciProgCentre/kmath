package space.kscience.kmath.torch

import kotlin.test.Test


internal class TestTorchTensorAlgebra {

    @Test
    fun testScalarProduct() = TorchTensorRealAlgebra {
        withChecks {
            withCuda { device ->
                testingScalarProduct(device)
            }
        }
    }

    @Test
    fun testMatrixMultiplication() = TorchTensorRealAlgebra {
        withChecks {
            withCuda { device ->
                testingMatrixMultiplication(device)
            }
        }
    }

    @Test
    fun testLinearStructure() = TorchTensorRealAlgebra {
        withChecks {
            withCuda { device ->
                testingLinearStructure(device)
            }
        }
    }

    @Test
    fun testTensorTransformations() = TorchTensorRealAlgebra {
        withChecks {
            withCuda { device ->
                testingTensorTransformations(device)
            }
        }
    }

    @Test
    fun testBatchedSVD() = TorchTensorRealAlgebra {
        withChecks {
            withCuda { device ->
                testingBatchedSVD(device)
            }
        }
    }

    @Test
    fun testBatchedSymEig() = TorchTensorRealAlgebra {
        withChecks {
            withCuda { device ->
                testingBatchedSymEig(device)
            }
        }
    }

}