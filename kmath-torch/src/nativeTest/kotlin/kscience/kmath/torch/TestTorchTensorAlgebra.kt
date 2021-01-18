package kscience.kmath.torch

import kotlin.test.*


internal class TestTorchTensorAlgebra {

    @Test
    fun testScalarProduct() = TorchTensorRealAlgebra {
        withCuda { device ->
            testingScalarProduct(device)
        }
    }

    @Test
    fun testMatrixMultiplication() = TorchTensorRealAlgebra {
        withCuda { device ->
            testingMatrixMultiplication(device)
        }
    }

    @Test
    fun testLinearStructure() = TorchTensorRealAlgebra {
        withCuda { device ->
            testingLinearStructure(device)
        }
    }

    @Test
    fun testTensorTransformations() = TorchTensorRealAlgebra {
        withCuda { device ->
            testingTensorTransformations(device)
        }
    }

    @Test
    fun testBatchedSVD() = TorchTensorRealAlgebra {
        withCuda { device ->
            testingBatchedSVD(device)
        }
    }

    @Test
    fun testBatchedSymEig() = TorchTensorRealAlgebra {
        withCuda { device ->
            testingBatchedSymEig(device)
        }
    }

}