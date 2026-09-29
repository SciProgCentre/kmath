/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.asm

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.*
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.expressions.Symbol.Companion.y
import space.kscience.kmath.operations.Algebra
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Int32Ring
import space.kscience.kmath.operations.Int64Ring
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

// Each array-based expression kind is called from both the generic and the primitive compiler.
@OptIn(UnstableKMathAPI::class)
internal class TestAsmArrayExpressionCalls {
    private val a = Symbol("a")
    private val b = Symbol("b")

    // Called with `a` and `b` swapped relative to the indexer.
    private val mst = MST.FunctionCall("f", mapOf(a to x, b to y))

    // 2a - b
    private val compiledF = MST.Binary("-", MST.Binary("*", a, MST.Numeric(2)), b)

    @Test
    fun doubleExpression() {
        val f = object : DoubleExpression {
            override val indexer = SimpleSymbolIndexer(listOf(b, a))
            override fun invoke(arguments: DoubleArray) = arguments[1] * 2 - arguments[0]
            override fun invoke(arguments: Map<Symbol, Double>): Double = fail("Map path used")
        }

        val arguments = mapOf(x to 3.0, y to 1.0)
        assertEquals(5.0, mst.compile(Float64Field, arguments, mapOf("f" to f)))
        assertEquals(5.0, mst.compile(Float64Field as Algebra<Double>, arguments, mapOf<String, Expression<Double>>("f" to f)))
    }

    @Test
    fun intExpression() {
        val f = object : IntExpression {
            override val indexer = SimpleSymbolIndexer(listOf(b, a))
            override fun invoke(arguments: IntArray) = arguments[1] * 2 - arguments[0]
            override fun invoke(arguments: Map<Symbol, Int>): Int = fail("Map path used")
        }

        val arguments = mapOf(x to 3, y to 1)
        assertEquals(5, mst.compile(Int32Ring, arguments, mapOf("f" to f)))
        assertEquals(5, mst.compile(Int32Ring as Algebra<Int>, arguments, mapOf<String, Expression<Int>>("f" to f)))
    }

    @Test
    fun longExpression() {
        val f = object : LongExpression {
            override val indexer = SimpleSymbolIndexer(listOf(b, a))
            override fun invoke(arguments: LongArray) = arguments[1] * 2 - arguments[0]
            override fun invoke(arguments: Map<Symbol, Long>): Long = fail("Map path used")
        }

        val arguments = mapOf(x to 3L, y to 1L)
        assertEquals(5L, mst.compile(Int64Ring, arguments, mapOf("f" to f)))
        assertEquals(5L, mst.compile(Int64Ring as Algebra<Long>, arguments, mapOf<String, Expression<Long>>("f" to f)))
    }

    @Test
    fun compiledExpressionsAsFunctions() {
        val double = compiledF.compileToExpression(Float64Field)
        assertEquals(5.0, mst.compile(Float64Field, mapOf(x to 3.0, y to 1.0), mapOf("f" to double)))
        assertEquals(5.0, mst.compile(Float64Field as Algebra<Double>, mapOf(x to 3.0, y to 1.0), mapOf("f" to double)))

        val int = compiledF.compileToExpression(Int32Ring)
        assertEquals(5, mst.compile(Int32Ring, mapOf(x to 3, y to 1), mapOf("f" to int)))
        assertEquals(5, mst.compile(Int32Ring as Algebra<Int>, mapOf(x to 3, y to 1), mapOf("f" to int)))

        val long = compiledF.compileToExpression(Int64Ring)
        assertEquals(5L, mst.compile(Int64Ring, mapOf(x to 3L, y to 1L), mapOf("f" to long)))
        assertEquals(5L, mst.compile(Int64Ring as Algebra<Long>, mapOf(x to 3L, y to 1L), mapOf("f" to long)))
    }
}
