/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast.rendering

import space.kscience.kmath.misc.UnstableKMathAPI

/**
 * Abstraction of writing [MathSyntax] as a string of an actual markup language. Typical implementation should
 * involve traversal of MathSyntax with handling each its subtype.
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun interface SyntaxRenderer {
    /**
     * Renders the [MathSyntax] to [output].
     */
    public fun render(node: MathSyntax, output: Appendable)
}

/**
 * Calls [SyntaxRenderer.render] with given [node] and a new [StringBuilder] instance, and returns its content.
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun SyntaxRenderer.renderWithStringBuilder(node: MathSyntax): String {
    val sb = StringBuilder()
    render(node, sb)
    return sb.toString()
}
