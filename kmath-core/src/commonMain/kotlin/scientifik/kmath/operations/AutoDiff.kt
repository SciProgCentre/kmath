package scientifik.kmath.operations

import kotlin.math.pow
import kotlin.math.sqrt

/*
 * Implementation of backward-mode automatic differentiation.
 * Initial gist by Roman Elizarov: https://gist.github.com/elizarov/1ad3a8583e88cb6ea7a0ad09bb591d3d
 */

/**
 * Differentiable variable with value and derivative of differentiation ([deriv]) result
 * with respect to this variable.
 */
open class Variable(val x: Double) {
    constructor(x: Number) : this(x.toDouble())
}

class DerivationResult(x: Double, val deriv: Map<Variable, Double>): Variable(x) {
    fun deriv(variable: Variable) = deriv[variable] ?: 0.0
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
fun deriv(body: AutoDiffField.() -> Variable): DerivationResult =
    AutoDiffContext().run {
        val result = body()
        result.d = 1.0 // computing derivative w.r.t result
        runBackwardPass()
        DerivationResult(result.x, derivatives)
    }


abstract class AutoDiffField : Field<Variable> {
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
    abstract fun <R> derive(value: R, block: (R) -> Unit): R

    /**
     * A variable accessing inner state of derivatives. Use only in extensions
     */
    abstract var Variable.d: Double

    // Overloads for Double constants

    operator fun Number.plus(that: Variable): Variable = derive(Variable(this.toDouble() + that.x)) { z ->
        that.d += z.d
    }

    operator fun Variable.plus(b: Number): Variable = b.plus(this)

    operator fun Number.minus(that: Variable): Variable = derive(Variable(this.toDouble() - that.x)) { z ->
        that.d -= z.d
    }

    operator fun Variable.minus(that: Number): Variable = derive(Variable(this.x - that.toDouble())) { z ->
        this.d += z.d
    }
}

/**
 * Automatic Differentiation context class.
 */
private class AutoDiffContext : AutoDiffField() {

    // this stack contains pairs of blocks and values to apply them to
    private var stack = arrayOfNulls<Any?>(8)
    private var sp = 0

    internal val derivatives = HashMap<Variable, Double>()

    override var Variable.d: Double
        get() = derivatives[this] ?: 0.0
        set(value) {
            derivatives[this] = value
        }

    @Suppress("UNCHECKED_CAST")
    override fun <R> derive(value: R, block: (R) -> Unit): R {
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
            val block = stack[--sp] as (Any?) -> Unit
            block(value)
        }
    }

    // Basic math (+, -, *, /)


    override fun add(a: Variable, b: Variable): Variable =
        derive(Variable(a.x + b.x)) { z ->
            a.d += z.d
            b.d += z.d
        }

    override fun multiply(a: Variable, b: Variable): Variable =
        derive(Variable(a.x * b.x)) { z ->
            a.d += z.d * b.x
            b.d += z.d * a.x
        }

    override fun divide(a: Variable, b: Variable): Variable =
        derive(Variable(a.x / b.x)) { z ->
            a.d += z.d / b.x
            b.d -= z.d * a.x / (b.x * b.x)
        }

    override fun multiply(a: Variable, k: Number): Variable =
        derive(Variable(k.toDouble() * a.x)) { z ->
            a.d += z.d * k.toDouble()
        }

    override val zero: Variable get() = Variable(0.0)
    override val one: Variable get() = Variable(1.0)
}

// Extensions for differentiation of various basic mathematical functions

// x ^ 2
fun AutoDiffField.sqr(x: Variable): Variable = derive(Variable(x.x * x.x)) { z ->
    x.d += z.d * 2 * x.x
}

// x ^ 1/2
fun AutoDiffField.sqrt(x: Variable): Variable = derive(Variable(sqrt(x.x))) { z ->
    x.d += z.d * 0.5 / z.x
}

// x ^ y (const)
fun AutoDiffField.pow(x: Variable, y: Double): Variable = derive(Variable(x.x.pow(y))) { z ->
    x.d += z.d * y * x.x.pow(y - 1)
}

fun AutoDiffField.pow(x: Variable, y: Int): Variable = pow(x, y.toDouble())

// exp(x)
fun AutoDiffField.exp(x: Variable): Variable = derive(Variable(kotlin.math.exp(x.x))) { z ->
    x.d += z.d * z.x
}

// ln(x)
fun AutoDiffField.ln(x: Variable): Variable = derive(Variable(kotlin.math.ln(x.x))) { z ->
    x.d += z.d / x.x
}

// x ^ y (any)
fun AutoDiffField.pow(x: Variable, y: Variable): Variable = exp(y * ln(x))

// sin(x)
fun AutoDiffField.sin(x: Variable): Variable = derive(Variable(kotlin.math.sin(x.x))) { z ->
    x.d += z.d * kotlin.math.cos(x.x)
}

// cos(x)
fun AutoDiffField.cos(x: Variable): Variable = derive(Variable(kotlin.math.cos(x.x))) { z ->
    x.d -= z.d * kotlin.math.sin(x.x)
}