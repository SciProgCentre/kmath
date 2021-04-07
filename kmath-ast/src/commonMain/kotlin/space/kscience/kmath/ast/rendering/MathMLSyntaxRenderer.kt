package space.kscience.kmath.ast.rendering

/**
 * [SyntaxRenderer] implementation for MathML.
 *
 * The generated XML string is a valid MathML instance.
 *
 * @author Iaroslav Postovalov
 */
public object MathMLSyntaxRenderer : SyntaxRenderer {
    public override fun render(node: MathSyntax, output: Appendable) {
        output.append("<math xmlns=\"http://www.w3.org/1998/Math/MathML\"><mrow>")
        render0(node, output)
        output.append("</mrow></math>")
    }

    private fun render0(node: MathSyntax, output: Appendable): Unit = output.run {
        fun tag(tagName: String, vararg attr: Pair<String, String>, block: () -> Unit = {}) {
            append('<')
            append(tagName)

            if (attr.isNotEmpty()) {
                append(' ')
                var count = 0

                for ((name, value) in attr) {
                    if (++count > 1) append(' ')
                    append(name)
                    append("=\"")
                    append(value)
                    append('"')
                }
            }

            append('>')
            block()
            append("</")
            append(tagName)
            append('>')
        }

        fun render(syntax: MathSyntax) = render0(syntax, output)

        when (node) {
            is NumberSyntax -> tag("mn") { append(node.string) }
            is SymbolSyntax -> tag("mi") { append(node.string) }
            is OperatorNameSyntax -> tag("mo") { append(node.name) }

            is SpecialSymbolSyntax -> when (node.kind) {
                SpecialSymbolSyntax.Kind.INFINITY -> tag("mo") { append("&infin;") }
                SpecialSymbolSyntax.Kind.SMALL_PI -> tag("mo") { append("&pi;") }
            }

            is OperandSyntax -> if (node.parentheses) {
                tag("mfenced", "open" to "(", "close" to ")", "separators" to "") {
                    render(node.operand)
                }
            } else {
                render(node.operand)
            }

            is UnaryOperatorSyntax -> {
                render(node.prefix)
                tag("mspace", "width" to "0.167em")
                render(node.operand)
            }

            is UnaryPlusSyntax -> {
                tag("mo") { append('+') }
                render(node.operand)
            }

            is UnaryMinusSyntax -> {
                tag("mo") { append("-") }
                render(node.operand)
            }

            is RadicalSyntax -> tag("msqrt") { render(node.operand) }

            is SuperscriptSyntax -> tag("msup") {
                tag("mrow") { render(node.left) }
                tag("mrow") { render(node.right) }
            }

            is SubscriptSyntax -> tag("msub") {
                tag("mrow") { render(node.left) }
                tag("mrow") { render(node.right) }
            }

            is BinaryOperatorSyntax -> {
                render(node.prefix)

                tag("mfenced", "open" to "(", "close" to ")", "separators" to "") {
                    render(node.left)
                    tag("mo") { append(',') }
                    render(node.right)
                }
            }

            is BinaryPlusSyntax -> {
                render(node.left)
                tag("mo") { append('+') }
                render(node.right)
            }

            is BinaryMinusSyntax -> {
                render(node.left)
                tag("mo") { append('-') }
                render(node.right)
            }

            is FractionSyntax -> tag("mfrac") {
                tag("mrow") {
                    render(node.left)
                }

                tag("mrow") {
                    render(node.right)
                }
            }

            is RadicalWithIndexSyntax -> tag("mroot") {
                tag("mrow") { render(node.right) }
                tag("mrow") { render(node.left) }
            }

            is MultiplicationSyntax -> {
                render(node.left)
                if (node.times) tag("mo") { append("&times;") } else tag("mspace", "width" to "0.167em")
                render(node.right)
            }
        }
    }
}
