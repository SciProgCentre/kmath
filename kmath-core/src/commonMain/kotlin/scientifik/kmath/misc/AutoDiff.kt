package scientifik.kmath.misc

import scientifik.kmath.linear.Point
import scientifik.kmath.operations.ExtendedField
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.invoke
import scientifik.kmath.operations.sum
import scientifik.kmath.structures.asBuffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/*
 * Implementation of backward-mode automatic differentiation.
 * Initial gist by Roman Elizarov: https://gist.github.com/elizarov/1ad3a8583e88cb6ea7a0ad09bb591d3d
 */

/**
 * Differentiable variable with value and derivative of differentiation ([deriv]) result
 * with respect to this variable.
 */
public open class Variable<T : Any>(public val value: T)

public class DerivationResult<T : Any>(
    value: T,
    public val deriv: Map<Variable<T>, T>,
    public val context: Field<T>
) : Variable<T>(value) {
    public fun deriv(variable: Variable<T>): T = deriv[variable] ?: context.zero

    /**
     * compute divergence
     */
    public fun div(): T = context { sum(deriv.values) }

    /**
     * Compute a gradient for variables in given order
     */
    public fun grad(vararg variables: Variable<T>): Point<T> {
        check(variables.isNotEmpty()) { "Variable order is not provided for gradient construction" }
        return variables.map(::deriv).asBuffer()
    }
}

/**
 * Runs differentiation and establishes [AutoDiffField] context inside the block of code.
 *
 * The partial derivatives are placed in argument `d` variable
 *
 * Example:
 * ```
 * val x = Variable(2) // define variable(s) and their values
 * val y = deriv { sqr(x) + 5 * x + 3 } // write formulate in deriv context
 * assertEquals(17.0, y.x) // the value of result (y)
 * assertEquals(9.0, x.d)  // dy/dx
 * ```
 */
public inline fun <T : Any, F : Field<T>> F.deriv(body: AutoDiffField<T, F>.() -> Variable<T>): DerivationResult<T> {
    contract { callsInPlace(body, InvocationKind.EXACTLY_ONCE) }

    return (AutoDiffContext(this)) {
        val result = body()
        result.d = context.one // computing derivative w.r.t result
        runBackwardPass()
        DerivationResult(result.value, derivatives, this@deriv)
    }
}


public abstract class AutoDiffField<T : Any, F : Field<T>> : Field<Variable<T>> {
    public abstract val context: F

    /**
     * A variable accessing inner state of derivatives.
     * Use this function in inner builders to avoid creating additional derivative bindings
     */
    public abstract var Variable<T>.d: T

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

    public abstract fun variable(value: T): Variable<T>

    public inline fun variable(block: F.() -> T): Variable<T> = variable(context.block())

    // Overloads for Double constants

    override operator fun Number.plus(b: Variable<T>): Variable<T> =
        derive(variable { this@plus.toDouble() * one + b.value }) { z ->
            b.d += z.d
        }

    override operator fun Variable<T>.plus(b: Number): Variable<T> = b.plus(this)

    override operator fun Number.minus(b: Variable<T>): Variable<T> =
        derive(variable { this@minus.toDouble() * one - b.value }) { z -> b.d -= z.d }

    override operator fun Variable<T>.minus(b: Number): Variable<T> =
        derive(variable { this@minus.value - one * b.toDouble() }) { z -> this@minus.d += z.d }
}

/**
 * Automatic Differentiation context class.
 */
@PublishedApi
internal class AutoDiffContext<T : Any, F : Field<T>>(override val context: F) : AutoDiffField<T, F>() {
    // this stack contains pairs of blocks and values to apply them to
    private var stack: Array<Any?> = arrayOfNulls<Any?>(8)
    private var sp: Int = 0
    val derivatives: MutableMap<Variable<T>, T> = hashMapOf()
    override val zero: Variable<T> get() = Variable(context.zero)
    override val one: Variable<T> get() = Variable(context.one)

    /**
     * A variable coupled with its derivative. For internal use only
     */
    private class VariableWithDeriv<T : Any>(x: T, var d: T) : Variable<T>(x)

    override fun variable(value: T): Variable<T> =
        VariableWithDeriv(value, context.zero)

    override var Variable<T>.d: T
        get() = (this as? VariableWithDeriv)?.d ?: derivatives[this] ?: context.zero
        set(value) = if (this is VariableWithDeriv) d = value else derivatives[this] = value

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

    override fun add(a: Variable<T>, b: Variable<T>): Variable<T> = derive(variable { a.value + b.value }) { z ->
        a.d += z.d
        b.d += z.d
    }

    override fun multiply(a: Variable<T>, b: Variable<T>): Variable<T> = derive(variable { a.value * b.value }) { z ->
        a.d += z.d * b.value
        b.d += z.d * a.value
    }

    override fun divide(a: Variable<T>, b: Variable<T>): Variable<T> = derive(variable { a.value / b.value }) { z ->
        a.d += z.d / b.value
        b.d -= z.d * a.value / (b.value * b.value)
    }

    override fun multiply(a: Variable<T>, k: Number): Variable<T> = derive(variable { k.toDouble() * a.value }) { z ->
        a.d += z.d * k.toDouble()
    }
}

// Extensions for differentiation of various basic mathematical functions

// x ^ 2
public fun <T : Any, F : Field<T>> AutoDiffField<T, F>.sqr(x: Variable<T>): Variable<T> =
    derive(variable { x.value * x.value }) { z -> x.d += z.d * 2 * x.value }

// x ^ 1/2
public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.sqrt(x: Variable<T>): Variable<T> =
    derive(variable { sqrt(x.value) }) { z -> x.d += z.d * 0.5 / z.value }

// x ^ y (const)
public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.pow(x: Variable<T>, y: Double): Variable<T> =
    derive(variable { power(x.value, y) }) { z -> x.d += z.d * y * power(x.value, y - 1) }

public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.pow(x: Variable<T>, y: Int): Variable<T> =
    pow(x, y.toDouble())

// exp(x)
public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.exp(x: Variable<T>): Variable<T> =
    derive(variable { exp(x.value) }) { z -> x.d += z.d * z.value }

// ln(x)
public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.ln(x: Variable<T>): Variable<T> =
    derive(variable { ln(x.value) }) { z -> x.d += z.d / x.value }

// x ^ y (any)
public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.pow(x: Variable<T>, y: Variable<T>): Variable<T> =
    exp(y * ln(x))

// sin(x)
public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.sin(x: Variable<T>): Variable<T> =
    derive(variable { sin(x.value) }) { z -> x.d += z.d * cos(x.value) }

// cos(x)
public fun <T : Any, F : ExtendedField<T>> AutoDiffField<T, F>.cos(x: Variable<T>): Variable<T> =
    derive(variable { cos(x.value) }) { z -> x.d -= z.d * sin(x.value) }
