/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.MutableBufferFactory
import space.kscience.kmath.structures.asBuffer
import kotlin.math.max
import kotlin.math.min

/**
 * Class representing both the value and the differentials of a function.
 *
 * This class is the workhorse of the differentiation package.
 *
 * This class is an implementation of the extension to Rall's numbers described in Dan Kalman's paper
 * [Doubly Recursive Multivariate Automatic Differentiation](http://www1.american.edu/cas/mathstat/People/kalman/pdffiles/mmgautodiff.pdf),
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
public interface DS<T, A : Ring<T>> {
    public val derivativeAlgebra: DSAlgebra<T, A>
    public val data: Buffer<T>
}

/**
 * Get a partial derivative.
 *
 * @param orders derivation orders with respect to each variable (if all orders are 0, the value is returned).
 * @return partial derivative.
 * @see value
 */
@UnstableKMathAPI
private fun <T, A : Ring<T>> DS<T, A>.getPartialDerivative(vararg orders: Int): T =
    data[derivativeAlgebra.compiler.getPartialDerivativeIndex(*orders)]

/**
 * Provide a partial derivative with given symbols. On symbol could me mentioned multiple times
 */
@UnstableKMathAPI
public fun <T, A : Ring<T>> DS<T, A>.derivative(symbols: List<Symbol>): T {
    require(symbols.size <= derivativeAlgebra.order) { "The order of derivative ${symbols.size} exceeds computed order ${derivativeAlgebra.order}" }
    val ordersCount: Map<String, Int> = symbols.map { it.identity }.groupBy { it }.mapValues { it.value.size }
    return getPartialDerivative(*symbols.map { ordersCount[it] ?: 0 }.toIntArray())
}

/**
 * Provide a partial derivative with given symbols. On symbol could me mentioned multiple times
 */
@UnstableKMathAPI
public fun <T, A : Ring<T>> DS<T, A>.derivative(vararg symbols: Symbol): T {
    require(symbols.size <= derivativeAlgebra.order) { "The order of derivative ${symbols.size} exceeds computed order ${derivativeAlgebra.order}" }
    val ordersCount: Map<String, Int> = symbols.map { it.identity }.groupBy { it }.mapValues { it.value.size }
    return getPartialDerivative(*symbols.map { ordersCount[it] ?: 0 }.toIntArray())
}

/**
 * The value part of the derivative structure.
 *
 * @see getPartialDerivative
 */
@UnstableKMathAPI
public val <T, A : Ring<T>> DS<T, A>.value: T get() = data[0]

@UnstableKMathAPI
public abstract class DSAlgebra<T, A : Ring<T>>(
    public val algebra: A,
    public val order: Int,
    bindings: Map<Symbol, T>,
) : ExpressionAlgebra<T, DS<T, A>>, SymbolIndexer {

    /**
     * Get the compiler for number of free parameters and order.
     *
     * @return cached rules set.
     */
    @PublishedApi
    internal val compiler: DSCompiler<T, A> by lazy {
        val numberOfVariables = bindings.size
        // get the cached compilers
        val cache: Array<Array<DSCompiler<T, A>?>>? = null

        // we need to create more compilers
        val maxParameters: Int = max(numberOfVariables, cache?.size ?: 0)
        val maxOrder: Int = max(order, if (cache == null) 0 else cache[0].size)
        val newCache: Array<Array<DSCompiler<T, A>?>> = Array(maxParameters + 1) { arrayOfNulls(maxOrder + 1) }

        if (cache != null) {
            // preserve the already created compilers
            for (i in cache.indices) {
                cache[i].copyInto(newCache[i], endIndex = cache[i].size)
            }
        }

        // create the array in increasing diagonal order
        for (diag in 0..numberOfVariables + order) {
            for (o in max(0, diag - numberOfVariables)..min(order, diag)) {
                val p: Int = diag - o
                if (newCache[p][o] == null) {
                    val valueCompiler: DSCompiler<T, A>? = if (p == 0) null else newCache[p - 1][o]!!
                    val derivativeCompiler: DSCompiler<T, A>? = if (o == 0) null else newCache[p][o - 1]!!

                    newCache[p][o] = DSCompiler(
                        algebra,
                        p,
                        o,
                        valueCompiler,
                        derivativeCompiler,
                    )
                }
            }
        }

        return@lazy newCache[numberOfVariables][order]!!
    }

    private val variables: Map<Symbol, DSSymbol> by lazy {
        bindings.entries.mapIndexed { index, (key, value) ->
            key to DSSymbol(
                index,
                key,
                value,
            )
        }.toMap()
    }
    override val symbols: List<Symbol> = bindings.map { it.key }

    private fun bufferForVariable(index: Int, value: T): Buffer<T> {
        val buffer = algebra.bufferFactory(compiler.size) { algebra.zero }
        buffer[0] = value
        if (compiler.order > 0) {
            // the derivative of the variable with respect to itself is 1.

            val indexOfDerivative = compiler.getPartialDerivativeIndex(*IntArray(symbols.size).apply {
                set(index, 1)
            })

            buffer[indexOfDerivative] = algebra.one
        }
        return buffer
    }

    @UnstableKMathAPI
    private inner class DSImpl(
        override val data: Buffer<T>,
    ) : DS<T, A> {
        override val derivativeAlgebra: DSAlgebra<T, A> get() = this@DSAlgebra
    }

    protected fun DS(data: Buffer<T>): DS<T, A> = DSImpl(data)


    /**
     * Build an instance representing a variable.
     *
     * Instances built using this constructor are considered to be the free variables with respect to which
     * differentials are computed. As such, their differential with respect to themselves is +1.
     */
    public fun variable(
        index: Int,
        value: T,
    ): DS<T, A> {
        require(index < compiler.freeParameters) { "number is too large: $index >= ${compiler.freeParameters}" }
        return DS(bufferForVariable(index, value))
    }

    /**
     * Build an instance from all its derivatives.
     *
     * @param derivatives derivatives sorted according to [DSCompiler.getPartialDerivativeIndex].
     */
    public fun ofDerivatives(
        vararg derivatives: T,
    ): DS<T, A> {
        require(derivatives.size == compiler.size) { "dimension mismatch: ${derivatives.size} and ${compiler.size}" }
        val data = derivatives.asBuffer()

        return DS(data)
    }

    /**
     * A class implementing both [DS] and [Symbol].
     */
    @UnstableKMathAPI
    public inner class DSSymbol internal constructor(
        index: Int,
        symbol: Symbol,
        value: T,
    ) : Symbol by symbol, DS<T, A> {
        override val derivativeAlgebra: DSAlgebra<T, A> get() = this@DSAlgebra
        override val data: Buffer<T> = bufferForVariable(index, value)
    }

    public override fun const(value: T): DS<T, A> {
        val buffer = algebra.bufferFactory(compiler.size) { algebra.zero }
        buffer[0] = value

        return DS(buffer)
    }

    override fun bindSymbolOrNull(value: String): DSSymbol? = variables[StringSymbol(value)]

    override fun bindSymbol(value: String): DSSymbol =
        bindSymbolOrNull(value) ?: error("Symbol '$value' is not supported in $this")

    public fun bindSymbolOrNull(symbol: Symbol): DSSymbol? = variables[symbol.identity]

    public fun bindSymbol(symbol: Symbol): DSSymbol =
        bindSymbolOrNull(symbol.identity) ?: error("Symbol '${symbol}' is not supported in $this")

    public fun DS<T, A>.derivative(symbols: List<Symbol>): T {
        require(symbols.size <= order) { "The order of derivative ${symbols.size} exceeds computed order $order" }
        val ordersCount = symbols.groupBy { it }.mapValues { it.value.size }
        return getPartialDerivative(*variables.keys.map { ordersCount[it] ?: 0 }.toIntArray())
    }

    public fun DS<T, A>.derivative(vararg symbols: Symbol): T = derivative(symbols.toList())

}


/**
 * A ring over [DS].
 *
 * @property order The derivation order.
 * @param bindings The map of bindings values. All bindings are considered free parameters.
 */
@UnstableKMathAPI
public open class DSRing<T, A>(
    algebra: A,
    order: Int,
    bindings: Map<Symbol, T>,
) : DSAlgebra<T, A>(algebra, order, bindings),
    Ring<DS<T, A>>,
    ScaleOperations<DS<T, A>>,
    NumericAlgebra<DS<T, A>>,
    NumbersAddOps<DS<T, A>>
        where A : Ring<T>, A : NumericAlgebra<T>, A : ScaleOperations<T> {

    public val elementBufferFactory: MutableBufferFactory<T> = algebra.bufferFactory

    override fun bindSymbolOrNull(value: String): DSSymbol? =
        super<DSAlgebra>.bindSymbolOrNull(value)

    override fun DS<T, A>.unaryMinus(): DS<T, A> = mapData { -it }

    /**
     * Create a copy of given [Buffer] and modify it according to [block]
     */
    protected inline fun DS<T, A>.transformDataBuffer(block: A.(MutableBuffer<T>) -> Unit): DS<T, A> {
        require(derivativeAlgebra == this@DSRing) { "All derivative operations should be done in the same algebra" }
        val newData = elementBufferFactory(compiler.size) { data[it] }
        algebra.block(newData)
        return DS(newData)
    }

    protected fun DS<T, A>.mapData(block: A.(T) -> T): DS<T, A> {
        require(derivativeAlgebra == this@DSRing) { "All derivative operations should be done in the same algebra" }
        val newData: Buffer<T> = data.mapToBuffer(elementBufferFactory) {
            algebra.block(it)
        }
        return DS(newData)
    }

    protected fun DS<T, A>.mapDataIndexed(block: (Int, T) -> T): DS<T, A> {
        require(derivativeAlgebra == this@DSRing) { "All derivative operations should be done in the same algebra" }
        val newData: Buffer<T> = data.mapIndexedToBuffer(elementBufferFactory, block)
        return DS(newData)
    }

    override val zero: DS<T, A> by lazy {
        const(algebra.zero)
    }

    override val one: DS<T, A> by lazy {
        const(algebra.one)
    }

    override fun number(value: Number): DS<T, A> = const(algebra.number(value))

    override fun add(left: DS<T, A>, right: DS<T, A>): DS<T, A> = left.transformDataBuffer { result ->
        require(right.derivativeAlgebra == this@DSRing) { "All derivative operations should be done in the same algebra" }
        compiler.add(left.data, 0, right.data, 0, result, 0)
    }

    override fun scale(a: DS<T, A>, value: Double): DS<T, A> = a.mapData {
        it.times(value)
    }

    override fun multiply(
        left: DS<T, A>,
        right: DS<T, A>,
    ): DS<T, A> = left.transformDataBuffer { result ->
        compiler.multiply(left.data, 0, right.data, 0, result, 0)
    }
//
//    override fun DS<T, A>.minus(arg: DS): DS<T, A> = transformDataBuffer { result ->
//        subtract(data, 0, arg.data, 0, result, 0)
//    }

    override operator fun DS<T, A>.plus(other: Number): DS<T, A> = transformDataBuffer {
        it[0] += number(other)
    }

//
//    override operator fun DS<T, A>.minus(other: Number): DS<T, A> =
//        this + (-other.toDouble())

    override operator fun Number.plus(other: DS<T, A>): DS<T, A> = other + this
    override operator fun Number.minus(other: DS<T, A>): DS<T, A> = other - this
}

@UnstableKMathAPI
public class DerivativeStructureRingExpression<T, A>(
    public val algebra: A,
    public val elementBufferFactory: MutableBufferFactory<T> = algebra.bufferFactory,
    public val function: DSRing<T, A>.() -> DS<T, A>,
) : DifferentiableExpression<T> where A : Ring<T>, A : ScaleOperations<T>, A : NumericAlgebra<T> {
    override operator fun invoke(arguments: Map<Symbol, T>): T =
        DSRing(algebra, 0, arguments).function().value

    override fun derivativeOrNull(symbols: List<Symbol>): Expression<T> = Expression { arguments ->
        with(
            DSRing(
                algebra,
                symbols.size,
                arguments
            )
        ) { function().derivative(symbols) }
    }
}

/**
 * A field over [DS].
 *
 * @property order The derivation order.
 * @param bindings The map of bindings values. All bindings are considered free parameters.
 */
@UnstableKMathAPI
public class DSField<T, A : ExtendedField<T>>(
    algebra: A,
    order: Int,
    bindings: Map<Symbol, T>,
) : DSRing<T, A>(algebra, order, bindings), ExtendedField<DS<T, A>> {
    override fun number(value: Number): DS<T, A> = const(algebra.number(value))

    override fun divide(left: DS<T, A>, right: DS<T, A>): DS<T, A> = left.transformDataBuffer { result ->
        compiler.divide(left.data, 0, right.data, 0, result, 0)
    }

    override fun sin(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.sin(arg.data, 0, result, 0)
    }

    override fun cos(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.cos(arg.data, 0, result, 0)
    }

    override fun tan(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.tan(arg.data, 0, result, 0)
    }

    override fun asin(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.asin(arg.data, 0, result, 0)
    }

    override fun acos(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.acos(arg.data, 0, result, 0)
    }

    override fun atan(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.atan(arg.data, 0, result, 0)
    }

    override fun sinh(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.sinh(arg.data, 0, result, 0)
    }

    override fun cosh(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.cosh(arg.data, 0, result, 0)
    }

    override fun tanh(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.tanh(arg.data, 0, result, 0)
    }

    override fun asinh(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.asinh(arg.data, 0, result, 0)
    }

    override fun acosh(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.acosh(arg.data, 0, result, 0)
    }

    override fun atanh(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.atanh(arg.data, 0, result, 0)
    }

    override fun power(arg: DS<T, A>, pow: Number): DS<T, A> = when (pow) {
        is Int -> arg.transformDataBuffer { result ->
            compiler.pow(arg.data, 0, pow, result, 0)
        }

        else -> arg.transformDataBuffer { result ->
            compiler.pow(arg.data, 0, pow.toDouble(), result, 0)
        }
    }

    override fun sqrt(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.sqrt(arg.data, 0, result, 0)
    }

    public fun power(arg: DS<T, A>, pow: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.pow(arg.data, 0, pow.data, 0, result, 0)
    }

    override fun exp(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.exp(arg.data, 0, result, 0)
    }

    override fun ln(arg: DS<T, A>): DS<T, A> = arg.transformDataBuffer { result ->
        compiler.ln(arg.data, 0, result, 0)
    }
}

@UnstableKMathAPI
public class DSFieldExpression<T, A : ExtendedField<T>>(
    public val algebra: A,
    public val function: DSField<T, A>.() -> DS<T, A>,
) : DifferentiableExpression<T> {
    override operator fun invoke(arguments: Map<Symbol, T>): T =
        DSField(algebra, 0, arguments).function().value

    override fun derivativeOrNull(symbols: List<Symbol>): Expression<T> = Expression { arguments ->
        DSField(
            algebra,
            symbols.size,
            arguments,
        ).run { function().derivative(symbols) }
    }
}


@UnstableKMathAPI
public class DSFieldProcessor<T, A : ExtendedField<T>>(
    public val algebra: A,
) : AutoDiffProcessor<T, DS<T, A>, DSField<T, A>> {
    override fun differentiate(
        function: DSField<T, A>.() -> DS<T, A>,
    ): DifferentiableExpression<T> = DSFieldExpression(algebra, function)
}

@UnstableKMathAPI
public val Double.Companion.autodiff: DSFieldProcessor<Double, DoubleField> get() = DSFieldProcessor(DoubleField)