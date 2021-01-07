package kscience.kmath.gsl.codegen

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.ImportPath
import java.io.File

private fun KtPsiFactory.createVectorClass(
    f: KtFile,
    cTypeName: String,
    kotlinTypeName: String,
    kotlinTypeAlias: String = kotlinTypeName
) {
    val className = "Gsl${kotlinTypeAlias}Vector"
    val structName = sn("gsl_vectorR", cTypeName)

    @Language("kotlin") val text =
        """internal class $className(override val nativeHandle: CPointer<$structName>, scope: DeferScope) : 
    GslVector<$kotlinTypeName, $structName>(scope) {
    override val size: Int
        get() = nativeHandleChecked().pointed.size.toInt()

    override operator fun get(index: Int): $kotlinTypeName = 
        ${fn("gsl_vectorRget", cTypeName)}(nativeHandleChecked(), index.toULong())

    override operator fun set(index: Int, value: $kotlinTypeName): Unit = 
        ${fn("gsl_vectorRset", cTypeName)}(nativeHandleChecked(), index.toULong(), value)

    override fun copy(): $className {
        val new = requireNotNull(${fn("gsl_vectorRalloc", cTypeName)}(size.toULong()))
        ${fn("gsl_vectorRmemcpy", cTypeName)}(new, nativeHandleChecked())
        return ${className}(new, scope)
    }

    override fun equals(other: Any?): Boolean {
        if (other is $className)
            return ${
            fn("gsl_vectorRequal",
                cTypeName)
        }(nativeHandleChecked(), other.nativeHandleChecked()) == 1

        return super.equals(other)
    }

    override fun close(): Unit = ${fn("gsl_vectorRfree", cTypeName)}(nativeHandleChecked())
}"""

    f += createClass(text)
    f += createNewLine(2)
}

/**
 * Generates vectors source code for kmath-gsl.
 */
fun vectorsCodegen(outputFile: String, project: Project = createProject()) {
    val f = KtPsiFactory(project, true).run {
        createFile("").also { f ->
            f += createPackageDirective(FqName("kscience.kmath.gsl"))
            f += createNewLine(2)
            f += createImportDirective(ImportPath.fromString("kotlinx.cinterop.*"))
            f += createNewLine(1)
            f += createImportDirective(ImportPath.fromString("org.gnu.gsl.*"))
            f += createNewLine(2)
            createVectorClass(f, "double", "Double", "Real")
            createVectorClass(f, "float", "Float")
            createVectorClass(f, "short", "Short")
            createVectorClass(f, "ushort", "UShort")
            createVectorClass(f, "long", "Long")
            createVectorClass(f, "ulong", "ULong")
            createVectorClass(f, "int", "Int")
            createVectorClass(f, "uint", "UInt")
        }
    }

    PsiTestUtil.checkFileStructure(f)

    File(outputFile).apply {
        parentFile.mkdirs()
        writeText(f.text)
    }
}
