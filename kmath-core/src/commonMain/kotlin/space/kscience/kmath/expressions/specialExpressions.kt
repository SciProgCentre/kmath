/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.indices

/**
 * Generate a chi squared expression from given x-y-sigma data and inline model. Provides automatic
 * differentiation.
 */
public fun <T : Any, I : Any, A> AutoDiffProcessor<T, I, A>.chiSquaredExpression(
    x: Buffer<T>,
    y: Buffer<T>,
    yErr: Buffer<T>,
    model: A.(I) -> I,
): DifferentiableExpression<T> where A : ExtendedField<I>, A : ExpressionAlgebra<T, I> {
    require(x.size == y.size) { "X and y buffers should be of the same size" }
    require(y.size == yErr.size) { "Y and yErr buffer should of the same size" }

    return differentiate {
        var sum = zero

        x.indices.forEach {
            val xValue = const(x[it])
            val yValue = const(y[it])
            val yErrValue = const(yErr[it])
            val modelValue = model(xValue)
            sum += ((yValue - modelValue) / yErrValue).pow(2)
        }

        sum
    }
}