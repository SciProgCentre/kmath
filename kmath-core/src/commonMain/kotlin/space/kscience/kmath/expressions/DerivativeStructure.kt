/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.asBuffer

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
public open class DerivativeStructure<T, A : Ring<T>> @PublishedApi internal constructor(
    private val derivativeAlgebra: DerivativeStructureAlgebra<T, A>,
    @PublishedApi internal val data: Buffer<T>,
) {

    public val compiler: DSCompiler<T, A> get() = derivativeAlgebra.compiler

    /**
     * The number of free parameters.
     */
    public val freeParameters: Int get() = compiler.freeParameters

    /**
     * The derivation order.
     */
    public val order: Int get() = compiler.order

    /**
     * The value part of the derivative structure.
     *
     * @see getPartialDerivative
     */
    public val value: T get() = data[0]

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

    public companion object {

        /**
         * Build an instance representing a variable.
         *
         * Instances built using this constructor are considered to be the free variables with respect to which
         * differentials are computed. As such, their differential with respect to themselves is +1.
         */
        public fun <T, A : Ring<T>> variable(
            derivativeAlgebra: DerivativeStructureAlgebra<T, A>,
            index: Int,
            value: T,
        ): DerivativeStructure<T, A> {
            val compiler = derivativeAlgebra.compiler
            require(index < compiler.freeParameters) { "number is too large: $index >= ${compiler.freeParameters}" }
            return DerivativeStructure(derivativeAlgebra, derivativeAlgebra.bufferForVariable(index, value))
        }

        /**
         * Build an instance from all its derivatives.
         *
         * @param derivatives derivatives sorted according to [DSCompiler.getPartialDerivativeIndex].
         */
        public fun <T, A : Ring<T>> ofDerivatives(
            derivativeAlgebra: DerivativeStructureAlgebra<T, A>,
            vararg derivatives: T,
        ): DerivativeStructure<T, A> {
            val compiler = derivativeAlgebra.compiler
            require(derivatives.size == compiler.size) { "dimension mismatch: ${derivatives.size} and ${compiler.size}" }
            val data = derivatives.asBuffer()

            return DerivativeStructure(
                derivativeAlgebra,
                data
            )
        }
    }
}

@OptIn(UnstableKMathAPI::class)
private fun <T, A : Ring<T>> DerivativeStructureAlgebra<T, A>.bufferForVariable(index: Int, value: T): Buffer<T> {
    val buffer = bufferFactory(compiler.size) { algebra.zero }
    buffer[0] = value
    if (compiler.order > 0) {
        // the derivative of the variable with respect to itself is 1.

        val indexOfDerivative = compiler.getPartialDerivativeIndex(*IntArray(numberOfVariables).apply {
            set(index, 1)
        })

        buffer[indexOfDerivative] = algebra.one
    }
    return buffer
}

/**
 * A class implementing both [DerivativeStructure] and [Symbol].
 */
@UnstableKMathAPI
public class DerivativeStructureSymbol<T, A : Ring<T>> internal constructor(
    derivativeAlgebra: DerivativeStructureAlgebra<T, A>,
    index: Int,
    symbol: Symbol,
    value: T,
) : Symbol by symbol, DerivativeStructure<T, A>(
    derivativeAlgebra, derivativeAlgebra.bufferForVariable(index, value)
) {
    override fun toString(): String = symbol.toString()
    override fun equals(other: Any?): Boolean = (other as? Symbol) == symbol
    override fun hashCode(): Int = symbol.hashCode()
}
