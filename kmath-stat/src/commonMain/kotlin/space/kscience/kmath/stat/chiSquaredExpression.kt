/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.stat

import space.kscience.kmath.expressions.AutoDiffProcessor
import space.kscience.kmath.expressions.DifferentiableExpression
import space.kscience.kmath.expressions.ExpressionAlgebra
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.asIterable
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.indices
import kotlin.jvm.JvmName

//TODO move to stat

/**
 * Generate a chi squared expression from given x-y-sigma data and inline model. Provides automatic
 * differentiation.
 *
 * **WARNING** All elements of [yErr] must be positive.
 */
@JvmName("genericChiSquaredExpression")
public fun <T : Comparable<T>, I : Any, A> AutoDiffProcessor<T, I, A>.chiSquaredExpression(
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

public fun <I : Any, A> AutoDiffProcessor<Double, I, A>.chiSquaredExpression(
    x: Buffer<Double>,
    y: Buffer<Double>,
    yErr: Buffer<Double>,
    model: A.(I) -> I,
): DifferentiableExpression<Double> where A : ExtendedField<I>, A : ExpressionAlgebra<Double, I> {
    require(yErr.asIterable().all { it > 0.0 }) { "All errors must be strictly positive" }
    return chiSquaredExpression<Double, I, A>(x, y, yErr, model)
}