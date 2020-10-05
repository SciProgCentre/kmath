package kscience.kmath.gsl.codegen

import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.ImportPath
import java.io.File

private fun KtPsiFactory.createMatrixClass(
    f: KtFile,
    cTypeName: String,
    kotlinTypeName: String,
    kotlinTypeAlias: String = kotlinTypeName
) {
    val className = "Gsl${kotlinTypeAlias}Matrix"
    val structName = sn("gsl_matrixR", cTypeName)

    @Language("kotlin") val text = """internal class $className(
    override val nativeHandle: CPointer<$structName>,
    features: Set<MatrixFeature> = emptySet()
) : GslMatrix<$kotlinTypeName, $structName>() {
    override val rowNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size1.toInt() }

    override val colNum: Int
        get() = memScoped { nativeHandle.getPointer(this).pointed.size2.toInt() }

    override val features: Set<MatrixFeature> = features

    override fun suggestFeature(vararg features: MatrixFeature): $className =
        ${className}(nativeHandle, this.features + features)

    override operator fun get(i: Int, j: Int): $kotlinTypeName = ${
        fn("gsl_matrixRget", cTypeName)
    }(nativeHandle, i.toULong(), j.toULong())

    override operator fun set(i: Int, j: Int, value: ${kotlinTypeName}): Unit =
        ${fn("gsl_matrixRset", cTypeName)}(nativeHandle, i.toULong(), j.toULong(), value)

    override fun copy(): $className = memScoped {
        val new = requireNotNull(${fn("gsl_matrixRalloc", cTypeName)}(rowNum.toULong(), colNum.toULong()))
        ${fn("gsl_matrixRmemcpy", cTypeName)}(new, nativeHandle)
        $className(new, features)
    }

    override fun close(): Unit = ${fn("gsl_matrixRfree", cTypeName)}(nativeHandle)

    override fun equals(other: Any?): Boolean {
        if (other is $className) ${fn("gsl_matrixRequal", cTypeName)}(nativeHandle, other.nativeHandle)
        return super.equals(other)
    }
}"""
    f += createClass(
        text
    )

    f += createNewLine(2)
}

fun matricesCodegen(outputFile: String, project: Project = createProject()) {
    val f = KtPsiFactory(project, true).run {
        createFile("@file:Suppress(\"PackageDirectoryMismatch\")").also { f ->
            f += createNewLine(2)
            f += createPackageDirective(FqName("kscience.kmath.gsl"))
            f += createNewLine(2)
            f += createImportDirective(ImportPath.fromString("kotlinx.cinterop.*"))
            f += createNewLine(1)
            f += createImportDirective(ImportPath.fromString("kscience.kmath.linear.*"))
            f += createNewLine(1)
            f += createImportDirective(ImportPath.fromString("org.gnu.gsl.*"))
            f += createNewLine(2)
            createMatrixClass(f, "double", "Double", "Real")
            createMatrixClass(f, "float", "Float")
            createMatrixClass(f, "short", "Short")
            createMatrixClass(f, "ushort", "UShort")
            createMatrixClass(f, "long", "Long")
            createMatrixClass(f, "ulong", "ULong")
            createMatrixClass(f, "int", "Int")
            createMatrixClass(f, "uint", "UInt")
        }
    }

    PsiTestUtil.checkFileStructure(f)

    File(outputFile).apply {
        parentFile.mkdirs()
        writeText(f.text)
    }
}
