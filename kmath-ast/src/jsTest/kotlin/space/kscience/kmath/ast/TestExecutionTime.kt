/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.expressions.*
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.bindSymbol
import space.kscience.kmath.operations.invoke
import kotlin.math.sin
import kotlin.random.Random
import kotlin.test.Test
import kotlin.time.measureTime
import space.kscience.kmath.estree.compileToExpression as estreeCompileToExpression
import space.kscience.kmath.wasm.compileToExpression as wasmCompileToExpression

// TODO move to benchmarks when https://github.com/Kotlin/kotlinx-benchmark/pull/38 or similar feature is merged
internal class TestExecutionTime {
    private companion object {
        private const val times = 1_000_000
        private val x by symbol
        private val algebra = DoubleField

        private val functional = DoubleField.expressionInExtendedField {
            bindSymbol(x) * const(2.0) + const(2.0) / bindSymbol(x) - const(16.0) / sin(bindSymbol(x))
        }

        private val node = MstExtendedField {
            x * number(2.0) + number(2.0) / x - number(16.0) / sin(x)
        }

        private val mst = node.toExpression(DoubleField)
        private val wasm = node.wasmCompileToExpression(DoubleField)
        private val estree = node.estreeCompileToExpression(DoubleField)

        // In JavaScript, the expression below is implemented like
        //   _no_name_provided__125.prototype.invoke_178 = function (args) {
        //    var tmp = getValue(args, raw$_get_x__3(this._$x$delegate_2)) * 2.0 + 2.0 / getValue(args, raw$_get_x__3(this._$x$delegate_2));
        //    var tmp0_sin_0_5 = getValue(args, raw$_get_x__3(this._$x$delegate_2));
        //    return tmp - 16.0 / Math.sin(tmp0_sin_0_5);
        //  };

        private val raw = Expression<Double> { args ->
            val x = args[x]!!
            x * 2.0 + 2.0 / x - 16.0 / sin(x)
        }

        private val justCalculate = { args: dynamic ->
            val x = args[x].unsafeCast<Double>()
            x * 2.0 + 2.0 / x - 16.0 / sin(x)
        }
    }

    private fun invokeAndSum(name: String, expr: Expression<Double>) {
        println(name)
        val rng = Random(0)
        var sum = 0.0
        measureTime {
            repeat(times) { sum += expr(x to rng.nextDouble()) }
        }.also(::println)
    }

    /**
     * [Expression] created with [expressionInExtendedField].
     */
    @Test
    fun functionalExpression() = invokeAndSum("functional", functional)

    /**
     * [Expression] created with [mstExpression].
     */
    @Test
    fun mstExpression() = invokeAndSum("mst", mst)

    /**
     * [Expression] created with [wasmCompileToExpression].
     */
    @Test
    fun wasmExpression() = invokeAndSum("wasm", wasm)

    /**
     * [Expression] created with [estreeCompileToExpression].
     */
    @Test
    fun estreeExpression() = invokeAndSum("estree", wasm)

    /**
     * [Expression] implemented manually with `kotlin.math`.
     */
    @Test
    fun rawExpression() = invokeAndSum("raw", raw)

    /**
     * Direct computation w/o [Expression].
     */
    @Test
    fun justCalculateExpression() {
        println("justCalculate")
        val rng = Random(0)
        var sum = 0.0
        measureTime {
            repeat(times) {
                val arg = rng.nextDouble()
                val o = js("{}")
                o["x"] = arg
                sum += justCalculate(o)
            }
        }.also(::println)
    }
}
