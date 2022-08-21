/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast.rendering

import space.kscience.kmath.ast.rendering.TestUtils.testLatex
import kotlin.test.Test

internal class TestStages {
    @Test
    fun betterMultiplication() {
        testLatex("a*1", "a\\times1")
        testLatex("1*(2/3)", "1\\times\\left(\\frac{2}{3}\\right)")
        testLatex("1*1", "1\\times1")
        testLatex("2e10", "2\\times10^{10}")
        testLatex("2*x", "2\\,x")
        testLatex("2*(x+1)", "2\\,\\left(x+1\\right)")
        testLatex("x*y", "x\\,y")
    }

    @Test
    fun parentheses() {
        testLatex("(x+1)", "x+1")
        testLatex("x*x*x", "x\\,x\\,x")
        testLatex("(x+x)*x", "\\left(x+x\\right)\\,x")
        testLatex("x+x*x", "x+x\\,x")
        testLatex("x+x^x*x+x", "x+x^{x}\\,x+x")
        testLatex("(x+x)^x+x*x", "\\left(x+x\\right)^{x}+x\\,x")
        testLatex("x^(x+x)", "x^{x+x}")
    }

    @Test
    fun exponent() {
        testLatex("exp(x)", "e^{x}")
        testLatex("exp(x/2)", "\\operatorname{exp}\\,\\left(\\frac{x}{2}\\right)")
        testLatex("exp(x^2)", "\\operatorname{exp}\\,\\left(x^{2}\\right)")
    }

    @Test
    fun fraction() {
        testLatex("x/y", "\\frac{x}{y}")
        testLatex("x^(x/y)", "x^{x/y}")
    }
}
