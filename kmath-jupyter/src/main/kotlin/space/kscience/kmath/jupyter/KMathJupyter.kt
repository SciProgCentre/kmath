package space.kscience.kmath.jupyter

import kotlinx.html.Unsafe
import kotlinx.html.div
import kotlinx.html.stream.createHTML
import kotlinx.html.unsafe
import org.jetbrains.kotlinx.jupyter.api.DisplayResult
import org.jetbrains.kotlinx.jupyter.api.HTML
import org.jetbrains.kotlinx.jupyter.api.annotations.JupyterLibrary
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.ast.rendering.FeaturedMathRendererWithPostProcess
import space.kscience.kmath.ast.rendering.MathMLSyntaxRenderer
import space.kscience.kmath.ast.rendering.renderWithStringBuilder
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.operations.GroupOperations
import space.kscience.kmath.operations.RingOperations
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.asSequence

@JupyterLibrary
internal class KMathJupyter : JupyterIntegration() {
    private val mathRender = FeaturedMathRendererWithPostProcess.Default
    private val syntaxRender = MathMLSyntaxRenderer

    override fun Builder.onLoaded() {
        import(
            "space.kscience.kmath.ast.*",
            "space.kscience.kmath.ast.rendering.*",
            "space.kscience.kmath.operations.*",
            "space.kscience.kmath.expressions.*",
            "space.kscience.kmath.misc.*",
            "space.kscience.kmath.real.*",
        )

        fun MST.toDisplayResult(): DisplayResult = HTML(createHTML().div {
            unsafe {
                +syntaxRender.renderWithStringBuilder(mathRender.render(this@toDisplayResult))
            }
        })

        render<MST> { it.toDisplayResult() }
        render<Number> { MST.Numeric(it).toDisplayResult() }

        fun Unsafe.appendCellValue(it: Any?) {
            when (it) {
                is Number -> {
                    val s = StringBuilder()
                    syntaxRender.renderPart(mathRender.render(MST.Numeric(it)), s)
                    +s.toString()
                }
                is MST -> {
                    val s = StringBuilder()
                    syntaxRender.renderPart(mathRender.render(it), s)
                    +s.toString()
                }
                else -> {
                    +"<ms>"
                    +it.toString()
                    +"</ms>"
                }
            }
        }

        render<Structure2D<*>> { structure ->
            HTML(createHTML().div {
                unsafe {
                    +"<math xmlns=\"https://www.w3.org/1998/Math/MathML\">"
                    +"<mrow>"
                    +"<mfenced open=\"[\" close=\"]\" separators=\"\">"
                    +"<mtable>"
                    structure.rows.forEach { row ->
                        +"<mtr>"
                        row.asSequence().forEach {
                            +"<mtd>"
                            appendCellValue(it)
                            +"</mtd>"
                        }
                        +"</mtr>"
                    }
                    +"</mtable>"
                    +"</mfenced>"
                    +"</mrow>"
                    +"</math>"
                }
            })
        }

        render<Buffer<*>> { buffer ->
            HTML(createHTML().div {
                unsafe {
                    +"<math xmlns=\"https://www.w3.org/1998/Math/MathML\">"
                    +"<mrow>"
                    +"<mfenced open=\"[\" close=\"]\" separators=\"\">"
                    +"<mtable>"
                    buffer.asSequence().forEach {
                        +"<mtr>"
                        +"<mtd>"
                        appendCellValue(it)
                        +"</mtd>"
                        +"</mtr>"
                    }
                    +"</mtable>"
                    +"</mfenced>"
                    +"</mrow>"
                    +"</math>"
                }
            })
        }

        render<Complex> {
            MST.Binary(
                operation = GroupOperations.PLUS_OPERATION,
                left = MST.Numeric(it.re),
                right = MST.Binary(RingOperations.TIMES_OPERATION, MST.Numeric(it.im), MST.Symbolic("i")),
            ).toDisplayResult()
        }
    }
}
