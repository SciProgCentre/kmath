/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast.rendering

import space.kscience.kmath.ast.parseMath
import space.kscience.kmath.expressions.MST
import kotlin.test.assertEquals

internal object TestUtils {
    private fun mathSyntax(mst: MST) = FeaturedMathRendererWithPostProcess.Default.render(mst)
    private fun latex(mst: MST) = LatexSyntaxRenderer.renderWithStringBuilder(mathSyntax(mst))
    private fun mathML(mst: MST) = MathMLSyntaxRenderer.renderWithStringBuilder(mathSyntax(mst))

    internal fun testLatex(mst: MST, expectedLatex: String) = assertEquals(
        expected = expectedLatex,
        actual = latex(mst),
    )

    internal fun testLatex(expression: String, expectedLatex: String) = assertEquals(
        expected = expectedLatex,
        actual = latex(expression.parseMath()),
    )

    internal fun testLatex(expression: MathSyntax, expectedLatex: String) = assertEquals(
        expected = expectedLatex,
        actual = LatexSyntaxRenderer.renderWithStringBuilder(expression),
    )

    internal fun testMathML(mst: MST, expectedMathML: String) = assertEquals(
        expected = "<math xmlns=\"https://www.w3.org/1998/Math/MathML\"><mrow>$expectedMathML</mrow></math>",
        actual = mathML(mst),
    )

    internal fun testMathML(expression: String, expectedMathML: String) = assertEquals(
        expected = "<math xmlns=\"https://www.w3.org/1998/Math/MathML\"><mrow>$expectedMathML</mrow></math>",
        actual = mathML(expression.parseMath()),
    )

    internal fun testMathML(expression: MathSyntax, expectedMathML: String) = assertEquals(
        expected = "<math xmlns=\"https://www.w3.org/1998/Math/MathML\"><mrow>$expectedMathML</mrow></math>",
        actual = MathMLSyntaxRenderer.renderWithStringBuilder(expression),
    )
}
