/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.tensors.core.algebras

import space.kscience.kmath.tensors.api.AnalyticTensorAlgebra
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.DoubleTensor
import space.kscience.kmath.tensors.core.tensor
import kotlin.math.*

public object DoubleAnalyticTensorAlgebra :
    AnalyticTensorAlgebra<Double>,
    DoubleTensorAlgebra() {
    override fun Tensor<Double>.exp(): DoubleTensor = tensor.map(::exp)

    override fun Tensor<Double>.log(): DoubleTensor = tensor.map(::ln)

    override fun Tensor<Double>.sqrt(): DoubleTensor = tensor.map(::sqrt)

    override fun Tensor<Double>.cos(): DoubleTensor = tensor.map(::cos)

    override fun Tensor<Double>.acos(): DoubleTensor = tensor.map(::acos)

    override fun Tensor<Double>.cosh(): DoubleTensor = tensor.map(::cosh)

    override fun Tensor<Double>.acosh(): DoubleTensor = tensor.map(::acosh)

    override fun Tensor<Double>.sin(): DoubleTensor = tensor.map(::sin)

    override fun Tensor<Double>.asin(): DoubleTensor = tensor.map(::asin)

    override fun Tensor<Double>.sinh(): DoubleTensor = tensor.map(::sinh)

    override fun Tensor<Double>.asinh(): DoubleTensor = tensor.map(::asinh)

    override fun Tensor<Double>.tan(): DoubleTensor = tensor.map(::tan)

    override fun Tensor<Double>.atan(): DoubleTensor = tensor.map(::atan)

    override fun Tensor<Double>.tanh(): DoubleTensor = tensor.map(::tanh)

    override fun Tensor<Double>.atanh(): DoubleTensor = tensor.map(::atanh)

    override fun Tensor<Double>.ceil(): DoubleTensor = tensor.map(::ceil)

    override fun Tensor<Double>.floor(): DoubleTensor = tensor.map(::floor)

}