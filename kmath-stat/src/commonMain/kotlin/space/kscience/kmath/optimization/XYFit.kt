/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.optimization

import space.kscience.kmath.data.ColumnarData
import space.kscience.kmath.expressions.*
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.Field

@UnstableKMathAPI
public interface XYFit<T : Any> : Optimization<T> {

    public val algebra: Field<T>

    /**
     * Set X-Y data for this fit optionally including x and y errors
     */
    public fun data(
        dataSet: ColumnarData<T>,
        xSymbol: Symbol,
        ySymbol: Symbol,
        xErrSymbol: Symbol? = null,
        yErrSymbol: Symbol? = null,
    )

    public fun model(model: (T) -> DifferentiableExpression<T>)

    /**
     * Set the differentiable model for this fit
     */
    public fun <I : Any, A> model(
        autoDiff: AutoDiffProcessor<T, I, A, Expression<T>>,
        modelFunction: A.(I) -> I,
    ): Unit where A : ExtendedField<I>, A : ExpressionAlgebra<T, I> = model { arg ->
        autoDiff.process { modelFunction(const(arg)) }
    }
}