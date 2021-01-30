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
    fun fn(pattern: String) = fn(pattern, cTypeName)
    val className = "Gsl${kotlinTypeAlias}Matrix"
    val structName = sn("gsl_matrixR", cTypeName)

    @Language("kotlin") val text = """internal class $className(
    override val rawNativeHandle: CPointer<$structName>,
    scope: AutofreeScope,
    owned: Boolean,
) : GslMatrix<$kotlinTypeName, $structName>(scope, owned) {
    override val rowNum: Int
        get() = nativeHandle.pointed.size1.toInt()

    override val colNum: Int
        get() = nativeHandle.pointed.size2.toInt()

    override val rows: Buffer<Buffer<$kotlinTypeName>>
        get() = VirtualBuffer(rowNum) { r ->
            Gsl${kotlinTypeAlias}Vector(
                ${fn("gsl_matrixRrow")}(nativeHandle, r.toULong()).placeTo(scope).pointed.vector.ptr,
                scope,
                false,
            )
        }

    override val columns: Buffer<Buffer<$kotlinTypeName>>
        get() = VirtualBuffer(rowNum) { c ->
            Gsl${kotlinTypeAlias}Vector(
                ${fn("gsl_matrixRcolumn")}(nativeHandle, c.toULong()).placeTo(scope).pointed.vector.ptr,
                scope,
                false,
            )
        }

    override operator fun get(i: Int, j: Int): $kotlinTypeName = 
        ${fn("gsl_matrixRget")}(nativeHandle, i.toULong(), j.toULong())

    override operator fun set(i: Int, j: Int, value: ${kotlinTypeName}): Unit =
        ${fn("gsl_matrixRset")}(nativeHandle, i.toULong(), j.toULong(), value)

    override fun copy(): $className {
        val new = checkNotNull(${fn("gsl_matrixRalloc")}(rowNum.toULong(), colNum.toULong()))
        ${fn("gsl_matrixRmemcpy")}(new, nativeHandle)
        return $className(new, scope, true)
    }

    override fun close(): Unit = ${fn("gsl_matrixRfree")}(nativeHandle)

    override fun equals(other: Any?): Boolean {
        if (other is $className)
            return ${fn("gsl_matrixRequal")}(nativeHandle, other.nativeHandle) == 1

        return super.equals(other)
    }
}"""
    f += createClass(text)
    f += createNewLine(2)
}

/**
 * Generates matrices source code for kmath-gsl.
 */
fun matricesCodegen(outputFile: String, project: Project = createProject()) {
    val f = KtPsiFactory(project, true).run {
        createFile("").also { f ->
            f += createPackageDirective(FqName("kscience.kmath.gsl"))
            f += createNewLine(2)
            f += createImportDirective(ImportPath.fromString("kotlinx.cinterop.*"))
            f += createNewLine(1)
            f += createImportDirective(ImportPath.fromString("kscience.kmath.structures.*"))
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
