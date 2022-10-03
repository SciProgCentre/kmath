/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast.rendering

import space.kscience.kmath.ast.rendering.TestUtils.testLatex
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.operations.GroupOps
import kotlin.test.Test

internal class TestLatex {
    @Test
    fun number() = testLatex("42", "42")

    @Test
    fun symbol() = testLatex("x", "x")

    @Test
    fun operatorName() = testLatex("sin(1)", "\\operatorname{sin}\\,\\left(1\\right)")

    @Test
    fun specialSymbol() {
        testLatex(MST.Numeric(Double.POSITIVE_INFINITY), "\\infty")
        testLatex("pi", "\\pi")
    }

    @Test
    fun operand() {
        testLatex("sin(1)", "\\operatorname{sin}\\,\\left(1\\right)")
        testLatex("1+1", "1+1")
    }

    @Test
    fun unaryOperator() = testLatex("sin(1)", "\\operatorname{sin}\\,\\left(1\\right)")

    @Test
    fun unaryPlus() = testLatex(MST.Unary(GroupOps.PLUS_OPERATION, MST.Numeric(1)), "+1")

    @Test
    fun unaryMinus() = testLatex("-x", "-x")

    @Test
    fun radical() = testLatex("sqrt(x)", "\\sqrt{x}")

    @Test
    fun superscript() = testLatex("x^y", "x^{y}")

    @Test
    fun subscript() = testLatex(SubscriptSyntax("", SymbolSyntax("x"), NumberSyntax("123")), "x_{123}")

    @Test
    fun binaryOperator() = testLatex("f(x, y)", "\\operatorname{f}\\left(x,y\\right)")

    @Test
    fun binaryPlus() = testLatex("x+x", "x+x")

    @Test
    fun binaryMinus() = testLatex("x-x", "x-x")

    @Test
    fun fraction() = testLatex("x/x", "\\frac{x}{x}")

    @Test
    fun radicalWithIndex() = testLatex(RadicalWithIndexSyntax("", SymbolSyntax("x"), SymbolSyntax("y")), "\\sqrt[x]{y}")

    @Test
    fun multiplication() {
        testLatex("x*1", "x\\times1")
        testLatex("1*x", "1\\,x")
    }
}
