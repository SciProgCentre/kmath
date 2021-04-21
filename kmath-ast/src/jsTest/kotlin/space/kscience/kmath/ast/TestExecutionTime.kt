/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.expressions.*
import space.kscience.kmath.misc.symbol
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.bindSymbol
import space.kscience.kmath.operations.invoke
import kotlin.math.sin
import kotlin.random.Random
import kotlin.test.Test
import kotlin.time.measureTime
import space.kscience.kmath.estree.compileToExpression as estreeCompileToExpression
import space.kscience.kmath.wasm.compileToExpression as wasmCompileToExpression

internal class TestExecutionTime {
    private companion object {
        private const val times = 1_000_000
        private val x by symbol
        private val algebra: ExtendedField<Double> = DoubleField

        private val functional = DoubleField.expressionInExtendedField {
            bindSymbol(x) * const(2.0) + const(2.0) / bindSymbol(x) - const(16.0) / sin(bindSymbol(x))
        }

        private val node = MstExtendedField {
            bindSymbol(x) * number(2.0) + number(2.0) / bindSymbol(x) - number(16.0) / sin(bindSymbol(x))
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
            args.getValue(x) * 2.0 + 2.0 / args.getValue(x) - 16.0 / sin(args.getValue(x))
        }
    }

    private fun invokeAndSum(name: String, expr: Expression<Double>) {
        println(name)
        val rng = Random(0)
        var sum = 0.0
        measureTime { repeat(times) { sum += expr(x to rng.nextDouble()) } }.also(::println)
    }

    @Test
    fun functionalExpression() = invokeAndSum("functional", functional)

    @Test
    fun mstExpression() = invokeAndSum("mst", mst)

    @Test
    fun wasmExpression() = invokeAndSum("wasm", wasm)

    @Test
    fun estreeExpression() = invokeAndSum("estree", wasm)

    @Test
    fun rawExpression() = invokeAndSum("raw", raw)
}
