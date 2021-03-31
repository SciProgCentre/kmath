package space.kscience.kmath.ast.rendering

/**
 * Abstraction of writing [MathSyntax] as a string of an actual markup language. Typical implementation should
 * involve traversal of MathSyntax with handling each its subtype.
 *
 * @author Iaroslav Postovalov
 */
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
public fun SyntaxRenderer.renderWithStringBuilder(node: MathSyntax): String {
    val sb = StringBuilder()
    render(node, sb)
    return sb.toString()
}
