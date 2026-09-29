/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.*
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.expressions.Symbol.Companion.y
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Int32Ring
import space.kscience.kmath.structures.Float64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

private val a = Symbol("a")
private val b = Symbol("b")
private val c = Symbol("c")

private class Functions(val double: Map<String, Expression<Float64>>, val int: Map<String, Expression<Int>>)

private val mapFunctions = Functions(
    double = mapOf(
        "f" to Expression(Float64Field.type) { it.getValue(a) * 2 - it.getValue(b) },
        "g" to Expression(Float64Field.type) { it.getValue(a) + 1 },
        "h" to Expression(Float64Field.type) { it.getValue(a) * 100 + it.getValue(b) * 10 + it.getValue(c) },
    ),
    int = mapOf("f" to Expression(Int32Ring.type) { it.getValue(a) * 2 - it.getValue(b) }),
)

// Same functions as array-based expressions; indexers deliberately differ from the order the calls use, and the
// map-based invoke throws to make sure compilers call the array-based one.
@OptIn(UnstableKMathAPI::class)
private val arrayFunctions = Functions(
    double = mapOf(
        "f" to object : DoubleExpression {
            override val indexer = SimpleSymbolIndexer(listOf(b, a))
            override fun invoke(arguments: DoubleArray) = arguments[1] * 2 - arguments[0]
            override fun invoke(arguments: Map<Symbol, Double>): Double = error("Map path used")
        },
        "g" to object : DoubleExpression {
            override val indexer = SimpleSymbolIndexer(listOf(a))
            override fun invoke(arguments: DoubleArray) = arguments[0] + 1
            override fun invoke(arguments: Map<Symbol, Double>): Double = error("Map path used")
        },
        "h" to object : DoubleExpression {
            override val indexer = SimpleSymbolIndexer(listOf(c, a, b))
            override fun invoke(arguments: DoubleArray) = arguments[1] * 100 + arguments[2] * 10 + arguments[0]
            override fun invoke(arguments: Map<Symbol, Double>): Double = error("Map path used")
        },
    ),
    int = mapOf(
        "f" to object : IntExpression {
            override val indexer = SimpleSymbolIndexer(listOf(b, a))
            override fun invoke(arguments: IntArray) = arguments[1] * 2 - arguments[0]
            override fun invoke(arguments: Map<Symbol, Int>): Int = error("Map path used")
        },
    ),
)

private inline fun runFunctionCallTest(action: CompilerTestContext.(Functions) -> Unit) = runCompilerTest {
    if (supportsFunctionCalls) {
        action(mapFunctions)
        action(arrayFunctions)
    }
}

internal class TestCompilerFunctionCalls {
    @Test
    fun functionCall() = runFunctionCallTest { functions ->
        val mst = MST.FunctionCall("f", mapOf(a to x, b to y))
        assertEquals(5.0, mst.compile(Float64Field, mapOf(x to 3.0, y to 1.0), functions.double))
        assertEquals(5, mst.compile(Int32Ring, mapOf(x to 3, y to 1), functions.int))
    }

    @Test
    fun nestedFunctionCall() = runFunctionCallTest { functions ->
        val mst = MST.Binary(
            "+",
            MST.FunctionCall("f", mapOf(b to MST.FunctionCall("g", mapOf(a to x)), a to MST.Numeric(4.0))),
            x,
        )

        assertEquals(7.0, mst.compile(Float64Field, mapOf(x to 2.0), functions.double))
    }

    @Test
    fun undefinedFunctionFails() = runFunctionCallTest { functions ->
        val mst = MST.FunctionCall("undefined", mapOf(a to x))
        assertFails { mst.compile(Float64Field, mapOf(x to 1.0), functions.double) }
    }

    @Test
    fun argumentOrderDoesNotMatter() = runFunctionCallTest { functions ->
        val arguments = mapOf(x to 3.0, y to 1.0)
        assertEquals(5.0, MST.FunctionCall("f", mapOf(a to x, b to y)).compile(Float64Field, arguments, functions.double))
        assertEquals(5.0, MST.FunctionCall("f", mapOf(b to y, a to x)).compile(Float64Field, arguments, functions.double))
    }

    @Test
    fun threeArgumentsInEveryOrder() = runFunctionCallTest { functions ->
        val bindings = listOf(a to MST.Numeric(1), b to x, c to y)

        listOf(listOf(0, 1, 2), listOf(0, 2, 1), listOf(1, 0, 2), listOf(1, 2, 0), listOf(2, 0, 1), listOf(2, 1, 0))
            .forEach { order ->
                val mst = MST.FunctionCall("h", order.associate { bindings[it] })
                assertEquals(123.0, mst.compile(Float64Field, mapOf(x to 2.0, y to 3.0), functions.double), "$order")
            }
    }

    @Test
    fun sameFunctionWithDifferentBindings() = runFunctionCallTest { functions ->
        val mst = MST.Binary(
            "-",
            MST.FunctionCall("f", mapOf(a to x, b to y)),
            MST.FunctionCall("f", mapOf(a to y, b to x)),
        )

        // (2*5 - 2) - (2*2 - 5) = 9
        assertEquals(9.0, mst.compile(Float64Field, mapOf(x to 5.0, y to 2.0), functions.double))
        assertEquals(9, mst.compile(Int32Ring, mapOf(x to 5, y to 2), functions.int))
    }

    @Test
    fun sameVariableInSeveralArguments() = runFunctionCallTest { functions ->
        val mst = MST.FunctionCall("f", mapOf(a to x, b to x))
        assertEquals(4.0, mst.compile(Float64Field, mapOf(x to 4.0), functions.double))
    }

    @Test
    fun constantArguments() = runFunctionCallTest { functions ->
        val mst = MST.FunctionCall("f", mapOf(a to MST.Numeric(3), b to MST.Numeric(1)))
        assertEquals(5.0, mst.compile(Float64Field, emptyMap(), functions.double))
        assertEquals(5, mst.compile(Int32Ring, emptyMap(), functions.int))
    }

    @Test
    fun extraArgumentIsIgnored() = runFunctionCallTest { functions ->
        val mst = MST.FunctionCall("g", mapOf(a to x, b to y))
        assertEquals(3.0, mst.compile(Float64Field, mapOf(x to 2.0, y to 7.0), functions.double))
    }

    @Test
    fun missingArgumentFails() = runFunctionCallTest { functions ->
        val mst = MST.FunctionCall("f", mapOf(a to x))
        assertFails { mst.compile(Float64Field, mapOf(x to 1.0), functions.double) }
    }
}
