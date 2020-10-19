package kscience.kmath.expressions

import kscience.kmath.linear.Point
import kscience.kmath.operations.*
import kscience.kmath.structures.asBuffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/*
 * Implementation of backward-mode automatic differentiation.
 * Initial gist by Roman Elizarov: https://gist.github.com/elizarov/1ad3a8583e88cb6ea7a0ad09bb591d3d
 */


/**
 * A [Symbol] with bound value
 */
public interface BoundSymbol<out T> : Symbol {
    public val value: T
}

/**
 * Bind a [Symbol] to a [value] and produce [BoundSymbol]
 */
public fun <T> Symbol.bind(value: T): BoundSymbol<T> = object : BoundSymbol<T> {
    override val identity = this@bind.identity
    override val value: T = value
}

/**
 * Represents result of [withAutoDiff] call.
 *
 * @param T the non-nullable type of value.
 * @param value the value of result.
 * @property withAutoDiff The mapping of differentiated variables to their derivatives.
 * @property context The field over [T].
 */
public class DerivationResult<T : Any>(
    override val value: T,
    private val derivativeValues: Map<Any, T>,
    public val context: Field<T>,
) : BoundSymbol<T> {
    /**
     * Returns derivative of [variable] or returns [Ring.zero] in [context].
     */
    public fun derivative(variable: Symbol): T = derivativeValues[variable.identity] ?: context.zero

    /**
     * Computes the divergence.
     */
    public fun div(): T = context { sum(derivativeValues.values) }
}

/**
 * Computes the gradient for variables in given order.
 */
public fun <T : Any> DerivationResult<T>.grad(vararg variables: Symbol): Point<T> {
    check(variables.isNotEmpty()) { "Variable order is not provided for gradient construction" }
    return variables.map(::derivative).asBuffer()
}

/**
 * Runs differentiation and establishes [AutoDiffField] context inside the block of code.
 *
 * The partial derivatives are placed in argument `d` variable
 *
 * Example:
 * ```
 * val x by symbol // define variable(s) and their values
 * val y = RealField.withAutoDiff() { sqr(x) + 5 * x + 3 } // write formulate in deriv context
 * assertEquals(17.0, y.x) // the value of result (y)
 * assertEquals(9.0, x.d)  // dy/dx
 * ```
 *
 * @param body the action in [AutoDiffField] context returning [AutoDiffVariable] to differentiate with respect to.
 * @return the result of differentiation.
 */
public fun <T : Any, F : Field<T>> F.withAutoDiff(
    bindings: Collection<BoundSymbol<T>>,
    body: AutoDiffField<T, F>.() -> BoundSymbol<T>,
): DerivationResult<T> {
    contract { callsInPlace(body, InvocationKind.EXACTLY_ONCE) }

    return AutoDiffContext(this, bindings).derivate(body)
}

public fun <T : Any, F : Field<T>> F.withAutoDiff(
    vararg bindings: Pair<Symbol, T>,
    body: AutoDiffField<T, F>.() -> BoundSymbol<T>,
): DerivationResult<T> = withAutoDiff(bindings.map { it.first.bind(it.second) }, body)

/**
 * Represents field in context of which functions can be derived.
 */
public abstract class AutoDiffField<T : Any, F : Field<T>>
    : Field<BoundSymbol<T>>, ExpressionAlgebra<T, BoundSymbol<T>> {

    public abstract val context: F

    /**
     * A variable accessing inner state of derivatives.
     * Use this value in inner builders to avoid creating additional derivative bindings.
     */
    public abstract var BoundSymbol<T>.d: T

    /**
     * Performs update of derivative after the rest of the formula in the back-pass.
     *
     * For example, implementation of `sin` function is:
     *
     * ```
     * fun AD.sin(x: Variable): Variable = derive(Variable(sin(x.x)) { z -> // call derive with function result
     *     x.d += z.d * cos(x.x) // update derivative using chain rule and derivative of the function
     * }
     * ```
     */
    public abstract fun <R> derive(value: R, block: F.(R) -> Unit): R

    public inline fun const(block: F.() -> T): BoundSymbol<T> = const(context.block())

    // Overloads for Double constants

    override operator fun Number.plus(b: BoundSymbol<T>): BoundSymbol<T> =
        derive(const { this@plus.toDouble() * one + b.value }) { z ->
            b.d += z.d
        }

    override operator fun BoundSymbol<T>.plus(b: Number): BoundSymbol<T> = b.plus(this)

    override operator fun Number.minus(b: BoundSymbol<T>): BoundSymbol<T> =
        derive(const { this@minus.toDouble() * one - b.value }) { z -> b.d -= z.d }

    override operator fun BoundSymbol<T>.minus(b: Number): BoundSymbol<T> =
        derive(const { this@minus.value - one * b.toDouble() }) { z -> this@minus.d += z.d }
}

/**
 * Automatic Differentiation context class.
 */
private class AutoDiffContext<T : Any, F : Field<T>>(
    override val context: F,
    bindings: Collection<BoundSymbol<T>>,
) : AutoDiffField<T, F>() {
    // this stack contains pairs of blocks and values to apply them to
    private var stack: Array<Any?> = arrayOfNulls<Any?>(8)
    private var sp: Int = 0
    private val derivatives: MutableMap<Any, T> = hashMapOf()
    override val zero: BoundSymbol<T> get() = const(context.zero)
    override val one: BoundSymbol<T> get() = const(context.one)

    /**
     * Differentiable variable with value and derivative of differentiation ([withAutoDiff]) result
     * with respect to this variable.
     *
     * @param T the non-nullable type of value.
     * @property value The value of this variable.
     */
    private class AutoDiffVariableWithDeriv<T : Any>(override val value: T, var d: T) : BoundSymbol<T>

    private val bindings: Map<Any, BoundSymbol<T>> = bindings.associateBy { it.identity }

    override fun bindOrNull(symbol: Symbol): BoundSymbol<T>? = bindings[symbol.identity]

    override fun const(value: T): BoundSymbol<T> = AutoDiffVariableWithDeriv(value, context.zero)

    override var BoundSymbol<T>.d: T
        get() = (this as? AutoDiffVariableWithDeriv)?.d ?: derivatives[identity] ?: context.zero
        set(value) = if (this is AutoDiffVariableWithDeriv) d = value else derivatives[identity] = value

    @Suppress("UNCHECKED_CAST")
    override fun <R> derive(value: R, block: F.(R) -> Unit): R {
        // save block to stack for backward pass
        if (sp >= stack.size) stack = stack.copyOf(stack.size * 2)
        stack[sp++] = block
        stack[sp++] = value
        return value
    }

    @Suppress("UNCHECKED_CAST")
    fun runBackwardPass() {
        while (sp > 0) {
            val value = stack[--sp]
            val block = stack[--sp] as F.(Any?) -> Unit
            context.block(value)
        }
    }

    // Basic math (+, -, *, /)

    override fun add(a: BoundSymbol<T>, b: BoundSymbol<T>): BoundSymbol<T> =
        derive(const { a.value + b.value }) { z ->
            a.d += z.d
            b.d += z.d
        }

    override fun multiply(a: BoundSymbol<T>, b: BoundSymbol<T>): BoundSymbol<T> =
        derive(const { a.value * b.value }) { z ->
            a.d += z.d * b.value
            b.d += z.d * a.value
        }

    override fun divide(a: BoundSymbol<T>, b: BoundSymbol<T>): BoundSymbol<T> =
        derive(const { a.value / b.value }) { z ->
            a.d += z.d / b.value
            b.d -= z.d * a.value / (b.value * b.value)
        }

    override fun multiply(a: BoundSymbol<T>, k: Number): BoundSymbol<T> =
        derive(const { k.toDouble() * a.value }) { z ->
            a.d += z.d * k.toDouble()
        }

    inline fun derivate(function: AutoDiffField<T, F>.() -> BoundSymbol<T>): DerivationResult<T> {
        val result = function()
        result.d = context.one // computing derivative w.r.t result
        runBackwardPass()
        return DerivationResult(result.value, derivatives, context)
    }
}

/**
 * A constructs that creates a derivative structure with required order on-demand
 */
public class SimpleAutoDiffExpression<T : Any, F : Field<T>>(
    public val field: F,
    public val function: AutoDiffField<T, F>.() -> BoundSymbol<T>,
) : DifferentiableExpression<T> {
    public override operator fun invoke(arguments: Map<Symbol, T>): T {
        val bindings = arguments.entries.map { it.key.bind(it.value) }
        return AutoDiffContext(field, bindings).function().value
    }

    /**
     * Get the derivative expression with given orders
     */
    public override fun derivative(orders: Map<Symbol, Int>): Expression<T> {
        val dSymbol = orders.entries.singleOrNull { it.value == 1 }
            ?: error("SimpleAutoDiff supports only first order derivatives")
        return Expression { arguments ->
            val bindings = arguments.entries.map { it.key.bind(it.value) }
            val derivationResult = AutoDiffContext(field, bindings).derivate(function)
            derivationResult.derivative(dSymbol.key)
        }
    }
}


// Extensions for differentiation of various basic mathematical functions

// x ^ 2
public fun <T : Any, F : Field<T>> AutoDiffField<T, F>.sqr(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { x.value * x.value }) { z -> x.d += z.d * 2 * x.value }

// x ^ 1/2
public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.sqrt(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { sqrt(x.value) }) { z -> x.d += z.d * 0.5 / z.value }

// x ^ y (const)
public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.pow(
    x: BoundSymbol<T>,
    y: Double,
): BoundSymbol<T> =
    derive(const { power(x.value, y) }) { z -> x.d += z.d * y * power(x.value, y - 1) }

public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.pow(
    x: BoundSymbol<T>,
    y: Int,
): BoundSymbol<T> =
    pow(x, y.toDouble())

// exp(x)
public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.exp(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { exp(x.value) }) { z -> x.d += z.d * z.value }

// ln(x)
public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.ln(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { ln(x.value) }) { z -> x.d += z.d / x.value }

// x ^ y (any)
public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.pow(
    x: BoundSymbol<T>,
    y: BoundSymbol<T>,
): BoundSymbol<T> =
    exp(y * ln(x))

// sin(x)
public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.sin(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { sin(x.value) }) { z -> x.d += z.d * cos(x.value) }

// cos(x)
public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.cos(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { cos(x.value) }) { z -> x.d -= z.d * sin(x.value) }

public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.tan(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { tan(x.value) }) { z ->
        val c = cos(x.value)
        x.d += z.d / (c * c)
    }

public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.asin(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { asin(x.value) }) { z -> x.d += z.d / sqrt(one - x.value * x.value) }

public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.acos(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { acos(x.value) }) { z -> x.d -= z.d / sqrt(one - x.value * x.value) }

public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.atan(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { atan(x.value) }) { z -> x.d += z.d / (one + x.value * x.value) }

public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.sinh(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { sin(x.value) }) { z -> x.d += z.d * cosh(x.value) }

public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.cosh(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { cos(x.value) }) { z -> x.d += z.d * sinh(x.value) }

public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.tanh(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { tan(x.value) }) { z ->
        val c = cosh(x.value)
        x.d += z.d / (c * c)
    }

public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.asinh(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { asinh(x.value) }) { z -> x.d += z.d / sqrt(one + x.value * x.value) }

public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.acosh(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { acosh(x.value) }) { z -> x.d += z.d / (sqrt((x.value - one) * (x.value + one))) }

public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.atanh(x: BoundSymbol<T>): BoundSymbol<T> =
    derive(const { atanh(x.value) }) { z -> x.d += z.d / (one - x.value * x.value) }

