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
data class ValueWithDeriv(var x: Double, var d: Double = 0.0) {
    constructor(x: Number) : this(x.toDouble())
}

/**
 * Runs differentiation and establishes [Field<ValueWithDeriv>] context inside the block of code.
 *
 * Example:
 * ```
 * val x = ValueWithDeriv(2) // define variable(s) and their values
 * val y = deriv { sqr(x) + 5 * x + 3 } // write formulate in deriv context
 * assertEquals(17.0, y.x) // the value of result (y)
 * assertEquals(9.0, x.d)  // dy/dx
 * ```
 */
fun deriv(body: AutoDiffField.() -> ValueWithDeriv): ValueWithDeriv =
    ValueWithDerivField().run {
        val result = body()
        result.d = 1.0 // computing derivative w.r.t result
        runBackwardPass()
        result
    }


abstract class AutoDiffField : Field<ValueWithDeriv> {
    /**
     * Performs update of derivative after the rest of the formula in the back-pass.
     *
     * For example, implementation of `sin` function is:
     *
     * ```
     * fun AD.sin(x: ValueWithDeriv): ValueWithDeriv = derive(ValueWithDeriv(sin(x.x)) { z -> // call derive with function result
     *     x.d += z.d * cos(x.x) // update derivative using chain rule and derivative of the function
     * }
     * ```
     */
    abstract fun <R> derive(value: R, block: (R) -> Unit): R

    // Overloads for Double constants

    operator fun Number.plus(that: ValueWithDeriv): ValueWithDeriv = derive(ValueWithDeriv(this.toDouble() + that.x)) { z ->
        that.d += z.d
    }

    operator fun ValueWithDeriv.plus(b: Number): ValueWithDeriv = b.plus(this)

    operator fun Number.minus(that: ValueWithDeriv): ValueWithDeriv = derive(ValueWithDeriv(this.toDouble() - that.x)) { z ->
        that.d -= z.d
    }

    operator fun ValueWithDeriv.minus(that: Number): ValueWithDeriv = derive(ValueWithDeriv(this.x - that.toDouble())) { z ->
        this.d += z.d
    }

    override operator fun Number.times(that: ValueWithDeriv): ValueWithDeriv = derive(ValueWithDeriv(this.toDouble() * that.x)) { z ->
        that.d += z.d * this.toDouble()
    }

    override operator fun ValueWithDeriv.times(b: Number): ValueWithDeriv = b.times(this)

    override operator fun Number.div(that: ValueWithDeriv): ValueWithDeriv = derive(ValueWithDeriv(this.toDouble() / that.x)) { z ->
        that.d -= z.d * this.toDouble() / (that.x * that.x)
    }

    override operator fun ValueWithDeriv.div(that: Number): ValueWithDeriv = derive(ValueWithDeriv(this.x / that.toDouble())) { z ->
        this.d += z.d / that.toDouble()
    }
}

/**
 * Automatic Differentiation context class.
 */
private class ValueWithDerivField : AutoDiffField() {

    // this stack contains pairs of blocks and values to apply them to
    private var stack = arrayOfNulls<Any?>(8)
    private var sp = 0


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


    override fun add(a: ValueWithDeriv, b: ValueWithDeriv): ValueWithDeriv =
        derive(ValueWithDeriv(a.x + b.x)) { z ->
            a.d += z.d
            b.d += z.d
        }

    override fun multiply(a: ValueWithDeriv, b: ValueWithDeriv): ValueWithDeriv =
        derive(ValueWithDeriv(a.x * b.x)) { z ->
            a.d += z.d * b.x
            b.d += z.d * a.x
        }

    override fun divide(a: ValueWithDeriv, b: ValueWithDeriv): ValueWithDeriv =
        derive(ValueWithDeriv(a.x / b.x)) { z ->
            a.d += z.d / b.x
            b.d -= z.d * a.x / (b.x * b.x)
        }

    override fun multiply(a: ValueWithDeriv, k: Number): ValueWithDeriv =
        derive(ValueWithDeriv(k.toDouble() * a.x)) { z ->
            a.d += z.d * k.toDouble()
        }

    override val zero: ValueWithDeriv get() = ValueWithDeriv(0.0, 0.0)
    override val one: ValueWithDeriv get() = ValueWithDeriv(1.0, 0.0)
}

// Extensions for differentiation of various basic mathematical functions

// x ^ 2
fun AutoDiffField.sqr(x: ValueWithDeriv): ValueWithDeriv = derive(ValueWithDeriv(x.x * x.x)) { z ->
    x.d += z.d * 2 * x.x
}

// x ^ 1/2
fun AutoDiffField.sqrt(x: ValueWithDeriv): ValueWithDeriv = derive(ValueWithDeriv(sqrt(x.x))) { z ->
    x.d += z.d * 0.5 / z.x
}

// x ^ y (const)
fun AutoDiffField.pow(x: ValueWithDeriv, y: Double): ValueWithDeriv = derive(ValueWithDeriv(x.x.pow(y))) { z ->
    x.d += z.d * y * x.x.pow(y - 1)
}

fun AutoDiffField.pow(x: ValueWithDeriv, y: Int): ValueWithDeriv = pow(x, y.toDouble())

// exp(x)
fun AutoDiffField.exp(x: ValueWithDeriv): ValueWithDeriv = derive(ValueWithDeriv(kotlin.math.exp(x.x))) { z ->
    x.d += z.d * z.x
}

// ln(x)
fun AutoDiffField.ln(x: ValueWithDeriv): ValueWithDeriv = derive(ValueWithDeriv(kotlin.math.ln(x.x))) { z ->
    x.d += z.d / x.x
}

// x ^ y (any)
fun AutoDiffField.pow(x: ValueWithDeriv, y: ValueWithDeriv): ValueWithDeriv = exp(y * ln(x))

// sin(x)
fun AutoDiffField.sin(x: ValueWithDeriv): ValueWithDeriv = derive(ValueWithDeriv(kotlin.math.sin(x.x))) { z ->
    x.d += z.d * kotlin.math.cos(x.x)
}

// cos(x)
fun AutoDiffField.cos(x: ValueWithDeriv): ValueWithDeriv = derive(ValueWithDeriv(kotlin.math.cos(x.x))) { z ->
    x.d -= z.d * kotlin.math.sin(x.x)
}