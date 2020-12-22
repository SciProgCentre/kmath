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


public open class AutoDiffValue<out T>(public val value: T)

/**
 * Represents result of [simpleAutoDiff] call.
 *
 * @param T the non-nullable type of value.
 * @param value the value of result.
 * @property simpleAutoDiff The mapping of differentiated variables to their derivatives.
 * @property context The field over [T].
 */
public class DerivationResult<T : Any>(
    public val value: T,
    private val derivativeValues: Map<String, T>,
    public val context: Field<T>,
) {
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
 * Runs differentiation and establishes [SimpleAutoDiffField] context inside the block of code.
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
 * @param body the action in [SimpleAutoDiffField] context returning [AutoDiffVariable] to differentiate with respect to.
 * @return the result of differentiation.
 */
public fun <T : Any, F : Field<T>> F.simpleAutoDiff(
    bindings: Map<Symbol, T>,
    body: SimpleAutoDiffField<T, F>.() -> AutoDiffValue<T>,
): DerivationResult<T> {
    contract { callsInPlace(body, InvocationKind.EXACTLY_ONCE) }

    return SimpleAutoDiffField(this, bindings).differentiate(body)
}

public fun <T : Any, F : Field<T>> F.simpleAutoDiff(
    vararg bindings: Pair<Symbol, T>,
    body: SimpleAutoDiffField<T, F>.() -> AutoDiffValue<T>,
): DerivationResult<T> = simpleAutoDiff(bindings.toMap(), body)

/**
 * Represents field in context of which functions can be derived.
 */
public open class SimpleAutoDiffField<T : Any, F : Field<T>>(
    public val context: F,
    bindings: Map<Symbol, T>,
) : Field<AutoDiffValue<T>>, ExpressionAlgebra<T, AutoDiffValue<T>> {
    public override val zero: AutoDiffValue<T>
        get() = const(context.zero)

    public override val one: AutoDiffValue<T>
        get() = const(context.one)

    // this stack contains pairs of blocks and values to apply them to
    private var stack: Array<Any?> = arrayOfNulls<Any?>(8)
    private var sp: Int = 0
    private val derivatives: MutableMap<AutoDiffValue<T>, T> = hashMapOf()

    private val bindings: Map<String, AutoDiffVariableWithDerivative<T>> = bindings.entries.associate {
        it.key.identity to AutoDiffVariableWithDerivative(it.key.identity, it.value, context.zero)
    }

    /**
     * Differentiable variable with value and derivative of differentiation ([simpleAutoDiff]) result
     * with respect to this variable.
     *
     * @param T the non-nullable type of value.
     * @property value The value of this variable.
     */
    private class AutoDiffVariableWithDerivative<T : Any>(
        override val identity: String,
        value: T,
        var d: T,
    ) : AutoDiffValue<T>(value), Symbol {
        override fun toString(): String = identity
        override fun equals(other: Any?): Boolean = this.identity == (other as? Symbol)?.identity
        override fun hashCode(): Int = identity.hashCode()
    }

    public override fun bindOrNull(symbol: Symbol): AutoDiffValue<T>? = bindings[symbol.identity]

    private fun getDerivative(variable: AutoDiffValue<T>): T =
        (variable as? AutoDiffVariableWithDerivative)?.d ?: derivatives[variable] ?: context.zero

    private fun setDerivative(variable: AutoDiffValue<T>, value: T) {
        if (variable is AutoDiffVariableWithDerivative) variable.d = value else derivatives[variable] = value
    }

    @Suppress("UNCHECKED_CAST")
    private fun runBackwardPass() {
        while (sp > 0) {
            val value = stack[--sp]
            val block = stack[--sp] as F.(Any?) -> Unit
            context.block(value)
        }
    }

    override fun const(value: T): AutoDiffValue<T> = AutoDiffValue(value)

    /**
     * A variable accessing inner state of derivatives.
     * Use this value in inner builders to avoid creating additional derivative bindings.
     */
    public var AutoDiffValue<T>.d: T
        get() = getDerivative(this)
        set(value) = setDerivative(this, value)

    public inline fun const(block: F.() -> T): AutoDiffValue<T> = const(context.block())

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
    @Suppress("UNCHECKED_CAST")
    public fun <R> derive(value: R, block: F.(R) -> Unit): R {
        // save block to stack for backward pass
        if (sp >= stack.size) stack = stack.copyOf(stack.size * 2)
        stack[sp++] = block
        stack[sp++] = value
        return value
    }


    internal fun differentiate(function: SimpleAutoDiffField<T, F>.() -> AutoDiffValue<T>): DerivationResult<T> {
        val result = function()
        result.d = context.one // computing derivative w.r.t result
        runBackwardPass()
        return DerivationResult(result.value, bindings.mapValues { it.value.d }, context)
    }

    // Overloads for Double constants

    public override operator fun Number.plus(b: AutoDiffValue<T>): AutoDiffValue<T> =
        derive(const { this@plus.toDouble() * one + b.value }) { z ->
            b.d += z.d
        }

    public override operator fun AutoDiffValue<T>.plus(b: Number): AutoDiffValue<T> = b.plus(this)

    public override operator fun Number.minus(b: AutoDiffValue<T>): AutoDiffValue<T> =
        derive(const { this@minus.toDouble() * one - b.value }) { z -> b.d -= z.d }

    public override operator fun AutoDiffValue<T>.minus(b: Number): AutoDiffValue<T> =
        derive(const { this@minus.value - one * b.toDouble() }) { z -> this@minus.d += z.d }


    // Basic math (+, -, *, /)

    public override fun add(a: AutoDiffValue<T>, b: AutoDiffValue<T>): AutoDiffValue<T> =
        derive(const { a.value + b.value }) { z ->
            a.d += z.d
            b.d += z.d
        }

    public override fun multiply(a: AutoDiffValue<T>, b: AutoDiffValue<T>): AutoDiffValue<T> =
        derive(const { a.value * b.value }) { z ->
            a.d += z.d * b.value
            b.d += z.d * a.value
        }

    public override fun divide(a: AutoDiffValue<T>, b: AutoDiffValue<T>): AutoDiffValue<T> =
        derive(const { a.value / b.value }) { z ->
            a.d += z.d / b.value
            b.d -= z.d * a.value / (b.value * b.value)
        }

    public override fun multiply(a: AutoDiffValue<T>, k: Number): AutoDiffValue<T> =
        derive(const { k.toDouble() * a.value }) { z ->
            a.d += z.d * k.toDouble()
        }
}

/**
 * A constructs that creates a derivative structure with required order on-demand
 */
public class SimpleAutoDiffExpression<T : Any, F : Field<T>>(
    public val field: F,
    public val function: SimpleAutoDiffField<T, F>.() -> AutoDiffValue<T>,
) : FirstDerivativeExpression<T, Expression<T>>() {
    public override operator fun invoke(arguments: Map<Symbol, T>): T {
        //val bindings = arguments.entries.map { it.key.bind(it.value) }
        return SimpleAutoDiffField(field, arguments).function().value
    }

    public override fun derivativeOrNull(symbol: Symbol): Expression<T> = Expression { arguments ->
        //val bindings = arguments.entries.map { it.key.bind(it.value) }
        val derivationResult = SimpleAutoDiffField(field, arguments).differentiate(function)
        derivationResult.derivative(symbol)
    }
}

/**
 * Generate [AutoDiffProcessor] for [SimpleAutoDiffExpression]
 */
public fun <T : Any, F : Field<T>> simpleAutoDiff(field: F): AutoDiffProcessor<T, AutoDiffValue<T>, SimpleAutoDiffField<T, F>, Expression<T>> =
    AutoDiffProcessor { function ->
        SimpleAutoDiffExpression(field, function)
    }

// Extensions for differentiation of various basic mathematical functions

// x ^ 2
public fun <T : Any, F : Field<T>> SimpleAutoDiffField<T, F>.sqr(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { x.value * x.value }) { z -> x.d += z.d * 2 * x.value }

// x ^ 1/2
public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.sqrt(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { sqrt(x.value) }) { z -> x.d += z.d * 0.5 / z.value }

// x ^ y (const)
public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.pow(
    x: AutoDiffValue<T>,
    y: Double,
): AutoDiffValue<T> =
    derive(const { power(x.value, y) }) { z -> x.d += z.d * y * power(x.value, y - 1) }

public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.pow(
    x: AutoDiffValue<T>,
    y: Int,
): AutoDiffValue<T> = pow(x, y.toDouble())

// exp(x)
public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.exp(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { exp(x.value) }) { z -> x.d += z.d * z.value }

// ln(x)
public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.ln(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { ln(x.value) }) { z -> x.d += z.d / x.value }

// x ^ y (any)
public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.pow(
    x: AutoDiffValue<T>,
    y: AutoDiffValue<T>,
): AutoDiffValue<T> =
    exp(y * ln(x))

// sin(x)
public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.sin(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { sin(x.value) }) { z -> x.d += z.d * cos(x.value) }

// cos(x)
public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.cos(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { cos(x.value) }) { z -> x.d -= z.d * sin(x.value) }

public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.tan(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { tan(x.value) }) { z ->
        val c = cos(x.value)
        x.d += z.d / (c * c)
    }

public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.asin(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { asin(x.value) }) { z -> x.d += z.d / sqrt(one - x.value * x.value) }

public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.acos(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { acos(x.value) }) { z -> x.d -= z.d / sqrt(one - x.value * x.value) }

public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.atan(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { atan(x.value) }) { z -> x.d += z.d / (one + x.value * x.value) }

public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.sinh(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { sinh(x.value) }) { z -> x.d += z.d * cosh(x.value) }

public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.cosh(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { cosh(x.value) }) { z -> x.d += z.d * sinh(x.value) }

public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.tanh(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { tanh(x.value) }) { z ->
        val c = cosh(x.value)
        x.d += z.d / (c * c)
    }

public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.asinh(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { asinh(x.value) }) { z -> x.d += z.d / sqrt(one + x.value * x.value) }

public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.acosh(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { acosh(x.value) }) { z -> x.d += z.d / (sqrt((x.value - one) * (x.value + one))) }

public fun <T : Any, F : ExtendedField<T>> SimpleAutoDiffField<T, F>.atanh(x: AutoDiffValue<T>): AutoDiffValue<T> =
    derive(const { atanh(x.value) }) { z -> x.d += z.d / (one - x.value * x.value) }

public class SimpleAutoDiffExtendedField<T : Any, F : ExtendedField<T>>(
    context: F,
    bindings: Map<Symbol, T>,
) : ExtendedField<AutoDiffValue<T>>, SimpleAutoDiffField<T, F>(context, bindings) {
    // x ^ 2
    public fun sqr(x: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).sqr(x)

    // x ^ 1/2
    public override fun sqrt(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).sqrt(arg)

    // x ^ y (const)
    public override fun power(arg: AutoDiffValue<T>, pow: Number): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).pow(arg, pow.toDouble())

    // exp(x)
    public override fun exp(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).exp(arg)

    // ln(x)
    public override fun ln(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).ln(arg)

    // x ^ y (any)
    public fun pow(
        x: AutoDiffValue<T>,
        y: AutoDiffValue<T>,
    ): AutoDiffValue<T> = exp(y * ln(x))

    // sin(x)
    public override fun sin(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).sin(arg)

    // cos(x)
    public override fun cos(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).cos(arg)

    public override fun tan(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).tan(arg)

    public override fun asin(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).asin(arg)

    public override fun acos(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).acos(arg)

    public override fun atan(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).atan(arg)

    public override fun sinh(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).sinh(arg)

    public override fun cosh(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).cosh(arg)

    public override fun tanh(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).tanh(arg)

    public override fun asinh(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).asinh(arg)

    public override fun acosh(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).acosh(arg)

    public override fun atanh(arg: AutoDiffValue<T>): AutoDiffValue<T> =
        (this as SimpleAutoDiffField<T, F>).atanh(arg)
}
