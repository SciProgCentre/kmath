/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.NumericAlgebra
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.structures.MutableBuffer

/**
 * Class representing both the value and the differentials of a function.
 *
 * This class is the workhorse of the differentiation package.
 *
 * This class is an implementation of the extension to Rall's numbers described in Dan Kalman's paper [Doubly Recursive
 * Multivariate Automatic Differentiation](http://www1.american.edu/cas/mathstat/People/kalman/pdffiles/mmgautodiff.pdf),
 * Mathematics Magazine, vol. 75, no. 3, June 2002. Rall's numbers are an extension to the real numbers used
 * throughout mathematical expressions; they hold the derivative together with the value of a function. Dan Kalman's
 * derivative structures hold all partial derivatives up to any specified order, with respect to any number of free
 * parameters. Rall's numbers therefore can be seen as derivative structures for order one derivative and one free
 * parameter, and real numbers can be seen as derivative structures with zero order derivative and no free parameters.
 *
 * Derived from
 * [Commons Math's `DerivativeStructure`](https://github.com/apache/commons-math/blob/924f6c357465b39beb50e3c916d5eb6662194175/commons-math-legacy/src/main/java/org/apache/commons/math4/legacy/analysis/differentiation/DerivativeStructure.java).
 */
@UnstableKMathAPI
public open class DerivativeStructure<T, A> internal constructor(
    internal val derivativeAlgebra: DerivativeStructureRing<T, A>,
    internal val compiler: DSCompiler<T, A>,
) where A : Ring<T>, A : NumericAlgebra<T>, A : ScaleOperations<T> {
    /**
     * Combined array holding all values.
     */
    internal var data: MutableBuffer<T> =
        derivativeAlgebra.bufferFactory(compiler.size) { derivativeAlgebra.algebra.zero }

    /**
     * Build an instance with all values and derivatives set to 0.
     *
     * @param parameters number of free parameters.
     * @param order derivation order.
     */
    public constructor (
        derivativeAlgebra: DerivativeStructureRing<T, A>,
        parameters: Int,
        order: Int,
    ) : this(
        derivativeAlgebra,
        getCompiler<T, A>(derivativeAlgebra.algebra, derivativeAlgebra.bufferFactory, parameters, order),
    )

    /**
     * Build an instance representing a constant value.
     *
     * @param parameters number of free parameters.
     * @param order derivation order.
     * @param value value of the constant.
     * @see DerivativeStructure
     */
    public constructor (
        derivativeAlgebra: DerivativeStructureRing<T, A>,
        parameters: Int,
        order: Int,
        value: T,
    ) : this(
        derivativeAlgebra,
        parameters,
        order,
    ) {
        data[0] = value
    }

    /**
     * Build an instance representing a variable.
     *
     * Instances built using this constructor are considered to be the free variables with respect to which
     * differentials are computed. As such, their differential with respect to themselves is +1.
     *
     * @param parameters number of free parameters.
     * @param order derivation order.
     * @param index index of the variable (from 0 to `parameters - 1`).
     * @param value value of the variable.
     */
    public constructor (
        derivativeAlgebra: DerivativeStructureRing<T, A>,
        parameters: Int,
        order: Int,
        index: Int,
        value: T,
    ) : this(derivativeAlgebra, parameters, order, value) {
        require(index < parameters) { "number is too large: $index >= $parameters" }

        if (order > 0) {
            // the derivative of the variable with respect to itself is 1.
            data[getCompiler(derivativeAlgebra.algebra, derivativeAlgebra.bufferFactory, index, order).size] =
                derivativeAlgebra.algebra.one
        }
    }

    /**
     * Build an instance from all its derivatives.
     *
     * @param parameters number of free parameters.
     * @param order derivation order.
     * @param derivatives derivatives sorted according to [DSCompiler.getPartialDerivativeIndex].
     */
    public constructor (
        derivativeAlgebra: DerivativeStructureRing<T, A>,
        parameters: Int,
        order: Int,
        vararg derivatives: T,
    ) : this(
        derivativeAlgebra,
        parameters,
        order,
    ) {
        require(derivatives.size == data.size) { "dimension mismatch: ${derivatives.size} and ${data.size}" }
        data = derivativeAlgebra.bufferFactory(data.size) { derivatives[it] }
    }

    /**
     * Copy constructor.
     *
     * @param ds instance to copy.
     */
    internal constructor(ds: DerivativeStructure<T, A>) : this(ds.derivativeAlgebra, ds.compiler) {
        this.data = ds.data.copy()
    }

    /**
     * The number of free parameters.
     */
    public val freeParameters: Int
        get() = compiler.freeParameters

    /**
     * The derivation order.
     */
    public val order: Int
        get() = compiler.order

    /**
     * The value part of the derivative structure.
     *
     * @see getPartialDerivative
     */
    public val value: T
        get() = data[0]

    /**
     * Get a partial derivative.
     *
     * @param orders derivation orders with respect to each variable (if all orders are 0, the value is returned).
     * @return partial derivative.
     * @see value
     */
    public fun getPartialDerivative(vararg orders: Int): T = data[compiler.getPartialDerivativeIndex(*orders)]


    /**
     * Test for the equality of two derivative structures.
     *
     * Derivative structures are considered equal if they have the same number
     * of free parameters, the same derivation order, and the same derivatives.
     *
     * @return `true` if two derivative structures are equal.
     */
    public override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other is DerivativeStructure<*, *>) {
            return ((freeParameters == other.freeParameters) &&
                    (order == other.order) &&
                    data == other.data)
        }

        return false
    }

    public override fun hashCode(): Int =
        227 + 229 * freeParameters + 233 * order + 239 * data.hashCode()
}
