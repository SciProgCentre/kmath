/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import com.github.h0tk3y.betterParse.parser.ErrorResult
import com.github.h0tk3y.betterParse.parser.Parsed
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.ComplexField
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.interpret
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.MutableBufferFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

internal class TestParser {
    @Test
    fun evaluateParsedMst() {
        val mst = "2+2*(2+2)".parseMath()
        val res = mst.interpret(ComplexField)
        assertEquals(Complex(10.0, 0.0), res)
    }

    @Test
    fun evaluateMstSymbol() {
        val mst = "i".parseMath()
        val res = mst.interpret(ComplexField)
        assertEquals(ComplexField.i, res)
    }

    @Test
    fun evaluateMstUnary() {
        val mst = "sin(0)".parseMath()
        val res = mst.interpret(Float64Field)
        assertEquals(0.0, res)
    }

    @Test
    fun evaluateMstBinary() {
        val magicalAlgebra = object : Algebra<String> {
            override val bufferFactory: MutableBufferFactory<String> get() = MutableBufferFactory()

            override fun bindSymbolOrNull(value: String): String = value

            override fun unaryOperationFunction(operation: String): (arg: String) -> String {
                throw NotImplementedError()
            }

            override fun binaryOperationFunction(operation: String): (left: String, right: String) -> String =
                when (operation) {
                    "magic" -> { left, right -> "$left ★ $right" }
                    else -> throw NotImplementedError()
                }
        }

        val mst = "magic(a, b)".parseMath()
        val res = mst.interpret(magicalAlgebra)
        assertEquals("a ★ b", res)
    }

    // LLM generated code: Added comprehensive tests for ArithmeticsEvaluator and parser functionality

    @Test
    fun testNumericLiterals() {
        assertEquals(MST.Numeric(0L), "0".parseMath())
        assertEquals(MST.Numeric(42L), "42".parseMath())
        assertEquals(MST.Numeric(123456789L), "123456789".parseMath())

        assertEquals(MST.Numeric(3.14), "3.14".parseMath())
        assertEquals(MST.Numeric(0.5), "0.5".parseMath())
        assertEquals(MST.Numeric(0.5), ".5".parseMath())
        assertEquals(MST.Numeric(2.0), "2.0".parseMath())
        assertEquals(MST.Numeric(100.0), "100.0".parseMath())

        assertEquals(MST.Numeric(100000.0), "1e5".parseMath())
        assertEquals(MST.Numeric(1500.0), "1.5e3".parseMath())
        assertEquals(MST.Numeric(0.002), "2e-3".parseMath())
        assertEquals(MST.Numeric(10000.0), "1E+4".parseMath())
        assertEquals(MST.Numeric(0.0125), "1.25E-2".parseMath())
        assertEquals(MST.Numeric(50.0), ".5e2".parseMath())
        assertEquals(MST.Numeric(1e100), "1e100".parseMath())
    }

    @Test
    fun testIdentifiers() {
        assertEquals(Symbol("x"), "x".parseMath())
        assertEquals(Symbol("y"), "y".parseMath())
        assertEquals(Symbol("alpha"), "alpha".parseMath())
        assertEquals(Symbol("Beta"), "Beta".parseMath())
        assertEquals(Symbol("var_1"), "var_1".parseMath())
        assertEquals(Symbol("_var"), "_var".parseMath())
        assertEquals(Symbol("_"), "_".parseMath())
        assertEquals(Symbol("a1b2c3"), "a1b2c3".parseMath())
    }

    @Test
    fun testUnaryMinus() {
        assertEquals(MST.Unary(GroupOps.MINUS_OPERATION, Symbol("x")), "-x".parseMath())
        assertEquals(
            MST.Unary(GroupOps.MINUS_OPERATION, MST.Unary(GroupOps.MINUS_OPERATION, Symbol("x"))),
            "--x".parseMath()
        )
        assertEquals(MST.Unary(GroupOps.MINUS_OPERATION, MST.Numeric(5L)), "-5".parseMath())
        assertEquals(
            MST.Unary(
                GroupOps.MINUS_OPERATION,
                MST.Binary(GroupOps.PLUS_OPERATION, Symbol("x"), Symbol("y"))
            ),
            "-(x + y)".parseMath()
        )
        assertEquals(
            MST.Binary(
                RingOps.TIMES_OPERATION,
                MST.Unary(GroupOps.MINUS_OPERATION, Symbol("x")),
                Symbol("y")
            ),
            "-x * y".parseMath()
        )
        assertEquals(
            MST.Binary(
                RingOps.TIMES_OPERATION,
                Symbol("x"),
                MST.Unary(GroupOps.MINUS_OPERATION, Symbol("y"))
            ),
            "x * -y".parseMath()
        )
        assertEquals(
            MST.Binary(
                PowerOperations.POW_OPERATION,
                MST.Unary(GroupOps.MINUS_OPERATION, Symbol("x")),
                MST.Numeric(2L)
            ),
            "-x ^ 2".parseMath()
        )
        assertEquals(
            MST.Unary(
                GroupOps.MINUS_OPERATION,
                MST.Unary("sin", Symbol("x"))
            ),
            "-sin(x)".parseMath()
        )
    }

    @Test
    fun testBinaryOperationsAndAssociativity() {
        // Addition & Subtraction (left-associative)
        assertEquals(
            MST.Binary(
                GroupOps.PLUS_OPERATION,
                MST.Binary(GroupOps.PLUS_OPERATION, Symbol("a"), Symbol("b")),
                Symbol("c")
            ),
            "a + b + c".parseMath()
        )
        assertEquals(
            MST.Binary(
                GroupOps.MINUS_OPERATION,
                MST.Binary(GroupOps.MINUS_OPERATION, Symbol("a"), Symbol("b")),
                Symbol("c")
            ),
            "a - b - c".parseMath()
        )
        assertEquals(
            MST.Binary(
                GroupOps.MINUS_OPERATION,
                MST.Binary(GroupOps.PLUS_OPERATION, Symbol("a"), Symbol("b")),
                Symbol("c")
            ),
            "a + b - c".parseMath()
        )

        // Multiplication & Division (left-associative)
        assertEquals(
            MST.Binary(
                RingOps.TIMES_OPERATION,
                MST.Binary(RingOps.TIMES_OPERATION, Symbol("a"), Symbol("b")),
                Symbol("c")
            ),
            "a * b * c".parseMath()
        )
        assertEquals(
            MST.Binary(
                FieldOps.DIV_OPERATION,
                MST.Binary(FieldOps.DIV_OPERATION, Symbol("a"), Symbol("b")),
                Symbol("c")
            ),
            "a / b / c".parseMath()
        )
        assertEquals(
            MST.Binary(
                RingOps.TIMES_OPERATION,
                MST.Binary(FieldOps.DIV_OPERATION, Symbol("a"), Symbol("b")),
                Symbol("c")
            ),
            "a / b * c".parseMath()
        )

        // Power (left-associative in ArithmeticsEvaluator grammar)
        assertEquals(
            MST.Binary(
                PowerOperations.POW_OPERATION,
                MST.Binary(PowerOperations.POW_OPERATION, Symbol("a"), Symbol("b")),
                Symbol("c")
            ),
            "a ^ b ^ c".parseMath()
        )

        // Precedence: Pow > Mul/Div > Add/Sub
        assertEquals(
            MST.Binary(
                GroupOps.PLUS_OPERATION,
                Symbol("a"),
                MST.Binary(RingOps.TIMES_OPERATION, Symbol("b"), Symbol("c"))
            ),
            "a + b * c".parseMath()
        )
        assertEquals(
            MST.Binary(
                GroupOps.PLUS_OPERATION,
                MST.Binary(RingOps.TIMES_OPERATION, Symbol("a"), Symbol("b")),
                Symbol("c")
            ),
            "a * b + c".parseMath()
        )
        assertEquals(
            MST.Binary(
                GroupOps.MINUS_OPERATION,
                Symbol("a"),
                MST.Binary(FieldOps.DIV_OPERATION, Symbol("b"), Symbol("c"))
            ),
            "a - b / c".parseMath()
        )
        assertEquals(
            MST.Binary(
                RingOps.TIMES_OPERATION,
                Symbol("a"),
                MST.Binary(PowerOperations.POW_OPERATION, Symbol("b"), Symbol("c"))
            ),
            "a * b ^ c".parseMath()
        )
        assertEquals(
            MST.Binary(
                RingOps.TIMES_OPERATION,
                MST.Binary(PowerOperations.POW_OPERATION, Symbol("a"), Symbol("b")),
                Symbol("c")
            ),
            "a ^ b * c".parseMath()
        )
        assertEquals(
            MST.Binary(
                FieldOps.DIV_OPERATION,
                Symbol("a"),
                MST.Binary(PowerOperations.POW_OPERATION, Symbol("b"), Symbol("c"))
            ),
            "a / b ^ c".parseMath()
        )
    }

    @Test
    fun testParentheses() {
        assertEquals(Symbol("x"), "(x)".parseMath())
        assertEquals(Symbol("x"), "((x))".parseMath())
        assertEquals(Symbol("x"), "(((x)))".parseMath())

        assertEquals(
            MST.Binary(
                RingOps.TIMES_OPERATION,
                MST.Binary(GroupOps.PLUS_OPERATION, Symbol("a"), Symbol("b")),
                Symbol("c")
            ),
            "(a + b) * c".parseMath()
        )
        assertEquals(
            MST.Binary(
                RingOps.TIMES_OPERATION,
                Symbol("a"),
                MST.Binary(GroupOps.PLUS_OPERATION, Symbol("b"), Symbol("c"))
            ),
            "a * (b + c)".parseMath()
        )
        assertEquals(
            MST.Binary(
                PowerOperations.POW_OPERATION,
                MST.Binary(GroupOps.PLUS_OPERATION, Symbol("a"), Symbol("b")),
                MST.Binary(GroupOps.MINUS_OPERATION, Symbol("c"), Symbol("d"))
            ),
            "(a + b) ^ (c - d)".parseMath()
        )
    }

    @Test
    fun testUnaryAndBinaryFunctions() {
        // Unary function calls
        assertEquals(MST.Unary("sin", Symbol("x")), "sin(x)".parseMath())
        assertEquals(MST.Unary("cos", MST.Numeric(0L)), "cos(0)".parseMath())
        assertEquals(
            MST.Unary("exp", MST.Unary("sin", Symbol("x"))),
            "exp(sin(x))".parseMath()
        )
        assertEquals(
            MST.Unary(
                "cos",
                MST.Binary(
                    GroupOps.PLUS_OPERATION,
                    MST.Binary(RingOps.TIMES_OPERATION, MST.Numeric(2L), Symbol("x")),
                    MST.Numeric(1L)
                )
            ),
            "cos(2 * x + 1)".parseMath()
        )

        // Binary function calls
        assertEquals(
            MST.Binary("min", Symbol("a"), Symbol("b")),
            "min(a, b)".parseMath()
        )
        assertEquals(
            MST.Binary(
                "hypot",
                MST.Binary(GroupOps.PLUS_OPERATION, Symbol("x"), MST.Numeric(1L)),
                MST.Binary(RingOps.TIMES_OPERATION, Symbol("y"), MST.Numeric(2L))
            ),
            "hypot(x + 1, y * 2)".parseMath()
        )
        assertEquals(
            MST.Binary(
                "min",
                MST.Binary("max", Symbol("a"), Symbol("b")),
                Symbol("c")
            ),
            "min(max(a, b), c)".parseMath()
        )
        assertEquals(
            MST.Binary(
                "atan2",
                MST.Unary("sin", Symbol("y")),
                MST.Unary("cos", Symbol("x"))
            ),
            "atan2(sin(y), cos(x))".parseMath()
        )

        // Function names with underscores and numbers
        assertEquals(MST.Unary("func_1", Symbol("x")), "func_1(x)".parseMath())
        assertEquals(
            MST.Binary("log_base", Symbol("x"), Symbol("b")),
            "log_base(x, b)".parseMath()
        )
    }

    @Test
    fun testWhitespaceHandling() {
        assertEquals(
            "2+3*4".parseMath(),
            "  \t 2   +   3 \n * \r\n 4  ".parseMath()
        )
        assertEquals(
            "sin(x+1)".parseMath(),
            " sin (  x   +   1  ) ".parseMath()
        )
        assertEquals(
            "min(a,b)".parseMath(),
            " min (  a  ,  b  ) ".parseMath()
        )
        assertEquals(
            "(x+y)".parseMath(),
            " (  x  +  y  ) ".parseMath()
        )
    }

    @Test
    fun testComplexExpression() {
        val expr = "-(sin(x) ^ 2 + cos(x) ^ 2) * exp(-t / tau) + min(a, b)"
        val mst = expr.parseMath()
        val expected = MST.Binary(
            GroupOps.PLUS_OPERATION,
            MST.Binary(
                RingOps.TIMES_OPERATION,
                MST.Unary(
                    GroupOps.MINUS_OPERATION,
                    MST.Binary(
                        GroupOps.PLUS_OPERATION,
                        MST.Binary(
                            PowerOperations.POW_OPERATION,
                            MST.Unary("sin", Symbol("x")),
                            MST.Numeric(2L)
                        ),
                        MST.Binary(
                            PowerOperations.POW_OPERATION,
                            MST.Unary("cos", Symbol("x")),
                            MST.Numeric(2L)
                        )
                    )
                ),
                MST.Unary(
                    "exp",
                    MST.Binary(
                        FieldOps.DIV_OPERATION,
                        MST.Unary(GroupOps.MINUS_OPERATION, Symbol("t")),
                        Symbol("tau")
                    )
                )
            ),
            MST.Binary("min", Symbol("a"), Symbol("b"))
        )
        assertEquals(expected, mst)
    }

    @Test
    fun testTryParseMathSuccess() {
        val result = "2 + 2".tryParseMath()
        assertIs<Parsed<MST>>(result)
        assertEquals(
            MST.Binary(GroupOps.PLUS_OPERATION, MST.Numeric(2L), MST.Numeric(2L)),
            result.value
        )
    }

    @Test
    fun testTryParseMathErrors() {
        val invalidInputs = listOf(
            "",
            "   ",
            "2 +",
            "* 3",
            "2 ^",
            "/",
            "-",
            "(2 + 3",
            "2 + 3)",
            "((2 + 3)",
            "(2 + (3 * 4)",
            "2 @ 3",
            "2 $ 3",
            "2 # 3",
            "a & b",
            "sin()",
            "foo(x,)",
            "foo(,y)",
            "foo()",
            "foo(a, b, c)",
            "sin(",
            "sin(x"
        )

        for (input in invalidInputs) {
            val result = input.tryParseMath()
            assertIs<ErrorResult>(result, "Expected ErrorResult for input: \"$input\"")
        }
    }

    @Test
    fun testParseMathThrowsOnInvalidInput() {
        val invalidInputs = listOf(
            "",
            "2 +",
            "(1 + 2",
            "2 @ 3",
            "foo(1, 2, 3)",
            "min()",
            "foo(a=)",
            "foo(=1)",
            "foo(a=1,)"
        )

        for (input in invalidInputs) {
            assertFailsWith<Exception>("Expected parseMath to fail on: \"$input\"") {
                input.parseMath()
            }
        }
    }

    // LLM generated code: Tests for string literals and named argument function calls (MST.FunctionCall)

    @Test
    fun testStringLiterals() {
        assertEquals(Symbol("bar"), "\"bar\"".parseMath())
        assertEquals(Symbol("bar"), "'bar'".parseMath())
        assertEquals(Symbol("hello world"), "\"hello world\"".parseMath())
        assertEquals(Symbol("hello world"), "'hello world'".parseMath())
        assertEquals(Symbol(""), "\"\"".parseMath())
        assertEquals(Symbol(""), "''".parseMath())
        assertEquals(Symbol("a \\\" b"), "\"a \\\" b\"".parseMath())
    }

    @Test
    fun testFunctionCallNamedArguments() {
        // Construct from issue description: foo(a=22,b="bar", c=false)
        val mst = "foo(a=22,b=\"bar\", c=false)".parseMath()
        assertEquals(
            MST.FunctionCall(
                "foo",
                mapOf(
                    Symbol("a") to MST.Numeric(22L),
                    Symbol("b") to Symbol("bar"),
                    Symbol("c") to Symbol("false")
                )
            ),
            mst
        )

        // Single named argument
        assertEquals(
            MST.FunctionCall("foo", mapOf(Symbol("a") to MST.Numeric(22L))),
            "foo(a=22)".parseMath()
        )

        // Multiple arguments with different expressions
        assertEquals(
            MST.FunctionCall(
                "foo",
                mapOf(
                    Symbol("x") to MST.Binary(GroupOps.PLUS_OPERATION, Symbol("a"), MST.Numeric(1L)),
                    Symbol("y") to MST.Binary(RingOps.TIMES_OPERATION, Symbol("b"), MST.Numeric(2L)),
                    Symbol("z") to MST.Unary("sin", Symbol("c"))
                )
            ),
            "foo(x=a + 1, y=b * 2, z=sin(c))".parseMath()
        )

        // Nested function calls
        assertEquals(
            MST.FunctionCall(
                "outer",
                mapOf(
                    Symbol("inner") to MST.FunctionCall(
                        "bar",
                        mapOf(
                            Symbol("x") to MST.Numeric(1L),
                            Symbol("msg") to Symbol("hello")
                        )
                    ),
                    Symbol("flag") to Symbol("true")
                )
            ),
            "outer(inner=bar(x=1, msg=\"hello\"), flag=true)".parseMath()
        )

        // Function call in expressions
        assertEquals(
            MST.Binary(
                GroupOps.PLUS_OPERATION,
                MST.FunctionCall("foo", mapOf(Symbol("a") to MST.Numeric(1L))),
                MST.Binary(
                    RingOps.TIMES_OPERATION,
                    MST.FunctionCall("bar", mapOf(Symbol("b") to MST.Numeric(2L))),
                    MST.Numeric(3L)
                )
            ),
            "foo(a=1) + bar(b=2) * 3".parseMath()
        )

        assertEquals(
            MST.Unary(
                GroupOps.MINUS_OPERATION,
                MST.FunctionCall("foo", mapOf(Symbol("a") to MST.Numeric(1L)))
            ),
            "-foo(a=1)".parseMath()
        )
    }

    @Test
    fun testFunctionCallEvaluation() {
        val mst = "myFunc(a=20, b=22)".parseMath()
        val context = space.kscience.kmath.expressions.MstInterpreterContext(
            algebra = Float64Field,
            arguments = emptyMap(),
            functions = mapOf(
                "myFunc" to space.kscience.kmath.expressions.Expression(Float64Field.type) { args ->
                    (args[Symbol("a")] ?: 0.0) + (args[Symbol("b")] ?: 0.0)
                }
            )
        )
        val result = context(context) { mst.interpret() }
        assertEquals(42.0, result)
    }
}
