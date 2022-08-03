/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core

import space.kscience.kmath.operations.invoke
import kotlin.math.*
import kotlin.test.Test
import kotlin.test.assertTrue

internal class TestDoubleAnalyticTensorAlgebra {

    val shapeWithNegative = intArrayOf(4)
    val bufferWithNegative = doubleArrayOf(9.3348, -7.5889,  -1.2005, 1.1584)
    val tensorWithNegative = DoubleTensor(shapeWithNegative, bufferWithNegative)

    val shape1 = intArrayOf(4)
    val buffer1 = doubleArrayOf(1.3348, 1.5889,  1.2005, 1.1584)
    val tensor1 = DoubleTensor(shape1, buffer1)

    val shape2 = intArrayOf(2, 2)
    val buffer2 = doubleArrayOf(1.0, 9.456, 3.0, 4.0)
    val tensor2 = DoubleTensor(shape2, buffer2)

    val shape3 = intArrayOf(2, 3, 2)
    val buffer3 = doubleArrayOf(1.0, 9.456, 7.0, 2.123, 1.0, 9.456, 30.8888, 6.0, 1.0, 9.456, 3.0, 4.99)
    val tensor3 = DoubleTensor(shape3, buffer3)

    val shape4 = intArrayOf(2, 1, 3, 2)
    val buffer4 = doubleArrayOf(27.1, 20.0, 19.84, 23.123, 3.0, 2.0, 3.23, 133.7, 25.3, 100.3, 11.0, 12.012)
    val tensor4 = DoubleTensor(shape4, buffer4)

    val bufferWithNegativeMod1 = bufferWithNegative.map { x -> x % 1 }.toDoubleArray()
    val tensorWithNegativeMod1 = DoubleTensor(shapeWithNegative, bufferWithNegativeMod1)

    val buffer1Mod1 = buffer1.map { x -> x % 1 }.toDoubleArray()
    val tensor1Mod1 = DoubleTensor(shape1, buffer1Mod1)

    val buffer2Mod1 = buffer2.map { x -> x % 1 }.toDoubleArray()
    val tensor2Mod1 = DoubleTensor(shape2, buffer2Mod1)

    val buffer3Mod1 = buffer3.map { x -> x % 1 }.toDoubleArray()
    val tensor3Mod1 = DoubleTensor(shape3, buffer3Mod1)

    val buffer4Mod1 = buffer4.map { x -> x % 1 }.toDoubleArray()
    val tensor4Mod1 = DoubleTensor(shape4, buffer4Mod1)

    fun DoubleArray.fmap(transform: (Double) -> Double): DoubleArray {
        return this.map(transform).toDoubleArray()
    }

    fun expectedTensorWithNegative(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(shapeWithNegative, bufferWithNegative.fmap(transform))
    }

    fun expectedTensor1(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(shape1, buffer1.fmap(transform))
    }

    fun expectedTensor2(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(shape2, buffer2.fmap(transform))
    }

    fun expectedTensor3(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(shape3, buffer3.fmap(transform))
    }

    fun expectedTensor4(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(shape4, buffer4.fmap(transform))
    }

    fun expectedTensorWithNegativeMod1(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(shapeWithNegative, bufferWithNegativeMod1.fmap(transform))
    }

    fun expectedTensor1Mod1(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(shape1, buffer1Mod1.fmap(transform))
    }

    fun expectedTensor2Mod1(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(shape2, buffer2Mod1.fmap(transform))
    }

    fun expectedTensor3Mod1(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(shape3, buffer3Mod1.fmap(transform))
    }

    fun expectedTensor4Mod1(transform: (Double) -> Double): DoubleTensor {
        return DoubleTensor(shape4, buffer4Mod1.fmap(transform))
    }

    @Test
    fun testExp() = DoubleTensorAlgebra {
        assertTrue { tensorWithNegative.exp() eq expectedTensorWithNegative(::exp) }
        assertTrue { tensor1.exp() eq expectedTensor1(::exp) }
        assertTrue { tensor2.exp() eq expectedTensor2(::exp) }
        assertTrue { tensor3.exp() eq expectedTensor3(::exp) }
        assertTrue { tensor4.exp() eq expectedTensor4(::exp) }
    }

    @Test
    fun testLog() = DoubleTensorAlgebra {
        assertTrue { tensor1.ln() eq expectedTensor1(::ln) }
        assertTrue { tensor2.ln() eq expectedTensor2(::ln) }
        assertTrue { tensor3.ln() eq expectedTensor3(::ln) }
        assertTrue { tensor4.ln() eq expectedTensor4(::ln) }
    }

    @Test
    fun testSqrt() = DoubleTensorAlgebra {
        assertTrue { tensor1.sqrt() eq expectedTensor1(::sqrt) }
        assertTrue { tensor2.sqrt() eq expectedTensor2(::sqrt) }
        assertTrue { tensor3.sqrt() eq expectedTensor3(::sqrt) }
        assertTrue { tensor4.sqrt() eq expectedTensor4(::sqrt) }
    }

    @Test
    fun testCos() = DoubleTensorAlgebra {
        assertTrue { tensorWithNegative.cos() eq expectedTensorWithNegative(::cos) }
        assertTrue { tensor1.cos() eq expectedTensor1(::cos) }
        assertTrue { tensor2.cos() eq expectedTensor2(::cos) }
        assertTrue { tensor3.cos() eq expectedTensor3(::cos) }
        assertTrue { tensor4.cos() eq expectedTensor4(::cos) }
    }

    @Test
    fun testAcos() = DoubleTensorAlgebra {
        assertTrue { tensorWithNegativeMod1.acos() eq expectedTensorWithNegativeMod1(::acos) }
        assertTrue { tensor1Mod1.acos() eq expectedTensor1Mod1(::acos) }
        assertTrue { tensor2Mod1.acos() eq expectedTensor2Mod1(::acos) }
        assertTrue { tensor3Mod1.acos() eq expectedTensor3Mod1(::acos) }
        assertTrue { tensor4Mod1.acos() eq expectedTensor4Mod1(::acos) }
    }

    @Test
    fun testCosh() = DoubleTensorAlgebra {
        assertTrue { tensorWithNegative.cosh() eq expectedTensorWithNegative(::cosh) }
        assertTrue { tensor1.cosh() eq expectedTensor1(::cosh) }
        assertTrue { tensor2.cosh() eq expectedTensor2(::cosh) }
        assertTrue { tensor3.cosh() eq expectedTensor3(::cosh) }
        assertTrue { tensor4.cosh() eq expectedTensor4(::cosh) }
    }

    @Test
    fun testAcosh() = DoubleTensorAlgebra {
        assertTrue { tensor1.acosh() eq expectedTensor1(::acosh) }
        assertTrue { tensor2.acosh() eq expectedTensor2(::acosh) }
        assertTrue { tensor3.acosh() eq expectedTensor3(::acosh) }
        assertTrue { tensor4.acosh() eq expectedTensor4(::acosh) }
    }

    @Test
    fun testSin() = DoubleTensorAlgebra {
        assertTrue { tensorWithNegative.sin() eq expectedTensorWithNegative(::sin) }
        assertTrue { tensor1.sin() eq expectedTensor1(::sin) }
        assertTrue { tensor2.sin() eq expectedTensor2(::sin) }
        assertTrue { tensor3.sin() eq expectedTensor3(::sin) }
        assertTrue { tensor4.sin() eq expectedTensor4(::sin) }
    }

    @Test
    fun testAsin() = DoubleTensorAlgebra {
        assertTrue { tensorWithNegativeMod1.asin() eq expectedTensorWithNegativeMod1(::asin) }
        assertTrue { tensor1Mod1.asin() eq expectedTensor1Mod1(::asin) }
        assertTrue { tensor2Mod1.asin() eq expectedTensor2Mod1(::asin) }
        assertTrue { tensor3Mod1.asin() eq expectedTensor3Mod1(::asin) }
        assertTrue { tensor4Mod1.asin() eq expectedTensor4Mod1(::asin) }
    }

    @Test
    fun testSinh() = DoubleTensorAlgebra {
        assertTrue { tensorWithNegative.sinh() eq expectedTensorWithNegative(::sinh) }
        assertTrue { tensor1.sinh() eq expectedTensor1(::sinh) }
        assertTrue { tensor2.sinh() eq expectedTensor2(::sinh) }
        assertTrue { tensor3.sinh() eq expectedTensor3(::sinh) }
        assertTrue { tensor4.sinh() eq expectedTensor4(::sinh) }
    }

    @Test
    fun testAsinh() = DoubleTensorAlgebra {
        assertTrue { tensorWithNegative.asinh() eq expectedTensorWithNegative(::asinh) }
        assertTrue { tensor1.asinh() eq expectedTensor1(::asinh) }
        assertTrue { tensor2.asinh() eq expectedTensor2(::asinh) }
        assertTrue { tensor3.asinh() eq expectedTensor3(::asinh) }
        assertTrue { tensor4.asinh() eq expectedTensor4(::asinh) }
    }

    @Test
    fun testTan() = DoubleTensorAlgebra {
        assertTrue { tensorWithNegative.tan() eq expectedTensorWithNegative(::tan) }
        assertTrue { tensor1.tan() eq expectedTensor1(::tan) }
        assertTrue { tensor2.tan() eq expectedTensor2(::tan) }
        assertTrue { tensor3.tan() eq expectedTensor3(::tan) }
        assertTrue { tensor4.tan() eq expectedTensor4(::tan) }
    }

    @Test
    fun testAtan() = DoubleTensorAlgebra {
        assertTrue { tensorWithNegative.atan() eq expectedTensorWithNegative(::atan) }
        assertTrue { tensor1.atan() eq expectedTensor1(::atan) }
        assertTrue { tensor2.atan() eq expectedTensor2(::atan) }
        assertTrue { tensor3.atan() eq expectedTensor3(::atan) }
        assertTrue { tensor4.atan() eq expectedTensor4(::atan) }
    }

    @Test
    fun testTanh() = DoubleTensorAlgebra {
        assertTrue { tensorWithNegative.tanh() eq expectedTensorWithNegative(::tanh) }
        assertTrue { tensor1.tanh() eq expectedTensor1(::tanh) }
        assertTrue { tensor2.tanh() eq expectedTensor2(::tanh) }
        assertTrue { tensor3.tanh() eq expectedTensor3(::tanh) }
        assertTrue { tensor4.tanh() eq expectedTensor4(::tanh) }
    }

    @Test
    fun testAtanh() = DoubleTensorAlgebra {
        assertTrue { tensorWithNegativeMod1.atanh() eq expectedTensorWithNegativeMod1(::atanh) }
        assertTrue { tensor1Mod1.atanh() eq expectedTensor1Mod1(::atanh) }
        assertTrue { tensor2Mod1.atanh() eq expectedTensor2Mod1(::atanh) }
        assertTrue { tensor3Mod1.atanh() eq expectedTensor3Mod1(::atanh) }
        assertTrue { tensor4Mod1.atanh() eq expectedTensor4Mod1(::atanh) }
    }

    @Test
    fun testCeil() = DoubleTensorAlgebra {
        assertTrue { tensorWithNegative.ceil() eq expectedTensorWithNegative(::ceil) }
        assertTrue { tensor1.ceil() eq expectedTensor1(::ceil) }
        assertTrue { tensor2.ceil() eq expectedTensor2(::ceil) }
        assertTrue { tensor3.ceil() eq expectedTensor3(::ceil) }
        assertTrue { tensor4.ceil() eq expectedTensor4(::ceil) }
    }

    @Test
    fun testFloor() = DoubleTensorAlgebra {
        assertTrue { tensorWithNegative.floor() eq expectedTensorWithNegative(::floor) }
        assertTrue { tensor1.floor() eq expectedTensor1(::floor) }
        assertTrue { tensor2.floor() eq expectedTensor2(::floor) }
        assertTrue { tensor3.floor() eq expectedTensor3(::floor) }
        assertTrue { tensor4.floor() eq expectedTensor4(::floor) }
    }

    val shape5 = intArrayOf(2, 2)
    val buffer5 = doubleArrayOf(
        1.0, 2.0,
        -3.0, 4.0
    )
    val tensor5 = DoubleTensor(shape5, buffer5)

    @Test
    fun testMin() = DoubleTensorAlgebra {
        assertTrue { tensor5.min() == -3.0 }
        assertTrue { tensor5.min(0, true) eq fromArray(
            intArrayOf(1, 2),
            doubleArrayOf(-3.0, 2.0)
        )}
        assertTrue { tensor5.min(1, false) eq fromArray(
            intArrayOf(2),
            doubleArrayOf(1.0, -3.0)
        )}
    }

    @Test
    fun testMax() = DoubleTensorAlgebra {
        assertTrue { tensor5.max() == 4.0 }
        assertTrue { tensor5.max(0, true) eq fromArray(
            intArrayOf(1, 2),
            doubleArrayOf(1.0, 4.0)
        )}
        assertTrue { tensor5.max(1, false) eq fromArray(
            intArrayOf(2),
            doubleArrayOf(2.0, 4.0)
        )}
    }

    @Test
    fun testSum() = DoubleTensorAlgebra {
        assertTrue { tensor5.sum() == 4.0 }
        assertTrue { tensor5.sum(0, true) eq fromArray(
            intArrayOf(1, 2),
            doubleArrayOf(-2.0, 6.0)
        )}
        assertTrue { tensor5.sum(1, false) eq fromArray(
            intArrayOf(2),
            doubleArrayOf(3.0, 1.0)
        )}
    }

    @Test
    fun testMean() = DoubleTensorAlgebra {
        assertTrue { tensor5.mean() == 1.0 }
        assertTrue { tensor5.mean(0, true) eq fromArray(
            intArrayOf(1, 2),
            doubleArrayOf(-1.0, 3.0)
        )}
        assertTrue { tensor5.mean(1, false) eq fromArray(
            intArrayOf(2),
            doubleArrayOf(1.5, 0.5)
        )}
    }

    @Test
    fun testStd() = DoubleTensorAlgebra {
        assertTrue { floor(tensor5.std() * 10000 ) / 10000 == 2.9439 }
    }

    @Test
    fun testVariance() = DoubleTensorAlgebra {
        assertTrue { floor(tensor5.variance() * 10000 ) / 10000 == 8.6666 }
    }
}
