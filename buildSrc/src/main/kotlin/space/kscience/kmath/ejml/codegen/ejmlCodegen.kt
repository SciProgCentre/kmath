/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("KDocUnresolvedReference")

package space.kscience.kmath.ejml.codegen

import org.intellij.lang.annotations.Language
import java.io.File

private fun Appendable.appendEjmlVector(type: String, ejmlMatrixType: String) {
    @Language("kotlin") val text = """/**
 * [EjmlVector] specialization for [$type].
 */
public class Ejml${type}Vector<out M : $ejmlMatrixType>(override val origin: M) : EjmlVector<$type, M>(origin) {
    init {
        require(origin.numRows == 1) { "The origin matrix must have only one row to form a vector" }
    }

    override operator fun get(index: Int): $type = origin[0, index]
}"""
    appendLine(text)
    appendLine()
}

private fun Appendable.appendEjmlMatrix(type: String, ejmlMatrixType: String) {
    val text = """/**
 * [EjmlMatrix] specialization for [$type].
 */
public class Ejml${type}Matrix<out M : $ejmlMatrixType>(override val origin: M) : EjmlMatrix<$type, M>(origin) {
    override operator fun get(i: Int, j: Int): $type = origin[i, j]
}"""
    appendLine(text)
    appendLine()
}

private fun Appendable.appendEjmlLinearSpace(
    type: String,
    kmathAlgebra: String,
    ejmlMatrixParentTypeMatrix: String,
    ejmlMatrixType: String,
    ejmlMatrixDenseType: String,
    ops: String,
    denseOps: String,
    isDense: Boolean,
) {
    @Language("kotlin") val text = """/**
 * [EjmlLinearSpace] implementation based on [CommonOps_$ops], [DecompositionFactory_${ops}] operations and
 * [${ejmlMatrixType}] matrices.
 */
public object EjmlLinearSpace${ops} : EjmlLinearSpace<${type}, ${kmathAlgebra}, $ejmlMatrixType>() {
    /**
     * The [${kmathAlgebra}] reference.
     */
    override val elementAlgebra: $kmathAlgebra get() = $kmathAlgebra

    @Suppress("UNCHECKED_CAST")
    override fun Matrix<${type}>.toEjml(): Ejml${type}Matrix<${ejmlMatrixType}> = when {
        this is Ejml${type}Matrix<*> && origin is $ejmlMatrixType -> this as Ejml${type}Matrix<${ejmlMatrixType}>
        else -> buildMatrix(rowNum, colNum) { i, j -> get(i, j) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun Point<${type}>.toEjml(): Ejml${type}Vector<${ejmlMatrixType}> = when {
        this is Ejml${type}Vector<*> && origin is $ejmlMatrixType -> this as Ejml${type}Vector<${ejmlMatrixType}>
        else -> Ejml${type}Vector(${ejmlMatrixType}(size, 1).also {
            (0 until it.numRows).forEach { row -> it[row, 0] = get(row) }
        })
    }

    override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: ${kmathAlgebra}.(i: Int, j: Int) -> ${type},
    ): Ejml${type}Matrix<${ejmlMatrixType}> = ${ejmlMatrixType}(rows, columns).also {
        (0 until rows).forEach { row ->
            (0 until columns).forEach { col -> it[row, col] = elementAlgebra.initializer(row, col) }
        }
    }.wrapMatrix()

    override fun buildVector(
        size: Int,
        initializer: ${kmathAlgebra}.(Int) -> ${type},
    ): Ejml${type}Vector<${ejmlMatrixType}> = Ejml${type}Vector(${ejmlMatrixType}(size, 1).also {
        (0 until it.numRows).forEach { row -> it[row, 0] = elementAlgebra.initializer(row) }
    })

    private fun <T : ${ejmlMatrixParentTypeMatrix}> T.wrapMatrix() = Ejml${type}Matrix(this)
    private fun <T : ${ejmlMatrixParentTypeMatrix}> T.wrapVector() = Ejml${type}Vector(this)

    override fun Matrix<${type}>.unaryMinus(): Matrix<${type}> = this * elementAlgebra { -one }

    override fun Matrix<${type}>.dot(other: Matrix<${type}>): Ejml${type}Matrix<${ejmlMatrixType}> {
        val out = ${ejmlMatrixType}(1, 1)
        CommonOps_${ops}.mult(toEjml().origin, other.toEjml().origin, out)
        return out.wrapMatrix()
    }

    override fun Matrix<${type}>.dot(vector: Point<${type}>): Ejml${type}Vector<${ejmlMatrixType}> {
        val out = ${ejmlMatrixType}(1, 1)
        CommonOps_${ops}.mult(toEjml().origin, vector.toEjml().origin, out)
        return out.wrapVector()
    }

    override operator fun Matrix<${type}>.minus(other: Matrix<${type}>): Ejml${type}Matrix<${ejmlMatrixType}> {
        val out = ${ejmlMatrixType}(1, 1)

        CommonOps_${ops}.add(
            elementAlgebra.one, 
            toEjml().origin, 
            elementAlgebra { -one },
            other.toEjml().origin, 
            out,${
        if (isDense) "" else
            """
            null, 
            null,"""
    }
        )

        return out.wrapMatrix()
    }

    override operator fun Matrix<${type}>.times(value: ${type}): Ejml${type}Matrix<${ejmlMatrixType}> {
        val res = ${ejmlMatrixType}(1, 1)
        CommonOps_${ops}.scale(value, toEjml().origin, res)
        return res.wrapMatrix()
    }

    override fun Point<${type}>.unaryMinus(): Ejml${type}Vector<${ejmlMatrixType}> {
        val res = ${ejmlMatrixType}(1, 1)
        CommonOps_${ops}.changeSign(toEjml().origin, res)
        return res.wrapVector()
    }

    override fun Matrix<${type}>.plus(other: Matrix<${type}>): Ejml${type}Matrix<${ejmlMatrixType}> {
        val out = ${ejmlMatrixType}(1, 1)
        
        CommonOps_${ops}.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra.one,
            other.toEjml().origin, 
            out,${
        if (isDense) "" else
            """
            null, 
            null,"""
    }
        )

        return out.wrapMatrix()
    }

    override fun Point<${type}>.plus(other: Point<${type}>): Ejml${type}Vector<${ejmlMatrixType}> {
        val out = ${ejmlMatrixType}(1, 1)

        CommonOps_${ops}.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra.one,
            other.toEjml().origin,
            out,${
        if (isDense) "" else
            """
            null, 
            null,"""
    }
        )

        return out.wrapVector()
    }

    override fun Point<${type}>.minus(other: Point<${type}>): Ejml${type}Vector<${ejmlMatrixType}> {
        val out = ${ejmlMatrixType}(1, 1)

        CommonOps_${ops}.add(
            elementAlgebra.one,
            toEjml().origin,
            elementAlgebra { -one },
            other.toEjml().origin,
            out,${
        if (isDense) "" else
            """
            null, 
            null,"""
    }
        )

        return out.wrapVector()
    }

    override fun ${type}.times(m: Matrix<${type}>): Ejml${type}Matrix<${ejmlMatrixType}> = m * this

    override fun Point<${type}>.times(value: ${type}): Ejml${type}Vector<${ejmlMatrixType}> {
        val res = ${ejmlMatrixType}(1, 1)
        CommonOps_${ops}.scale(value, toEjml().origin, res)
        return res.wrapVector()
    }

    override fun ${type}.times(v: Point<${type}>): Ejml${type}Vector<${ejmlMatrixType}> = v * this

    @UnstableKMathAPI
    override fun <F : StructureFeature> computeFeature(structure: Matrix<${type}>, type: KClass<out F>): F? {
        structure.getFeature(type)?.let { return it }
        val origin = structure.toEjml().origin

        return when (type) {
        ${
        if (isDense)
            """    InverseMatrixFeature::class -> object : InverseMatrixFeature<${type}> {
                override val inverse: Matrix<${type}> by lazy {
                    val res = origin.copy()
                    CommonOps_${ops}.invert(res)
                    res.wrapMatrix()
                }
            }

            DeterminantFeature::class -> object : DeterminantFeature<${type}> {
                override val determinant: $type by lazy { CommonOps_${ops}.det(origin) }
            }

            SingularValueDecompositionFeature::class -> object : SingularValueDecompositionFeature<${type}> {
                private val svd by lazy {
                    DecompositionFactory_${ops}.svd(origin.numRows, origin.numCols, true, true, false)
                        .apply { decompose(origin.copy()) }
                }

                override val u: Matrix<${type}> by lazy { svd.getU(null, false).wrapMatrix() }
                override val s: Matrix<${type}> by lazy { svd.getW(null).wrapMatrix() }
                override val v: Matrix<${type}> by lazy { svd.getV(null, false).wrapMatrix() }
                override val singularValues: Point<${type}> by lazy { ${type}Buffer(svd.singularValues) }
            }

            QRDecompositionFeature::class -> object : QRDecompositionFeature<${type}> {
                private val qr by lazy {
                    DecompositionFactory_${ops}.qr().apply { decompose(origin.copy()) }
                }

                override val q: Matrix<${type}> by lazy {
                    qr.getQ(null, false).wrapMatrix().withFeature(OrthogonalFeature)
                }

                override val r: Matrix<${type}> by lazy { qr.getR(null, false).wrapMatrix().withFeature(UFeature) }
            }

            CholeskyDecompositionFeature::class -> object : CholeskyDecompositionFeature<${type}> {
                override val l: Matrix<${type}> by lazy {
                    val cholesky =
                        DecompositionFactory_${ops}.chol(structure.rowNum, true).apply { decompose(origin.copy()) }

                    cholesky.getT(null).wrapMatrix().withFeature(LFeature)
                }
            }

            LupDecompositionFeature::class -> object : LupDecompositionFeature<${type}> {
                private val lup by lazy {
                    DecompositionFactory_${ops}.lu(origin.numRows, origin.numCols).apply { decompose(origin.copy()) }
                }

                override val l: Matrix<${type}> by lazy {
                    lup.getLower(null).wrapMatrix().withFeature(LFeature)
                }

                override val u: Matrix<${type}> by lazy {
                    lup.getUpper(null).wrapMatrix().withFeature(UFeature)
                }

                override val p: Matrix<${type}> by lazy { lup.getRowPivot(null).wrapMatrix() }
            }""" else """    QRDecompositionFeature::class -> object : QRDecompositionFeature<$type> {
                private val qr by lazy {
                    DecompositionFactory_${ops}.qr(FillReducing.NONE).apply { decompose(origin.copy()) }
                }

                override val q: Matrix<${type}> by lazy {
                    qr.getQ(null, false).wrapMatrix().withFeature(OrthogonalFeature)
                }

                override val r: Matrix<${type}> by lazy { qr.getR(null, false).wrapMatrix().withFeature(UFeature) }
            }

            CholeskyDecompositionFeature::class -> object : CholeskyDecompositionFeature<${type}> {
                override val l: Matrix<${type}> by lazy {
                    val cholesky =
                        DecompositionFactory_${ops}.cholesky().apply { decompose(origin.copy()) }

                    (cholesky.getT(null) as ${ejmlMatrixParentTypeMatrix}).wrapMatrix().withFeature(LFeature)
                }
            }

            LUDecompositionFeature::class, DeterminantFeature::class, InverseMatrixFeature::class -> object :
                LUDecompositionFeature<${type}>, DeterminantFeature<${type}>, InverseMatrixFeature<${type}> {
                private val lu by lazy {
                    DecompositionFactory_${ops}.lu(FillReducing.NONE).apply { decompose(origin.copy()) }
                }

                override val l: Matrix<${type}> by lazy {
                    lu.getLower(null).wrapMatrix().withFeature(LFeature)
                }

                override val u: Matrix<${type}> by lazy {
                    lu.getUpper(null).wrapMatrix().withFeature(UFeature)
                }

                override val inverse: Matrix<${type}> by lazy {
                    var a = origin
                    val inverse = ${ejmlMatrixDenseType}(1, 1)
                    val solver = LinearSolverFactory_${ops}.lu(FillReducing.NONE)
                    if (solver.modifiesA()) a = a.copy()
                    val i = CommonOps_${denseOps}.identity(a.numRows)
                    solver.solve(i, inverse)
                    inverse.wrapMatrix()
                }

                override val determinant: $type by lazy { elementAlgebra.number(lu.computeDeterminant().real) }
            }"""
    }

            else -> null
        }?.let{
            type.cast(it)
        }
    }

    /**
     * Solves for *x* in the following equation: *x = [a] <sup>-1</sup> &middot; [b]*.
     *
     * @param a the base matrix.
     * @param b n by p matrix.
     * @return the solution for *x* that is n by p.
     */
    public fun solve(a: Matrix<${type}>, b: Matrix<${type}>): Ejml${type}Matrix<${ejmlMatrixType}> {
        val res = ${ejmlMatrixType}(1, 1)
        CommonOps_${ops}.solve(${ejmlMatrixType}(a.toEjml().origin), ${ejmlMatrixType}(b.toEjml().origin), res)
        return res.wrapMatrix()
    }

    /**
     * Solves for *x* in the following equation: *x = [a] <sup>-1</sup> &middot; [b]*.
     *
     * @param a the base matrix.
     * @param b n by p vector.
     * @return the solution for *x* that is n by p.
     */
    public fun solve(a: Matrix<${type}>, b: Point<${type}>): Ejml${type}Vector<${ejmlMatrixType}> {
        val res = ${ejmlMatrixType}(1, 1)
        CommonOps_${ops}.solve(${ejmlMatrixType}(a.toEjml().origin), ${ejmlMatrixType}(b.toEjml().origin), res)
        return Ejml${type}Vector(res)
    }
}"""
    appendLine(text)
    appendLine()
}


/**
 * Generates routine EJML classes.
 */
fun ejmlCodegen(outputFile: String): Unit = File(outputFile).run {
    parentFile.mkdirs()

    writer().use {
        it.appendLine("/*")
        it.appendLine(" * Copyright 2018-2021 KMath contributors.")
        it.appendLine(" * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.")
        it.appendLine(" */")
        it.appendLine()
        it.appendLine("/* This file is generated with buildSrc/src/main/kotlin/space/kscience/kmath/ejml/codegen/ejmlCodegen.kt */")
        it.appendLine()
        it.appendLine("package space.kscience.kmath.ejml")
        it.appendLine()
        it.appendLine("""import org.ejml.data.*
import org.ejml.dense.row.CommonOps_DDRM
import org.ejml.dense.row.CommonOps_FDRM
import org.ejml.dense.row.factory.DecompositionFactory_DDRM
import org.ejml.dense.row.factory.DecompositionFactory_FDRM
import org.ejml.sparse.FillReducing
import org.ejml.sparse.csc.CommonOps_DSCC
import org.ejml.sparse.csc.CommonOps_FSCC
import org.ejml.sparse.csc.factory.DecompositionFactory_DSCC
import org.ejml.sparse.csc.factory.DecompositionFactory_FSCC
import org.ejml.sparse.csc.factory.LinearSolverFactory_DSCC
import org.ejml.sparse.csc.factory.LinearSolverFactory_FSCC
import space.kscience.kmath.linear.*
import space.kscience.kmath.linear.Matrix
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.StructureFeature
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.FloatField
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.structures.DoubleBuffer
import space.kscience.kmath.structures.FloatBuffer
import kotlin.reflect.KClass
import kotlin.reflect.cast""")
        it.appendLine()
        it.appendEjmlVector("Double", "DMatrix")
        it.appendEjmlVector("Float", "FMatrix")
        it.appendEjmlMatrix("Double", "DMatrix")
        it.appendEjmlMatrix("Float", "FMatrix")
        it.appendEjmlLinearSpace("Double", "DoubleField", "DMatrix", "DMatrixRMaj", "DMatrixRMaj", "DDRM", "DDRM", true)
        it.appendEjmlLinearSpace("Float", "FloatField", "FMatrix", "FMatrixRMaj", "FMatrixRMaj", "FDRM", "FDRM", true)

        it.appendEjmlLinearSpace(
            type = "Double",
            kmathAlgebra = "DoubleField",
            ejmlMatrixParentTypeMatrix = "DMatrix",
            ejmlMatrixType = "DMatrixSparseCSC",
            ejmlMatrixDenseType = "DMatrixRMaj",
            ops = "DSCC",
            denseOps = "DDRM",
            isDense = false,
        )

        it.appendEjmlLinearSpace(
            type = "Float",
            kmathAlgebra = "FloatField",
            ejmlMatrixParentTypeMatrix = "FMatrix",
            ejmlMatrixType = "FMatrixSparseCSC",
            ejmlMatrixDenseType = "FMatrixRMaj",
            ops = "FSCC",
            denseOps = "FDRM",
            isDense = false,
        )
    }
}
