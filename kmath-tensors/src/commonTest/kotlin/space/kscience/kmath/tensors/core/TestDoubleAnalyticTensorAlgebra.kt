package space.kscience.kmath.tensors.core

import space.kscience.kmath.operations.invoke
import space.kscience.kmath.tensors.core.algebras.DoubleAnalyticTensorAlgebra
import space.kscience.kmath.tensors.core.algebras.DoubleTensorAlgebra
import kotlin.math.*
import kotlin.test.Test
import kotlin.test.assertTrue

internal class TestDoubleAnalyticTensorAlgebra {

    val shape = intArrayOf(2, 1, 3, 2)
    val buffer = doubleArrayOf(
        27.1, 20.0, 19.84,
        23.123, 3.0, 2.0,

        3.23, 133.7, 25.3,
        100.3, 11.0, 12.012
    )
    val tensor = DoubleTensor(shape, buffer)

    fun DoubleArray.fmap(transform: (Double) -> Double): DoubleArray {
        return this.map(transform).toDoubleArray()
    }

    fun expectedTensor(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(shape, buffer.fmap(transform))
    }

    @Test
    fun testExp() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor.exp() eq expectedTensor(::exp) }
    }

    @Test
    fun testLog() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor.ln() eq expectedTensor(::ln) }
    }

    @Test
    fun testSqrt() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor.sqrt() eq expectedTensor(::sqrt) }
    }

    @Test
    fun testCos() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor.cos() eq expectedTensor(::cos) }
    }


    @Test
    fun testCosh() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor.cosh() eq expectedTensor(::cosh) }
    }

    @Test
    fun testAcosh() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor.acosh() eq expectedTensor(::acosh) }
    }

    @Test
    fun testSin() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor.sin() eq expectedTensor(::sin) }
    }

    @Test
    fun testSinh() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor.sinh() eq expectedTensor(::sinh) }
    }

    @Test
    fun testAsinh() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor.asinh() eq expectedTensor(::asinh) }
    }

    @Test
    fun testTan() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor.tan() eq expectedTensor(::tan) }
    }

    @Test
    fun testAtan() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor.atan() eq expectedTensor(::atan) }
    }

    @Test
    fun testTanh() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor.tanh() eq expectedTensor(::tanh) }
    }

    @Test
    fun testCeil() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor.ceil() eq expectedTensor(::ceil) }
    }

    @Test
    fun testFloor() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor.floor() eq expectedTensor(::floor) }
    }

    val shape2 = intArrayOf(2, 2)
    val buffer2 = doubleArrayOf(
        1.0, 2.0,
        -3.0, 4.0
    )
    val tensor2 = DoubleTensor(shape2, buffer2)

    @Test
    fun testMin() = DoubleTensorAlgebra {
        assertTrue { tensor2.min() == -3.0 }
        assertTrue { tensor2.min(0, true) eq fromArray(
            intArrayOf(1, 2),
            doubleArrayOf(-3.0, 2.0)
        )}
        assertTrue { tensor2.min(1, false) eq fromArray(
            intArrayOf(2),
            doubleArrayOf(1.0, -3.0)
        )}
    }

    @Test
    fun testMax() = DoubleTensorAlgebra {
        assertTrue { tensor2.max() == 4.0 }
        assertTrue { tensor2.max(0, true) eq fromArray(
            intArrayOf(1, 2),
            doubleArrayOf(1.0, 4.0)
        )}
        assertTrue { tensor2.max(1, false) eq fromArray(
            intArrayOf(2),
            doubleArrayOf(2.0, 4.0)
        )}
    }

    @Test
    fun testSum() = DoubleTensorAlgebra {
        assertTrue { tensor2.sum() == 4.0 }
        assertTrue { tensor2.sum(0, true) eq fromArray(
            intArrayOf(1, 2),
            doubleArrayOf(-2.0, 6.0)
        )}
        assertTrue { tensor2.sum(1, false) eq fromArray(
            intArrayOf(2),
            doubleArrayOf(3.0, 1.0)
        )}
    }

    @Test
    fun testMean() = DoubleAnalyticTensorAlgebra {
        assertTrue { tensor2.mean() == 1.0 }
        assertTrue { tensor2.mean(0, true) eq fromArray(
            intArrayOf(1, 2),
            doubleArrayOf(-1.0, 3.0)
        )}
        assertTrue { tensor2.mean(1, false) eq fromArray(
            intArrayOf(2),
            doubleArrayOf(1.5, 0.5)
        )}
    }

}