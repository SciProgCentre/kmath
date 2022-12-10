/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.asBuffer
import kotlin.math.*
import kotlin.test.Test
import kotlin.test.assertTrue

internal class TestDoubleAnalyticTensorAlgebra {

    val shape = ShapeND(2, 1, 3, 2)
    val buffer = doubleArrayOf(
        27.1, 20.0, 19.84,
        23.123, 3.0, 2.0,

        3.23, 133.7, 25.3,
        100.3, 11.0, 12.012
    )
    val tensor = DoubleTensor(shape, buffer.asBuffer())

    fun DoubleArray.fmap(transform: (Double) -> Double): DoubleArray {
        return this.map(transform).toDoubleArray()
    }

    fun expectedTensor(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(shape, buffer.fmap(transform).asBuffer())
    }

    @Test
    fun testExp() = DoubleTensorAlgebra {
        assertTrue { exp(tensor) eq expectedTensor(::exp) }
    }

    @Test
    fun testLog() = DoubleTensorAlgebra {
        assertTrue { ln(tensor) eq expectedTensor(::ln) }
    }

    @Test
    fun testSqrt() = DoubleTensorAlgebra {
        assertTrue { sqrt(tensor) eq expectedTensor(::sqrt) }
    }

    @Test
    fun testCos() = DoubleTensorAlgebra {
        assertTrue { cos(tensor) eq expectedTensor(::cos) }
    }


    @Test
    fun testCosh() = DoubleTensorAlgebra {
        assertTrue { cosh(tensor) eq expectedTensor(::cosh) }
    }

    @Test
    fun testAcosh() = DoubleTensorAlgebra {
        assertTrue { acosh(tensor) eq expectedTensor(::acosh) }
    }

    @Test
    fun testSin() = DoubleTensorAlgebra {
        assertTrue { sin(tensor) eq expectedTensor(::sin) }
    }

    @Test
    fun testSinh() = DoubleTensorAlgebra {
        assertTrue { sinh(tensor) eq expectedTensor(::sinh) }
    }

    @Test
    fun testAsinh() = DoubleTensorAlgebra {
        assertTrue { asinh(tensor) eq expectedTensor(::asinh) }
    }

    @Test
    fun testTan() = DoubleTensorAlgebra {
        assertTrue { tan(tensor) eq expectedTensor(::tan) }
    }

    @Test
    fun testAtan() = DoubleTensorAlgebra {
        assertTrue { atan(tensor) eq expectedTensor(::atan) }
    }

    @Test
    fun testTanh() = DoubleTensorAlgebra {
        assertTrue { tanh(tensor) eq expectedTensor(::tanh) }
    }

    @Test
    fun testCeil() = DoubleTensorAlgebra {
        assertTrue { ceil(tensor) eq expectedTensor(::ceil) }
    }

    @Test
    fun testFloor() = DoubleTensorAlgebra {
        assertTrue { floor(tensor) eq expectedTensor(::floor) }
    }

    val shape2 = ShapeND(2, 2)
    val buffer2 = doubleArrayOf(
        1.0, 2.0,
        -3.0, 4.0
    )
    val tensor2 = DoubleTensor(shape2, buffer2.asBuffer())

    @Test
    fun testMin() = DoubleTensorAlgebra {
        assertTrue { tensor2.min() == -3.0 }
        assertTrue {
            tensor2.min(0, true) eq fromArray(
                ShapeND(1, 2),
                doubleArrayOf(-3.0, 2.0)
            )
        }
        assertTrue {
            tensor2.min(1, false) eq fromArray(
                ShapeND(2),
                doubleArrayOf(1.0, -3.0)
            )
        }
    }

    @Test
    fun testMax() = DoubleTensorAlgebra {
        assertTrue { tensor2.max() == 4.0 }
        assertTrue {
            tensor2.max(0, true) eq fromArray(
                ShapeND(1, 2),
                doubleArrayOf(1.0, 4.0)
            )
        }
        assertTrue {
            tensor2.max(1, false) eq fromArray(
                ShapeND(2),
                doubleArrayOf(2.0, 4.0)
            )
        }
    }

    @Test
    fun testSum() = DoubleTensorAlgebra {
        assertTrue { tensor2.sum() == 4.0 }
        assertTrue {
            tensor2.sum(0, true) eq fromArray(
                ShapeND(1, 2),
                doubleArrayOf(-2.0, 6.0)
            )
        }
        assertTrue {
            tensor2.sum(1, false) eq fromArray(
                ShapeND(2),
                doubleArrayOf(3.0, 1.0)
            )
        }
    }

    @Test
    fun testMean() = DoubleTensorAlgebra {
        assertTrue { mean(tensor2) == 1.0 }
        assertTrue {
            mean(tensor2, 0, true) eq fromArray(
                ShapeND(1, 2),
                doubleArrayOf(-1.0, 3.0)
            )
        }
        assertTrue {
            mean(tensor2, 1, false) eq fromArray(
                ShapeND(2),
                doubleArrayOf(1.5, 0.5)
            )
        }
    }

}
