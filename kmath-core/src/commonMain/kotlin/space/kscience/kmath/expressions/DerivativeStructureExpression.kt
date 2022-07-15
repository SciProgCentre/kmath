/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.MutableBuffer
import space.kscience.kmath.structures.MutableBufferFactory
import kotlin.math.max
import kotlin.math.min

@UnstableKMathAPI
public abstract class DerivativeStructureAlgebra<T, A : Ring<T>>(
    public val algebra: A,
    public val bufferFactory: MutableBufferFactory<T>,
    public val order: Int,
    bindings: Map<Symbol, T>,
) : ExpressionAlgebra<T, DerivativeStructure<T, A>> {

    public val numberOfVariables: Int = bindings.size


    /**
     * Get the compiler for number of free parameters and order.
     *
     * @return cached rules set.
     */
    @PublishedApi
    internal val compiler: DSCompiler<T, A> by lazy {
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
                        bufferFactory,
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

    private val variables: Map<Symbol, DerivativeStructureSymbol<T, A>> =
        bindings.entries.mapIndexed { index, (key, value) ->
            key to DerivativeStructureSymbol(
                this,
                index,
                key,
                value,
            )
        }.toMap()



    public override fun const(value: T): DerivativeStructure<T, A> {
        val buffer = bufferFactory(compiler.size) { algebra.zero }
        buffer[0] = value

        return DerivativeStructure(
            this,
            buffer
        )
    }

    override fun bindSymbolOrNull(value: String): DerivativeStructureSymbol<T, A>? = variables[StringSymbol(value)]

    override fun bindSymbol(value: String): DerivativeStructureSymbol<T, A> =
        bindSymbolOrNull(value) ?: error("Symbol '$value' is not supported in $this")

    public fun bindSymbolOrNull(symbol: Symbol): DerivativeStructureSymbol<T, A>? = variables[symbol.identity]

    public fun bindSymbol(symbol: Symbol): DerivativeStructureSymbol<T, A> =
        bindSymbolOrNull(symbol.identity) ?: error("Symbol '${symbol}' is not supported in $this")

    public fun DerivativeStructure<T, A>.derivative(symbols: List<Symbol>): T {
        require(symbols.size <= order) { "The order of derivative ${symbols.size} exceeds computed order $order" }
        val ordersCount = symbols.groupBy { it }.mapValues { it.value.size }
        return getPartialDerivative(*variables.keys.map { ordersCount[it] ?: 0 }.toIntArray())
    }

    public fun DerivativeStructure<T, A>.derivative(vararg symbols: Symbol): T = derivative(symbols.toList())

}


/**
 * A ring over [DerivativeStructure].
 *
 * @property order The derivation order.
 * @param bindings The map of bindings values. All bindings are considered free parameters.
 */
@UnstableKMathAPI
public open class DerivativeStructureRing<T, A>(
    algebra: A,
    bufferFactory: MutableBufferFactory<T>,
    order: Int,
    bindings: Map<Symbol, T>,
) : DerivativeStructureAlgebra<T, A>(algebra, bufferFactory, order, bindings),
    Ring<DerivativeStructure<T, A>>, ScaleOperations<DerivativeStructure<T, A>>,
    NumericAlgebra<DerivativeStructure<T, A>>,
    NumbersAddOps<DerivativeStructure<T, A>> where A : Ring<T>, A : NumericAlgebra<T>, A : ScaleOperations<T> {

    override fun bindSymbolOrNull(value: String): DerivativeStructureSymbol<T, A>? =
        super<DerivativeStructureAlgebra>.bindSymbolOrNull(value)

    override fun DerivativeStructure<T, A>.unaryMinus(): DerivativeStructure<T, A> {
        val newData = algebra { data.map(bufferFactory) { -it } }
        return DerivativeStructure(this@DerivativeStructureRing, newData)
    }

    /**
     * Create a copy of given [Buffer] and modify it according to [block]
     */
    protected inline fun DerivativeStructure<T, A>.transformDataBuffer(block: DSCompiler<T, A>.(MutableBuffer<T>) -> Unit): DerivativeStructure<T, A> {
        val newData = bufferFactory(compiler.size) { data[it] }
        compiler.block(newData)
        return DerivativeStructure(this@DerivativeStructureRing, newData)
    }

    protected fun DerivativeStructure<T, A>.mapData(block: (T) -> T): DerivativeStructure<T, A> {
        val newData: Buffer<T> = data.map(bufferFactory, block)
        return DerivativeStructure(this@DerivativeStructureRing, newData)
    }

    protected fun DerivativeStructure<T, A>.mapDataIndexed(block: (Int, T) -> T): DerivativeStructure<T, A> {
        val newData: Buffer<T> = data.mapIndexed(bufferFactory, block)
        return DerivativeStructure(this@DerivativeStructureRing, newData)
    }

    override val zero: DerivativeStructure<T, A> by lazy {
        const(algebra.zero)
    }

    override val one: DerivativeStructure<T, A> by lazy {
        const(algebra.one)
    }

    override fun number(value: Number): DerivativeStructure<T, A> = const(algebra.number(value))

    override fun add(left: DerivativeStructure<T, A>, right: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        left.compiler.checkCompatibility(right.compiler)
        return left.transformDataBuffer { result ->
            add(left.data, 0, right.data, 0, result, 0)
        }
    }

    override fun scale(a: DerivativeStructure<T, A>, value: Double): DerivativeStructure<T, A> = algebra {
        a.mapData { it.times(value) }
    }

    override fun multiply(
        left: DerivativeStructure<T, A>,
        right: DerivativeStructure<T, A>,
    ): DerivativeStructure<T, A> {
        left.compiler.checkCompatibility(right.compiler)
        return left.transformDataBuffer { result ->
            multiply(left.data, 0, right.data, 0, result, 0)
        }
    }

    override fun DerivativeStructure<T, A>.minus(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        compiler.checkCompatibility(arg.compiler)
        return transformDataBuffer { result ->
            subtract(data, 0, arg.data, 0, result, 0)
        }
    }

    override operator fun DerivativeStructure<T, A>.plus(other: Number): DerivativeStructure<T, A> = algebra {
        transformDataBuffer {
            it[0] += number(other)
        }
    }

    override operator fun DerivativeStructure<T, A>.minus(other: Number): DerivativeStructure<T, A> =
        this + (-other.toDouble())

    override operator fun Number.plus(other: DerivativeStructure<T, A>): DerivativeStructure<T, A> = other + this
    override operator fun Number.minus(other: DerivativeStructure<T, A>): DerivativeStructure<T, A> = other - this
}

@UnstableKMathAPI
public class DerivativeStructureRingExpression<T, A>(
    public val algebra: A,
    public val bufferFactory: MutableBufferFactory<T>,
    public val function: DerivativeStructureRing<T, A>.() -> DerivativeStructure<T, A>,
) : DifferentiableExpression<T> where A : Ring<T>, A : ScaleOperations<T>, A : NumericAlgebra<T> {
    override operator fun invoke(arguments: Map<Symbol, T>): T =
        DerivativeStructureRing(algebra, bufferFactory, 0, arguments).function().value

    override fun derivativeOrNull(symbols: List<Symbol>): Expression<T> = Expression { arguments ->
        with(
            DerivativeStructureRing(
                algebra,
                bufferFactory,
                symbols.size,
                arguments
            )
        ) { function().derivative(symbols) }
    }
}

/**
 * A field over commons-math [DerivativeStructure].
 *
 * @property order The derivation order.
 * @param bindings The map of bindings values. All bindings are considered free parameters.
 */
@UnstableKMathAPI
public class DerivativeStructureField<T, A : ExtendedField<T>>(
    algebra: A,
    bufferFactory: MutableBufferFactory<T>,
    order: Int,
    bindings: Map<Symbol, T>,
) : DerivativeStructureRing<T, A>(algebra, bufferFactory, order, bindings), ExtendedField<DerivativeStructure<T, A>> {
    override fun number(value: Number): DerivativeStructure<T, A> = const(algebra.number(value))

    override fun divide(left: DerivativeStructure<T, A>, right: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        left.compiler.checkCompatibility(right.compiler)
        return left.transformDataBuffer { result ->
            left.compiler.divide(left.data, 0, right.data, 0, result, 0)
        }
    }

    override fun sin(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        sin(arg.data, 0, result, 0)
    }

    override fun cos(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        cos(arg.data, 0, result, 0)
    }

    override fun tan(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        tan(arg.data, 0, result, 0)
    }

    override fun asin(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        asin(arg.data, 0, result, 0)
    }

    override fun acos(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        acos(arg.data, 0, result, 0)
    }

    override fun atan(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        atan(arg.data, 0, result, 0)
    }

    override fun sinh(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        sinh(arg.data, 0, result, 0)
    }

    override fun cosh(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        cosh(arg.data, 0, result, 0)
    }

    override fun tanh(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        tanh(arg.data, 0, result, 0)
    }

    override fun asinh(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        asinh(arg.data, 0, result, 0)
    }

    override fun acosh(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        acosh(arg.data, 0, result, 0)
    }

    override fun atanh(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        atanh(arg.data, 0, result, 0)
    }

    override fun power(arg: DerivativeStructure<T, A>, pow: Number): DerivativeStructure<T, A> = when (pow) {
        is Int -> arg.transformDataBuffer { result ->
            pow(arg.data, 0, pow, result, 0)
        }
        else -> arg.transformDataBuffer { result ->
            pow(arg.data, 0, pow.toDouble(), result, 0)
        }
    }

    override fun sqrt(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        sqrt(arg.data, 0, result, 0)
    }

    public fun power(arg: DerivativeStructure<T, A>, pow: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        arg.compiler.checkCompatibility(pow.compiler)
        return arg.transformDataBuffer { result ->
            pow(arg.data, 0, pow.data, 0, result, 0)
        }
    }

    override fun exp(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        exp(arg.data, 0, result, 0)
    }

    override fun ln(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> = arg.transformDataBuffer { result ->
        ln(arg.data, 0, result, 0)
    }
}

@UnstableKMathAPI
public class DerivativeStructureFieldExpression<T, A : ExtendedField<T>>(
    public val algebra: A,
    public val bufferFactory: MutableBufferFactory<T>,
    public val function: DerivativeStructureField<T, A>.() -> DerivativeStructure<T, A>,
) : DifferentiableExpression<T> {
    override operator fun invoke(arguments: Map<Symbol, T>): T =
        DerivativeStructureField(algebra, bufferFactory, 0, arguments).function().value

    override fun derivativeOrNull(symbols: List<Symbol>): Expression<T> = Expression { arguments ->
        with(
            DerivativeStructureField(
                algebra,
                bufferFactory,
                symbols.size,
                arguments,
            )
        ) { function().derivative(symbols) }
    }
}
