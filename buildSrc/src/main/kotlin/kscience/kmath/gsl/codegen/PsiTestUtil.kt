package kscience.kmath.gsl.codegen

import org.jetbrains.kotlin.com.intellij.openapi.util.text.StringUtil
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.com.intellij.psi.impl.DebugUtil
import org.jetbrains.kotlin.com.intellij.psi.impl.source.PsiFileImpl
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.psi.KtFile
import kotlin.math.min

internal object PsiTestUtil {
    fun checkFileStructure(file: KtFile) {
        compareFromAllRoots(file) { f -> DebugUtil.psiTreeToString(f, false) }
    }

    private fun compareFromAllRoots(
        file: KtFile,
        function: (PsiFile) -> String
    ) {
        val dummyFile = createDummyCopy(file)

        val psiTree = StringUtil.join(
            file.viewProvider.allFiles,
            { param -> function(param) },
            "\n"
        )

        val reparsedTree = StringUtil.join(
            dummyFile.viewProvider.allFiles,
            { param -> function(param) },
            "\n"
        )

        assertPsiTextTreeConsistency(psiTree, reparsedTree)
    }

    private fun assertPsiTextTreeConsistency(psiTree: String, reparsedTree: String) {
        var psiTreeMutable = psiTree
        var reparsedTreeMutable = reparsedTree

        if (psiTreeMutable != reparsedTreeMutable) {
            val psiLines = splitByLinesDontTrim(psiTreeMutable)
            val reparsedLines = splitByLinesDontTrim(reparsedTreeMutable)
            var i = 0

            while (true) {
                if (i >= psiLines.size || i >= reparsedLines.size || psiLines[i] != reparsedLines[i]) {
                    psiLines[min(i, psiLines.size - 1)] += "   // in PSI structure"
                    reparsedLines[min(i, reparsedLines.size - 1)] += "   // re-created from text"
                    break
                }

                i++
            }

            psiTreeMutable = StringUtil.join(psiLines, "\n")
            reparsedTreeMutable = StringUtil.join(reparsedLines, "\n")
            assert(reparsedTreeMutable == psiTreeMutable)
        }
    }

    private fun createDummyCopy(file: KtFile): PsiFile {
        val copy = LightVirtualFile(file.name, file.text)
        copy.originalFile = file.viewProvider.virtualFile
        val dummyCopy = requireNotNull(file.manager.findFile(copy))
        if (dummyCopy is PsiFileImpl) dummyCopy.originalFile = file
        return dummyCopy
    }
}