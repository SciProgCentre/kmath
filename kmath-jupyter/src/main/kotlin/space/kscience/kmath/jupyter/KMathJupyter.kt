package space.kscience.kmath.jupyter

import kotlinx.html.div
import kotlinx.html.stream.createHTML
import kotlinx.html.unsafe
import org.jetbrains.kotlinx.jupyter.api.HTML
import org.jetbrains.kotlinx.jupyter.api.annotations.JupyterLibrary
import org.jetbrains.kotlinx.jupyter.api.libraries.JupyterIntegration
import space.kscience.kmath.ast.MST
import space.kscience.kmath.ast.rendering.FeaturedMathRendererWithPostProcess
import space.kscience.kmath.ast.rendering.MathMLSyntaxRenderer
import space.kscience.kmath.ast.rendering.renderWithStringBuilder

@JupyterLibrary
internal class KMathJupyter : JupyterIntegration() {
    override fun Builder.onLoaded() {
        import(
            "space.kscience.kmath.ast.*",
            "space.kscience.kmath.ast.rendering.*",
            "space.kscience.kmath.operations.*",
            "space.kscience.kmath.expressions.*",
            "space.kscience.kmath.misc.*",
        )

        render<MST> { mst ->
            HTML(createHTML().div {
                unsafe {
                    +MathMLSyntaxRenderer.renderWithStringBuilder(FeaturedMathRendererWithPostProcess.Default.render(mst))
                }
            })
        }
    }
}
