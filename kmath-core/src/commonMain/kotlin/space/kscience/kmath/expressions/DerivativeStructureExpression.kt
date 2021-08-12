/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.MutableBufferFactory
import space.kscience.kmath.structures.indices

/**
 * A class implementing both [DerivativeStructure] and [Symbol].
 */
@UnstableKMathAPI
public class DerivativeStructureSymbol<T, A>(
    derivativeAlgebra: DerivativeStructureRing<T, A>,
    size: Int,
    order: Int,
    index: Int,
    symbol: Symbol,
    value: T,
) : Symbol by symbol, DerivativeStructure<T, A>(
    derivativeAlgebra,
    size,
    order,
    index,
    value
) where A : Ring<T>, A : NumericAlgebra<T>, A : ScaleOperations<T> {
    override fun toString(): String = symbol.toString()
    override fun equals(other: Any?): Boolean = (other as? Symbol) == symbol
    override fun hashCode(): Int = symbol.hashCode()
}

/**
 * A ring over [DerivativeStructure].
 *
 * @property order The derivation order.
 * @param bindings The map of bindings values. All bindings are considered free parameters.
 */
@UnstableKMathAPI
public open class DerivativeStructureRing<T, A>(
    public val algebra: A,
    public val bufferFactory: MutableBufferFactory<T>,
    public val order: Int,
    bindings: Map<Symbol, T>,
) : Ring<DerivativeStructure<T, A>>, ScaleOperations<DerivativeStructure<T, A>>,
    NumericAlgebra<DerivativeStructure<T, A>>,
    ExpressionAlgebra<T, DerivativeStructure<T, A>>,
    NumbersAddOps<DerivativeStructure<T, A>> where A : Ring<T>, A : NumericAlgebra<T>, A : ScaleOperations<T> {
    public val numberOfVariables: Int = bindings.size

    override val zero: DerivativeStructure<T, A> by lazy {
        DerivativeStructure(
            this,
            numberOfVariables,
            order,
        )
    }

    override val one: DerivativeStructure<T, A> by lazy {
        DerivativeStructure(
            this,
            numberOfVariables,
            order,
            algebra.one,
        )
    }

    override fun number(value: Number): DerivativeStructure<T, A> = const(algebra.number(value))

    private val variables: Map<Symbol, DerivativeStructureSymbol<T, A>> =
        bindings.entries.mapIndexed { index, (key, value) ->
            key to DerivativeStructureSymbol(
                this,
                numberOfVariables,
                order,
                index,
                key,
                value,
            )
        }.toMap()

    public override fun const(value: T): DerivativeStructure<T, A> =
        DerivativeStructure(this, numberOfVariables, order, value)

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

    override fun DerivativeStructure<T, A>.unaryMinus(): DerivativeStructure<T, A> {
        val ds = DerivativeStructure(this@DerivativeStructureRing, compiler)
        for (i in ds.data.indices) {
            ds.data[i] = algebra { -data[i] }
        }
        return ds
    }

    override fun add(left: DerivativeStructure<T, A>, right: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        left.compiler.checkCompatibility(right.compiler)
        val ds = DerivativeStructure(left)
        left.compiler.add(left.data, 0, right.data, 0, ds.data, 0)
        return ds
    }

    override fun scale(a: DerivativeStructure<T, A>, value: Double): DerivativeStructure<T, A> {
        val ds = DerivativeStructure(a)
        for (i in ds.data.indices) {
            ds.data[i] = algebra { ds.data[i].times(value) }
        }
        return ds
    }

    override fun multiply(
        left: DerivativeStructure<T, A>,
        right: DerivativeStructure<T, A>
    ): DerivativeStructure<T, A> {
        left.compiler.checkCompatibility(right.compiler)
        val result = DerivativeStructure(this, left.compiler)
        left.compiler.multiply(left.data, 0, right.data, 0, result.data, 0)
        return result
    }

    override fun DerivativeStructure<T, A>.minus(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        compiler.checkCompatibility(arg.compiler)
        val ds = DerivativeStructure(this)
        compiler.subtract(data, 0, arg.data, 0, ds.data, 0)
        return ds
    }

    override operator fun DerivativeStructure<T, A>.plus(other: Number): DerivativeStructure<T, A> {
        val ds = DerivativeStructure(this)
        ds.data[0] = algebra { ds.data[0] + number(other) }
        return ds
    }

    override operator fun DerivativeStructure<T, A>.minus(other: Number): DerivativeStructure<T, A> =
        this + -other.toDouble()

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
        val result = DerivativeStructure(this, left.compiler)
        left.compiler.divide(left.data, 0, right.data, 0, result.data, 0)
        return result
    }

    override fun sin(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.sin(arg.data, 0, result.data, 0)
        return result
    }

    override fun cos(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.cos(arg.data, 0, result.data, 0)
        return result
    }

    override fun tan(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.tan(arg.data, 0, result.data, 0)
        return result
    }

    override fun asin(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.asin(arg.data, 0, result.data, 0)
        return result
    }

    override fun acos(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.acos(arg.data, 0, result.data, 0)
        return result
    }

    override fun atan(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.atan(arg.data, 0, result.data, 0)
        return result
    }

    override fun sinh(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.sinh(arg.data, 0, result.data, 0)
        return result
    }

    override fun cosh(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.cosh(arg.data, 0, result.data, 0)
        return result
    }

    override fun tanh(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.tanh(arg.data, 0, result.data, 0)
        return result
    }

    override fun asinh(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.asinh(arg.data, 0, result.data, 0)
        return result
    }

    override fun acosh(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.acosh(arg.data, 0, result.data, 0)
        return result
    }

    override fun atanh(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.atanh(arg.data, 0, result.data, 0)
        return result
    }

    override fun power(arg: DerivativeStructure<T, A>, pow: Number): DerivativeStructure<T, A> = when (pow) {
        is Int -> {
            val result = DerivativeStructure(this, arg.compiler)
            arg.compiler.pow(arg.data, 0, pow, result.data, 0)
            result
        }
        else -> {
            val result = DerivativeStructure(this, arg.compiler)
            arg.compiler.pow(arg.data, 0, pow.toDouble(), result.data, 0)
            result
        }
    }

    override fun sqrt(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.sqrt(arg.data, 0, result.data, 0)
        return result
    }

    public fun power(arg: DerivativeStructure<T, A>, pow: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        arg.compiler.checkCompatibility(pow.compiler)
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.pow(arg.data, 0, pow.data, 0, result.data, 0)
        return result
    }

    override fun exp(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.exp(arg.data, 0, result.data, 0)
        return result
    }

    override fun ln(arg: DerivativeStructure<T, A>): DerivativeStructure<T, A> {
        val result = DerivativeStructure(this, arg.compiler)
        arg.compiler.ln(arg.data, 0, result.data, 0)
        return result
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
