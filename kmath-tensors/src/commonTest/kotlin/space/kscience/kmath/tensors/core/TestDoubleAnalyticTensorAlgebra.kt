/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.operations.invoke
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
    fun testExp() = DoubleTensorAlgebra {
        assertTrue { tensor.exp() eq expectedTensor(::exp) }
    }

    @Test
    fun testLog() = DoubleTensorAlgebra {
        assertTrue { tensor.ln() eq expectedTensor(::ln) }
    }

    @Test
    fun testSqrt() = DoubleTensorAlgebra {
        assertTrue { tensor.sqrt() eq expectedTensor(::sqrt) }
    }

    @Test
    fun testCos() = DoubleTensorAlgebra {
        assertTrue { tensor.cos() eq expectedTensor(::cos) }
    }


    @Test
    fun testCosh() = DoubleTensorAlgebra {
        assertTrue { tensor.cosh() eq expectedTensor(::cosh) }
    }

    @Test
    fun testAcosh() = DoubleTensorAlgebra {
        assertTrue { tensor.acosh() eq expectedTensor(::acosh) }
    }

    @Test
    fun testSin() = DoubleTensorAlgebra {
        assertTrue { tensor.sin() eq expectedTensor(::sin) }
    }

    @Test
    fun testSinh() = DoubleTensorAlgebra {
        assertTrue { tensor.sinh() eq expectedTensor(::sinh) }
    }

    @Test
    fun testAsinh() = DoubleTensorAlgebra {
        assertTrue { tensor.asinh() eq expectedTensor(::asinh) }
    }

    @Test
    fun testTan() = DoubleTensorAlgebra {
        assertTrue { tensor.tan() eq expectedTensor(::tan) }
    }

    @Test
    fun testAtan() = DoubleTensorAlgebra {
        assertTrue { tensor.atan() eq expectedTensor(::atan) }
    }

    @Test
    fun testTanh() = DoubleTensorAlgebra {
        assertTrue { tensor.tanh() eq expectedTensor(::tanh) }
    }

    @Test
    fun testCeil() = DoubleTensorAlgebra {
        assertTrue { tensor.ceil() eq expectedTensor(::ceil) }
    }

    @Test
    fun testFloor() = DoubleTensorAlgebra {
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
    fun testMean() = DoubleTensorAlgebra {
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
