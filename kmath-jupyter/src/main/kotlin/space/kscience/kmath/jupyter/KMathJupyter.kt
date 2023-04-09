/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.jupyter

import kotlinx.html.Unsafe
import kotlinx.html.div
import kotlinx.html.stream.createHTML
import kotlinx.html.unsafe
import org.jetbrains.kotlinx.jupyter.api.DisplayResult
import org.jetbrains.kotlinx.jupyter.api.HTML
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.ast.rendering.FeaturedMathRendererWithPostProcess
import space.kscience.kmath.ast.rendering.MathMLSyntaxRenderer
import space.kscience.kmath.ast.rendering.renderWithStringBuilder
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.Quaternion
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.MstRing
import space.kscience.kmath.nd.Structure2D
import space.kscience.kmath.operations.asSequence
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.Buffer

/**
 * A function for conversion of number to MST for pretty print
 */
public fun Number.toMst(): MST.Numeric = MST.Numeric(this)

@OptIn(UnstableKMathAPI::class)
internal class KMathJupyter : JupyterIntegration() {
    private val mathRender = FeaturedMathRendererWithPostProcess.Default
    private val syntaxRender = MathMLSyntaxRenderer

    private fun MST.toDisplayResult(): DisplayResult = HTML(createHTML().div {
        unsafe {
            +syntaxRender.renderWithStringBuilder(mathRender.render(this@toDisplayResult))
        }
    })

    private fun Unsafe.appendCellValue(it: Any?) {
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

    @OptIn(PerformancePitfall::class)
    override fun Builder.onLoaded() {
        import(
            "space.kscience.kmath.ast.*",
            "space.kscience.kmath.ast.rendering.*",
            "space.kscience.kmath.structures.*",
            "space.kscience.kmath.operations.*",
            "space.kscience.kmath.expressions.*",
            "space.kscience.kmath.nd.*",
            "space.kscience.kmath.misc.*",
            "space.kscience.kmath.real.*",
        )

        import("space.kscience.kmath.jupyter.toMst")

        render<MST> { it.toDisplayResult() }
        //render<Number> { MST.Numeric(it).toDisplayResult() }

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
            MstRing {
                number(it.re) + number(it.im) * bindSymbol("i")
            }.toDisplayResult()
        }

        render<Quaternion> {
            MstRing {
                number(it.w) +
                        number(it.x) * bindSymbol("i") +
                        number(it.x) * bindSymbol("j") +
                        number(it.x) * bindSymbol("k")
            }.toDisplayResult()
        }
    }
}
