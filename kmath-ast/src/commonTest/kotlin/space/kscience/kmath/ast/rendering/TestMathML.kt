/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast.rendering

import space.kscience.kmath.ast.rendering.TestUtils.testMathML
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.operations.GroupOps
import kotlin.test.Test

internal class TestMathML {
    @Test
    fun number() = testMathML("42", "<mn>42</mn>")

    @Test
    fun symbol() = testMathML("x", "<mi>x</mi>")

    @Test
    fun operatorName() = testMathML(
        "sin(1)",
        "<mo>sin</mo><mspace width=\"0.167em\"></mspace><mfenced open=\"(\" close=\")\" separators=\"\"><mn>1</mn></mfenced>",
    )

    @Test
    fun specialSymbol() {
        testMathML(MST.Numeric(Double.POSITIVE_INFINITY), "<mo>&infin;</mo>")
        testMathML("pi", "<mo>&pi;</mo>")
    }

    @Test
    fun operand() {
        testMathML(
            "sin(1)",
            "<mo>sin</mo><mspace width=\"0.167em\"></mspace><mfenced open=\"(\" close=\")\" separators=\"\"><mn>1</mn></mfenced>",
        )

        testMathML("1+1", "<mn>1</mn><mo>+</mo><mn>1</mn>")
    }

    @Test
    fun unaryOperator() = testMathML(
        "sin(1)",
        "<mo>sin</mo><mspace width=\"0.167em\"></mspace><mfenced open=\"(\" close=\")\" separators=\"\"><mn>1</mn></mfenced>",
    )

    @Test
    fun unaryPlus() =
        testMathML(MST.Unary(GroupOps.PLUS_OPERATION, MST.Numeric(1)), "<mo>+</mo><mn>1</mn>")

    @Test
    fun unaryMinus() = testMathML("-x", "<mo>-</mo><mi>x</mi>")

    @Test
    fun radical() = testMathML("sqrt(x)", "<msqrt><mi>x</mi></msqrt>")

    @Test
    fun superscript() = testMathML("x^y", "<msup><mrow><mi>x</mi></mrow><mrow><mi>y</mi></mrow></msup>")

    @Test
    fun subscript() = testMathML(
        SubscriptSyntax("", SymbolSyntax("x"), NumberSyntax("123")),
        "<msub><mrow><mi>x</mi></mrow><mrow><mn>123</mn></mrow></msub>",
    )

    @Test
    fun binaryOperator() = testMathML(
        "f(x, y)",
        "<mo>f</mo><mfenced open=\"(\" close=\")\" separators=\"\"><mi>x</mi><mo>,</mo><mi>y</mi></mfenced>",
    )

    @Test
    fun binaryPlus() = testMathML("x+x", "<mi>x</mi><mo>+</mo><mi>x</mi>")

    @Test
    fun binaryMinus() = testMathML("x-x", "<mi>x</mi><mo>-</mo><mi>x</mi>")

    @Test
    fun fraction() = testMathML("x/x", "<mfrac><mrow><mi>x</mi></mrow><mrow><mi>x</mi></mrow></mfrac>")

    @Test
    fun radicalWithIndex() =
        testMathML(RadicalWithIndexSyntax("", SymbolSyntax("x"), SymbolSyntax("y")),
            "<mroot><mrow><mi>y</mi></mrow><mrow><mi>x</mi></mrow></mroot>")

    @Test
    fun multiplication() {
        testMathML("x*1", "<mi>x</mi><mo>&times;</mo><mn>1</mn>")
        testMathML("1*x", "<mn>1</mn><mspace width=\"0.167em\"></mspace><mi>x</mi>")
    }
}
